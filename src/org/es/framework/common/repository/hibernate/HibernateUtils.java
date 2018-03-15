package org.es.framework.common.repository.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Cache;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;

/**
 * ���� jpa api ��ȡhibernate���api
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-23 ����6:24
 * <p>Version: 1.0
 */
@SuppressWarnings("deprecation")
public class HibernateUtils {

    /**
     * ����jpa EntityManager ��ȡ hibernate Session API
     *
     * @param em
     * @return
     */
    public static Session getSession(EntityManager em) {
        return (Session) em.getDelegate();
    }

    /**
     * ����jpa EntityManager ��ȡ hibernate SessionFactory API
     *
     * @param em
     * @return
     * @see #getSessionFactory(javax.persistence.EntityManagerFactory)
     */
    public static SessionFactory getSessionFactory(EntityManager em) {
        return getSessionFactory(em.getEntityManagerFactory());
    }

    /**
     * ����jpa EntityManagerFactory ��ȡ hibernate SessionFactory API
     *
     * @param emf
     * @return
     */
    public static SessionFactory getSessionFactory(EntityManagerFactory emf) {
        return ((HibernateEntityManagerFactory) emf).getSessionFactory();
    }

    /**
     * ���� jpa EntityManager ��ȡhibernate Cache API
     *
     * @param em
     * @return
     * @see #getCache(javax.persistence.EntityManagerFactory)
     */
    public static Cache getCache(EntityManager em) {
        return getCache(em.getEntityManagerFactory());
    }

    /**
     * ����jpa EntityManagerFactory ��ȡ hibernate Cache API
     *
     * @param emf
     * @return
     */
    public static Cache getCache(EntityManagerFactory emf) {
        return getSessionFactory(emf).getCache();
    }

    /**
     * ���һ������
     *
     * @param em
     */
    public static void evictLevel1Cache(EntityManager em) {
        em.clear();
    }

    /**
     * ����jpa EntityManager ��ն�������
     *
     * @param em
     * @see #evictLevel2Cache(javax.persistence.EntityManagerFactory)
     */
    public static void evictLevel2Cache(EntityManager em) {
        evictLevel2Cache(em.getEntityManagerFactory());
    }

    /**
     * ����jpa EntityManagerFactory ��ն������� ������
     * 1��ʵ�建��
     * 2�����ϻ���
     * 3����ѯ����
     * ע�⣺
     * jpa Cache api ֻ��evict ʵ�建�棬����������ɾ�����ġ�����
     *
     * @param emf
     * @see org.hibernate.ejb.EntityManagerFactoryImpl.JPACache#evictAll()
     */
    public static void evictLevel2Cache(EntityManagerFactory emf) {
        Cache cache = HibernateUtils.getCache(emf);
        cache.evictEntityRegions();
        cache.evictCollectionRegions();
        cache.evictDefaultQueryRegion();
        cache.evictQueryRegions();
        cache.evictNaturalIdRegions();
    }
}
