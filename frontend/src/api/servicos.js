import api from './axios';

export const listarServicos = () => api.get('/servicos');
export const buscarServico = (id) => api.get(`/servicos/${id}`);
export const criarServico = (dados) => api.post('/servicos', dados);
export const atualizarServico = (id, dados) => api.put(`/servicos/${id}`, dados);
export const excluirServico = (id) => api.delete(`/servicos/${id}`);