import axios from './axios'

export const getDashboardStats = () => {
    return axios.get('/stats/dashboard')
}

export const getUserStats = () => {
    return axios.get('/stats/users')
}

export const getBookStats = () => {
    return axios.get('/stats/books')
}

export const getRevenueStats = (period) => {
    return axios.get('/stats/revenue', { params: { period } })
}

export const getReviewStats = () => {
    return axios.get('/stats/review')
}
