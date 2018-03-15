package com.sishuok.es.sys.resource.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.common.entity.BaseEntity;
import org.es.framework.common.plugin.entity.Treeable;
import org.es.framework.common.repository.support.annotation.EnableQueryCache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "sys_resource")
@EnableQueryCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Resource extends BaseEntity<Long> implements Treeable<Long> {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * ����
     */
    private String            name;

    /**
     * ��Դ��ʶ�� ����Ȩ��ƥ��� ��sys:resource
     */
    private String            identity;

    /**
     * �����ǰ���ĵ�ַ
     * �˵�����
     */
    private String            url;

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
    @Formula(value = "(select count(*) from sys_resource f_t where f_t.parent_id = id)")
    private boolean           hasChildren;

    /**
     * �Ƿ���ʾ
     */
    @Column(name = "is_show")
    private Boolean           show             = Boolean.FALSE;

    @Override
    public String getName() {

        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getTreetableIds() {
        String selfId = makeSelfAsNewParentIds().replace("/", "-");
        return selfId.substring(0, selfId.length() - 1);
    }

    public String getTreetableParentIds() {
        String parentIds = getParentIds().replace("/", "-");
        return parentIds.substring(0, parentIds.length() - 1);
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
        return "ztree_setting";
    }

    /**
     * ��֦�ڵ�Ĭ��ͼ�� ���û��Ĭ�� �ռ���
     *
     * @return
     */
    @Override
    public String getBranchDefaultIcon() {
        return "ztree_folder";
    }

    /**
     * ��Ҷ�ڵ�Ĭ��ͼ�� ���û��Ĭ�� �ռ���
     *
     * @return
     */
    @Override
    public String getLeafDefaultIcon() {
        return "ztree_file";
    }

}
