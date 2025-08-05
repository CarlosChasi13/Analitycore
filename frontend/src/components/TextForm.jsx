// src/components/TextForm.jsx
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
      const { data } = await client.post('/api/submit', { texto: text });
      onResult(data.resultado);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <textarea
        id="text-input"
        name="text"
        rows={6}
        value={text}
        onChange={e => setText(e.target.value)}
        placeholder="Escribe el texto a analizar…"
      />
      <button type="submit" disabled={loading || !text.trim()}>
        {loading ? 'Procesando…' : 'Enviar al análisis'}
      </button>
    </form>
  );
}
