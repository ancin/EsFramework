package org.es.framework.common.repository.support;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.es.framework.common.entity.search.Searchable;
import org.es.framework.common.plugin.entity.LogicDeleteable;
import org.es.framework.common.repository.BaseRepository;
import org.es.framework.common.repository.RepositoryHelper;
import org.es.framework.common.repository.callback.SearchCallback;
import org.es.framework.common.repository.support.annotation.QueryJoin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.LockMetadataProvider;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.google.common.collect.Sets;

/***
 * <p>�������Custom Repository ʵ��</p>
 * 
 * @author kejun.song
 * @version $Id: SimpleBaseRepository.java, v 0.1 2014��11��19�� ����11:05:55 kejun.song Exp $
 */
public class SimpleBaseRepository<M, ID extends Serializable> extends SimpleJpaRepository<M, ID>
                                                                                                implements
                                                                                                BaseRepository<M, ID> {

    public static final String                LOGIC_DELETE_ALL_QUERY_STRING = "update %s x set x.deleted=true where x in (?1)";
    public static final String                DELETE_ALL_QUERY_STRING       = "delete from %s x where x in (?1)";
    public static final String                FIND_QUERY_STRING             = "from %s x where 1=1 ";
    public static final String                COUNT_QUERY_STRING            = "select count(x) from %s x where 1=1 ";

    private final EntityManager               em;
    private final JpaEntityInformation<M, ID> entityInformation;

    private final RepositoryHelper            repositoryHelper;

    private LockMetadataProvider              lockMetadataProvider;

    private final Class<M>                    entityClass;
    private final String                      entityName;
    private final String                      idName;

    /**
     * ��ѯ���е�QL
     */
    private String                            findAllQL;
    /**
     * ͳ��QL
     */
    private String                            countAllQL;

    private QueryJoin[]                       joins;

    private SearchCallback                    searchCallback                = SearchCallback.DEFAULT;

    public SimpleBaseRepository(JpaEntityInformation<M, ID> entityInformation,
                                EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.entityInformation = entityInformation;
        this.entityClass = this.entityInformation.getJavaType();
        this.entityName = this.entityInformation.getEntityName();
        this.idName = this.entityInformation.getIdAttributeNames().iterator().next();
        this.em = entityManager;

        repositoryHelper = new RepositoryHelper(entityClass);

        findAllQL = String.format(FIND_QUERY_STRING, entityName);
        countAllQL = String.format(COUNT_QUERY_STRING, entityName);
    }

    /**
     * Configures a custom {@link org.springframework.data.jpa.repository.support.LockMetadataProvider} to be used to detect {@link javax.persistence.LockModeType}s to be applied to
     * queries.
     *
     * @param lockMetadataProvider
     */
    @Override
    public void setLockMetadataProvider(LockMetadataProvider lockMetadataProvider) {
        super.setLockMetadataProvider(lockMetadataProvider);
        this.lockMetadataProvider = lockMetadataProvider;
    }

    /**
     * ����searchCallback
     *
     * @param searchCallback
     */
    public void setSearchCallback(SearchCallback searchCallback) {
        this.searchCallback = searchCallback;
    }

    /**
     * ���ò�ѯ���е�ql
     *
     * @param findAllQL
     */
    public void setFindAllQL(String findAllQL) {
        this.findAllQL = findAllQL;
    }

    /**
     * ����ͳ�Ƶ�ql
     *
     * @param countAllQL
     */
    public void setCountAllQL(String countAllQL) {
        this.countAllQL = countAllQL;
    }

    public void setJoins(QueryJoin[] joins) {
        this.joins = joins;
    }

    /////////////////////////////////////////////////
    ////////����Ĭ��spring data jpa��ʵ��////////////
    /////////////////////////////////////////////////

    /**
     * ��������ɾ����Ӧʵ��
     *
     * @param id ����
     */
    @Transactional
    @Override
    public void delete(final ID id) {
        M m = findOne(id);
        delete(m);
    }

    /**
     * ɾ��ʵ��
     *
     * @param m ʵ��
     */
    @Transactional
    @Override
    public void delete(final M m) {
        if (m == null) {
            return;
        }
        if (m instanceof LogicDeleteable) {
            ((LogicDeleteable) m).markDeleted();
            save(m);
        } else {
            super.delete(m);
        }
    }

    /**
     * ��������ɾ����Ӧʵ��
     *
     * @param ids ʵ��
     */
    @Transactional
    @Override
    public void delete(final ID[] ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return;
        }
        List<M> models = new ArrayList<M>();
        for (ID id : ids) {
            M model = null;
            try {
                model = entityClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("batch delete " + entityClass + " error", e);
            }
            try {
                BeanUtils.setProperty(model, idName, id);
            } catch (Exception e) {
                throw new RuntimeException(
                    "batch delete " + entityClass + " error, can not set id", e);
            }
            models.add(model);
        }
        deleteInBatch(models);
    }

    @SuppressWarnings("rawtypes")
    @Transactional
    @Override
    public void deleteInBatch(final Iterable<M> entities) {
        Iterator<M> iter = entities.iterator();
        if (entities == null || !iter.hasNext()) {
            return;
        }

        Set models = Sets.newHashSet(iter);

        boolean logicDeleteableEntity = LogicDeleteable.class.isAssignableFrom(this.entityClass);

        if (logicDeleteableEntity) {
            String ql = String.format(LOGIC_DELETE_ALL_QUERY_STRING, entityName);
            repositoryHelper.batchUpdate(ql, models);
        } else {
            String ql = String.format(DELETE_ALL_QUERY_STRING, entityName);
            repositoryHelper.batchUpdate(ql, models);
        }
    }

    /**
     * ����������ѯ
     *
     * @param id ����
     * @return ����id��Ӧ��ʵ��
     */
    @Transactional
    @Override
    public M findOne(ID id) {
        if (id == null) {
            return null;
        }
        if (id instanceof Integer && ((Integer) id).intValue() == 0) {
            return null;
        }
        if (id instanceof Long && ((Long) id).longValue() == 0L) {
            return null;
        }
        return super.findOne(id);
    }

    ////////����Specification��ѯ ֱ�Ӵ�SimpleJpaRepository���ƹ�����///////////////////////////////////
    @Override
    public M findOne(Specification<M> spec) {
        try {
            return getQuery(spec, (Sort) null).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll(ID[])
     */
    @Override
    public List<M> findAll(Iterable<ID> ids) {

        return getQuery(new Specification<M>() {
            @Override
            public Predicate toPredicate(Root<M> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<?> path = root.get(entityInformation.getIdAttribute());
                return path.in(cb.parameter(Iterable.class, "ids"));
            }
        }, (Sort) null).setParameter("ids", ids).getResultList();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification)
     */
    @Override
    public List<M> findAll(Specification<M> spec) {
        return getQuery(spec, (Sort) null).getResultList();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<M> findAll(Specification<M> spec, Pageable pageable) {

        TypedQuery<M> query = getQuery(spec, pageable);
        return pageable == null ? new PageImpl<M>(query.getResultList()) : readPage(query,
            pageable, spec);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Sort)
     */
    @Override
    public List<M> findAll(Specification<M> spec, Sort sort) {

        return getQuery(spec, sort).getResultList();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#count(org.springframework.data.jpa.domain.Specification)
     */
    @Override
    public long count(Specification<M> spec) {

        return getCountQuery(spec).getSingleResult();
    }

    ////////����Specification��ѯ ֱ�Ӵ�SimpleJpaRepository���ƹ�����///////////////////////////////////

    ///////ֱ�Ӵ�SimpleJpaRepository���ƹ�����///////////////////////////////

    /**
     * Reads the given {@link javax.persistence.TypedQuery} into a {@link org.springframework.data.domain.Page} applying the given {@link org.springframework.data.domain.Pageable} and
     * {@link org.springframework.data.jpa.domain.Specification}.
     *
     * @param query    must not be {@literal null}.
     * @param spec     can be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return
     */
    private Page<M> readPage(TypedQuery<M> query, Pageable pageable, Specification<M> spec) {

        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        Long total = QueryUtils.executeCountQuery(getCountQuery(spec));
        List<M> content = total > pageable.getOffset() ? query.getResultList() : Collections
            .<M> emptyList();

        return new PageImpl<M>(content, pageable, total);
    }

    /**
     * Creates a new count query for the given {@link org.springframework.data.jpa.domain.Specification}.
     *
     * @param spec can be {@literal null}.
     * @return
     */
    private TypedQuery<Long> getCountQuery(Specification<M> spec) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<M> root = applySpecificationToCriteria(spec, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        TypedQuery<Long> q = em.createQuery(query);
        repositoryHelper.applyEnableQueryCache(q);
        return q;
    }

    /**
     * Creates a new {@link javax.persistence.TypedQuery} from the given {@link org.springframework.data.jpa.domain.Specification}.
     *
     * @param spec     can be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return
     */
    private TypedQuery<M> getQuery(Specification<M> spec, Pageable pageable) {

        Sort sort = pageable == null ? null : pageable.getSort();
        return getQuery(spec, sort);
    }

    /**
     * Creates a {@link javax.persistence.TypedQuery} for the given {@link org.springframework.data.jpa.domain.Specification} and {@link org.springframework.data.domain.Sort}.
     *
     * @param spec can be {@literal null}.
     * @param sort can be {@literal null}.
     * @return
     */
    private TypedQuery<M> getQuery(Specification<M> spec, Sort sort) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<M> query = builder.createQuery(entityClass);

        Root<M> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        applyJoins(root);

        if (sort != null) {
            query.orderBy(toOrders(sort, root, builder));
        }

        TypedQuery<M> q = em.createQuery(query);

        repositoryHelper.applyEnableQueryCache(q);

        return applyLockMode(q);
    }

    private void applyJoins(Root<M> root) {
        if (joins == null) {
            return;
        }

        for (QueryJoin join : joins) {
            root.join(join.property(), join.joinType());
        }
    }

    /**
     * Applies the given {@link org.springframework.data.jpa.domain.Specification} to the given {@link javax.persistence.criteria.CriteriaQuery}.
     *
     * @param spec  can be {@literal null}.
     * @param query must not be {@literal null}.
     * @return
     */
    private <S> Root<M> applySpecificationToCriteria(Specification<M> spec, CriteriaQuery<S> query) {

        Assert.notNull(query);
        Root<M> root = query.from(entityClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

    private TypedQuery<M> applyLockMode(TypedQuery<M> query) {
        LockModeType type = lockMetadataProvider == null ? null : lockMetadataProvider
            .getLockModeType();
        return type == null ? query : query.setLockMode(type);
    }

    ///////ֱ�Ӵ�SimpleJpaRepository���ƹ�����///////////////////////////////

    @Override
    public List<M> findAll() {
        return repositoryHelper.findAll(findAllQL);
    }

    @Override
    public List<M> findAll(final Sort sort) {
        return repositoryHelper.findAll(findAllQL, sort);
    }

    @Override
    public Page<M> findAll(final Pageable pageable) {
        return new PageImpl<M>(repositoryHelper.<M> findAll(findAllQL, pageable), pageable,
            repositoryHelper.count(countAllQL));
    }

    @Override
    public long count() {
        return repositoryHelper.count(countAllQL);
    }

    /////////////////////////////////////////////////
    ///////////////////�Զ���ʵ��////////////////////
    /////////////////////////////////////////////////

    @Override
    public Page<M> findAll(final Searchable searchable) {
        List<M> list = repositoryHelper.findAll(findAllQL, searchable, searchCallback);
        long total = searchable.hasPageable() ? count(searchable) : list.size();
        return new PageImpl<M>(list, searchable.getPage(), total);
    }

    @Override
    public long count(final Searchable searchable) {
        return repositoryHelper.count(countAllQL, searchable, searchCallback);
    }

    /**
     * ��дĬ�ϵ� ����������һ��/��������
     *
     * @param id
     * @return
     */
    @Override
    public boolean exists(ID id) {
        return findOne(id) != null;
    }

}
