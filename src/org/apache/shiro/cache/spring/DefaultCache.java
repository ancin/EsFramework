/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package org.apache.shiro.cache.spring;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

/**
 * 
 * @author kejun.song
 * @version $Id: DefaultCache.java, v 0.1 2015��7��31�� ����8:01:38 kejun.song Exp $
 */
public class DefaultCache<K, V> implements Cache<K, V> {

    // ����һ�� Data Map�����ڴ�Ÿ� Cache �����е�����
    private final Map<K, V> dataMap = new ConcurrentHashMap<K, V>();

    @Override
    public V get(K key) {
        if (key == null) {
            throw new NullPointerException("���󣺲��� key ����Ϊ�գ�");
        }
        return dataMap.get(key);
    }

    @Override
    public void clear() {
        dataMap.clear();
    }

    /** 
     * @see org.apache.shiro.cache.Cache#keys()
     */
    @Override
    public Set<K> keys() {
        return dataMap.keySet();
    }

    /** 
     * @see org.apache.shiro.cache.Cache#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(K key, V value) throws CacheException {
        if (key == null) {
            throw new NullPointerException("���󣺲��� key ����Ϊ�գ�");
        }
        if (value == null) {
            throw new NullPointerException("���󣺲��� value ����Ϊ�գ�");
        }
        dataMap.put(key, value);
        return dataMap.get(key);
    }

    /** 
     * @see org.apache.shiro.cache.Cache#remove(java.lang.Object)
     */
    @Override
    public V remove(K key) throws CacheException {
        if (key == null) {
            throw new NullPointerException("���󣺲��� key ����Ϊ�գ�");
        }
        return dataMap.remove(key);
    }

    /** 
     * @see org.apache.shiro.cache.Cache#size()
     */
    @Override
    public int size() {
        return 0;
    }

    /** 
     * @see org.apache.shiro.cache.Cache#values()
     */
    @Override
    public Collection<V> values() {
        return null;
    }
}
