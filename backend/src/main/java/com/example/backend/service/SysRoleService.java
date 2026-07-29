package com.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.SysRole;

import java.util.List;

public interface SysRoleService {

    Page<SysRole> queryPage(Integer pageNum, Integer pageSize, String keyword);

    SysRole getById(Long id);

    void saveOrUpdate(SysRole role);

    void deleteById(Long id);

    // 分配菜单权限
    void assignMenus(Long roleId, List<Long> menuIds);

    // 获取角色已分配的菜单ID列表
    List<Long> getMenuIdsByRoleId(Long roleId);
}