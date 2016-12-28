package io.fabric8.process.spring.boot.actuator.camel.rest;

import java.io.Serializable;

/**
 * Copyright (C) Coderion
 */
public class RestCodeResponse implements Serializable {

    private String code;
    private String message;

    public RestCodeResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
