package org.example.tp2.client;

import com.google.gson.Gson;
import org.example.tp2.Message;

import java.io.*;
import java.net.Socket;

public class ClientFacade {

    private final Gson gson = new Gson();

    public void run() {
        try (Socket socket = new Socket("localhost", 8080);
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
             DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Client connected to server");
            while (true) {
                Message message = readMessage(inputStream);
                if (message.getContent().equals("EXIT")) {
                    Message answer = readMessage(inputStream);
                    System.out.println("The answer: \n" + answer.getContent());
                    System.out.println("Client close the connection");
                    break;
                }
                if (!message.getContent().equals("-")) {
                    System.out.println("Previous line: " + message.getContent());
                }
                System.out.print("Write the line to server: ");
                String lineToSend = consoleReader.readLine();
                Message serverMessage = new Message(lineToSend);
                String serverMessageJson = gson.toJson(serverMessage);
                outputStream.writeUTF(serverMessageJson);
                System.out.println("Send message to server: " + serverMessageJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Message readMessage(DataInputStream inputStream) throws IOException {
        String messageJson = inputStream.readUTF();
        System.out.println("Get message from server: " + messageJson);
        return gson.fromJson(messageJson, Message.class);
    }
}
