import { useState } from 'react';
import { gerarRelatorio } from '../api/relatorios';

export default function Relatorio() {
    const [dataInicio, setDataInicio] = useState('');
    const [dataFim, setDataFim] = useState('');
    const [relatorio, setRelatorio] = useState(null);
    const [erro, setErro] = useState('');

    const handleGerar = async (e) => {
        e.preventDefault();
        if (!dataInicio || !dataFim) {
            setErro('Selecione as datas');
            return;
        }
        try {
            const res = await gerarRelatorio(dataInicio, dataFim);
            setRelatorio(res.data);
            setErro('');
        } catch (e) { setErro('Erro ao gerar relatório'); }
    };

    return (
        <div style={{ padding: 20, maxWidth: 700, margin: '0 auto' }}>
            <h2>Relatório de Faturamento</h2>
            {erro && <p style={{ color: 'red' }}>{erro}</p>}

            <form onSubmit={handleGerar} style={{ marginBottom: 20 }}>
                <label>Data Início: </label>
                <input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} required />
                <label>Data Fim: </label>
                <input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} required />
                <button type="submit">Gerar Relatório</button>
            </form>

            {relatorio && (
                <div>
                    <p>Total de Ordens: <strong>{relatorio.totalOrdens}</strong></p>
                    <p>Ordens Concluídas: <strong>{relatorio.totalConcluidas}</strong></p>
                    <p>Faturamento Total: <strong>R$ {relatorio.faturamentoTotal}</strong></p>
                </div>
            )}
        </div>
    );
}