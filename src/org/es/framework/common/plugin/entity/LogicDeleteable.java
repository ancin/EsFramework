package org.es.framework.common.plugin.entity;

/***
 *  <p>ʵ��ʵ�ָýӿڱ�ʾ��Ҫ�߼�ɾ��
 * Ϊ�˼򻯿��� Լ��ɾ����ʶ����Ϊdeleted��������Զ���ɾ���ı�ʶ������
 * 1��ʹ��ע��Ԫ����
 * 2��дһ�� getColumn() ���� ��������
 * @author kejun.song
 * @version $Id: LogicDeleteable.java, v 0.1 2014��11��19�� ����10:48:53 kejun.song Exp $
 */
public interface LogicDeleteable {

    public Boolean getDeleted();

    public void setDeleted(Boolean deleted);

    public void markDeleted();

}
