import api from './axios';

export const listarOrdens = () => api.get('/ordens');
export const buscarOrdem = (id) => api.get(`/ordens/${id}`);
export const criarOrdem = (dados) => api.post('/ordens', dados);
export const atualizarOrdem = (id, dados) => api.put(`/ordens/${id}`, dados);
export const excluirOrdem = (id) => api.delete(`/ordens/${id}`);