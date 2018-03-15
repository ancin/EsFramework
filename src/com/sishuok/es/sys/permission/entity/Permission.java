package com.sishuok.es.sys.permission.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.repository.support.annotation.EnableQueryCache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "sys_permission")
@EnableQueryCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Permission extends BaseEntity<Long> {

    /**  */
    private static final long serialVersionUID = 1L;
    /**
     * ǰ����ʾ����
     */
    private String            name;
    /**
     * ϵͳ����֤ʱʹ�õ�Ȩ�ޱ�ʶ
     */
    private String            permission;

    /**
     * ��ϸ����
     */
    private String            description;

    /**
     * �Ƿ���ʾ Ҳ��ʾ�Ƿ���� Ϊ��ͳһ ��ʹ�����
     */
    @Column(name = "is_show")
    private Boolean           show             = Boolean.FALSE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }
}
