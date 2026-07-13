'use client'

import { useState } from 'react';

const API_URL = 'https://t-minus-r53c.onrender.com/api/compilar';

export default function Compilador() {
  const [codigo, setCodigo] = useState('');
  const [resultado, setResultado] = useState('');
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState(false);

  async function traduzir() {
    if (!codigo.trim()) return;

    setCarregando(true);
    setErro(false);
    setResultado('');

    try {
      const res = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        body: codigo,
      });

      const texto = await res.text();

      if (!res.ok) {
        setErro(true);
      }

      setResultado(texto);
    } catch (e) {
      setErro(true);
      setResultado('Erro ao conectar com o compilador. O servidor está rodando?');
    }

    setCarregando(false);
  }

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: '2rem', fontFamily: 'monospace' }}>
      <h1>Compilador T-Minus → Pascal</h1>
      <p>Digite seu código na linguagem T-Minus e clique em traduzir.</p>

      <textarea
        value={codigo}
        onChange={(e) => setCodigo(e.target.value)}
        rows={12}
        placeholder={`acesso_livre nave TESTE <<\n\tacesso_livre iniciar_missao <Unidade final> <<\n\t\ttransmitir<>\n\t>>\n>>`}
        style={{
          width: '100%',
          fontFamily: 'monospace',
          fontSize: '14px',
          padding: '1rem',
          marginBottom: '1rem',
          boxSizing: 'border-box',
        }}
      />

      <button
        onClick={traduzir}
        disabled={carregando}
        style={{
          padding: '0.75rem 1.5rem',
          fontSize: '16px',
          cursor: carregando ? 'not-allowed' : 'pointer',
        }}
      >
        {carregando ? 'Compilando...' : 'Traduzir para Pascal'}
      </button>

      {resultado && (
        <div style={{ marginTop: '2rem' }}>
          <h3>{erro ? 'Erro:' : 'Pascal gerado:'}</h3>
          <pre
            style={{
              background: erro ? '#ffe6e6' : '#f4f4f4',
              padding: '1rem',
              borderRadius: '4px',
              whiteSpace: 'pre-wrap',
              overflowX: 'auto',
            }}
          >
            {resultado}
          </pre>
        </div>
      )}
    </div>
  );
}