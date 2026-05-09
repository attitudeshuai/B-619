import request from '../utils/request'

export const getRooms = (params) => request.get('/rooms', { params })

export const getRoomStatistics = () => request.get('/rooms/statistics')

export const createRoom = (data) => request.post('/rooms', data)

export const updateRoom = (id, data) => request.put(`/rooms/${id}`, data)

export const deleteRoom = (id) => request.delete(`/rooms/${id}`)

export const changeRoomStatus = (id, status) => request.put(`/rooms/${id}/status`, { status })
