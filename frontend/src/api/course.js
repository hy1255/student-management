import request from '@/utils/request'

export const getCoursePage = (pageNum, pageSize, keyword) => {
    return request.get('/course/page', {
        params: { pageNum, pageSize, keyword }
    })
}

export const getCourseById = (id) => {
    return request.get(`/course/${id}`)
}

export const saveOrUpdateCourse = (data) => {
    return request.post('/course', data)
}

export const deleteCourse = (id) => {
    return request.delete(`/course/${id}`)
}