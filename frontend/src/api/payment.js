import axios from './axios'

export const createOrder = (memberType) => {
  return axios.post('/payment/create-order', { memberType })
}

export const payOrder = (orderNo, payType) => {
  return axios.post('/payment/pay', { orderNo, payType })
}

export const refundOrder = (orderNo) => {
  return axios.post('/payment/refund', { orderNo })
}
