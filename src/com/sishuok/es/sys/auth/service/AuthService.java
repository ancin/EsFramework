/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.sys.auth.service;

import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.es.framework.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sishuok.es.sys.auth.entity.Auth;
import com.sishuok.es.sys.auth.repository.AuthRepository;
import com.sishuok.es.sys.group.entity.Group;
import com.sishuok.es.sys.group.service.GroupService;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.service.UserService;

@Service
public class AuthService extends BaseService<Auth, Long> {

    @Autowired
    private UserService  userService;

    @Autowired
    private GroupService groupService;

    private AuthRepository getAuthRepository() {
        return (AuthRepository) baseRepository;
    }

    public void addUserAuth(Long[] userIds, Auth m) {

        if (ArrayUtils.isEmpty(userIds)) {
            return;
        }

        for (Long userId : userIds) {

            User user = userService.findOne(userId);
            if (user == null) {
                continue;
            }

            Auth auth = getAuthRepository().findByUserId(userId);
            if (auth != null) {
                auth.addRoleIds(m.getRoleIds());
                continue;
            }
            auth = new Auth();
            auth.setUserId(userId);
            auth.setType(m.getType());
            auth.setRoleIds(m.getRoleIds());
            save(auth);
        }
    }

    public void addGroupAuth(Long[] groupIds, Auth m) {
        if (ArrayUtils.isEmpty(groupIds)) {
            return;
        }

        for (Long groupId : groupIds) {
            Group group = groupService.findOne(groupId);
            if (group == null) {
                continue;
            }

            Auth auth = getAuthRepository().findByGroupId(groupId);
            if (auth != null) {
                auth.addRoleIds(m.getRoleIds());
                continue;
            }
            auth = new Auth();
            auth.setGroupId(groupId);
            auth.setType(m.getType());
            auth.setRoleIds(m.getRoleIds());
            save(auth);
        }
    }

    public void addOrganizationJobAuth(Long[] organizationIds, Long[][] jobIds, Auth m) {

        if (ArrayUtils.isEmpty(organizationIds)) {
            return;
        }
        for (int i = 0, l = organizationIds.length; i < l; i++) {
            Long organizationId = organizationIds[i];
            if (jobIds[i].length == 0) {
                addOrganizationJobAuth(organizationId, null, m);
                continue;
            }

            //������/�޸�һ�� spring���Զ�split����������--->������
            if (l == 1) {
                for (int j = 0, l2 = jobIds.length; j < l2; j++) {
                    Long jobId = jobIds[i][0];
                    addOrganizationJobAuth(organizationId, jobId, m);
                }
            } else {
                for (int j = 0, l2 = jobIds[i].length; j < l2; j++) {
                    Long jobId = jobIds[i][0];
                    addOrganizationJobAuth(organizationId, jobId, m);
                }
            }

        }
    }

    private void addOrganizationJobAuth(Long organizationId, Long jobId, Auth m) {
        if (organizationId == null) {
            organizationId = 0L;
        }
        if (jobId == null) {
            jobId = 0L;
        }

        Auth auth = getAuthRepository().findByOrganizationIdAndJobId(organizationId, jobId);
        if (auth != null) {
            auth.addRoleIds(m.getRoleIds());
            return;
        }

        auth = new Auth();
        auth.setOrganizationId(organizationId);
        auth.setJobId(jobId);
        auth.setType(m.getType());
        auth.setRoleIds(m.getRoleIds());
        save(auth);

    }

    /**
     * �����û���Ϣ��ȡ ��ɫ
     * 1.1���û�  �����û�����ƥ��
     * 1.2����֯���� ������֯��������ƥ�� �˴���Ҫע�� ������Ҫ�Լ���ȡ
     * 1.3������ְ�� ���ݹ���ְ�����ƥ�� �˴���Ҫע�� ������Ҫ�Լ���ȡ
     * 1.4����֯�����͹���ְ��  ������֯�����͹���ְ�����ƥ�� �˴���ƥ������
     * 1.5����  ���������ƥ��
     *
     * @param userId             ������
     * @param groupIds           ��ѡ
     * @param organizationIds    ��ѡ
     * @param jobIds             ��ѡ
     * @param organizationJobIds ��ѡ
     * @return
     */
    public Set<Long> findRoleIds(Long userId, Set<Long> groupIds, Set<Long> organizationIds,
                                 Set<Long> jobIds, Set<Long[]> organizationJobIds) {
        return getAuthRepository().findRoleIds(userId, groupIds, organizationIds, jobIds,
            organizationJobIds);
    }
}
