import React from 'react';
import { createRoot } from 'react-dom/client';  // ← OJO: CLIENT
import App from './App';

const container = document.getElementById('root');
// crea el "root" y luego le decimos qué renderizar
const root = createRoot(container);
root.render(<App />);
