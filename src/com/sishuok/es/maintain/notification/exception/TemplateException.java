package com.sishuok.es.maintain.notification.exception;

import org.es.framework.common.exception.BaseException;

public class TemplateException extends BaseException {

    /**  */
    private static final long serialVersionUID = 1L;

    public TemplateException(final String code, final Object[] args) {
        super("notification", code, args);
    }
}
