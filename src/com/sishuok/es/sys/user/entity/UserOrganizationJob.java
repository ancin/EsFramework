package com.sishuok.es.sys.user.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.repository.support.annotation.EnableQueryCache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Ϊ������������� ʹ���������� ������ ��֯����(1)-----(*)ְ����м��
 * ���ڸñ��� �û�--��֯����--ְ�� ��Ψһ��  �� �û�-��֯���������ظ�
 * 
 */
@Entity
@Table(name = "sys_user_organization_job")
@EnableQueryCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserOrganizationJob extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @Basic(optional = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private User              user;

    @Column(name = "organization_id")
    private Long              organizationId;

    @Column(name = "job_id")
    private Long              jobId;

    public UserOrganizationJob() {
    }

    public UserOrganizationJob(Long id) {
        setId(id);
    }

    public UserOrganizationJob(Long organizationId, Long jobId) {
        this.organizationId = organizationId;
        this.jobId = jobId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    @Override
    public String toString() {
        return "UserOrganizationJob{id = " + this.getId() + ",organizationId=" + organizationId
               + ", jobId=" + jobId + ", userId=" + (user != null ? user.getId() : "null") + '}';
    }
}
