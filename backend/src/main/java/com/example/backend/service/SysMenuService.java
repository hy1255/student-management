package com.example.backend.service;

import com.example.backend.entity.SysMenu;

import java.util.List;

public interface SysMenuService {

    // 获取菜单树
    List<SysMenu> getMenuTree();

    // 根据ID查询
    SysMenu getById(Long id);

    // 新增或修改
    void saveOrUpdate(SysMenu menu);

    // 删除
    void deleteById(Long id);

    // 检查是否有子菜单
    boolean hasChildren(Long id);
}
