package com.sishuok.es.sys.user.exception;

import org.es.framework.common.exception.BaseException;

public class UserException extends BaseException {

    /**  */
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }

}
