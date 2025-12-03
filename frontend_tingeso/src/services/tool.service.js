import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/tools/');
}

const getById = (id) => {
    return httpClient.get(`/api/v1/tools/id/${id}`);
}

const getByName = name => {
    return httpClient.get(`/api/v1/tools/name/${name}`);
}

const getToolByNameAndCategory = (name, category) => {
    return httpClient.get(`/api/v1/tools/name/category/${name}/${category}`);
}

const getByStatus = status => {
    return httpClient.get(`/api/v1/tools/status/${status}`);
}

const getByCategory = category => {
    return httpClient.get(`/api/v1/tools/category/${category}`);
}

const getInventory = () => {
    return httpClient.get('/api/v1/tools/inventory');
}

const getInventoryByCategory = (category) => {
    return httpClient.get(`/api/v1/tools/inventory/category/${category}`);
}

const findInventoryByName = (name) => {
    return httpClient.get(`/api/v1/tools/inventory/name/${name}`);
}

const findInventoryById = (id) => {
    return httpClient.get(`/api/v1/tools/inventory/id/${id}`);
}

const findInventoryByNameAndCategory = (name, category) => {
    return httpClient.get(`/api/v1/tools/inventory/name/category/${name}/${category}`);
}

const updateReplacementValue = (id, value) => {
    return httpClient.put(`/api/v1/tools/replacementValue/${id}/${value}`);
}

const updateDailyRentalRate = (id, value) => {
    return httpClient.put(`/api/v1/tools/dailyRentalRate/${id}/${value}`);
}

const saveTool = (toolData, quantity, replacementValue, dailyRentalRate) => {
    return httpClient.post(`/api/v1/tools/${quantity}/${replacementValue}/${dailyRentalRate}`, toolData);
}

const saveRegisteredTool = (toolData, quantity) => {
    return httpClient.post(`/api/v1/tools/existing/${quantity}`, toolData);
}

const repairedTool = (toolId) => {
    return httpClient.put(`/api/v1/tools/repairedTool/${toolId}`);
}

const getToolsInRepairWithMinorDamage = () => {
    return httpClient.get('/api/v1/tools/inRepairWithMinorDamage');
}

export default { 
    getAll, 
    getById, 
    getByName,
    getToolByNameAndCategory,
    getByStatus, 
    getByCategory, 
    getInventory, 
    getInventoryByCategory, 
    findInventoryByName, 
    findInventoryById, 
    findInventoryByNameAndCategory,
    updateReplacementValue, 
    updateDailyRentalRate,
    saveTool, 
    saveRegisteredTool, 
    repairedTool,
    getToolsInRepairWithMinorDamage
};


