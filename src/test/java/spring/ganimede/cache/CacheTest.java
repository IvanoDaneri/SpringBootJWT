package spring.ganimede.cache;

import org.junit.Test;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class CacheTest {

    @Test
    public void whenEntryIdle_thenEviction() throws InterruptedException
    {
        CacheLoader<String, String> loader;
        loader = new CacheLoader<String, String>() {
            @Override
            public String load(String key) {
                return key.toUpperCase();
            }
        };

        LoadingCache<String, String> cache;
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(8, TimeUnit.SECONDS)
                .build(loader);

        cache.put("hello", "hello");
        assertNotNull(cache.getIfPresent("hello"));
        Thread.sleep(10000);
        assertNull(cache.getIfPresent("hello"));
    }
}
