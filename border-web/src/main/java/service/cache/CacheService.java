package service.cache;


public interface CacheService {

    public <T> T get(String key, Class<T> clazz);
    public void put(String key, Object value);

}
