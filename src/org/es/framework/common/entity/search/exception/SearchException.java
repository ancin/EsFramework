package org.es.framework.common.entity.search.exception;

import org.springframework.core.NestedRuntimeException;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: SearchException.java, v 0.1 2014年11月19日 上午10:25:25 kejun.song Exp $
 */
public class SearchException extends NestedRuntimeException {

    /**  */
    private static final long serialVersionUID = 1L;

    public SearchException(String msg) {
        super(msg);
    }

    public SearchException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
