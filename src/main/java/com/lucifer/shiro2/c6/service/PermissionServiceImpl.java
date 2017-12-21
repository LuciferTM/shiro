package com.lucifer.shiro2.c6.service;

import com.lucifer.shiro2.c6.dao.PermissionDao;
import com.lucifer.shiro2.c6.dao.PermissionDaoImpl;
import com.lucifer.shiro2.c6.entity.Permission;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-28
 * <p>Version: 1.0
 */
public class PermissionServiceImpl implements PermissionService {

    private PermissionDao permissionDao = new PermissionDaoImpl();

    public Permission createPermission(Permission permission) {
        return permissionDao.createPermission(permission);
    }

    public void deletePermission(Long permissionId) {
        permissionDao.deletePermission(permissionId);
    }
}
