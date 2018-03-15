package org.es.framework.common.plugin.entity;

import java.io.Serializable;

/***
 * <p>ʵ��ʵ�ָýӿڱ�ʾ��Ҫʵ�����ṹ
 * <p/>
 * 
 * @author kejun.song
 * @version $Id: Treeable.java, v 0.1 2014��11��19�� ����10:50:53 kejun.song Exp $
 */
public interface Treeable<ID extends Serializable> {

    public void setName(String name);

    public String getName();

    /**
     * ��ʾ��ͼ�� ��СΪ16��16
     *
     * @return
     */
    public String getIcon();

    public void setIcon(String icon);

    /**
     * ��·��
     *
     * @return
     */
    public ID getParentId();

    public void setParentId(ID parentId);

    /**
     * ���и�·�� ��1,2,3,
     *
     * @return
     */
    public String getParentIds();

    public void setParentIds(String parentIds);

    /**
     * ��ȡ parentIds ֮��ķָ���
     *
     * @return
     */
    public String getSeparator();

    /**
     * ���Լ�������µĸ��ڵ�·��
     *
     * @return
     */
    public String makeSelfAsNewParentIds();

    /**
     * Ȩ�� �������� ԽСԽ����ǰ��
     *
     * @return
     */
    public Integer getWeight();

    public void setWeight(Integer weight);

    /**
     * �Ƿ��Ǹ��ڵ�
     *
     * @return
     */
    public boolean isRoot();

    /**
     * �Ƿ���Ҷ�ӽڵ�
     *
     * @return
     */
    public boolean isLeaf();

    /**
     * �Ƿ��к��ӽڵ�
     *
     * @return
     */
    public boolean isHasChildren();

    /**
     * ���ڵ�Ĭ��ͼ�� ���û��Ĭ�� �ռ���  ��СΪ16��16
     */
    public String getRootDefaultIcon();

    /**
     * ��֦�ڵ�Ĭ��ͼ�� ���û��Ĭ�� �ռ���  ��СΪ16��16
     */
    public String getBranchDefaultIcon();

    /**
     * ��Ҷ�ڵ�Ĭ��ͼ�� ���û��Ĭ�� �ռ���  ��СΪ16��16
     */
    public String getLeafDefaultIcon();

}
