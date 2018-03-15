package org.es.framework.common.entity.search.exception;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: InvalidSearchPropertyException.java, v 0.1 2014年11月19日 上午10:24:44 kejun.song Exp $
 */
public final class InvalidSearchPropertyException extends SearchException {

    /**  */
    private static final long serialVersionUID = 1L;

    public InvalidSearchPropertyException(String searchProperty, String entityProperty) {
        this(searchProperty, entityProperty, null);
    }

    public InvalidSearchPropertyException(String searchProperty, String entityProperty,
                                          Throwable cause) {
        super("Invalid Search Property [" + searchProperty + "] Entity Property [" + entityProperty
              + "]", cause);
    }

}
