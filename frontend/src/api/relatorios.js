import api from './axios';

export const gerarRelatorio = (dataInicio, dataFim) =>
    api.get('/relatorios/faturamento', { params: { dataInicio, dataFim } });