import request from '@/utils/request'

export const getClassPage = (pageNum, pageSize, keyword) => {
    return request.get('/class/page', {
        params: { pageNum, pageSize, keyword }
    })
}

export const getClassById = (id) => {
    return request.get(`/class/${id}`)
}

export const saveOrUpdateClass = (data) => {
    return request.post('/class', data)
}

export const deleteClass = (id) => {
    return request.delete(`/class/${id}`)
}