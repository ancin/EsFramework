package org.es.framework.common.entity.search.filter;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.es.framework.common.entity.search.SearchOperator;
import org.es.framework.common.entity.search.exception.SearchException;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: SearchFilterHelper.java, v 0.1 2014��11��19�� ����10:29:06 kejun.song Exp $
 */
public final class SearchFilterHelper {
    /**
     * ���ݲ�ѯkey��ֵ����Condition
     *
     * @param key   �� name_like
     * @param value
     * @return
     */
    public static SearchFilter newCondition(final String key, final Object value)
                                                                                 throws SearchException {
        return Condition.newCondition(key, value);
    }

    /**
     * ���ݲ�ѯ���ԡ���������ֵ����Condition
     *
     * @param searchProperty
     * @param operator
     * @param value
     * @return
     */
    public static SearchFilter newCondition(final String searchProperty,
                                            final SearchOperator operator, final Object value) {
        return Condition.newCondition(searchProperty, operator, value);
    }

    /**
     * ƴor����
     *
     * @param first
     * @param others
     * @return
     */
    public static SearchFilter or(SearchFilter first, SearchFilter... others) {
        OrCondition orCondition = new OrCondition();
        orCondition.getOrFilters().add(first);
        if (ArrayUtils.isNotEmpty(others)) {
            orCondition.getOrFilters().addAll(Arrays.asList(others));
        }
        return orCondition;
    }

    /**
     * ƴand����
     *
     * @param first
     * @param others
     * @return
     */
    public static SearchFilter and(SearchFilter first, SearchFilter... others) {
        AndCondition andCondition = new AndCondition();
        andCondition.getAndFilters().add(first);
        if (ArrayUtils.isNotEmpty(others)) {
            andCondition.getAndFilters().addAll(Arrays.asList(others));
        }
        return andCondition;
    }

}
