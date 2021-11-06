package org.example.tp3;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IOUtils {

    public static final String SERVER_NAME = "server";
    public static final String CLIENT_NAME = "client";
    public static final String GET_STATE_COMMAND = "GET_STATE";
    private static final Gson gson = new Gson();

    private IOUtils() {
    }

    public static Message readMessage(DataInputStream inputStream, String from) throws IOException {
        String messageJson = inputStream.readUTF();
        System.out.println("Get message from " + from + ": " + messageJson);
        return gson.fromJson(messageJson, Message.class);
    }

    public static void sendMessage(String content, DataOutputStream outputStream, String to) throws IOException {
        Message message = new Message(content);
        String clientMessageJson = gson.toJson(message);
        outputStream.writeUTF(clientMessageJson);
        System.out.println("Send message to " + to + ": " + clientMessageJson);
    }
}
