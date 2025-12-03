import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/fines/');
}

const getByStatus = status => {
    return httpClient.get(`/api/v1/fines/status/${status}`);
}

const getFineByCustomerRut = rut => {
    return httpClient.get(`/api/v1/fines/customerRut/${rut}`);
}

const getFinesByType = type => {
    return httpClient.get(`/api/v1/fines/type/${type}`);
}

const getFineByCustomerRutAndStatus = (rut, status) => {
    return httpClient.get(`/api/v1/fines/customerRut/status/${rut}/${status}`);
}

const findFineByCustomerRutAndType = (rut, type) => {
    return httpClient.get(`/api/v1/fines/customerRut/type/${rut}/${type}`);
}

const findFineByCustomerRutAndStatusAndType = (rut, status, type) => {
    return httpClient.get(`/api/v1/fines/customerRut/status/type/${rut}/${status}/${type}`);
}

const createForIrreparableDamage = (customer, loanId) => {
    return httpClient.post(`/api/v1/fines/${loanId}`, customer);
}

const createFineForMinorDamage = (customer, loanId, fineValue) => {
    return httpClient.post(`/api/v1/fines/minorDamage/${loanId}/${fineValue}`, customer);
}

const payFine = fine => {
    return httpClient.put('/api/v1/fines/', fine);
}

// RF6.2: Listar clientes con atrasos
// Uso sin filtros: getCustomersWithOverdue()
// Uso con filtros: getCustomersWithOverdue('2024-01-01', '2024-12-31')
const getCustomersWithOverdue = (startDate = null, endDate = null) => {
    let url = '/api/v1/fines/reports/customers-with-overdue';
    const params = new URLSearchParams();
    
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    
    if (params.toString()) {
        url += `?${params.toString()}`;
    }
    
    return httpClient.get(url);
}
export default { getAll, getFineByCustomerRut, getFinesByType, getFineByCustomerRutAndStatus, findFineByCustomerRutAndType, findFineByCustomerRutAndStatusAndType, createForIrreparableDamage, createFineForMinorDamage, getByStatus, payFine, getCustomersWithOverdue};