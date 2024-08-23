package io.nicheblog.dreamdiary.global.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * CustomCacheResolver
 *
 * @author nichefish
 */
@Log4j2
public class CustomCacheResolver extends AbstractCacheResolver {

    private final CacheManager memoryCacheManager;

    private final CacheManager remoteCacheManager;

    public CustomCacheResolver(CacheManager memoryCacheManager,CacheManager remoteCacheManager) {
        super(memoryCacheManager);
        this.memoryCacheManager = memoryCacheManager;
        this.remoteCacheManager = remoteCacheManager;
    }

    @Override
    public Collection<? extends Cache> resolveCaches(
            CacheOperationInvocationContext<?> context) {
        Collection<Cache> caches = new ArrayList<>();

        CacheableConfig cacheableConfig = context.getMethod().getAnnotation(CacheableConfig.class);

        boolean hasMemory = cacheableConfig == null
                || cacheableConfig.cacheTarget() == CacheableConfig.CacheTarget.MEMORY
                || cacheableConfig.cacheTarget() == CacheableConfig.CacheTarget.MEMORY_AND_SHARED;

        boolean hasShared = cacheableConfig == null
                || cacheableConfig.cacheTarget() == CacheableConfig.CacheTarget.SHARED
                || cacheableConfig.cacheTarget() == CacheableConfig.CacheTarget.MEMORY_AND_SHARED;

        Collection<String> cacheNames = getCacheNames(context);
        for (String cacheName: cacheNames) {
            if (hasMemory) {
                Optional.ofNullable(memoryCacheManager.getCache(cacheName))
                        .ifPresent(caches::add);
            }
            if (hasShared) {
                Optional.ofNullable(remoteCacheManager.getCache(cacheName))
                        .ifPresent(caches::add);
            }
        }

        log.info("Cache resolved count: {}" , caches.size());
        log.info("Cache configuration: MEMORY = {}, SHARED = {}" , hasMemory, hasShared);
        return caches;
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return context.getOperation().getCacheNames();
    }
}