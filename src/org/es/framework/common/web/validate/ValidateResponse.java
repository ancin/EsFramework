package org.es.framework.common.web.validate;

import java.util.List;

import com.google.common.collect.Lists;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: ValidateResponse.java, v 0.1 2014��11��19�� ����11:42:24 kejun.song Exp $
 */
public class ValidateResponse {
    /**
     * ��֤�ɹ�
     */
    private static final Integer OK      = 1;
    /**
     * ��֤ʧ��
     */
    private static final Integer FAIL    = 0;

    private final List<Object>   results = Lists.newArrayList();

    private ValidateResponse() {
    }

    public static ValidateResponse newInstance() {
        return new ValidateResponse();
    }

    /**
     * ��֤�ɹ���ʹ��ǰ̨alertTextOk�������Ϣ��
     *
     * @param fieldId ��֤�ɹ����ֶ���
     */
    public void validateFail(String fieldId) {
        validateFail(fieldId, "");
    }

    /**
     * ��֤�ɹ�
     *
     * @param fieldId ��֤�ɹ����ֶ���
     * @param message ��֤�ɹ�ʱ��ʾ����Ϣ
     */
    public void validateFail(String fieldId, String message) {
        results.add(new Object[] { fieldId, FAIL, message });
    }

    /**
     * ��֤�ɹ���ʹ��ǰ̨alertTextOk�������Ϣ��
     *
     * @param fieldId ��֤�ɹ����ֶ���
     */
    public void validateSuccess(String fieldId) {
        validateSuccess(fieldId, "");
    }

    /**
     * ��֤�ɹ�
     *
     * @param fieldId ��֤�ɹ����ֶ���
     * @param message ��֤�ɹ�ʱ��ʾ����Ϣ
     */
    public void validateSuccess(String fieldId, String message) {
        results.add(new Object[] { fieldId, OK, message });
    }

    /**
     * ������֤���
     *
     * @return
     */
    public Object result() {
        if (results.size() == 1) {
            return results.get(0);
        }
        return results;
    }

}
