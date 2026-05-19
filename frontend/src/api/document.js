import axios from './axios'

export const uploadDocument = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return axios.post('/doc/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const parseDocument = (documentId) => {
  return axios.post(`/doc/parse/${documentId}`)
}

export const getDocumentDetail = (documentId) => {
  return axios.get(`/doc/detail/${documentId}`)
}

export const getDocumentContent = (documentId) => {
  return axios.get(`/doc/content/${documentId}`)
}
