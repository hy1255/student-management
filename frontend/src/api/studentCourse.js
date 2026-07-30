import request from '@/utils/request'

// 获取某课程已选学生
export const getCourseStudents = (courseId) => {
    return request.get(`/student-course/course/${courseId}`)
}

// 获取未选某课程的学生（可添加）
export const getAvailableStudents = (courseId) => {
    return request.get(`/student-course/course/${courseId}/available`)
}

// 批量添加学生到课程
export const addStudentsToCourse = (courseId, studentIds) => {
    return request.post('/student-course/batch', { courseId, studentIds })
}

// 从课程移除学生
export const removeStudentFromCourse = (courseId, studentId) => {
    return request.delete(`/student-course/course/${courseId}/student/${studentId}`)
}