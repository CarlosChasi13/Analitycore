from flask import Flask, jsonify, request
import psycopg2
import requests
import os
import logging
from flask_cors import CORS

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app, origins=["https://analitycore-mj75.onrender.com"])

# Configuraciones desde variables de entorno (sin defaults inseguros)
DB_HOST       = os.environ["DB_HOST"]
DB_PORT       = os.environ.get("DB_PORT", "5432")
DB_NAME       = os.environ["DB_NAME"]
DB_USER       = os.environ["DB_USER"]
DB_PASSWORD   = os.environ["DB_PASSWORD"]
ANALYSIS_URL  = os.environ["ANALYSIS_URL"]

# Ruta de prueba
@app.route("/", methods=["GET", "HEAD"])
def index():
    logger.info("Acceso a la ruta de prueba /")
    return jsonify({"message": "Python Service funcionando"})

# Ruta para recibir el texto desde el frontend
@app.route("/submit", methods=["POST"])
def submit_text():
    texto = request.json.get("texto")
    if not texto:
        logger.error("No se proporcionó texto en la solicitud")
        return jsonify({"error": "No se proporcionó texto"}), 400

    logger.info("Recibiendo texto para análisis: %s", texto)

    try:
        # Conexión a la base de datos
        conn = psycopg2.connect(
            host=DB_HOST,
            port=DB_PORT,
            dbname=DB_NAME,
            user=DB_USER,
            password=DB_PASSWORD
        )
        cur = conn.cursor()

        # Inserta registro PENDIENTE
        cur.execute(
            "INSERT INTO job (estado, texto) VALUES (%s, %s) RETURNING id",
            ("PENDIENTE", texto)
        )
        job_id = cur.fetchone()[0]
        conn.commit()

        # Llama al servicio Java
        logger.info("Llamando a Java para jobId=%s", job_id)
        resp = requests.post(f"{ANALYSIS_URL}/analyze/{job_id}")

        if resp.status_code == 200:
            # Marca COMPLETADO y devuelve resultado
            cur.execute("UPDATE job SET estado = %s WHERE id = %s",
                        ("COMPLETADO", job_id))
            conn.commit()
            return jsonify({
                "message": "Análisis completado",
                "resultado": resp.json()
            }), 200
        else:
            # Marca ERROR
            cur.execute("UPDATE job SET estado = %s WHERE id = %s",
                        ("ERROR", job_id))
            conn.commit()
            logger.error("Java devolvió %s: %s",
                         resp.status_code, resp.text)
            return jsonify({
                "error": "Error en servicio Java",
                "detalle": resp.text
            }), 502

    except Exception as e:
        logger.exception("Error inesperado")
        return jsonify({"error": str(e)}), 500

    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    # El puerto lo inyecta Render con $PORT, pero como default ponemos 5000
    port = int(os.environ.get("PORT", 5000))
    app.run(host="0.0.0.0", port=port)
