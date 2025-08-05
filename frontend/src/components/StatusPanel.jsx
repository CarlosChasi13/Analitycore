// src/components/StatusPanel.jsx
export default function StatusPanel({ job }) {
  if (!job) return null;

  return (
    <div className="status-panel">
      <h2>Resultado del análisis</h2>
      <p><strong>Job ID:</strong> {job.id}</p>
      <p><strong>Estado:</strong> {job.estado}</p>

      {job.estado === 'COMPLETADO' && (
        <>
          <p><strong>Sentimiento:</strong> {job.sentimiento}</p>
          <p><strong>Palabras clave:</strong> {job.palabrasClave}</p>
          <pre>
            Texto original:{'\n'}{job.texto}
          </pre>
        </>
      )}
    </div>
  );
}
