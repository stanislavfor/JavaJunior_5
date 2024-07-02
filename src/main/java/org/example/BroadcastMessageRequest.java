package org.example;

import lombok.Getter;

@Getter
public class BroadcastMessageRequest extends ListRequest {

    public static final String TYPE = "broadcast";

    private String message;

    public BroadcastMessageRequest() {
        super(TYPE);
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

