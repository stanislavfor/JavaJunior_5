package org.example;


import lombok.Getter;

/**
 * {
 *   "type": "sendMessage",
 *   "recipient: "nagibator",
 *   "message": "text to nagibator"
 * }
 */
@Getter
public class SendMessageRequest extends ListRequest {

    public static final String TYPE = "sendMessage";

    private String recipient;
    private String message;

    public SendMessageRequest() {
        setType(TYPE);
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
