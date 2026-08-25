import { useEffect, useState } from 'react';
import { listarServicos, criarServico, atualizarServico, excluirServico } from '../api/servicos';

export default function Servicos() {
    const [servicos, setServicos] = useState([]);
    const [form, setForm] = useState({ id: null, nome: '', descricao: '', precoBase: '' });
    const [erro, setErro] = useState('');

    const carregar = async () => {
        try {
            const res = await listarServicos();
            setServicos(res.data);
        } catch (e) { setErro('Erro ao carregar serviços'); }
    };

    useEffect(() => { carregar(); }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const dados = { nome: form.nome, descricao: form.descricao, precoBase: Number(form.precoBase) };
            if (form.id) await atualizarServico(form.id, dados);
            else await criarServico(dados);
            setForm({ id: null, nome: '', descricao: '', precoBase: '' });
            carregar();
        } catch (e) { setErro('Erro ao salvar serviço'); }
    };

    const handleEditar = (s) => setForm({ id: s.id, nome: s.nome, descricao: s.descricao, precoBase: s.precoBase });
    const handleExcluir = async (id) => {
        if (!window.confirm('Excluir este serviço?')) return;
        try { await excluirServico(id); carregar(); }
        catch (e) { setErro('Erro ao excluir serviço'); }
    };

    return (
        <div style={{ padding: 20, maxWidth: 800, margin: '0 auto' }}>
            <h2>Serviços</h2>
            {erro && <p style={{ color: 'red' }}>{erro}</p>}

            <form onSubmit={handleSubmit} style={{ marginBottom: 20 }}>
                <input placeholder="Nome" value={form.nome}
                       onChange={(e) => setForm({ ...form, nome: e.target.value })} required />
                <input placeholder="Descrição" value={form.descricao}
                       onChange={(e) => setForm({ ...form, descricao: e.target.value })} />
                <input placeholder="Preço Base" type="number" step="0.01" value={form.precoBase}
                       onChange={(e) => setForm({ ...form, precoBase: e.target.value })} required />
                <button type="submit">{form.id ? 'Atualizar' : 'Cadastrar'}</button>
                {form.id && <button type="button" onClick={() => setForm({ id: null, nome: '', descricao: '', precoBase: '' })}>Cancelar</button>}
            </form>

            <table border="1" cellPadding="8" style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                <tr><th>ID</th><th>Nome</th><th>Descrição</th><th>Preço Base</th><th>Ações</th></tr>
                </thead>
                <tbody>
                {servicos.map((s) => (
                    <tr key={s.id}>
                        <td>{s.id}</td>
                        <td>{s.nome}</td>
                        <td>{s.descricao}</td>
                        <td>{s.precoBase}</td>
                        <td>
                            <button onClick={() => handleEditar(s)}>Editar</button>
                            <button onClick={() => handleExcluir(s.id)}>Excluir</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}