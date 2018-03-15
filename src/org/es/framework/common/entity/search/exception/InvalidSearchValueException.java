package org.es.framework.common.entity.search.exception;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: InvalidSearchValueException.java, v 0.1 2014��11��19�� ����10:25:06 kejun.song Exp $
 */
public final class InvalidSearchValueException extends SearchException {

    /**  */
    private static final long serialVersionUID = 1L;

    public InvalidSearchValueException(String searchProperty, String entityProperty, Object value) {
        this(searchProperty, entityProperty, value, null);
    }

    public InvalidSearchValueException(String searchProperty, String entityProperty, Object value,
                                       Throwable cause) {
        super("Invalid Search Value, searchProperty [" + searchProperty + "], "
              + "entityProperty [" + entityProperty + "], value [" + value + "]", cause);
    }

}
