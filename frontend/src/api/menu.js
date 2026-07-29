import request from '@/utils/request'

// 获取菜单树
export const getMenuTree = () => {
    return request.get('/menu/tree')
}

// 获取菜单详情
export const getMenuById = (id) => {
    return request.get(`/menu/${id}`)
}

// 新增或修改
export const saveOrUpdateMenu = (data) => {
    return request.post('/menu', data)
}

// 删除菜单
export const deleteMenu = (id) => {
    return request.delete(`/menu/${id}`)
}