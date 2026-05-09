import request from '../utils/request'

/**
 * 管理员登录
 */
export function login(data) {
    return request.post('/auth/login', data)
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
    return request.get('/auth/info')
}

/**
 * 健康检查
 */
export function healthCheck() {
    return request.get('/auth/health')
}
