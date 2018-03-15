package com.sishuok.es.sys.auth.service;

import java.util.Set;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.sishuok.es.sys.group.service.GroupService;
import com.sishuok.es.sys.organization.service.JobService;
import com.sishuok.es.sys.organization.service.OrganizationService;
import com.sishuok.es.sys.permission.entity.Permission;
import com.sishuok.es.sys.permission.entity.Role;
import com.sishuok.es.sys.permission.entity.RoleResourcePermission;
import com.sishuok.es.sys.permission.service.PermissionService;
import com.sishuok.es.sys.permission.service.RoleService;
import com.sishuok.es.sys.resource.entity.Resource;
import com.sishuok.es.sys.resource.service.ResourceService;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.entity.UserOrganizationJob;

@Service
public class UserAuthService {

    @Autowired
    private GroupService        groupService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private JobService          jobService;

    @Autowired
    private AuthService         authService;

    @Autowired
    private RoleService         roleService;

    @Autowired
    private ResourceService     resourceService;

    @Autowired
    private PermissionService   permissionService;

    public Set<Role> findRoles(User user) {

        if (user == null) {
            return Sets.newHashSet();
        }

        Long userId = user.getId();

        Set<Long[]> organizationJobIds = Sets.newHashSet();
        Set<Long> organizationIds = Sets.newHashSet();
        Set<Long> jobIds = Sets.newHashSet();

        for (UserOrganizationJob o : user.getOrganizationJobs()) {
            Long organizationId = o.getOrganizationId();
            Long jobId = o.getJobId();

            if (organizationId != null && jobId != null && organizationId != 0L && jobId != 0L) {
                organizationJobIds.add(new Long[] { organizationId, jobId });
            }
            organizationIds.add(organizationId);
            jobIds.add(jobId);
        }

        //TODO ĿǰĬ���ӻ�̳и� ����ʵ�����flag�����Ƿ�̳�

        //����֯��������
        organizationIds.addAll(organizationService.findAncestorIds(organizationIds));
        //�ҹ���ְ�������
        jobIds.addAll(jobService.findAncestorIds(jobIds));

        //������֯���� ����ȡĿǰ���õ���֯��������
        organizationService.filterForCanShow(organizationIds, organizationJobIds);
        jobService.filterForCanShow(jobIds, organizationJobIds);

        //���˹���ְ�� ����ȡĿǰ���õĹ���ְ������

        //Ĭ�Ϸ��� + �����û���� �� ��֯��� �� ����
        Set<Long> groupIds = groupService.findShowGroupIds(userId, organizationIds);

        //��ȡȨ��
        //1.1����ȡ�û���ɫ
        //1.2����ȡ��֯������ɫ
        //1.3����ȡ����ְ���ɫ
        //1.4����ȡ��֯�����͹���ְ����ϵĽ�ɫ
        //1.5����ȡ���ɫ
        Set<Long> roleIds = authService.findRoleIds(userId, groupIds, organizationIds, jobIds,
            organizationJobIds);

        Set<Role> roles = roleService.findShowRoles(roleIds);

        return roles;

    }

    public Set<String> findStringRoles(User user) {
        Set<Role> roles = ((UserAuthService) AopContext.currentProxy()).findRoles(user);
        return Sets.newHashSet(Collections2.transform(roles, new Function<Role, String>() {
            @Override
            public String apply(Role input) {
                return input.getRole();
            }
        }));
    }

    /**
     * ���ݽ�ɫ��ȡ Ȩ���ַ��� ��sys:admin
     *
     * @param user
     * @return
     */
    public Set<String> findStringPermissions(User user) {
        Set<String> permissions = Sets.newHashSet();

        Set<Role> roles = ((UserAuthService) AopContext.currentProxy()).findRoles(user);
        for (Role role : roles) {
            for (RoleResourcePermission rrp : role.getResourcePermissions()) {
                Resource resource = resourceService.findOne(rrp.getResourceId());

                String actualResourceIdentity = resourceService
                    .findActualResourceIdentity(resource);

                //������ ��û�鵽 ���߱�ʶ�ַ���������
                if (resource == null || StringUtils.isEmpty(actualResourceIdentity)
                    || Boolean.FALSE.equals(resource.getShow())) {
                    continue;
                }

                for (Long permissionId : rrp.getPermissionIds()) {
                    Permission permission = permissionService.findOne(permissionId);

                    //������
                    if (permission == null || Boolean.FALSE.equals(permission.getShow())) {
                        continue;
                    }
                    permissions.add(actualResourceIdentity + ":" + permission.getPermission());

                }
            }

        }

        return permissions;
    }

}
