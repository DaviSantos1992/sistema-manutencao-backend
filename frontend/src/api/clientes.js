import api from './axios';

export const listarClientes = () => api.get('/clientes');
export const buscarCliente = (id) => api.get(`/clientes/${id}`);
export const criarCliente = (dados) => api.post('/clientes', dados);
export const atualizarCliente = (id, dados) => api.put(`/clientes/${id}`, dados);
export const excluirCliente = (id) => api.delete(`/clientes/${id}`);