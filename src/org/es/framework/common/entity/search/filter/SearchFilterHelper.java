package org.es.framework.common.entity.search.filter;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.es.framework.common.entity.search.SearchOperator;
import org.es.framework.common.entity.search.exception.SearchException;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: SearchFilterHelper.java, v 0.1 2014年11月19日 上午10:29:06 kejun.song Exp $
 */
public final class SearchFilterHelper {
    /**
     * 根据查询key和值生成Condition
     *
     * @param key   如 name_like
     * @param value
     * @return
     */
    public static SearchFilter newCondition(final String key, final Object value)
                                                                                 throws SearchException {
        return Condition.newCondition(key, value);
    }

    /**
     * 根据查询属性、操作符和值生成Condition
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
     * 拼or条件
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
     * 拼and条件
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
