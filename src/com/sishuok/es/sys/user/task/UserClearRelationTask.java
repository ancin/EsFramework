package com.sishuok.es.sys.user.task;

import java.util.Collection;

import org.es.framework.common.repository.RepositoryHelper;
import org.es.framework.common.utils.LogUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sishuok.es.sys.organization.service.JobService;
import com.sishuok.es.sys.organization.service.OrganizationService;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.entity.UserOrganizationJob;
import com.sishuok.es.sys.user.service.UserService;

/**
 * �����޹�����User-Organization/Job��ϵ
 * 1��User-Organization/Job
 * <p/>
 */
@Service()
public class UserClearRelationTask {

    @Autowired
    private UserService         userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private JobService          jobService;

    /**
     * ���ɾ�����û�-��֯����/����ְ���Ӧ�Ĺ�ϵ
     */
    public void clearDeletedUserRelation() {

        //ɾ���û������ڵ������UserOrganizationJob�������ֹ������ݿ�����ɾ��������
        userService.deleteUserOrganizationJobOnNotExistsUser();

        Page<UserOrganizationJob> page = null;

        int pn = 0;
        final int PAGE_SIZE = 100;
        Pageable pageable = null;
        do {
            pageable = new PageRequest(pn++, PAGE_SIZE);
            page = userService.findUserOrganizationJobOnNotExistsOrganizationOrJob(pageable);

            //�������������
            try {
                UserClearRelationTask userClearRelationTask = (UserClearRelationTask) AopContext
                    .currentProxy();
                userClearRelationTask.doClear(page.getContent());
            } catch (Exception e) {
                //���쳣Ҳ����ν
                LogUtils.logError("clear user relation error", e);

            }
            //��ջỰ
            RepositoryHelper.clear();

        } while (page.hasNextPage());

    }

    public void doClear(Collection<UserOrganizationJob> userOrganizationJobColl) {
        for (UserOrganizationJob userOrganizationJob : userOrganizationJobColl) {

            User user = userOrganizationJob.getUser();

            if (!organizationService.exists(userOrganizationJob.getOrganizationId())) {
                user.getOrganizationJobs().remove(userOrganizationJob);//�������֯����ɾ���� ֱ���Ƴ�
            } else if (!jobService.exists(userOrganizationJob.getJobId())) {
                user.getOrganizationJobs().remove(userOrganizationJob);
                userOrganizationJob.setJobId(null);
                user.getOrganizationJobs().add(userOrganizationJob);
            }
            //����Ҳ�� ���ϵ�Ŀ����Ϊ���建��
            userService.update(user);
        }

    }
}
