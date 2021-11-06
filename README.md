# Описание

- Номер лабораторной работы - 3
- Вариант - 6

# Описание задания

Добавьте в свой предыдущий проект возможность сохранения состояния
в виде периодического сохранения, либо в виде функций импорта и экспорта.
Выбранный формат для сериализации должен иметь схему. В проекте
обязателен код валидирующий данные. Валидация должна производиться
либо в программе при импорте данных, лбои в юнит тестах, проверяющих
корректность сохранения состояния.

# Описание алгоритма

Была добавлена возможность сохранения истории игр. Если клиент
хочет получить историю ему нужно отправить сообщение "GET_STATE".
Перед завершением сервер валидирует историю игр по schema.json и
сохраняет. Если что-то не так то остается предыдущая история.

schema.json:
```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "type": "object",
  "properties": {
    "state": {
      "type": "array",
      "items": [
        {
          "type": "object",
          "properties": {
            "answer": {
              "type": "string"
            }
          },
          "required": [
            "answer"
          ]
        }
      ]
    }
  },
  "required": [
    "state"
  ]
}
```

Валидатор:
```java
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
```

Код сохранения и валидации данных:
```java
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
```
