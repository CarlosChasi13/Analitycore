from flask import Flask, jsonify, request
import psycopg2
import requests
import os
import logging

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# Configuraciones desde variables de entorno
DB_HOST = os.environ.get("DB_HOST", "localhost")
DB_PORT = os.environ.get("DB_PORT", "5432")
DB_NAME = os.environ.get("DB_NAME", "analitycore")
DB_USER = os.environ.get("DB_USER", "postgres")
DB_PASSWORD = os.environ.get("DB_PASSWORD", "postgres")
ANALYSIS_URL = os.environ.get("ANALYSIS_URL", "http://java-service:8080")

# Ruta de prueba
@app.route("/")
def index():
    logger.info("Acceso a la ruta de prueba /")
    return jsonify({"message": "Python Service funcionando"})

# Ruta para recibir el texto desde el frontend y almacenarlo en la base de datos
@app.route("/submit", methods=["POST"])
def submit_text():
    try:
        # Obtener texto desde la solicitud
        texto = request.json.get("texto")
        if not texto:
            logger.error("No se proporcionó texto en la solicitud")
            return jsonify({"error": "No se proporcionó texto"}), 400
        
        logger.info("Recibiendo texto para análisis: %s", texto)

        # Conexión a la base de datos
        conn = psycopg2.connect(
            host=DB_HOST, port=DB_PORT, dbname=DB_NAME,
            user=DB_USER, password=DB_PASSWORD
        )
        cur = conn.cursor()

        # Guardar el texto en la base de datos con estado 'PENDIENTE'
        logger.info("Guardando el texto en la base de datos con estado 'PENDIENTE'")
        cur.execute("INSERT INTO job (estado, texto) VALUES (%s, %s) RETURNING id", ("PENDIENTE", texto))
        job_id = cur.fetchone()[0]
        conn.commit()

        # Iniciar el análisis enviando el jobId al servicio Java
        logger.info("Enviando solicitud de análisis al servicio Java para el Job ID: %s", job_id)
        response = requests.post(f"{ANALYSIS_URL}/analyze/{job_id}")
        
        # Cambiar el estado a 'COMPLETADO' si el análisis fue exitoso
        if response.status_code == 200:
            logger.info("Análisis completado exitosamente para el Job ID: %s", job_id)
            cur.execute("UPDATE job SET estado = %s WHERE id = %s", ("COMPLETADO", job_id))
            conn.commit()
            return jsonify({"message": "Análisis completado", "resultado": response.json()}), 200
        else:
            logger.error("Error en servicio Java para el Job ID: %s. Detalle: %s", job_id, response.text)
            cur.execute("UPDATE job SET estado = %s WHERE id = %s", ("ERROR", job_id))
            conn.commit()
            return jsonify({"error": "Error en servicio Java", "detalle": response.text}), 500

    except Exception as e:
        logger.exception("Error inesperado en el proceso de análisis")
        return jsonify({"error": str(e)}), 500
    finally:
        if 'conn' in locals():
            logger.info("Cerrando la conexión con la base de datos")
            conn.close()

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
