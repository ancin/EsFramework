package org.es.framework.common.repository.callback;

import javax.persistence.Query;

import org.es.framework.common.entity.search.Searchable;

/***
 * 
 * 
 * @author kejun.song
 * @version $Id: SearchCallback.java, v 0.1 2014��11��19�� ����10:58:52 kejun.song Exp $
 */
public interface SearchCallback {

    public static final SearchCallback NONE    = new NoneSearchCallback();
    public static final SearchCallback DEFAULT = new DefaultSearchCallback();

    /**
     * ��̬ƴHQL where��group by having
     *
     * @param ql
     * @param search
     */
    public void prepareQL(StringBuilder ql, Searchable search);

    public void prepareOrder(StringBuilder ql, Searchable search);

    /**
     * ����search��query��ֵ�����÷�ҳ��Ϣ
     *
     * @param query
     * @param search
     */
    public void setValues(Query query, Searchable search);

    public void setPageable(Query query, Searchable search);

}
