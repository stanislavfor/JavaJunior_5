package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
//import java.time.Duration;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.function.Consumer;
import java.util.Scanner;
//import java.util.UUID;

public class ChatClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
//        String clientLogin = "User_" + UUID.randomUUID().toString();
//        String clientLogin = "user2";

        Scanner console = new Scanner(System.in);
        System.out.println("Введите логин клиента : ");
        String clientLogin = console.nextLine();

        // 127.0.0.1 или localhost
        try (Socket server = new Socket("localhost", 8888)) {
            System.out.println("Успешно подключились к серверу");

            try (PrintWriter out = new PrintWriter(server.getOutputStream(), true)) {
                Scanner in = new Scanner(server.getInputStream());
                String loginRequest = createLoginRequest(clientLogin);
                out.println(loginRequest);
                String loginResponseString = in.nextLine();
                if (!checkLoginResponse(loginResponseString)) {
                    // TODO: Можно обогатить причиной, чтобы клиент получал эту причину
                    // (логин уже занят, ошибка аутентификации\авторизации, ...)
                    System.out.println("Не удалось подключиться к серверу");
                    return;
                }

                // client <----------------> server
                // client getUsers ->        server
                // client <- (getUsers|sendMessage from client) server    <--------sendMessage client2
                //

                // Отдельный поток на чтение сообщений
//        ServerListener serverListener = new ServerListener(in);
//        new Thread(serverListener).start();


                // перенесли в Homework
                new Thread(() -> {
                    while (true) {
                        // TODO: парсим сообщение в AbstractRequest
                        //  по полю type понимаем, что это за request, и обрабатываем его нужным образом
                        String msgFromServer = in.nextLine();
                        System.out.println("Сообщение от сервера : " + msgFromServer);
                    }
                }).start();


                label:
                while (true) {
                    System.out.println("Что сделать? от имени '" + clientLogin + "' переключение Enter");
                    System.out.println("1. Послать сообщение другу");
                    System.out.println("2. Послать сообщение всем");
                    System.out.println("3. Получить список (пользователей)");
                    System.out.println("4. Отключиться");
//                    System.out.println("5. Прочитать сообщения от сервера");

                    String type = console.nextLine();
                    switch (type) {
                        case "1" -> {
                            // TODO: считываете с консоли логин, кому отправить
                            SendMessageRequest request = new SendMessageRequest();
                            System.out.println("Введите логин получателя : ");
                            request.setRecipient(console.nextLine());
                            System.out.println("Введите сообщение : ");
                            request.setMessage(console.nextLine());
                            String sendMsgRequest = objectMapper.writeValueAsString(request);
                            out.println(sendMsgRequest);

                        }
                        case "2" -> {
                            BroadcastMessageRequest request = new BroadcastMessageRequest();
                            System.out.println("Введите сообщение : ");
                            request.setMessage(console.nextLine());
                            String broadcastMsgRequest = objectMapper.writeValueAsString(request);
                            out.println(broadcastMsgRequest);
                        }
                        case "3" -> {
                            UsersRequest request = new UsersRequest();
                            String usersRequest = objectMapper.writeValueAsString(request);
                            out.println(usersRequest);
                        }
                        case "4" -> {
                            DisconnectRequest request = new DisconnectRequest();
                            String disconnectRequest = objectMapper.writeValueAsString(request);
                            out.println(disconnectRequest);
                            break label;
                        }
//                        case "5" -> {
//                            System.out.println("Сообщения от сервера : ");
//                            while (in.hasNextLine()) {
//                                String serverMessage = in.nextLine();
//                                System.out.println(serverMessage);
//                            }
//                        }
                    }
//            serverListener.subscribe("get users", new Consumer<String>() {
//              @Override
//              public void accept(String s) {
//                System.out.println("Список юзеров: " + s);
//              }
//            });



                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка во время подключения к серверу : " + e.getMessage());
        }

        System.out.println("Отключились от сервера");
    }

    private static String createLoginRequest(String login) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(login);

        try {
            return objectMapper.writeValueAsString(loginRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка JSON : " + e.getMessage());
        }
    }

    private static boolean checkLoginResponse(String loginResponse) {
        try {
            LoginResponse resp = objectMapper.reader().readValue(loginResponse, LoginResponse.class);
            return resp.isConnected();
        } catch (IOException e) {
            System.err.println("Ошибка чтения JSON : " + e.getMessage());
            return false;
        }
    }

//    private static void readMessages(Scanner in) {
//        System.out.println("Чтение сообщений от сервера");
//        while (in.hasNextLine()) {
//            String msgFromServer = in.nextLine();
//            System.out.println("Сообщение от сервера : " + msgFromServer);
//        }
//    }

//    private static void sleep() {
//        try {
//            Thread.sleep(Duration.ofMinutes(5));
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

//  private static class ServerListener implements Runnable {
//    private final Scanner in;
//    private final Map<String, List<Consumer<String>>> subscribers = new ConcurrentHashMap<>();
//
//    public ServerListener(Scanner in) {
//      this.in = in;
//    }
//
//    public void subscribe(String type, Consumer<String> consumer) {
//      List<Consumer<String>> consumers = subscribers.getOrDefault(type, new ArrayList<>());
//      consumers.add(consumer);
//      subscribers.put(type, consumers);
//    }
//
//    @Override
//    public void run() {
//      while (true) {
//        String msgFromServer = in.nextLine();
//
//        // TODO: парсим сообщение в AbstractRequest
//        //  по полю type понимаем, что это за request, и обрабатываем его нужным образом
//        String type = null;
//
//        subscribers.getOrDefault(type, List.of()).forEach(it -> {
//          it.accept(msgFromServer);
//        });
//        subscribers.remove(type);
//
//        System.out.println("Сообщение от сервера: " + msgFromServer);
//      }
//    }
//  }

}
