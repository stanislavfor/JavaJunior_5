package org.example;

public class DisconnectRequest extends ListRequest {
    public static final String TYPE = "disconnect";

    public DisconnectRequest() {
        super(TYPE);
    }
}
