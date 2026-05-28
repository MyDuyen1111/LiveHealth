package com.livehealth.shared.base;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.redis.datasource.set.SetCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livehealth.auth.dto.request.otp.OtpRedisData;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Quarkus Redis wrapper — non-generic, uses String keys and String values internally
 * with Jackson serialization for complex objects.
 * Callers cast the returned Object to the expected type.
 */
@ApplicationScoped
public class RedisTemplate {

    private final ValueCommands<String, String> valueCommands;
    private final SetCommands<String, String> setCommands;
    private final KeyCommands<String> keyCommands;
    private final ObjectMapper objectMapper;

    @Inject
    public RedisTemplate(RedisDataSource ds, ObjectMapper objectMapper) {
        this.valueCommands = ds.value(String.class);
        this.setCommands = ds.set(String.class);
        this.keyCommands = ds.key();
        this.objectMapper = objectMapper;
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
            try {
                if (value instanceof String) {
                    valueCommands.set(key, (String) value);
                } else {
                    valueCommands.set(key, objectMapper.writeValueAsString(value));
                }
            } catch (Exception e) {
                throw new RuntimeException("Error serializing value to Redis", e);
            }
        }

        public void set(String key, Object value, long timeout, TimeUnit unit) {
            if (key == null) return;
            long seconds = unit.toSeconds(timeout);
            try {
                if (value instanceof String) {
                    valueCommands.setex(key, seconds, (String) value);
                } else {
                    valueCommands.setex(key, seconds, objectMapper.writeValueAsString(value));
                }
            } catch (Exception e) {
                throw new RuntimeException("Error serializing value to Redis", e);
            }
        }

        @SuppressWarnings("unchecked")
        public <V> V get(String key) {
            if (key == null) return null;
            String val = valueCommands.get(key);
            if (val == null) return null;
            String trimmed = val.trim();
            if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                try {
                    return (V) objectMapper.readValue(val, OtpRedisData.class);
                } catch (Exception e) {
                    return (V) val;
                }
            }
            return (V) val;
        }
    }

    public class OpsForSet {
        public Long add(String key, Object... values) {
            if (key == null || values == null || values.length == 0) return 0L;
            String[] strValues = new String[values.length];
            try {
                for (int i = 0; i < values.length; i++) {
                    if (values[i] instanceof String) {
                        strValues[i] = (String) values[i];
                    } else {
                        strValues[i] = objectMapper.writeValueAsString(values[i]);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error serializing set values to Redis", e);
            }
            return (long) setCommands.sadd(key, strValues);
        }

        public Long remove(String key, Object... values) {
            if (key == null || values == null || values.length == 0) return 0L;
            String[] strValues = new String[values.length];
            try {
                for (int i = 0; i < values.length; i++) {
                    if (values[i] instanceof String) {
                        strValues[i] = (String) values[i];
                    } else {
                        strValues[i] = objectMapper.writeValueAsString(values[i]);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error serializing set values for removal from Redis", e);
            }
            return (long) setCommands.srem(key, strValues);
        }

        @SuppressWarnings("unchecked")
        public Set<Object> members(String key) {
            if (key == null) return Set.of();
            Set<String> members = setCommands.smembers(key);
            if (members == null) return Set.of();
            return (Set) members;
        }
    }
}
