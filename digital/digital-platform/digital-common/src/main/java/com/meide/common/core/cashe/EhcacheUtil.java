package com.meide.common.core.cashe;

import com.meide.common.enums.CacheType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "ehcache")
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class EhcacheUtil implements CacheUtil {

    @Autowired
    private CacheManager cacheManager;

    /**
     * 根据预设缓存类型，新增缓存
     *
     * @param cacheType 缓存类型
     * @param key       缓存key
     * @param value     缓存值
     * @see CacheType
     */
    @Override
    public <T> void setCacheObject(CacheType cacheType, String key, T value) {
        Cache cache = cacheManager.getCache(cacheType.getPerfix());
        cache.put(cacheType.getPerfix() + ":" + key, value);
    }

    /**
     * 查找指定缓存类型的所有缓存的key
     *
     * @param cacheType 缓存类型
     * @return 所有缓存
     */
    @Override
    public Collection<String> findAllByType(CacheType cacheType) {
        Cache cache = cacheManager.getCache(cacheType.getPerfix());
        net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
        List<String> keys = nativeCache.getKeys();
        return keys.stream().filter(k -> k.startsWith(cacheType.getPerfix() + ":")).map(k -> k.split(":")[1]).collect(Collectors.toList());
    }

    /**
     * 查找指定缓存的值
     *
     * @param cacheType 缓存类型
     * @param key       缓存key
     * @return 存入缓存的对象
     */
    @Override
    public <T> T getCacheObject(CacheType cacheType, String key) {
        Cache.ValueWrapper valueWrapper = cacheManager.getCache(cacheType.getPerfix()).get(cacheType.getPerfix() + ":" + key);
        if (valueWrapper == null) {
            return null;
        } else {
            return (T)valueWrapper.get();
        }
    }
        /**
         * 删除指定缓存
         *
         * @param cacheType 缓存类型
         * @param key       缓存key
         */
        @Override
        public void deleteObject (CacheType cacheType, String key){
            Cache cache = cacheManager.getCache(cacheType.getPerfix());
            cache.evict(cacheType.getPerfix() + ":" + key);
        }
    }
