import { useEffect, useState } from 'react';
import { listarClientes, criarCliente, atualizarCliente, excluirCliente } from '../api/clientes';

export default function Clientes() {
    const [clientes, setClientes] = useState([]);
    const [form, setForm] = useState({ id: null, nome: '', email: '', telefone: '' });
    const [erro, setErro] = useState('');

    const carregar = async () => {
        try {
            const res = await listarClientes();
            setClientes(res.data);
        } catch (e) {
            setErro('Erro ao carregar clientes');
        }
    };

    useEffect(() => { carregar(); }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (form.id) {
                await atualizarCliente(form.id, { nome: form.nome, email: form.email, telefone: form.telefone });
            } else {
                await criarCliente({ nome: form.nome, email: form.email, telefone: form.telefone });
            }
            setForm({ id: null, nome: '', email: '', telefone: '' });
            carregar();
        } catch (e) {
            setErro('Erro ao salvar cliente');
        }
    };

    const handleEditar = (c) => setForm({ id: c.id, nome: c.nome, email: c.email, telefone: c.telefone });

    const handleExcluir = async (id) => {
        if (!window.confirm('Excluir este cliente?')) return;
        try {
            await excluirCliente(id);
            carregar();
        } catch (e) {
            setErro('Erro ao excluir cliente');
        }
    };

    return (
        <div style={{ padding: 20, maxWidth: 800, margin: '0 auto' }}>
            <h2>Clientes</h2>
            {erro && <p style={{ color: 'red' }}>{erro}</p>}

            <form onSubmit={handleSubmit} style={{ marginBottom: 20 }}>
                <input placeholder="Nome" value={form.nome}
                       onChange={(e) => setForm({ ...form, nome: e.target.value })} required />
                <input placeholder="Email" type="email" value={form.email}
                       onChange={(e) => setForm({ ...form, email: e.target.value })} required />
                <input placeholder="Telefone" value={form.telefone}
                       onChange={(e) => setForm({ ...form, telefone: e.target.value })} />
                <button type="submit">{form.id ? 'Atualizar' : 'Cadastrar'}</button>
                {form.id && <button type="button" onClick={() => setForm({ id: null, nome: '', email: '', telefone: '' })}>Cancelar</button>}
            </form>

            <table border="1" cellPadding="8" style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                <tr><th>ID</th><th>Nome</th><th>Email</th><th>Telefone</th><th>Ações</th></tr>
                </thead>
                <tbody>
                {clientes.map((c) => (
                    <tr key={c.id}>
                        <td>{c.id}</td>
                        <td>{c.nome}</td>
                        <td>{c.email}</td>
                        <td>{c.telefone}</td>
                        <td>
                            <button onClick={() => handleEditar(c)}>Editar</button>
                            <button onClick={() => handleExcluir(c.id)}>Excluir</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}