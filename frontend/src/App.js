import { useState } from 'react';
import TextForm from './components/TextForm';
import StatusPanel from './components/StatusPanel';

export default function App() {
  const [job, setJob] = useState(null);

  return (
    <div style={{ padding: 20, maxWidth: 700, margin: '0 auto' }}>
      <h1>AnalytiCore</h1>
      <TextForm onResult={setJob} />
      <StatusPanel job={job} />
    </div>
  );
}
