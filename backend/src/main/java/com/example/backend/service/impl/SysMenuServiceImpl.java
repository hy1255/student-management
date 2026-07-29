package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.backend.entity.SysMenu;
import com.example.backend.mapper.SysMenuMapper;
import com.example.backend.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> getMenuTree() {
        // 查询所有菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSortOrder);
        List<SysMenu> allMenus = sysMenuMapper.selectList(wrapper);

        // 构建树形结构
        return buildTree(allMenus, 0L);
    }

    private List<SysMenu> buildTree(List<SysMenu> allMenus, Long parentId) {
        List<SysMenu> result = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (menu.getParentId().equals(parentId)) {
                // 递归查找子菜单
                menu.setChildren(buildTree(allMenus, menu.getId()));
                result.add(menu);
            }
        }
        return result;
    }

    @Override
    public SysMenu getById(Long id) {
        return sysMenuMapper.selectById(id);
    }

    @Override
    public void saveOrUpdate(SysMenu menu) {
        if (menu.getId() == null) {
            sysMenuMapper.insert(menu);
        } else {
            sysMenuMapper.updateById(menu);
        }
    }

    @Override
    public void deleteById(Long id) {
        sysMenuMapper.deleteById(id);
    }

    @Override
    public boolean hasChildren(Long id) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        return sysMenuMapper.selectCount(wrapper) > 0;
    }
}