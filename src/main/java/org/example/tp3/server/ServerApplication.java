package org.example.tp3.server;

public class ServerApplication {
    public static final int FULL_ITERATIONS_COUNT = 2;

    public static void main(String[] args) {
        ServerFacade serverFacade = new ServerFacade();
        serverFacade.run();
    }
}
