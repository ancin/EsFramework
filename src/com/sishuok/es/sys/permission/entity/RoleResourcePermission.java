package com.sishuok.es.sys.permission.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.repository.hibernate.type.CollectionToStringUserType;
import org.es.framework.common.repository.support.annotation.EnableQueryCache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.google.common.collect.Sets;

/**
 * �˴�û��ʹ�ù��� ��Ϊ��������ܣ������ᰤ�Ų�ѯ��Դ��Ȩ���б���Ϊ�л��棬������Ҳ���Ǻܴ� �������ܲ���
 * 
 */

@TypeDef(name = "SetToStringUserType", typeClass = CollectionToStringUserType.class, parameters = {
                                                                                                   @Parameter(name = "separator", value = ","),
                                                                                                   @Parameter(name = "collectionType", value = "java.util.HashSet"),
                                                                                                   @Parameter(name = "elementType", value = "java.lang.Long") })
@Entity
@Table(name = "sys_role_resource_permission")
@EnableQueryCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RoleResourcePermission extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * ��ɫid
     */
    /**
     * ��ɫid
     */
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private Role              role;

    /**
     * ��Դid
     */
    @Column(name = "resource_id")
    private Long              resourceId;

    /**
     * Ȩ��id�б�
     * ���ݿ�ͨ���ַ����洢 ���ŷָ�
     */
    @Column(name = "permission_ids")
    @Type(type = "SetToStringUserType")
    private Set<Long>         permissionIds;

    public RoleResourcePermission() {
    }

    public RoleResourcePermission(Long id) {
        setId(id);
    }

    public RoleResourcePermission(Long resourceId, Set<Long> permissionIds) {
        this.resourceId = resourceId;
        this.permissionIds = permissionIds;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Set<Long> getPermissionIds() {
        if (permissionIds == null) {
            permissionIds = Sets.newHashSet();
        }
        return permissionIds;
    }

    public void setPermissionIds(Set<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }

    @Override
    public String toString() {
        return "RoleResourcePermission{id=" + this.getId() + ",roleId="
               + (role != null ? role.getId() : "null") + ", resourceId=" + resourceId
               + ", permissionIds=" + permissionIds + '}';
    }
}
