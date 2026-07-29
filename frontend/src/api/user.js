import request from '@/utils/request'

// 分页查询用户
export const getUserPage = (pageNum, pageSize, keyword) => {
    return request.get('/user/page', {
        params: { pageNum, pageSize, keyword }
    })
}

// 获取用户详情
export const getUserById = (id) => {
    return request.get(`/user/${id}`)
}

// 新增或修改用户
export const saveOrUpdateUser = (data) => {
    return request.post('/user', data)
}

// 删除用户
export const deleteUser = (id) => {
    return request.delete(`/user/${id}`)
}

// 重置密码
export const resetPassword = (id) => {
    return request.put(`/user/${id}/reset-password`)
}