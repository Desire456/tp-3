package org.example.tp3.server;

import com.google.gson.Gson;
import org.example.tp3.IOUtils;
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
    private final PersistentStateHandler persistentStateHandler;

    public ClientHandler(DataCoordinator dataCoordinator, Socket clientSocket, int clientNumber,
                         PersistentStateHandler persistentStateHandler) {
        this.dataCoordinator = dataCoordinator;
        this.clientSocket = clientSocket;
        this.clientNumber = clientNumber;
        this.persistentStateHandler = persistentStateHandler;
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
                        String content = Optional.ofNullable(dataCoordinator.getLastMessage())
                                .map(Message::getContent)
                                .orElse("-");
                        IOUtils.sendMessage(content, outputStream, IOUtils.CLIENT_NAME);

                        Message serverMessage = IOUtils.readMessage(inputStream, IOUtils.CLIENT_NAME);
                        if (serverMessage.getContent().equals(IOUtils.GET_STATE_COMMAND)) {
                            IOUtils.sendMessage(persistentStateHandler.getPersistentStateJson(), outputStream,
                                    IOUtils.CLIENT_NAME);
                        } else {
                            dataCoordinator.addMessage(serverMessage);
                            System.out.println("Get message from client " + clientNumber + ": " +
                                    serverMessage.getContent());
                            dataCoordinator.incrementIterationsCount();
                        }
                        break;
                    }
                    case WAIT: {
                        Thread.sleep(3000);
                        break;
                    }
                    case EXIT: {
                        IOUtils.sendMessage("EXIT", outputStream, IOUtils.CLIENT_NAME);
                        IOUtils.sendMessage(dataCoordinator.getAnswer(), outputStream, IOUtils.CLIENT_NAME);
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
}
