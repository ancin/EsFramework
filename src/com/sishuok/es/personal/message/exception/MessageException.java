package com.sishuok.es.personal.message.exception;

import org.es.framework.common.exception.BaseException;

public class MessageException extends BaseException {

    /**  */
    private static final long serialVersionUID = 1L;

    public MessageException(String code, Object[] args) {
        super("personal", code, args);
    }
}
