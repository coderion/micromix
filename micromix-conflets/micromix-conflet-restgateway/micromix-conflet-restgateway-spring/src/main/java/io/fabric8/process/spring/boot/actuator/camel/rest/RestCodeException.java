package io.fabric8.process.spring.boot.actuator.camel.rest;

/**
 * Copyright (C) Coderion
 */
public class RestCodeException extends Exception {

    private String messageInfo;
    private String code;

    public RestCodeException(String code) {
        super(code);
        this.code = code;
    }

    public RestCodeException(String code, String messageInfo) {
        super(code);
        this.messageInfo = messageInfo;
        this.code = code;
    }

    public String getMessageInfo() {
        return messageInfo != null ? messageInfo : "";
    }

    public String getCode() {
        return code != null ? code : "";
    }
}
