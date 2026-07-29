import request from '@/utils/request'

// 分页查询学生
export const getStudentPage = (pageNum, pageSize, name) => {
    return request.get('/student/page', {
        params: { pageNum, pageSize, name }
    })
}

// 查询学生详情
export const getStudentById = (id) => {
    return request.get(`/student/${id}`)
}

// 新增或修改学生
export const saveOrUpdateStudent = (data) => {
    return request.post('/student', data)
}

// 删除学生
export const deleteStudent = (id) => {
    return request.delete(`/student/${id}`)
}