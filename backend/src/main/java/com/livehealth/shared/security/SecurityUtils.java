package com.livehealth.shared.security;

import com.livehealth.shared.constant.ErrorMessage;
import com.livehealth.shared.exception.VsException;
import com.livehealth.shared.base.HttpStatus;
import io.quarkus.arc.Arc;
import java.util.UUID;

public class SecurityUtils {

    public static UUID getCurrentUserId() {
        var instance = Arc.container().instance(CurrentRequestUser.class);
        if (instance.isAvailable()) {
            CurrentRequestUser user = instance.get();
            if (user.getUserId() != null) {
                return user.getUserId();
            }
        }
        throw new VsException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.ERR_TOKEN_INVALIDATED);
    }

    public static String getCurrentEmail() {
        var instance = Arc.container().instance(CurrentRequestUser.class);
        if (instance.isAvailable()) {
            CurrentRequestUser user = instance.get();
            return user.getEmail();
        }
        return null;
    }
}
