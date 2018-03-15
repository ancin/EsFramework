package org.es.framework.common.entity.search.filter;

import java.util.List;

import com.google.common.collect.Lists;

/***
 * and ����
 * 
 * @author kejun.song
 * @version $Id: AndCondition.java, v 0.1 2014��11��19�� ����10:26:55 kejun.song Exp $
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
