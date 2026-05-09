import request from '../utils/request'

export const getRevenueReport = (params) => request.get('/reports/revenue', { params })

export const getOccupancyReport = () => request.get('/reports/occupancy')

export const getMemberReport = (params) => request.get('/reports/members', { params })

export const exportReport = (params) => request.get('/reports/export', { params, responseType: 'blob' })
