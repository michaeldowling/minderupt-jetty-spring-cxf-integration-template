package service.cache.memory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import service.cache.CacheService;

import java.util.HashMap;
import java.util.Map;

public class MemoryHashCacheServiceImpl implements CacheService {

    private static Log LOGGER = LogFactory.getLog(MemoryHashCacheServiceImpl.class);
    private HashMap cache;


    public MemoryHashCacheServiceImpl() {
        this.cache = new HashMap();
    }

    public MemoryHashCacheServiceImpl(Map seed) {
        this.cache = new HashMap();
        for(Object key : seed.keySet()) {
            LOGGER.info("Seeding cache with key (" + key + ") and value (" + seed.get(key) + ")");
            this.cache.put(key, seed.get(key));
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        T cachedValue = (T)this.cache.get(key);
        return(cachedValue);
    }

    @Override
    public void put(String key, Object value) {
        this.cache.put(key, value);
    }
}
