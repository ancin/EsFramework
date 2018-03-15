package org.es.framework.common.entity.search;

import java.util.Collection;
import java.util.Map;

import org.es.framework.common.entity.search.exception.InvalidSearchPropertyException;
import org.es.framework.common.entity.search.exception.InvalidSearchValueException;
import org.es.framework.common.entity.search.exception.SearchException;
import org.es.framework.common.entity.search.filter.SearchFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/***
 * <p>��ѯ�����ӿ�</p>
 * 
 * @author kejun.song
 * @version $Id: Searchable.java, v 0.1 2014��11��19�� ����10:18:13 kejun.song Exp $
 */
public abstract class Searchable {

    /**
     * ����һ���µĲ�ѯ
     *
     * @return
     */
    public static Searchable newSearchable() {
        return new SearchRequest();
    }

    /**
     * ����һ���µĲ�ѯ
     *
     * @return
     */
    public static Searchable newSearchable(final Map<String, Object> searchParams)
                                                                                  throws SearchException {
        return new SearchRequest(searchParams);
    }

    /**
     * ����һ���µĲ�ѯ
     *
     * @return
     */
    public static Searchable newSearchable(final Map<String, Object> searchParams,
                                           final Pageable page) throws SearchException {
        return new SearchRequest(searchParams, page);
    }

    /**
     * ����һ���µĲ�ѯ
     *
     * @return
     */
    public static Searchable newSearchable(final Map<String, Object> searchParams, final Sort sort)
                                                                                                   throws SearchException {
        return new SearchRequest(searchParams, sort);
    }

    /**
     * ����һ���µĲ�ѯ
     *
     * @return
     */
    public static Searchable newSearchable(final Map<String, Object> searchParams,
                                           final Pageable page, final Sort sort) {
        return new SearchRequest(searchParams, page, sort);
    }

    /**
     * ��ӹ������� ��key="parent.id_eq" value = 1
     * ������ʱ���Ӳ����� Ĭ����custom ����key=parent ʵ��key��parent_custom
     *
     * @param key   �� name_like
     * @param value �����in��ѯ ���ֵ֮��","�ָ�
     * @return
     */
    public abstract Searchable addSearchParam(final String key, final Object value)
                                                                                   throws SearchException;

    /**
     * ���һ���ѯ����
     *
     * @param searchParams
     * @return
     */
    public abstract Searchable addSearchParams(final Map<String, Object> searchParams)
                                                                                      throws SearchException;

    /**
     * ��ӹ�������
     *
     * @param searchProperty ��ѯ��������
     * @param operator       ���������
     * @param value          ֵ
     */
    public abstract Searchable addSearchFilter(final String searchProperty,
                                               final SearchOperator operator, final Object value)
                                                                                                 throws SearchException;

    public abstract Searchable addSearchFilter(final SearchFilter searchFilter);

    /**
     * ��Ӷ��and���ӵĹ�������
     *
     * @param searchFilters
     * @return
     */
    public abstract Searchable addSearchFilters(final Collection<? extends SearchFilter> searchFilters);

    /**
     * ��Ӷ��or���ӵĹ�������
     *
     * @param first  ��һ��
     * @param others ����
     * @return
     */
    public abstract Searchable or(final SearchFilter first, final SearchFilter... others);

    /**
     * ��Ӷ��and���ӵĹ�������
     *
     * @param first
     * @param others
     * @return
     */
    public abstract Searchable and(final SearchFilter first, final SearchFilter... others);

    /**
     * �Ƴ�ָ��key�Ĺ�������
     *
     * @param key
     */
    public abstract Searchable removeSearchFilter(final String key);

    /**
     * �Ƴ�ָ������ �� �������Ĺ�������
     * @param searchProperty
     * @param operator
     * @return
     */
    public abstract Searchable removeSearchFilter(String searchProperty, SearchOperator operator);

    /**
     * ���ַ������͵�ֵת��Ϊentity����ֵ
     *
     * @param entityClass
     * @param <T>
     */
    public abstract <T> Searchable convert(final Class<T> entityClass)
                                                                      throws InvalidSearchValueException,
                                                                      InvalidSearchPropertyException;

    /**
     * ��ʶΪ�Ѿ�ת������ ������ת��
     */
    public abstract Searchable markConverted();

    public abstract Searchable setPage(final Pageable page);

    /**
     * @param pageNumber ��ҳҳ�� ������ 0 ��ʼ
     * @param pageSize   ÿҳ��С
     * @return
     */
    public abstract Searchable setPage(final int pageNumber, final int pageSize);

    public abstract Searchable addSort(final Sort sort);

    public abstract Searchable addSort(final Sort.Direction direction, String property);

    /**
     * ��ȡ��ѯ��������
     *
     * @return
     */
    public abstract Collection<SearchFilter> getSearchFilters();

    /**
     * �Ƿ��Ѿ�ת������ ������ת��
     *
     * @return
     */
    public abstract boolean isConverted();

    /**
     * �Ƿ��в�ѯ����
     *
     * @return
     */
    public abstract boolean hasSearchFilter();

    /**
     * �Ƿ�������
     *
     * @return
     */
    public abstract boolean hashSort();

    public abstract void removeSort();

    /**
     * �Ƿ��з�ҳ
     *
     * @return
     */
    public abstract boolean hasPageable();

    public abstract void removePageable();

    /**
     * ��ȡ��ҳ��������Ϣ
     *
     * @return
     */
    public abstract Pageable getPage();

    /**
     * ��ȡ������Ϣ
     *
     * @return
     */
    public abstract Sort getSort();

    /**
     * �Ƿ������ѯ��  �� name_like
     * ���� or �� and��
     *
     * @param key
     * @return
     */
    public abstract boolean containsSearchKey(final String key);

    /**
     * ��ȡ��ѯ���Զ�Ӧ��ֵ
     * ���ܻ�ȡor �� and ��
     *
     * @param key
     * @return
     */
    public abstract <T> T getValue(final String key);

}
