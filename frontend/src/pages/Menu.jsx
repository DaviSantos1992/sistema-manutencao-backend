import { useNavigate } from 'react-router-dom';

export default function Menu() {
    const navigate = useNavigate();

    const sair = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    const cards = [
        { titulo: 'Clientes', descricao: 'Cadastro e gestão de clientes', rota: '/clientes', icone: '👥' },
        { titulo: 'Serviços', descricao: 'Cadastro e gestão de serviços', rota: '/servicos', icone: '🛠️' },
        { titulo: 'Ordens de Serviço', descricao: 'Acompanhamento das OS', rota: '/ordens', icone: '📋' },
        { titulo: 'Relatório', descricao: 'Faturamento por período', rota: '/relatorio', icone: '📊' },
    ];

    return (
        <div style={{ padding: 20, maxWidth: 900, margin: '0 auto' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                <h2>Menu Principal</h2>
                <button onClick={sair} style={{ padding: '8px 16px', cursor: 'pointer' }}>Sair</button>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 16 }}>
                {cards.map((card) => (
                    <div
                        key={card.rota}
                        onClick={() => navigate(card.rota)}
                        style={{
                            border: '1px solid #ccc',
                            borderRadius: 8,
                            padding: 20,
                            cursor: 'pointer',
                            textAlign: 'center',
                            transition: 'box-shadow 0.2s',
                        }}
                        onMouseEnter={(e) => (e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)')}
                        onMouseLeave={(e) => (e.currentTarget.style.boxShadow = 'none')}
                    >
                        <div style={{ fontSize: 40 }}>{card.icone}</div>
                        <h3>{card.titulo}</h3>
                        <p style={{ color: '#666' }}>{card.descricao}</p>
                    </div>
                ))}
            </div>
        </div>
    );
}