package org.es.framework.common.plugin.entity;

/***
 * <p>ʵ��ʵ�ָýӿڣ���ʾ��Ҫ����״̬����<p/>
 * 
 * @author kejun.song
 * @version $Id: Stateable.java, v 0.1 2014��11��19�� ����10:50:18 kejun.song Exp $
 */
public interface Stateable<T extends Enum<? extends Stateable.Status>> {

    public void setStatus(T status);

    public T getStatus();

    /**
     * ��ʶ�ӿڣ�����״̬ʵ�֣���Ҫʵ�ָ�״̬�ӿ�
     */
    public static interface Status {
    }

    /**
     * ���״̬
     */
    public static enum AuditStatus implements Status {
        waiting("�ȴ����"), fail("���ʧ��"), success("��˳ɹ�");
        private final String info;

        private AuditStatus(String info) {
            this.info = info;
        }

        public String getInfo() {
            return info;
        }
    }

    /**
     * �Ƿ���ʾ
     */
    public static enum ShowStatus implements Status {
        hide("����ʾ"), show("��ʾ");
        private final String info;

        private ShowStatus(String info) {
            this.info = info;
        }

        public String getInfo() {
            return info;
        }
    }
}
