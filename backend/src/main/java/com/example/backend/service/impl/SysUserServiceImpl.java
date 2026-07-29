package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.SysUser;
import com.example.backend.mapper.SysUserMapper;
import com.example.backend.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Page<SysUser> queryPage(Integer pageNum, Integer pageSize, String keyword) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getRealName, keyword);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return sysUserMapper.selectPage(page, wrapper);
    }

    @Override
    public SysUser getById(Long id) {
        return sysUserMapper.selectById(id);
    }

    @Override
    public void saveOrUpdate(SysUser user) {
        if (user.getId() == null) {
            // 新增：加密密码
            if (StringUtils.hasText(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // 默认密码 123456
                user.setPassword(passwordEncoder.encode("123456"));
            }
            sysUserMapper.insert(user);
        } else {
            // 修改：如果密码不为空，则加密更新
            if (StringUtils.hasText(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // 不修改密码，从数据库查询原有密码
                SysUser existing = sysUserMapper.selectById(user.getId());
                user.setPassword(existing.getPassword());
            }
            sysUserMapper.updateById(user);
        }
    }

    @Override
    public void deleteById(Long id) {
        // 防止删除管理员
        SysUser user = sysUserMapper.selectById(id);
        if ("ADMIN".equals(user.getRole())) {
            throw new RuntimeException("不能删除管理员账号");
        }
        sysUserMapper.deleteById(id);
    }

    @Override
    public void resetPassword(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        sysUserMapper.updateById(user);
    }
}