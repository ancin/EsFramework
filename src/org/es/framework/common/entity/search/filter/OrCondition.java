package org.es.framework.common.entity.search.filter;

import java.util.List;

import com.google.common.collect.Lists;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: OrCondition.java, v 0.1 2014年11月19日 上午10:28:29 kejun.song Exp $
 */
public class OrCondition implements SearchFilter {

    private final List<SearchFilter> orFilters = Lists.newArrayList();

    OrCondition() {
    }

    public OrCondition add(SearchFilter filter) {
        this.orFilters.add(filter);
        return this;
    }

    public List<SearchFilter> getOrFilters() {
        return orFilters;
    }

    @Override
    public String toString() {
        return "OrCondition{" + orFilters + '}';
    }
}
