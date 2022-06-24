package com.meide.common.core.cashe;

import com.meide.common.enums.CacheType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisCacheUtil implements CacheUtil {

    @Autowired
    public RedisTemplate redisTemplate;

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
        String perfix = cacheType.getPerfix();
        String realKey = perfix + ":" + key;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        if (cacheType.getNum() < 0) {
            valueOperations.set(realKey, value);
        } else {
            valueOperations.set(realKey, value, cacheType.getNum(), cacheType.getUnit());
        }
    }

    /**
     * 查找指定缓存类型的所有缓存的key
     *
     * @param cacheType 缓存类型
     * @return 所有缓存
     */
    @Override
    public Collection<String> findAllByType(CacheType cacheType) {
        Set<String> keys = redisTemplate.keys(cacheType.getPerfix() + ":*");
        if (keys == null || keys.size() == 0) {
            return null;
        }
        return keys.stream().map(k->k.split(":")[1]).collect(Collectors.toList());
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
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(cacheType.getPerfix() + ":" + key);
    }

    /**
     * 删除指定缓存
     *
     * @param cacheType 缓存类型
     * @param key       缓存key
     */
    @Override
    public void deleteObject(CacheType cacheType, String key) {
        redisTemplate.delete(cacheType.getPerfix() + ":" + key);
    }
}
