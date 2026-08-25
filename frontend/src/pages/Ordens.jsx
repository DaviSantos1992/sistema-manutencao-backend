import { useEffect, useState } from 'react';
import { listarOrdens } from '../api/ordens';

export default function Ordens() {
    const [ordens, setOrdens] = useState([]);
    const [erro, setErro] = useState('');

    useEffect(() => {
        listarOrdens()
            .then((res) => setOrdens(res.data))
            .catch(() => setErro('Erro ao carregar ordens'));
    }, []);

    const formatarData = (dt) => dt ? new Date(dt).toLocaleString('pt-BR') : '-';

    return (
        <div style={{ padding: 20, maxWidth: 900, margin: '0 auto' }}>
            <h2>Ordens de Serviço</h2>
            {erro && <p style={{ color: 'red' }}>{erro}</p>}
            <table border="1" cellPadding="8" style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                <tr><th>ID</th><th>Cliente</th><th>Abertura</th><th>Status</th><th>Valor Total</th></tr>
                </thead>
                <tbody>
                {ordens.map((o) => (
                    <tr key={o.id}>
                        <td>{o.id}</td>
                        <td>{o.clienteNome}</td>
                        <td>{formatarData(o.dataAbertura)}</td>
                        <td>{o.status}</td>
                        <td>R$ {o.valorTotal}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}