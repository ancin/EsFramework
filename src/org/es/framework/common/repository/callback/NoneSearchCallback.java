package org.es.framework.common.repository.callback;

import javax.persistence.Query;

import org.es.framework.common.entity.search.Searchable;

/****
 * 
 * 
 * @author kejun.song
 * @version $Id: NoneSearchCallback.java, v 0.1 2014年11月19日 上午10:58:17 kejun.song Exp $
 */
public final class NoneSearchCallback implements SearchCallback {

    @Override
    public void prepareQL(StringBuilder ql, Searchable search) {
    }

    @Override
    public void prepareOrder(StringBuilder ql, Searchable search) {
    }

    @Override
    public void setValues(Query query, Searchable search) {
    }

    @Override
    public void setPageable(Query query, Searchable search) {
    }
}
