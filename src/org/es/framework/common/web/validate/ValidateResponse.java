package org.es.framework.common.web.validate;

import java.util.List;

import com.google.common.collect.Lists;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: ValidateResponse.java, v 0.1 2014年11月19日 上午11:42:24 kejun.song Exp $
 */
public class ValidateResponse {
    /**
     * 验证成功
     */
    private static final Integer OK      = 1;
    /**
     * 验证失败
     */
    private static final Integer FAIL    = 0;

    private final List<Object>   results = Lists.newArrayList();

    private ValidateResponse() {
    }

    public static ValidateResponse newInstance() {
        return new ValidateResponse();
    }

    /**
     * 验证成功（使用前台alertTextOk定义的消息）
     *
     * @param fieldId 验证成功的字段名
     */
    public void validateFail(String fieldId) {
        validateFail(fieldId, "");
    }

    /**
     * 验证成功
     *
     * @param fieldId 验证成功的字段名
     * @param message 验证成功时显示的消息
     */
    public void validateFail(String fieldId, String message) {
        results.add(new Object[] { fieldId, FAIL, message });
    }

    /**
     * 验证成功（使用前台alertTextOk定义的消息）
     *
     * @param fieldId 验证成功的字段名
     */
    public void validateSuccess(String fieldId) {
        validateSuccess(fieldId, "");
    }

    /**
     * 验证成功
     *
     * @param fieldId 验证成功的字段名
     * @param message 验证成功时显示的消息
     */
    public void validateSuccess(String fieldId, String message) {
        results.add(new Object[] { fieldId, OK, message });
    }

    /**
     * 返回验证结果
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
