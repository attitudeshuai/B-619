import request from '../utils/request'

export const getAdmins = (params) => request.get('/admins', { params })

export const createAdmin = (data) => request.post('/admins', data)

export const updateAdmin = (id, data) => request.put(`/admins/${id}`, data)

export const deleteAdmin = (id) => request.delete(`/admins/${id}`)
