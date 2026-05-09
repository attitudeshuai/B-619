import request from '../utils/request'

export const getMembers = (params) => request.get('/members', { params })

export const createMember = (data) => request.post('/members', data)

export const updateMember = (id, data) => request.put(`/members/${id}`, data)

export const deleteMember = (id) => request.delete(`/members/${id}`)

export const rechargeMember = (id, amount) => request.post(`/members/${id}/recharge`, { amount })

export const adjustPoints = (id, points) => request.post(`/members/${id}/points`, { points })
