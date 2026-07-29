package com.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.entity.SysUser;

public interface SysUserService {

    Page<SysUser> queryPage(Integer pageNum, Integer pageSize, String keyword);

    SysUser getById(Long id);

    void saveOrUpdate(SysUser user);

    void deleteById(Long id);

    void resetPassword(Long id);
}