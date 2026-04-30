package com.livehealth.shared.base;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MultivaluedMap;

public class VsResponseUtil {
    public static <T> Response success(T data) {
        return success(HttpStatus.OK, data);
    }

    public static <T> Response success(HttpStatus status, T data) {
        RestData<T> response = new RestData<>(data);
        return Response.status(status.value()).entity(response).build();
    }

    public static <T> Response success(MultivaluedMap<String, Object> header, T data) {
        return success(HttpStatus.OK, header, data);
    }

    public static <T> Response success(HttpStatus status, MultivaluedMap<String, Object> header, T data) {
        RestData<T> response = new RestData<>(data);
        Response.ResponseBuilder builder = Response.status(status.value()).entity(response);
        if (header != null) {
            for (String key : header.keySet()) {
                for (Object value : header.get(key)) {
                    builder.header(key, value);
                }
            }
        }
        return builder.build();
    }

    public static <T> Response error(HttpStatus status, T message) {
        RestData<T> response = RestData.error(message);
        return Response.status(status.value()).entity(response).build();
    }
}
