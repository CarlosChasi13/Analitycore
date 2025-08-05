export default function StatusPanel({ job }) {
  // Sólo renderiza si ya hemos recibido un job
  if (!job) return null;

  return (
    <div style={{ marginTop: 20 }}>
      <h2>Resultado del análisis</h2>
      <p><strong>Job ID:</strong> {job.id}</p>
      <p><strong>Estado:</strong> {job.estado}</p>

      {job.estado === 'COMPLETADO' && (
        <>
          <p><strong>Sentimiento:</strong> {job.sentimiento}</p>
          <p><strong>Palabras clave:</strong> {job.palabrasClave}</p>
          <pre style={{ background: '#f5f5f5', padding: 10, whiteSpace: 'pre-wrap' }}>
            <strong>Texto original:</strong>{'\n'}{job.texto}
          </pre>
        </>
      )}
    </div>
  );
}
