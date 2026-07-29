import request from '@/utils/request'

export const getExamPage = (pageNum, pageSize, keyword, courseId) => {
    return request.get('/exam/page', {
        params: { pageNum, pageSize, keyword, courseId }
    })
}

export const getExamById = (id) => {
    return request.get(`/exam/${id}`)
}

export const saveOrUpdateExam = (data) => {
    return request.post('/exam', data)
}

export const deleteExam = (id) => {
    return request.delete(`/exam/${id}`)
}