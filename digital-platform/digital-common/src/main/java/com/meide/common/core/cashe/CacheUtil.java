package com.meide.common.core.cashe;

import java.util.Collection;

import com.meide.common.enums.CacheType;


public interface CacheUtil {

    /**
     * 根据预设缓存类型，新增缓存
     * @param cacheType 缓存类型
     * @param key 缓存key
     * @param value 缓存值
     *
     * @see CacheType
     */
    <T> void setCacheObject(CacheType cacheType, String key, T value);

    /**
     * 查找指定缓存类型的所有缓存的key
     * @param cacheType 缓存类型
     * @return 所有缓存
     */
    Collection<String> findAllByType(CacheType cacheType);

    /**
     * 查找指定缓存的值
     * @param cacheType 缓存类型
     * @param key 缓存key
     * @return 存入缓存的对象
     */
    <T> T getCacheObject(CacheType cacheType, String key);

    /**
     * 删除指定缓存
     * @param cacheType 缓存类型
     * @param key 缓存key
     */
    void deleteObject(CacheType cacheType, String key);
}
