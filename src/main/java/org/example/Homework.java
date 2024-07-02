package org.example;


import java.util.HashMap;
import java.util.Map;

public class Homework {

    /**
     * 0. Осознать код, который мы написали на уроке.
     * При появлении вопросов, пишем в общий чат в телеграме.
     * 1. По аналогии с командой отправки сообщений, реализовать следующие команды:
     * 1.1 BroadcastMessageRequest - послать сообщение ВСЕМ пользователям (кроме себя)
     * 1.2 UsersRequest - получить список всех логинов, которые в данный момент есть в чате (в любом формате)
     * 1.3 DisconnectRequest - клиент оповещает сервер о том, что он отключился
     * 1.3.1 * Дополнительное задание: При отключении юзера, делать рассылку на остальных
     * <p>
     * Можно сделать только один пункт из 1.1-1.3.
     */


    public static void main(String[] args) {

        new Thread(() -> {
            ChatServer.main(args);
        }).start();


        new Thread(() -> {
            String[] clientArgs = {"user1"};
            ChatClient.main(clientArgs);
        }).start();


        new Thread(() -> {
            String[] clientArgs = {"user2"};
            ChatClient.main(clientArgs);
        }).start();


    }

//    -----


//    public interface Command {
//        String execute();
//    }
//
//
//    public static class BroadcastMessageCommand implements Command {
//        private final String message;
//        private final Server server;
//
//        public BroadcastMessageCommand(String message, Server server) {
//            this.message = message;
//            this.server = server;
//        }
//
//        @Override
//        public String execute() {
//            return server.broadcastMessage(message);
//        }
//    }
//
//    public static class UsersRequestCommand implements Command {
//        private final Server server;
//
//        public UsersRequestCommand(Server server) {
//            this.server = server;
//        }
//
//        @Override
//        public String execute() {
//            return server.getUsers();
//        }
//    }
//
//    public static class DisconnectRequestCommand implements Command {
//        private final String username;
//        private final Server server;
//
//        public DisconnectRequestCommand(String username, Server server) {
//            this.username = username;
//            this.server = server;
//        }
//
//        @Override
//        public String execute() {
//            String disconnectMessage = server.disconnectUser(username);
//            server.broadcastMessage(disconnectMessage);
//            return disconnectMessage;
//        }
//    }
//
//
//    public static class Server {
//        private final Map<String, User> users;
//
//        public Server() {
//            this.users = new HashMap<>();
//        }
//
//
//        public void addUser(String username) {
//            if (!users.containsKey(username)) {
//                users.put(username, new User(username));
//            }
//        }
//
//
//        public void removeUser(String username) {
//            users.remove(username);
//        }
//
//
//        public String broadcastMessage(String message) {
//            StringBuilder messageBuilder = new StringBuilder();
//            for (User user : users.values()) {
//                if (!user.getUsername().equals(message.split(" ")[0])) {
//                    user.sendMessage(message);
//                    messageBuilder.append("Отправка сообщения для ").append(user.getUsername()).append("\n");
//                }
//            }
//            return messageBuilder.toString();
//        }
//
//
//        public String getUsers() {
//            StringBuilder userListBuilder = new StringBuilder();
//            for (String username : users.keySet()) {
//                userListBuilder.append(username).append("\n");
//            }
//            return userListBuilder.toString().trim();
//        }
//
//
//        public String disconnectUser(String username) {
//            if (users.containsKey(username)) {
//                removeUser(username);
//                return username + " отключился.";
//            } else {
//                return "Пользователь " + username + " не найден.";
//            }
//        }
//    }
//
//
//    public static class User {
//        private final String username;
//
//        public User(String username) {
//            this.username = username;
//        }
//
//        public String getUsername() {
//            return username;
//        }
//
//
//        public void sendMessage(String message) {
//            System.out.println("Сообщение для " + username + ": " + message);
//        }
//    }
//
//    public static void main(String[] args) {
//            Server server = new Server();
//            server.addUser("user3");
//            server.addUser("user4");
//            server.addUser("user5");
//            String message = "'Сообщение для всех'";
//            Command broadcastCommand = new BroadcastMessageCommand(message, server);
//            System.out.println(broadcastCommand.execute());
//
//            String userRequest = new UsersRequestCommand(server).execute();
//            System.out.println("Пользователи в системе : \n" + userRequest);
//
//            String username = "user6";
//            Command disconnectCommand = new DisconnectRequestCommand(username, server);
//            System.out.println(disconnectCommand.execute());
//        }


}



