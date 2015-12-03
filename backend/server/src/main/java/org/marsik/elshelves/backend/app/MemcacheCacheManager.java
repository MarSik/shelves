package org.marsik.elshelves.backend.app;

import net.spy.memcached.MemcachedClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemcacheCacheManager implements CacheManager {
    private final Map<String, Memcache> caches = new ConcurrentHashMap<>();
    private final MemcachedClient memcachedClient;

    public MemcacheCacheManager(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    @Override
    public Cache getCache(String s) {
        caches.putIfAbsent(s, new Memcache(s, memcachedClient));
        return caches.get(s);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableCollection(caches.keySet());
    }

    public static class Memcache implements Cache {
        private final String name;
        private final MemcachedClient memcachedClient;

        public Memcache(String name, MemcachedClient memcachedClient) {
            this.name = name;
            this.memcachedClient = memcachedClient;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object getNativeCache() {
            return memcachedClient;
        }

        @Override
        public ValueWrapper get(Object o) {
            Object res = memcachedClient.get(getKey(o));
            if (res == null) {
                return null;
            } else {
                return new SimpleValueWrapper(res);
            }
        }

        private String getKey(Object o) {
            return "MCM-"+name+"-"+String.valueOf(o.hashCode());
        }

        @Override
        public <T> T get(Object o, Class<T> aClass) {
            Object res = memcachedClient.get(getKey(o));
            return (T)res;
        }

        @Override
        public void put(Object o, Object o1) {
            memcachedClient.add(getKey(o), 0, o1);
        }

        @Override
        public synchronized ValueWrapper putIfAbsent(Object o, Object o1) {
            ValueWrapper res = get(o);
            if (res == null) {
                put(o, o1);
            }
            return res;
        }

        @Override
        public void evict(Object o) {
            memcachedClient.delete(getKey(o));
        }

        @Override
        public void clear() {
            // TODO
        }
    }
}
