package org.example;


import lombok.Getter;

/**
 * {
 *   "connected": true
 * }
 */
@Getter
public class LoginResponse {

    private boolean connected;

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
