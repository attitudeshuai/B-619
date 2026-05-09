import request from '../utils/request'

export const getCheckIns = (params) => request.get('/checkins', { params })

export const createCheckIn = (data) => request.post('/checkins', data)

export const checkOut = (id, data) => request.put(`/checkins/${id}/checkout`, data)

export const extendStay = (id, data) => request.put(`/checkins/${id}/extend`, data)
