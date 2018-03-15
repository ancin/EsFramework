package org.es.framework.common.entity.search.exception;

import org.es.framework.common.entity.search.SearchOperator;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: InvlidSearchOperatorException.java, v 0.1 2014年11月19日 上午10:25:48 kejun.song Exp $
 */
public final class InvlidSearchOperatorException extends SearchException {

    /**  */
    private static final long serialVersionUID = 1L;

    public InvlidSearchOperatorException(String searchProperty, String operatorStr) {
        this(searchProperty, operatorStr, null);
    }

    public InvlidSearchOperatorException(String searchProperty, String operatorStr, Throwable cause) {
        super("Invalid Search Operator searchProperty [" + searchProperty + "], " + "operator ["
              + operatorStr + "], must be one of " + SearchOperator.toStringAllOperator(), cause);
    }
}
