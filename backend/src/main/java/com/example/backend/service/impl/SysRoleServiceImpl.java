package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.SysRole;
import com.example.backend.entity.SysRoleMenu;
import com.example.backend.mapper.SysRoleMapper;
import com.example.backend.mapper.SysRoleMenuMapper;
import com.example.backend.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public Page<SysRole> queryPage(Integer pageNum, Integer pageSize, String keyword) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysRole::getRoleName, keyword)
                    .or()
                    .like(SysRole::getRoleCode, keyword);
        }
        wrapper.orderByAsc(SysRole::getCreateTime);
        return sysRoleMapper.selectPage(page, wrapper);
    }

    @Override
    public SysRole getById(Long id) {
        return sysRoleMapper.selectById(id);
    }

    @Override
    public void saveOrUpdate(SysRole role) {
        if (role.getId() == null) {
            sysRoleMapper.insert(role);
        } else {
            sysRoleMapper.updateById(role);
        }
    }

    @Override
    public void deleteById(Long id) {
        // 检查是否内置角色
        SysRole role = sysRoleMapper.selectById(id);
        if (role != null && role.getIsBuiltin() == 1) {
            throw new RuntimeException("内置角色不可删除");
        }
        sysRoleMapper.deleteById(id);
        // 删除角色-菜单关联
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, id);
        sysRoleMenuMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 删除原有关联
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        sysRoleMenuMapper.delete(wrapper);

        // 插入新关联
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> list = sysRoleMenuMapper.selectList(wrapper);
        return list.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }
}