package com.livehealth.shared.exception;

import lombok.Getter;
import lombok.Setter;
import com.livehealth.shared.base.HttpStatus;

@Getter
@Setter
public class ForbiddenException extends RuntimeException {

    private String message;

    private HttpStatus status;

    private String[] params;

    public ForbiddenException(String message) {
        super(message);
        this.status = HttpStatus.FORBIDDEN;
        this.message = message;
    }

    public ForbiddenException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public ForbiddenException(String message, String[] params) {
        super(message);
        this.status = HttpStatus.FORBIDDEN;
        this.message = message;
        this.params = params;
    }

    public ForbiddenException(HttpStatus status, String message, String[] params) {
        super(message);
        this.status = status;
        this.message = message;
        this.params = params;
    }

}
