package com.sishuok.es.sys.user.exception;

public class UserBlockedException extends UserException {
    /**  */
    private static final long serialVersionUID = 1L;

    public UserBlockedException(String reason) {
        super("user.blocked", new Object[] { reason });
    }
}
