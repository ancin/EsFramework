package org.es.framework.common.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.repository.callback.SearchCallback;
import org.es.framework.common.repository.support.annotation.EnableQueryCache;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.util.Assert;

/**
 * �ֿ⸨����
 * <p>User: Zhang Kaitao
 * <p>Date: 13-4-14 ����5:28
 * <p>Version: 1.0
 */
public class RepositoryHelper {

    private static EntityManager entityManager;
    private final Class<?>       entityClass;
    private boolean              enableQueryCache = false;

    /**
     * @param entityClass �Ƿ�����ѯ����
     */
    public RepositoryHelper(Class<?> entityClass) {
        this.entityClass = entityClass;

        EnableQueryCache enableQueryCacheAnnotation = AnnotationUtils.findAnnotation(entityClass,
            EnableQueryCache.class);

        boolean enableQueryCache = false;
        if (enableQueryCacheAnnotation != null) {
            enableQueryCache = enableQueryCacheAnnotation.value();
        }
        this.enableQueryCache = enableQueryCache;
    }

    public static void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }

    public static EntityManager getEntityManager() {
        Assert
            .notNull(
                entityManager,
                "entityManager must null, please see "
                        + "[com.sishuok.es.common.repository.RepositoryHelper#setEntityManagerFactory]");

        return entityManager;
    }

    public static void flush() {
        getEntityManager().flush();
    }

    public static void clear() {
        flush();
        getEntityManager().clear();
    }

    /**
     * <p>ql������ѯ<br/>
     * searchCallbackĬ��ʵ����ο� {@see com.sishuok.es.common.repository.callback.DefaultSearchCallback}<br/>
     * <p/>
     * ����������ο���{@see com.sishuok.es.common.repository.UserRepositoryImplForCustomSearchIT}
     * ��{@see com.sishuok.es.common.repository.UserRepositoryImplForDefaultSearchIT}
     *
     * @param ql
     * @param searchable     ��ѯ��������ҳ ����
     * @param searchCallback ��ѯ�ص�  �Զ������ò�ѯ�����͸�ֵ
     * @return
     */
    @SuppressWarnings("unchecked")
    public <M> List<M> findAll(final String ql, final Searchable searchable,
                               final SearchCallback searchCallback) {

        assertConverted(searchable);
        StringBuilder s = new StringBuilder(ql);
        searchCallback.prepareQL(s, searchable);
        searchCallback.prepareOrder(s, searchable);
        Query query = getEntityManager().createQuery(s.toString());
        applyEnableQueryCache(query);
        searchCallback.setValues(query, searchable);
        searchCallback.setPageable(query, searchable);

        return query.getResultList();
    }

    /**
     * <p>������ͳ��<br/>
     * ����������ο���{@see com.sishuok.es.common.repository.UserRepositoryImplForCustomSearchIT}
     * ��{@see com.sishuok.es.common.repository.UserRepositoryImplForDefaultSearchIT}
     *
     * @param ql
     * @param searchable
     * @param searchCallback
     * @return
     */
    public long count(final String ql, final Searchable searchable,
                      final SearchCallback searchCallback) {

        assertConverted(searchable);

        StringBuilder s = new StringBuilder(ql);
        searchCallback.prepareQL(s, searchable);
        Query query = getEntityManager().createQuery(s.toString());
        applyEnableQueryCache(query);
        searchCallback.setValues(query, searchable);

        return (Long) query.getSingleResult();
    }

    /**
     * ��������ѯһ��ʵ��
     *
     * @param ql
     * @param searchable
     * @param searchCallback
     * @return
     */
    public <M> M findOne(final String ql, final Searchable searchable,
                         final SearchCallback searchCallback) {

        assertConverted(searchable);

        StringBuilder s = new StringBuilder(ql);
        searchCallback.prepareQL(s, searchable);
        searchCallback.prepareOrder(s, searchable);
        Query query = getEntityManager().createQuery(s.toString());
        applyEnableQueryCache(query);
        searchCallback.setValues(query, searchable);
        searchCallback.setPageable(query, searchable);
        query.setMaxResults(1);
        @SuppressWarnings("unchecked")
        List<M> result = query.getResultList();

        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    /**
     * @param ql
     * @param params
     * @param <M>
     * @return
     * @see RepositoryHelper#findAll(String, org.springframework.data.domain.Pageable, Object...)
     */
    public <M> List<M> findAll(final String ql, final Object... params) {

        //�˴����� (Pageable) null  ����Ĭ���е����Լ��� �ɱ���б�
        return findAll(ql, (Pageable) null, params);

    }

    /**
     * <p>����ql�Ͱ�������˳���paramsִ��ql��pageable�洢��ҳ��Ϣ null��ʾ����ҳ<br/>
     * ����ʹ����ο�����������{@see com.sishuok.es.common.repository.UserRepository2ImplIT#testFindAll()}
     *
     * @param ql
     * @param pageable null��ʾ����ҳ
     * @param params
     * @param <M>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <M> List<M> findAll(final String ql, final Pageable pageable, final Object... params) {

        Query query = getEntityManager().createQuery(
            ql + prepareOrder(pageable != null ? pageable.getSort() : null));
        applyEnableQueryCache(query);
        setParameters(query, params);
        if (pageable != null) {
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return query.getResultList();
    }

    /**
     * <p>����ql�Ͱ�������˳���paramsִ��ql��sort�洢������Ϣ null��ʾ������<br/>
     * ����ʹ����ο�����������{@see com.sishuok.es.common.repository.UserRepository2ImplIT#testFindAll()}
     *
     * @param ql
     * @param sort   null��ʾ������
     * @param params
     * @param <M>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <M> List<M> findAll(final String ql, final Sort sort, final Object... params) {

        Query query = getEntityManager().createQuery(ql + prepareOrder(sort));
        applyEnableQueryCache(query);
        setParameters(query, params);

        return query.getResultList();
    }

    /**
     * <p>����ql�Ͱ�������˳���params��ѯһ��ʵ��<br/>
     * ����ʹ����ο�����������{@see com.sishuok.es.common.repository.UserRepository2ImplIT#testFindOne()}
     *
     * @param ql
     * @param params
     * @param <M>
     * @return
     */
    public <M> M findOne(final String ql, final Object... params) {

        List<M> list = findAll(ql, new PageRequest(0, 1), params);

        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * <p>����ql�Ͱ�������˳���paramsִ��qlͳ��<br/>
     * ����ʹ����ο�����������com.sishuok.es.common.repository.UserRepository2ImplIT#testCountAll()
     *
     * @param ql
     * @param params
     * @return
     */
    public long count(final String ql, final Object... params) {

        Query query = entityManager.createQuery(ql);
        applyEnableQueryCache(query);
        setParameters(query, params);

        return (Long) query.getSingleResult();
    }

    /**
     * <p>ִ�����������.�� ֮��insert, update, delete ��.<br/>
     * ����ʹ����ο�����������{@see com.sishuok.es.common.repository.UserRepository2ImplIT#testBatchUpdate()}
     *
     * @param ql
     * @param params
     * @return
     */
    public int batchUpdate(final String ql, final Object... params) {

        Query query = getEntityManager().createQuery(ql);
        setParameters(query, params);

        return query.executeUpdate();
    }

    /**
     * ��˳������Query����
     *
     * @param query
     * @param params
     */
    public void setParameters(Query query, Object[] params) {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
    }

    /**
     * ƴ����
     *
     * @param sort
     * @return
     */
    public String prepareOrder(Sort sort) {
        if (sort == null || !sort.iterator().hasNext()) {
            return "";
        }
        StringBuilder orderBy = new StringBuilder("");
        orderBy.append(" order by ");
        orderBy.append(sort.toString().replace(":", " "));
        return orderBy.toString();
    }

    public <T> JpaEntityInformation<T, ?> getMetadata(Class<T> entityClass) {
        return JpaEntityInformationSupport.getMetadata(entityClass, entityManager);
    }

    public String getEntityName(Class<?> entityClass) {
        return getMetadata(entityClass).getEntityName();
    }

    private void assertConverted(Searchable searchable) {
        if (!searchable.isConverted()) {
            searchable.convert(this.entityClass);
        }
    }

    public void applyEnableQueryCache(Query query) {
        if (enableQueryCache) {
            query.setHint("org.hibernate.cacheable", true);//������ѯ����
        }
    }

}
