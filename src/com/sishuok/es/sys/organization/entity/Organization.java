package com.sishuok.es.sys.organization.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.plugin.entity.Treeable;
import org.es.framework.common.repository.support.annotation.EnableQueryCache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

/**
 * ��֯������
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_organization")
@EnableQueryCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Organization extends BaseEntity<Long> implements Treeable<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * ����
     */
    private String            name;

    /**
     * ��֯�������� Ĭ�� �ֹ�˾
     */
    @Enumerated(EnumType.STRING)
    private OrganizationType  type             = OrganizationType.branch_office;
    /**
     * ��·��
     */
    @Column(name = "parent_id")
    private Long              parentId;

    @Column(name = "parent_ids")
    private String            parentIds;

    private Integer           weight;

    /**
     * ͼ��
     */
    private String            icon;

    /**
     * �Ƿ���Ҷ�ӽڵ�
     */
    @Formula(value = "(select count(*) from sys_organization f_t where f_t.parent_id = id)")
    private boolean           hasChildren;

    /**
     * �Ƿ���ʾ
     */
    @Column(name = "is_show")
    private Boolean           show             = Boolean.FALSE;

    public Organization() {
    }

    public Organization(Long id) {
        setId(id);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String getParentIds() {
        return parentIds;
    }

    @Override
    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    @Override
    public String makeSelfAsNewParentIds() {
        return getParentIds() + getId() + getSeparator();
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public Integer getWeight() {
        return weight;
    }

    @Override
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String getIcon() {
        if (!StringUtils.isEmpty(icon)) {
            return icon;
        }
        if (isRoot()) {
            return getRootDefaultIcon();
        }
        if (isLeaf()) {
            return getLeafDefaultIcon();
        }
        return getBranchDefaultIcon();
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public boolean isRoot() {
        if (getParentId() != null && getParentId() == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isLeaf() {
        if (isRoot()) {
            return false;
        }
        if (isHasChildren()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    /**
     * ���ڵ�Ĭ��ͼ�� ���û��Ĭ�� �ռ���
     *
     * @return
     */
    @Override
    public String getRootDefaultIcon() {
        return "ztree_root_open";
    }

    /**
     * ��֦�ڵ�Ĭ��ͼ�� ���û��Ĭ�� �ռ���
     *
     * @return
     */
    @Override
    public String getBranchDefaultIcon() {
        return "ztree_branch";
    }

    /**
     * ��Ҷ�ڵ�Ĭ��ͼ�� ���û��Ĭ�� �ռ���
     *
     * @return
     */
    @Override
    public String getLeafDefaultIcon() {
        return "ztree_leaf";
    }

}