package com.sishuok.es.sys.group.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;

/**
 * ������ �û�/��֯������ϵ��
 * <p/>
 * ���û�/��֯������һ�ű�Ŀ������߲�ѯ����
 * <p/>
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_group_relation")
//@EnableQueryCache
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GroupRelation extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    @Column(name = "group_id")
    private Long              groupId;

    @Column(name = "organization_id")
    private Long              organizationId;

    /**
     * �����ĵ����û�
     */
    @Column(name = "user_id")
    private Long              userId;

    /**
     * ������ ����user id ��Ϊ����������һ���Ż�
     * ��user����ѡһ
     * [startUserId, endUserId]������
     */
    @Column(name = "start_user_id")
    private Long              startUserId;
    @Column(name = "end_user_id")
    private Long              endUserId;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStartUserId() {
        return startUserId;
    }

    public void setStartUserId(Long startUserId) {
        this.startUserId = startUserId;
    }

    public Long getEndUserId() {
        return endUserId;
    }

    public void setEndUserId(Long endUserId) {
        this.endUserId = endUserId;
    }
}
