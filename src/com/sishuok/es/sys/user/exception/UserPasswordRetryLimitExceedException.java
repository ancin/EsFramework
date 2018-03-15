package com.sishuok.es.sys.user.exception;

public class UserPasswordRetryLimitExceedException extends UserException {

    /**  */
    private static final long serialVersionUID = 1L;

    public UserPasswordRetryLimitExceedException(int retryLimitCount) {
        super("user.password.retry.limit.exceed", new Object[] { retryLimitCount });
    }
}
