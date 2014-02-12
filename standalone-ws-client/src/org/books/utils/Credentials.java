package org.books.utils;

public class Credentials {
    private String signature;
    private String associateTag;
    private String timestamp;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAssociateTag() {
        return associateTag;
    }

    public void setAssociateTag(String associateTag) {
        this.associateTag = associateTag;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
