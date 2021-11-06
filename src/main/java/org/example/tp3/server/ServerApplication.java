package org.example.tp3.server;

public class ServerApplication {
    public static final int FULL_ITERATIONS_COUNT = 1;
    public static final String STATE_FILE_PATH = "./state.json";
    public static final String JSON_SCHEMA_FILE_PATH = "./schema.json";

    public static void main(String[] args) {
        ServerFacade serverFacade = new ServerFacade();
        serverFacade.run();
    }
}
