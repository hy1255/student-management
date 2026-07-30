import request from '@/utils/request'

export const getLogPage = (pageNum, pageSize, username, module, operationType, startTime, endTime) => {
    return request.get('/log/page', {
        params: { pageNum, pageSize, username, module, operationType, startTime, endTime }
    })
}