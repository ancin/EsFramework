package com.sishuok.es.sys.auth.task;

import java.util.Collection;
import java.util.Set;

import org.es.framework.common.repository.RepositoryHelper;
import org.es.framework.common.utils.LogUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sishuok.es.sys.auth.entity.Auth;
import com.sishuok.es.sys.auth.service.AuthService;
import com.sishuok.es.sys.group.service.GroupService;
import com.sishuok.es.sys.organization.service.JobService;
import com.sishuok.es.sys.organization.service.OrganizationService;
import com.sishuok.es.sys.permission.entity.Role;
import com.sishuok.es.sys.permission.service.RoleService;

/**
 * ����Auth�޹����Ĺ�ϵ
 * 1��Auth-Role
 * 2��Auth-Organization/Job
 * 3��Auth-Group
 * <p/>
 */
@Service
public class AuthRelationClearTask {

    @Autowired
    private AuthService         authService;

    @Autowired
    private RoleService         roleService;

    @Autowired
    private GroupService        groupService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private JobService          jobService;

    /**
     * ���ɾ���Ľ�ɫ��Ӧ�Ĺ�ϵ
     */
    public void clearDeletedAuthRelation() {

        Set<Long> allRoleIds = findAllRoleIds();

        final int PAGE_SIZE = 100;
        int pn = 0;

        Page<Auth> authPage = null;

        do {
            Pageable pageable = new PageRequest(pn++, PAGE_SIZE);
            authPage = authService.findAll(pageable);
            //�������������
            try {
                AuthRelationClearTask authRelationClearService = (AuthRelationClearTask) AopContext
                    .currentProxy();
                authRelationClearService.doClear(authPage.getContent(), allRoleIds);
            } catch (Exception e) {
                //���쳣Ҳ����ν
                LogUtils.logError("clear auth relation error", e);
            }
            //��ջỰ
            RepositoryHelper.clear();
        } while (authPage.hasNextPage());
    }

    public void doClear(Collection<Auth> authColl, Set<Long> allRoleIds) {
        for (Auth auth : authColl) {
            switch (auth.getType()) {
                case user:
                    break;//��Ϊ�û����߼�ɾ�����ù�
                case user_group:
                case organization_group:
                    if (!groupService.exists(auth.getGroupId())) {
                        authService.delete(auth);
                        continue;
                    }
                    break;
                case organization_job:
                    if (!organizationService.exists(auth.getOrganizationId())) {
                        auth.setOrganizationId(0L);
                    }
                    if (!jobService.exists(auth.getOrganizationId())) {
                        auth.setJobId(0L);
                    }
                    //�����֯����/����ְ��Ϊ0L ��ô����ɾ��
                    if (auth.getOrganizationId() == 0L && auth.getJobId() == 0L) {
                        authService.delete(auth);
                        continue;
                    }
                    break;
            }

            boolean hasRemovedAny = auth.getRoleIds().retainAll(allRoleIds);
            if (hasRemovedAny) {
                authService.update(auth);
            }
        }

    }

    private Set<Long> findAllRoleIds() {
        return Sets.newHashSet(Lists.transform(roleService.findAll(), new Function<Role, Long>() {
            @Override
            public Long apply(Role input) {
                return input.getId();
            }
        }));
    }

}
