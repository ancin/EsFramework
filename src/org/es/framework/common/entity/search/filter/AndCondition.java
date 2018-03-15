package org.es.framework.common.entity.search.filter;

import java.util.List;

import com.google.common.collect.Lists;

/***
 * and 条件
 * 
 * @author kejun.song
 * @version $Id: AndCondition.java, v 0.1 2014年11月19日 上午10:26:55 kejun.song Exp $
 */
public class AndCondition implements SearchFilter {

    private final List<SearchFilter> andFilters = Lists.newArrayList();

    AndCondition() {
    }

    public AndCondition add(SearchFilter filter) {
        this.andFilters.add(filter);
        return this;
    }

    public List<SearchFilter> getAndFilters() {
        return andFilters;
    }

    @Override
    public String toString() {
        return "AndCondition{" + andFilters + '}';
    }
}
