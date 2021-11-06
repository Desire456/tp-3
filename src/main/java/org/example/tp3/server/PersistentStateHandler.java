package org.example.tp3.server;

import com.google.gson.Gson;
import org.example.tp3.PersistentState;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class PersistentStateHandler {

    private final PersistentState persistentState;
    private final String previousState;
    private final File file;
    private final Gson gson = new Gson();

    public PersistentStateHandler() throws IOException {
        file = new File(ServerApplication.STATE_FILE_PATH);
        if (file.createNewFile()) {
            previousState = "";
            persistentState = new PersistentState();
            file.createNewFile();
        } else {
            previousState = String.join("\n", Files.readAllLines(file.toPath()));
            persistentState = gson.fromJson(previousState, PersistentState.class);
        }

    }

    public void addNewStateUnit(PersistentState.PersistentStateUnit stateUnit) {
        persistentState.addNewStateUnit(stateUnit);
    }

    public boolean saveState() {
        String jsonState = gson.toJson(persistentState);
        JsonSchemaValidator validator = new JsonSchemaValidator();
        if (validator.validate(jsonState)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                writer.write(jsonState);
                writer.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getPersistentStateJson() {
        return gson.toJson(persistentState);
    }
}
