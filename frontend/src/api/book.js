import axios from './axios'

export const getBookList = (params) => {
  return axios.get('/book/list', { params })
}

export const getBookDetail = (id) => {
  return axios.get(`/book/detail/${id}`)
}

export const getRecommendBooks = (limit = 10) => {
  return axios.get('/book/recommend', { params: { limit } })
}

export const getFreeBooks = (limit = 20) => {
  return axios.get('/book/free', { params: { limit } })
}

export const addToBookshelf = (bookId) => {
  return axios.post('/book/shelf/add', { bookId })
}

export const removeFromBookshelf = (bookId) => {
  return axios.delete('/book/shelf/remove', { data: { bookId } })
}

export const getMyBookshelf = () => {
  return axios.get('/book/shelf')
}

export const getChapterList = (bookId) => {
  return axios.get(`/chapter/list/${bookId}`)
}

export const getChapterContent = (chapterId) => {
  return axios.get(`/chapter/content/${chapterId}`)
}