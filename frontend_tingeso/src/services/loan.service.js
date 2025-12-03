import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/loans/');
}

const getLoanById = (id) => {
    return httpClient.get(`/api/v1/loans/id/${id}`);
}

const findLoanByStatus = (status) => {
    return httpClient.get(`/api/v1/loans/status/${status}`);
}

const findLoanByCustomerRut = (customerRut) => {
    return httpClient.get(`/api/v1/loans/customerRut/${customerRut}`);
}

const findLoanByReturnDate = (returnDate) => {
    return httpClient.get(`/api/v1/loans/returnDate/${returnDate}`);
}

const findLoanByCustomerRutAndStatus = (customerRut, status) => {
    return httpClient.get(`/api/v1/loans/customerRut/status/${customerRut}/${status}`);
}

const createLoan = loanData => {
    return httpClient.post("/api/v1/loans/", loanData);
}

const returnLoan = (loanId, status) => {
    return httpClient.put(`/api/v1/loans/${loanId}/${status}`);
}

// ========== ENDPOINTS PARA REPORTES (ÉPICA 6) ==========

// RF6.1: Listar préstamos activos y su estado (vigentes, atrasados)
// Uso sin filtros: getActiveLoans()
// Uso con filtros: getActiveLoans('2024-01-01', '2024-12-31')
const getActiveLoans = (startDate = null, endDate = null) => {
    let url = '/api/v1/loans/reports/active-loans';
    const params = new URLSearchParams();
    
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    
    if (params.toString()) {
        url += `?${params.toString()}`;
    }
    
    return httpClient.get(url);
}



// RF6.3: Reporte de las herramientas más prestadas (Ranking)
// Uso sin filtros: getMostRentedTools()
// Uso con filtros: getMostRentedTools('2024-01-01', '2024-12-31')
// Retorna: Array de [toolName, categoryName, loanCount]
const getMostRentedTools = (startDate = null, endDate = null) => {
    let url = '/api/v1/loans/reports/most-rented-tools';
    const params = new URLSearchParams();
    
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    
    if (params.toString()) {
        url += `?${params.toString()}`;
    }
    
    return httpClient.get(url);
}

export default { 
    getAll, 
    getLoanById, 
    findLoanByStatus, 
    findLoanByCustomerRut, 
    findLoanByCustomerRutAndStatus, 
    findLoanByReturnDate, 
    createLoan, 
    returnLoan,
    // Reportes
    getActiveLoans,
    getMostRentedTools
};