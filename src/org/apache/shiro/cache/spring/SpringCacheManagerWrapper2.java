package org.apache.shiro.cache.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/***
 * ��װSpring cache����
 * 
 * @author kejun.song
 * @version $Id: SpringCacheManagerWrapper.java, v 0.1 2014��11��19�� ����2:42:16 kejun.song Exp $
 */
public class SpringCacheManagerWrapper2 implements CacheManager {

    // ����һ�� Cache Map�����ڴ�Ÿ� Cache Manager �����е� Cache
    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();

    public SpringCacheManagerWrapper2(String... cacheNames) {
        if (cacheNames == null) {
            throw new NullPointerException("���󣺲��� cacheNames ����Ϊ�գ�");
        }
        for (String cacheName : cacheNames) {
            createCache(cacheName);
        }
    }

    public void createCache(String cacheName) {
        if (cacheName == null) {
            throw new NullPointerException("���󣺲��� cacheName ����Ϊ�գ�");
        }
        if (cacheMap.containsKey(cacheName)) {
            throw new CacheException("����ͬ���� Cache �Ѵ��ڣ��޷�������");
        }
        Cache cache = new DefaultCache();
        cacheMap.put(cacheName, cache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String cacheName) {
        if (cacheName == null) {
            throw new NullPointerException("���󣺲��� cacheName ����Ϊ�գ�");
        }
        return cacheMap.get(cacheName);
    }

    public Iterable<String> getCacheNames() {
        return cacheMap.keySet();
    }

    public void destroyCache(String cacheName) {
        if (cacheName == null) {
            throw new NullPointerException("���󣺲��� cacheName ����Ϊ�գ�");
        }
        Cache cache = getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    public void destroyCacheAll() {
        for (String cacheName : getCacheNames()) {
            destroyCache(cacheName);
        }
    }

}
