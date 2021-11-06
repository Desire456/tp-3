package org.example.tp3.server;

import com.google.gson.Gson;
import org.example.tp3.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class ClientHandler implements Runnable {

    private final Gson gson = new Gson();

    private final DataCoordinator dataCoordinator;
    private final Socket clientSocket;
    private final int clientNumber;

    public ClientHandler(DataCoordinator dataCoordinator, Socket clientSocket, int clientNumber) {
        this.dataCoordinator = dataCoordinator;
        this.clientSocket = clientSocket;
        this.clientNumber = clientNumber;
    }

    @Override
    public void run() {
        System.out.println("Initialize io streams for messaging");
        boolean exit = false;
        try (DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
             DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream())) {
            while (!exit) {
                switch (dataCoordinator.getState(clientNumber)) {
                    case WORK: {
                        Message message = new Message(
                                Optional.ofNullable(dataCoordinator.getLastMessage())
                                        .map(Message::getContent)
                                        .orElse("-")
                        );
                        String clientMessageJson = gson.toJson(message);
                        outputStream.writeUTF(clientMessageJson);
                        System.out.println("Send message to client: " + message.getContent());

                        String serverMessageJson = inputStream.readUTF();
                        Message serverMessage = gson.fromJson(serverMessageJson, Message.class);
                        dataCoordinator.addMessage(serverMessage);
                        System.out.println("Get message from client " + clientNumber + " : " + serverMessageJson);
                        dataCoordinator.incrementIterationsCount();
                        break;
                    }
                    case WAIT: {
                        Thread.sleep(3000);
                        break;
                    }
                    case EXIT: {
                        sendMessage("EXIT", outputStream);
                        sendMessage(dataCoordinator.getAnswer(), outputStream);
                        exit = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String content, DataOutputStream outputStream) throws IOException {
        Message message = new Message(content);
        String clientMessageJson = gson.toJson(message);
        outputStream.writeUTF(clientMessageJson);
        System.out.println("Send message to client: " + clientMessageJson);
    }

}
