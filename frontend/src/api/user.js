import request from '@/utils/request'

export const getUserPage = (pageNum, pageSize, keyword) => {
    return request.get('/user/page', {
        params: { pageNum, pageSize, keyword }
    })
}

export const getUserById = (id) => {
    return request.get(`/user/${id}`)
}

export const saveOrUpdateUser = (data) => {
    return request.post('/user', data)
}

export const deleteUser = (id) => {
    return request.delete(`/user/${id}`)
}

export const resetPassword = (id) => {
    return request.put(`/user/${id}/reset-password`)
}