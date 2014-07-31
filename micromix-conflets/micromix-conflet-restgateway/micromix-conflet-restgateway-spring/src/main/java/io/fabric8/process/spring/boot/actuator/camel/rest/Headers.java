package io.fabric8.process.spring.boot.actuator.camel.rest;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    private static final ThreadLocal<Map<String, String>> headers = new ThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<String, String>();
        }
    };

    public static void clear() {
        headers.get().clear();
    }

    public static Map<String, String> headers() {
        return new HashMap<String, String>(headers.get());
    }

    public static void contentDisposition(String contentDisposition) {
        headers.get().put("Content-disposition", contentDisposition);
    }

    public static String contentDisposition() {
        return headers.get().get("Content-disposition");
    }

    public static void contentDispositionFilename(String filename) {
        contentDisposition(String.format("filename=\"%s\"", filename));
    }

    public static void contentType(String contentType) {
        headers.get().put("Content-Type", contentType);
    }

    public static String contentType() {
        return headers.get().get("Content-Type");
    }

}