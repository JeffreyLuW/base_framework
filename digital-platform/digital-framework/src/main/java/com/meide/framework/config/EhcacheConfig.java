package com.meide.framework.config;

import com.meide.common.enums.CacheType;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "ehcache")
//使用Ehcache时排除Redis自动配置
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class})
public class EhcacheConfig extends CachingConfigurerSupport {

    @Bean
    public CacheManager ehCacheCacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration();

        //默认缓存2分钟
        CacheConfiguration defaltConfig = new CacheConfiguration();
        defaltConfig.setMaxEntriesLocalHeap(20000);
        defaltConfig.setEternal(false);
        defaltConfig.setTimeToIdleSeconds(120);
        defaltConfig.setTimeToLiveSeconds(120);
        configuration.defaultCache(defaltConfig);
        //根据CacheType初始化Cache
        CacheType[] cts = CacheType.values();
        for (CacheType ct : cts) {
            CacheConfiguration cacheConfiguration = new CacheConfiguration();
            cacheConfiguration.setName(ct.getPerfix());
            cacheConfiguration.setMaxEntriesLocalHeap(20000);
            if (ct.getNum() < 0) {
                cacheConfiguration.setEternal(true);
            } else {
                cacheConfiguration.setTimeToLiveSeconds(ct.getUnit().toSeconds(ct.getNum()));
                cacheConfiguration.setTimeToLiveSeconds(ct.getUnit().toSeconds(ct.getNum()));
            }
            configuration.addCache(cacheConfiguration);
        }
        cacheManager.setCacheManager(new net.sf.ehcache.CacheManager(configuration));
        return cacheManager;
    }


}
