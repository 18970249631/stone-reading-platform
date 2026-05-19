import axios from './axios'

export const login = (username, password) => {
  return axios.post('/user/login', { username, password })
}

export const register = (data) => {
  return axios.post('/user/register', data)
}

export const getUserInfo = () => {
  return axios.get('/user/info')
}

export const updateUserInfo = (data) => {
  return axios.put('/user/info', data)
}