package com.livehealth.shared.exception;

import lombok.Getter;
import lombok.Setter;
import com.livehealth.shared.base.HttpStatus;

@Getter
@Setter
public class InternalServerException extends RuntimeException {

    private String message;

    private HttpStatus status;

    private String[] params;

    public InternalServerException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = message;
    }

    public InternalServerException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public InternalServerException(String message, String[] params) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = message;
        this.params = params;
    }

    public InternalServerException(HttpStatus status, String message, String[] params) {
        super(message);
        this.status = status;
        this.message = message;
        this.params = params;
    }

}