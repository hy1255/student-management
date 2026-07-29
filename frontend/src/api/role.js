import request from '@/utils/request'

// 分页查询角色
export const getRolePage = (pageNum, pageSize, keyword) => {
    return request.get('/role/page', {
        params: { pageNum, pageSize, keyword }
    })
}

// 获取角色详情
export const getRoleById = (id) => {
    return request.get(`/role/${id}`)
}

// 新增或修改角色
export const saveOrUpdateRole = (data) => {
    return request.post('/role', data)
}

// 删除角色
export const deleteRole = (id) => {
    return request.delete(`/role/${id}`)
}

// 分配菜单权限
export const assignMenus = (roleId, menuIds) => {
    return request.post(`/role/${roleId}/menus`, menuIds)
}

// 获取角色已分配的菜单ID
export const getRoleMenus = (roleId) => {
    return request.get(`/role/${roleId}/menus`)
}