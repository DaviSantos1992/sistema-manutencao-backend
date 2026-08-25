import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [erro, setErro] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await api.post('/auth/login', { username, password });
            localStorage.setItem('token', res.data.token);
            navigate('/menu');
        } catch (err) {
            setErro('Usuário ou senha inválidos');
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Login</h2>
            <input value={username} onChange={(e) => setUsername(e.target.value)}
                   placeholder="Usuário" />
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
                   placeholder="Senha" />
            <button type="submit">Entrar</button>
            {erro && <p style={{ color: 'red' }}>{erro}</p>}
        </form>
    );
}