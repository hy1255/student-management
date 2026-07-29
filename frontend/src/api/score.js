import request from '@/utils/request'

// 获取某考试的所有成绩
export const getScoresByExam = (examId) => {
    return request.get(`/score/exam/${examId}`)
}

// 批量保存成绩
export const batchSaveScores = (data) => {
    return request.post('/score/batch', data)
}

// 分页查询成绩
export const getScorePage = (pageNum, pageSize, studentId, courseId, examId) => {
    return request.get('/score/page', {
        params: { pageNum, pageSize, studentId, courseId, examId }
    })
}

// 获取考试统计
export const getExamStats = (examId) => {
    return request.get(`/score/stats/${examId}`)
}

// 删除成绩
export const deleteScore = (id) => {
    return request.delete(`/score/${id}`)
}