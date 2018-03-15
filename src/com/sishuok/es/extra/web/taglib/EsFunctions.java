package com.sishuok.es.extra.web.taglib;

import java.util.Iterator;

import org.es.framework.common.utils.SpringUtils;

import com.sishuok.es.sys.organization.entity.Job;
import com.sishuok.es.sys.organization.entity.Organization;
import com.sishuok.es.sys.organization.service.JobService;
import com.sishuok.es.sys.organization.service.OrganizationService;
import com.sishuok.es.sys.permission.entity.Permission;
import com.sishuok.es.sys.permission.entity.Role;
import com.sishuok.es.sys.permission.service.PermissionService;
import com.sishuok.es.sys.permission.service.RoleService;
import com.sishuok.es.sys.resource.entity.Resource;
import com.sishuok.es.sys.resource.service.ResourceService;

/***
 * �ṩel�п���ʹ�õ�һЩ����
 * 
 * @author kejun.song
 * @version $Id: EsFunctions.java, v 0.1 2014��11��19�� ����3:00:24 kejun.song Exp $
 */
public class EsFunctions {

    @SuppressWarnings("rawtypes")
    public static boolean in(Iterable iterable, Object obj) {
        if (iterable == null) {
            return false;
        }
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().equals(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * �ж��Ƿ�洢ָ��id����֯����
     *
     * @param id
     * @param onlyDisplayShow �Ƿ����ʾ�ɼ���
     * @return
     */
    public static boolean existsOrganization(Long id, Boolean onlyDisplayShow) {
        Organization organization = SpringUtils.getBean(OrganizationService.class).findOne(id);
        if (organization == null) {
            return false;
        }
        if (Boolean.TRUE.equals(onlyDisplayShow) && Boolean.FALSE.equals(organization.getShow())) {
            return false;
        }
        return true;
    }

    /**
     * �ж��Ƿ�洢ָ��id�Ĺ���ְ��
     *
     * @param id
     * @param onlyDisplayShow �Ƿ����ʾ�ɼ���
     * @return
     */
    public static boolean existsJob(Long id, Boolean onlyDisplayShow) {
        Job job = SpringUtils.getBean(JobService.class).findOne(id);
        if (job == null) {
            return false;
        }
        if (Boolean.TRUE.equals(onlyDisplayShow) && Boolean.FALSE.equals(job.getShow())) {
            return false;
        }
        return true;
    }

    /**
     * �ж��Ƿ�洢ָ��id����Դ
     *
     * @param id
     * @param onlyDisplayShow �Ƿ����ʾ�ɼ���
     * @return
     */
    public static boolean existsResource(Long id, Boolean onlyDisplayShow) {
        Resource resource = SpringUtils.getBean(ResourceService.class).findOne(id);
        if (resource == null) {
            return false;
        }
        if (Boolean.TRUE.equals(onlyDisplayShow) && Boolean.FALSE.equals(resource.getShow())) {
            return false;
        }
        return true;
    }

    /**
     * �ж��Ƿ�洢ָ��id��Ȩ��
     *
     * @param id
     * @param onlyDisplayShow �Ƿ����ʾ�ɼ���
     * @return
     */
    public static boolean existsPermission(Long id, Boolean onlyDisplayShow) {
        Permission permission = SpringUtils.getBean(PermissionService.class).findOne(id);
        if (permission == null) {
            return false;
        }
        if (Boolean.TRUE.equals(onlyDisplayShow) && Boolean.FALSE.equals(permission.getShow())) {
            return false;
        }
        return true;
    }

    /**
     * �ж��Ƿ�洢ָ��id�Ľ�ɫ
     *
     * @param id
     * @param onlyDisplayShow �Ƿ����ʾ�ɼ���
     * @return
     */
    public static boolean existsRole(Long id, Boolean onlyDisplayShow) {
        Role role = SpringUtils.getBean(RoleService.class).findOne(id);
        if (role == null) {
            return false;
        }
        if (Boolean.TRUE.equals(onlyDisplayShow) && Boolean.FALSE.equals(role.getShow())) {
            return false;
        }
        return true;
    }

}
