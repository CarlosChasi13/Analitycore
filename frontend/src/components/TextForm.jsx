import { useState } from 'react';
import client from '../api/client';

export default function TextForm({ onResult }) {
  const [text, setText] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async e => {
    e.preventDefault();
    if (!text.trim()) return;

    setLoading(true);
    try {
      // Enviamos a Python: éste llama a Java y devuelve el objeto completo
      const { data } = await client.post('/api/submit', { texto: text });
      // data.resultado === { id, texto, estado, sentimiento, palabrasClave }
      onResult(data.resultado);
    } catch (err) {
      console.error(err);
      alert('Error procesando el análisis');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: 20 }}>
      <textarea
        id="text-input"
        name="text"
        rows={6}
        value={text}
        onChange={e => setText(e.target.value)}
        placeholder="Escribe el texto a analizar…"
        style={{ width: '100%', fontSize: 16 }}
      />
      <button type="submit" disabled={loading || !text.trim()}>
        {loading ? 'Procesando…' : 'Enviar al análisis'}
      </button>
    </form>
  );
}
