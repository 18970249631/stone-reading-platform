import axios from './axios'

export const getProducts = (params) => {
    return axios.get('/market/products', { params })
}

export const getProductDetail = (id) => {
    return axios.get(`/market/products/${id}`)
}

export const createProduct = (data) => {
    return axios.post('/market/products', data)
}

export const buyProduct = (id) => {
    return axios.post(`/market/products/${id}/buy`)
}

export const getOrders = (params) => {
    return axios.get('/market/orders', { params })
}
