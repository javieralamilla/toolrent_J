import httpClient from "../http-common";
const getAllCategories = () => {
  return httpClient.get("/api/v1/categories/")
}
export default { 
    getAllCategories
};