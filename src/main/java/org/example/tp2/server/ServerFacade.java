package org.example.tp2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerFacade {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final DataCoordinator dataCoordinator = new DataCoordinator();

    public void run() {
        int clientNumber = 1;
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (clientNumber != 5) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(dataCoordinator, clientSocket, clientNumber++);
                executorService.execute(clientHandler);
            }

            awaitTerminationAfterShutdown(executorService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void awaitTerminationAfterShutdown(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(24, TimeUnit.HOURS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
