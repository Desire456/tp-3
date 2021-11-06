package org.example.tp3.server;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.util.Objects;

public class JsonSchemaValidator {

    private Schema schema;

    public JsonSchemaValidator() {
        try (FileInputStream inputStream = new FileInputStream(ServerApplication.JSON_SCHEMA_FILE_PATH)) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(Objects.requireNonNull(inputStream)));
            this.schema = SchemaLoader.load(rawSchema);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validate(String jsonObject) {
        try {
            schema.validate(new JSONObject(jsonObject));
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }
}
