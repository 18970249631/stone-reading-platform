import axios from './axios'

export const getAuthorProfile = () => {
    return axios.get('/author/profile')
}

export const applyAuthor = (data) => {
    return axios.post('/author/apply', data)
}

export const getAuthorDashboard = () => {
    return axios.get('/author/dashboard')
}

export const verifyAuthor = (profileId, data) => {
    return axios.post(`/author/verify/${profileId}`, data)
}
