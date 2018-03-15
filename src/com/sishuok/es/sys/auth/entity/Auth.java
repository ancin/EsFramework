package com.sishuok.es.sys.auth.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.repository.hibernate.type.CollectionToStringUserType;
import org.es.framework.common.repository.support.annotation.EnableQueryCache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.google.common.collect.Sets;

/**
 * ��֯���� ����ְλ  �û�  ��ɫ ��ϵ��
 * 1����Ȩ���������
 * ֻ����֯������Ȩ (orgnizationId=? and jobId=0)
 * ֻ������ְ����Ȩ (orgnizationId=0 and jobId=?)
 * ����֯�����͹���ְ����Ȩ (orgnizationId=? and jobId=?)
 * ���û���Ȩ  (userId=?)
 * ������Ȩ (groupId=?)
 * <p/>
 * ��˲�ѯ�û���û��Ȩ�� ����
 * where (orgnizationId=? and jobId=0) or (organizationId = 0 and jobId=?) or (orgnizationId=? and jobId=?) or (userId=?) or (groupId=?)
 * <p/>
 * <p/>
 * 2��Ϊ���������
 * �ŵ�һ�ű�
 * �˴�������ϵӳ�䣨������Ҫ��ϻ��棩
 * <p/>
 * 3�������һ���ǿ�ѡ�ģ���ֻѡ��֯���� �� ֻѡ����ְ�� ��ôĬ��0 ʹ��0��Ŀ����Ϊ��Ҳ��������
 * <p/>
 * 
 */
@TypeDef(name = "SetToStringUserType", typeClass = CollectionToStringUserType.class, parameters = {
                                                                                                   @Parameter(name = "separator", value = ","),
                                                                                                   @Parameter(name = "collectionType", value = "java.util.HashSet"),
                                                                                                   @Parameter(name = "elementType", value = "java.lang.Long") })
@Entity
@Table(name = "sys_auth")
@EnableQueryCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Auth extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * ��֯����
     */
    @Column(name = "organization_id")
    private Long              organizationId   = 0L;

    /**
     * ����ְ��
     */
    @Column(name = "job_id")
    private Long              jobId            = 0L;

    /**
     * �û�
     */
    @Column(name = "user_id")
    private Long              userId           = 0L;

    /**
     * ��
     */
    @Column(name = "group_id")
    private Long              groupId          = 0L;

    @Type(type = "SetToStringUserType")
    @Column(name = "role_ids")
    private Set<Long>         roleIds;

    @Enumerated(EnumType.STRING)
    private AuthType          type;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<Long> getRoleIds() {
        if (roleIds == null) {
            roleIds = Sets.newHashSet();
        }
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public void addRoleId(Long roleId) {
        getRoleIds().add(roleId);
    }

    public void addRoleIds(Set<Long> roleIds) {
        getRoleIds().addAll(roleIds);
    }

    public AuthType getType() {
        return type;
    }

    public void setType(AuthType type) {
        this.type = type;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

}
