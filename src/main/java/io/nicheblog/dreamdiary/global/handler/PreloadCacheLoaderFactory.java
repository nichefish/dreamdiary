package io.nicheblog.dreamdiary.global.handler;

import com.google.common.cache.CacheLoader;
import org.jetbrains.annotations.NotNull;

import javax.cache.integration.CacheLoaderException;
import java.util.HashMap;
import java.util.Map;

/**
 * PreloadCacheLoaderFactory
 *
 * @author nichefish
 */
public class PreloadCacheLoaderFactory
        extends CacheLoader<String, Object> {

    private final Map<String, Object> dataStore;

    public PreloadCacheLoaderFactory() {
        // 데이터를 초기화합니다. 실제 사용 시에는 데이터베이스나 파일 시스템에서 데이터를 로드할 수 있습니다.
        // TODO: DB에서 직접 데이터 조회
        dataStore = new HashMap<>();
        dataStore.put("key1", "value1");
        dataStore.put("key2", "value2");
        dataStore.put("key3", "value3");
    }

    @Override
    public Object load(@NotNull String key) throws CacheLoaderException {
        // 주어진 키에 대한 값을 데이터 저장소에서 로드합니다.
        return dataStore.get(key);
    }

    @Override
    public Map<String, Object> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
        Map<String, Object> results = new HashMap<>();
        for (String key : keys) {
            results.put(key, dataStore.get(key));
        }
        return results;
    }
}
