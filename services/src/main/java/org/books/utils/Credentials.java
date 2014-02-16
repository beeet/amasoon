package org.books.utils;

public class Credentials {

    private String signature;
    private String accessKeyId;
    private String timestamp;

    public String getSignature() {
        return signature;
    }

    void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
