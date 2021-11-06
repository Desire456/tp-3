package org.example.tp3.client;

import com.google.gson.Gson;
import org.example.tp3.IOUtils;
import org.example.tp3.Message;
import org.example.tp3.PersistentState;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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
                Message message = IOUtils.readMessage(inputStream, IOUtils.SERVER_NAME);
                if (message.getContent().equals("EXIT")) {
                    Message answer = IOUtils.readMessage(inputStream, IOUtils.SERVER_NAME);
                    System.out.println("The answer: \n" + answer.getContent());
                    System.out.println("Client close the connection");
                    break;
                }
                if (!message.getContent().equals("-")) {
                    System.out.println("Previous line: " + message.getContent());
                }
                System.out.print("Write the line to server: ");
                String lineToSend = consoleReader.readLine();
                IOUtils.sendMessage(lineToSend, outputStream, IOUtils.SERVER_NAME);

                if (lineToSend.equals(IOUtils.GET_STATE_COMMAND)) {
                    Message messageState = IOUtils.readMessage(inputStream, IOUtils.SERVER_NAME);
                    PersistentState persistentState = gson.fromJson(messageState.getContent(), PersistentState.class);
                    System.out.println("State: \n" + persistentState.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
