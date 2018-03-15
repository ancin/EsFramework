package org.apache.shiro.cache.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/***
 * 包装Spring cache抽象
 * 
 * @author kejun.song
 * @version $Id: SpringCacheManagerWrapper.java, v 0.1 2014年11月19日 下午2:42:16 kejun.song Exp $
 */
public class SpringCacheManagerWrapper2 implements CacheManager {

    // 定义一个 Cache Map，用于存放该 Cache Manager 中所有的 Cache
    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();

    public SpringCacheManagerWrapper2(String... cacheNames) {
        if (cacheNames == null) {
            throw new NullPointerException("错误：参数 cacheNames 不能为空！");
        }
        for (String cacheName : cacheNames) {
            createCache(cacheName);
        }
    }

    public void createCache(String cacheName) {
        if (cacheName == null) {
            throw new NullPointerException("错误：参数 cacheName 不能为空！");
        }
        if (cacheMap.containsKey(cacheName)) {
            throw new CacheException("错误：同名的 Cache 已存在，无法创建！");
        }
        Cache cache = new DefaultCache();
        cacheMap.put(cacheName, cache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String cacheName) {
        if (cacheName == null) {
            throw new NullPointerException("错误：参数 cacheName 不能为空！");
        }
        return cacheMap.get(cacheName);
    }

    public Iterable<String> getCacheNames() {
        return cacheMap.keySet();
    }

    public void destroyCache(String cacheName) {
        if (cacheName == null) {
            throw new NullPointerException("错误：参数 cacheName 不能为空！");
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
