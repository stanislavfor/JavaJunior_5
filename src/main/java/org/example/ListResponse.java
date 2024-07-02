package org.example;

import lombok.Getter;

import java.util.List;

/**
 * {
 *   "users": [
 *     {
 *       "login": "anonymous"
 *     },
 *     {
 *       "login": "nagibator"
 *     },
 *     {
 *       "login": "admin"
 *     }
 *   ]
 * }
 */
@Getter

public class ListResponse extends ListRequest {

    private List<User> users;

    public void setUsers(List<User> users) {

        this.users = users;
    }
}
