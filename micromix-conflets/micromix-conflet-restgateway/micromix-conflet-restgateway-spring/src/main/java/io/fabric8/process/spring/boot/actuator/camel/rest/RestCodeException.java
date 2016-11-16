package io.fabric8.process.spring.boot.actuator.camel.rest;

/**
 * Copyright (C) Coderion
 */
public class RestCodeException extends Exception {

    private String messageInfo;

    public RestCodeException(String code) {
        super(code);
    }

    public RestCodeException(String code, String messageInfo) {
        super(code);
        this.messageInfo = messageInfo;
    }

    public String getMessageInfo() {
        return messageInfo != null ? messageInfo : "";
    }
}
