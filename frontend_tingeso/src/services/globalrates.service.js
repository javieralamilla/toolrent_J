import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/globalRates/');
}

const getGlobalRatesById = (id) => {
    return httpClient.get(`/api/v1/globalRates/id/${id}`);
}

const create = data => {
    return httpClient.post("/api/v1/globalRates/", data);
}

const updateValueRate = (id, dailyRateValue) => {
    return httpClient.put(`/api/v1/globalRates/${id}/${dailyRateValue}`);
}

export default { getAll, create, updateValueRate, getGlobalRatesById };