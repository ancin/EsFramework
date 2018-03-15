package com.sishuok.es.sys.permission.task;

import java.util.Collection;
import java.util.Iterator;

import org.es.framework.common.repository.RepositoryHelper;
import org.es.framework.common.utils.LogUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sishuok.es.sys.permission.entity.Role;
import com.sishuok.es.sys.permission.entity.RoleResourcePermission;
import com.sishuok.es.sys.permission.service.PermissionService;
import com.sishuok.es.sys.permission.service.RoleService;
import com.sishuok.es.sys.resource.service.ResourceService;

/**
 * �����޹�����Role-Resource/Permission��ϵ
 * 1��Role-Resource
 * 2��Role-Permission
 * <p/>
 */
@Service()
public class RoleClearRelationTask {

    @Autowired
    private RoleService       roleService;

    @Autowired
    private ResourceService   resourceService;

    @Autowired
    private PermissionService permissionService;

    /**
     * ���ɾ���Ľ�ɫ��Ӧ�Ĺ�ϵ
     */
    public void clearDeletedRoleRelation() {

        final int PAGE_SIZE = 100;
        int pn = 0;

        Page<Role> rolePage = null;
        do {
            Pageable pageable = new PageRequest(pn++, PAGE_SIZE);
            rolePage = roleService.findAll(pageable);
            //�������������
            try {
                RoleClearRelationTask roleClearRelationTask = (RoleClearRelationTask) AopContext
                    .currentProxy();
                roleClearRelationTask.doClear(rolePage.getContent());
            } catch (Exception e) {
                //���쳣Ҳ����ν
                LogUtils.logError("clear role relation error", e);

            }
            //��ջỰ
            RepositoryHelper.clear();
        } while (rolePage.hasNextPage());
    }

    public void doClear(Collection<Role> roleColl) {

        for (Role role : roleColl) {

            boolean needUpdate = false;
            Iterator<RoleResourcePermission> iter = role.getResourcePermissions().iterator();

            while (iter.hasNext()) {
                RoleResourcePermission roleResourcePermission = iter.next();

                //�����Դ�������� ��ɾ��
                Long resourceId = roleResourcePermission.getResourceId();
                if (!resourceService.exists(resourceId)) {
                    iter.remove();
                    needUpdate = true;
                }

                Iterator<Long> permissionIdIter = roleResourcePermission.getPermissionIds()
                    .iterator();
                while (permissionIdIter.hasNext()) {
                    Long permissionId = permissionIdIter.next();

                    if (!permissionService.exists(permissionId)) {
                        permissionIdIter.remove();
                        needUpdate = true;
                    }
                }

            }

            if (needUpdate) {
                roleService.update(role);
            }

        }

    }

}
