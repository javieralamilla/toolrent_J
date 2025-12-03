import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/customers/');
}

const getByRut = rut => {
    return httpClient.get(`/api/v1/customers/rut/${rut}`);
}

const getById = id => {
    return httpClient.get(`/api/v1/customers/id/${id}`);
}

const getByStatus = status => {
    return httpClient.get(`/api/v1/customers/status/${status}`);
}

const create = data => {
    return httpClient.post("/api/v1/customers/", data);
}

const update = data => {
    return httpClient.put('/api/v1/customers/', data);
}

const remove = id => {
    return httpClient.delete(`/api/v1/customers/${id}`);
}
export default { getAll, create, getByRut, getById, getByStatus, update, remove };

