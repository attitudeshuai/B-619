import request from '../utils/request'

export const getBookings = (params) => request.get('/bookings', { params })

export const getAvailableRooms = () => request.get('/bookings/available-rooms')

export const createBooking = (data) => request.post('/bookings', data)

export const cancelBooking = (id) => request.put(`/bookings/${id}/cancel`)

export const confirmBooking = (id) => request.put(`/bookings/${id}/confirm`)
