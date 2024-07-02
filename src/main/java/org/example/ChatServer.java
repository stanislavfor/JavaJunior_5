package org.example;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


public class ChatServer {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    // Socket - абстракция, к которой можно подключиться
    // ip-address + port - socket
    // network - сеть - набор соединенных устройств
    // ip-address - это адрес устройства в какой-то сети
    // 8080 - http
    // 443 - https
    // 35 - smtp
    // 21 - ftp
    // 5432 - стандартный порт postgres
    // клиент подключается к серверу

    /**
     * Порядок взаимодействия:
     * 1. Клиент подключается к серверу
     * 2. Клиент посылает сообщение, в котором указан логин. Если на сервере уже есть подключеный клиент с таким логином, то соедение разрывается
     * 3. Клиент может посылать 3 типа команд:
     * 3.1 list - получить логины других клиентов
     * <p>
     * 3.2 send @login message - отправить личное сообщение с содержимым message другому клиенту с логином login
     * 3.3 send message - отправить сообщение всем с содержимым message
     */

    // 1324.132.12.3:8888
    public static void main(String[] args) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
        try (ServerSocket server = new ServerSocket(8888)) {
            System.out.println("Сервер запущен");
            while (true) {
                System.out.println("Ждем подключения пользователей");
                Socket client = server.accept();
                ClientHandler clientHandler = new ClientHandler(client, clients);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Ошибка во время работы сервера : " + e.getMessage());
        }
    }




    private static class ClientHandler implements Runnable {

        private final Socket client;
        private final Scanner in;
        private final PrintWriter out;
        private final Map<String, ClientHandler> clients;
        private String clientLogin;

        public ClientHandler(Socket client, Map<String, ClientHandler> clients) throws IOException {
            this.client = client;
            this.clients = clients;
            this.in = new Scanner(client.getInputStream());
            this.out = new PrintWriter(client.getOutputStream(), true);
        }



        @Override
        public void run() {
            System.out.println("Подключен новый пользователь");
            try {
                String loginRequest = in.nextLine();
                LoginRequest request = objectMapper.reader().readValue(loginRequest, LoginRequest.class);
                this.clientLogin = request.getLogin();
            } catch (IOException e) {
                System.err.println("Не удалось прочитать сообщение от [" + clientLogin + "]: " + e.getMessage());
                String unsuccessfulResponse = createLoginResponse(false);
                out.println(unsuccessfulResponse);
                doClose();
                return;
            }
            System.out.println("Запрос от " + clientLogin);
            if (clients.containsKey(clientLogin)) {
                String unsuccessfulResponse = createLoginResponse(false);
                out.println(unsuccessfulResponse);
                doClose();
                return;
            }
            clients.put(clientLogin, this);
            String successfulLoginResponse = createLoginResponse(true);
            out.println(successfulLoginResponse);

            label:
            while (true) {
                try {
                    if (!in.hasNextLine()) {
                        break;
                    }
                    String msgFromClient = in.nextLine();
                    final String type;
                    try {
                        ListRequest request = objectMapper.reader().readValue(msgFromClient, ListRequest.class);
                        type = request.getType();
                    } catch (IOException e) {
                        System.err.println("Не удалось прочитать сообщение от [" + clientLogin + "]: " + e.getMessage());
                        sendMessage("Не удалось прочитать сообщение от " + clientLogin + e.getMessage());
                        continue;
                    }
                    // Добавили код
                    switch (type) {
                        case SendMessageRequest.TYPE:
                            SendMessageRequest(msgFromClient);
                            break;
                        case BroadcastMessageRequest.TYPE:
                            BroadcastMessageRequest(msgFromClient);
                            break;
                        case UsersRequest.TYPE:
                            UsersRequest();
                            break;
                        case DisconnectRequest.TYPE:
                            DisconnectRequest();
                            break label;
                        case null:
                        default:
                            System.err.println("Неизвестный тип сообщения : " + type);
                            sendMessage("Неизвестный тип сообщения : " + type);
                            break;
                    }
                } catch (NoSuchElementException e) {
                    System.err.println("Пользователь [" + clientLogin + "] отключился");
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            doClose();
        }

        private void SendMessageRequest(String msgFromClient) throws IOException {
            SendMessageRequest request = objectMapper.reader().readValue(msgFromClient, SendMessageRequest.class);
            ClientHandler clientTo = clients.get(request.getRecipient());
            if (clientTo == null) {
                sendMessage("Пользователь с логином [" + request.getRecipient() + "] не найден");
                return;
            }
            clientTo.sendMessage(request.getMessage());
        }

        private void BroadcastMessageRequest(String msgFromClient) throws IOException {
            BroadcastMessageRequest request = objectMapper.reader().readValue(msgFromClient, BroadcastMessageRequest.class);
            String message = request.getMessage();
            for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
                if (!entry.getKey().equals(clientLogin)) {
                    entry.getValue().sendMessage(message);
                }
            }
        }

        private void UsersRequest() throws JsonProcessingException {
            String users;
            users = String.join(", ", clients.keySet());
            sendMessage("Список пользователей : " + users);
        }

        private void DisconnectRequest() {
            sendMessage("Вы отключены");
            final var remove = clients.remove(clientLogin);
        }

        private void doClose() {
            try {
                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.err.println("Ошибка во время отключения клиента : " + e.getMessage());
            }
        }

        public void sendMessage(String message) {
            // TODO: нужно придумать структуру сообщения
            try {
                String messageJson;
                messageJson = objectMapper.writeValueAsString(message);
                out.println(messageJson);
            } catch (JsonProcessingException e) {
                System.err.println("Ошибка сериализации сообщения: " + e.getMessage());
            }
//            out.println(message);
        }

        private String createLoginResponse(boolean success) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setConnected(success);
            try {
                return objectMapper.writer().writeValueAsString(loginResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Не удалось создать loginResponse : " + e.getMessage());
            }
        }

    }

}
