import request from '../utils/request'

export const getSettings = () => request.get('/settings')

export const updateSetting = (key, data) => request.put(`/settings/${key}`, data)
