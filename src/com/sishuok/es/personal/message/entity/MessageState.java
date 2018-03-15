package com.sishuok.es.personal.message.entity;

/**
 * ��Ϣ״̬
 * 
 */
public enum MessageState {

    /**
     * ���ռ���,365���״̬�ĳ�������
     */
    in_box("�ռ���"),
    /**
     * �ڷ�����,365���״̬�ĳ�������
     */
    out_box("������"),
    /**
     * ���ղ���,������ɾ��
     */
    store_box("�ղ���"),
    /**
     * ��������,30���״̬�ĳ���ɾ��
     */
    trash_box("������"),
    /**
     * �ڲݸ���,���ò�ɾ��
     */
    draft_box("�ݸ���"),
    /**
     * �ʼ�ɾ���ˣ�ֻ���ռ��˺ͷ����˶�ɾ���ˣ���������ɾ��
     */
    delete_box("��ɾ��");

    private final String info;

    private MessageState(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
