import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/movements/');
}

const getToolMovementHistory = toolId => {
    return httpClient.get(`/api/v1/movements/toolId/${toolId}`);
}

const getMovementsByDateRange = (startDate, endDate) => {
    return httpClient.get(`/api/v1/movements/dateRange/${startDate}/${endDate}`);
}

const getToolMovementsByDateRange = (longId, startDate, endDate) => {
    return httpClient.get(`/api/v1/movements/tool/dateRange/${longId}/${startDate}/${endDate}`);
}

export default { getAll, getToolMovementHistory, getMovementsByDateRange, getToolMovementsByDateRange};

