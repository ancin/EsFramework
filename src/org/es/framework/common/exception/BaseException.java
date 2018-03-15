/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.es.framework.common.exception;

import org.es.framework.common.utils.MessageUtils;
import org.springframework.util.StringUtils;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: BaseException.java, v 0.1 2014��11��19�� ����10:48:11 kejun.song Exp $
 */
public class BaseException extends RuntimeException {

    /**  */
    private static final long serialVersionUID = 1L;

    //����ģ��
    private final String      module;

    /**
     * ������
     */
    private final String      code;

    /**
     * �������Ӧ�Ĳ���
     */
    private final Object[]    args;

    /**
     * ������Ϣ
     */
    private final String      defaultMessage;

    public BaseException(String module, String code, Object[] args, String defaultMessage) {
        this.module = module;
        this.code = code;
        this.args = args;
        this.defaultMessage = defaultMessage;
    }

    public BaseException(String module, String code, Object[] args) {
        this(module, code, args, null);
    }

    public BaseException(String module, String defaultMessage) {
        this(module, null, null, defaultMessage);
    }

    public BaseException(String code, Object[] args) {
        this(null, code, args, null);
    }

    public BaseException(String defaultMessage) {
        this(null, null, null, defaultMessage);
    }

    @Override
    public String getMessage() {
        String message = null;
        if (!StringUtils.isEmpty(code)) {
            message = MessageUtils.message(code, args);
        }
        if (message == null) {
            message = defaultMessage;
        }
        return message;
    }

    public String getModule() {
        return module;
    }

    public String getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public String toString() {
        return this.getClass() + "{" + "module='" + module + '\'' + ", message='" + getMessage()
               + '\'' + '}';
    }
}
