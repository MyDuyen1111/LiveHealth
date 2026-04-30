package com.livehealth.shared.base;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.redis.datasource.set.SetCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Quarkus Redis wrapper — non-generic, uses String keys and Object values.
 * Callers cast the returned Object to the expected type.
 */
@ApplicationScoped
public class RedisTemplate {

    private final ValueCommands<String, Object> valueCommands;
    private final SetCommands<String, Object> setCommands;
    private final KeyCommands<String> keyCommands;

    @Inject
    public RedisTemplate(RedisDataSource ds) {
        this.valueCommands = ds.value(Object.class);
        this.setCommands = ds.set(Object.class);
        this.keyCommands = ds.key();
    }

    public OpsForValue opsForValue() {
        return new OpsForValue();
    }

    public OpsForSet opsForSet() {
        return new OpsForSet();
    }

    public Boolean hasKey(String key) {
        if (key == null) return false;
        return keyCommands.exists(key);
    }

    public Boolean delete(String key) {
        if (key == null) return false;
        return keyCommands.del(key) > 0;
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        if (key == null) return false;
        long seconds = unit.toSeconds(timeout);
        return keyCommands.expire(key, seconds);
    }

    public class OpsForValue {
        public void set(String key, Object value) {
            if (key == null) return;
            valueCommands.set(key, value);
        }

        public void set(String key, Object value, long timeout, TimeUnit unit) {
            if (key == null) return;
            long seconds = unit.toSeconds(timeout);
            valueCommands.setex(key, seconds, value);
        }

        @SuppressWarnings("unchecked")
        public <V> V get(String key) {
            if (key == null) return null;
            return (V) valueCommands.get(key);
        }
    }

    public class OpsForSet {
        public Long add(String key, Object... values) {
            if (key == null || values == null || values.length == 0) return 0L;
            return (long) setCommands.sadd(key, values);
        }

        public Long remove(String key, Object... values) {
            if (key == null || values == null || values.length == 0) return 0L;
            return (long) setCommands.srem(key, values);
        }

        public Set<Object> members(String key) {
            if (key == null) return Set.of();
            return setCommands.smembers(key);
        }
    }
}
