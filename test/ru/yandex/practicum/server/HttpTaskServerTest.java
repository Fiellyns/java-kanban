package ru.yandex.practicum.server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.taskmanager.HttpTaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.utils.Formatting;
import ru.yandex.practicum.utils.Status;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    protected KVServer kvServer;
    private HttpTaskServer server;
    private static final String url = "http://localhost:8078";
    private static final String urlServer = "http://localhost:8080";
    private Gson gson;

    private Task task;
    private Subtask subtask;
    private Epic epic;
    private HttpTaskManager httpTaskManager;
    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

    protected Task newTask() {
        return new Task("Отдых", "поехать на море",
                Status.NEW, 1L,
                Instant.ofEpochMilli(1700492000L));
    }

    protected Epic newEpic() {
        return new Epic("Дом", "любимый дом",
                Status.NEW, 1L,
                Instant.ofEpochMilli(1700492500L));
    }

    protected Subtask newSubtask(Epic epic) {
        return new Subtask("Купить молоко", "3.2%",
                Status.NEW, 1L,
                Instant.ofEpochMilli(1700683200L), epic.getId());
    }

    @BeforeEach
    void startServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskManager = (HttpTaskManager) Managers.getDefault(url);
        server = new HttpTaskServer(httpTaskManager);

        gson = Formatting.createGson();
        task = httpTaskManager.createTask(newTask());
        epic = httpTaskManager.createEpic(newEpic());
        subtask = httpTaskManager.createSubTask(newSubtask(epic));
        httpTaskManager.getTaskById(task.getId());
        httpTaskManager.getEpicById(epic.getId());
        httpTaskManager.getSubTaskById(subtask.getId());
        server.start();
    }

    @AfterEach
    void serverStop() {
        server.stop();
        kvServer.stop();
    }


    @Test
    void shouldGETTask() throws  IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/task/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        JsonElement jsonElement = JsonParser.parseString(response.body());

        Type taskType = new TypeToken<List<Task>>(){}.getType();

        List<Task> task = gson.fromJson(jsonElement, taskType);

        assertEquals(200, response.statusCode());
        assertEquals(1, task.size());
    }

    @Test
    void shouldGETTaskById() throws  IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/task/?id=" + task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int jsonId = jsonObject.get("id").getAsInt();
        String jsonDescription = jsonObject.get("description").getAsString();
        assertEquals(200, response.statusCode());
        assertTrue(jsonElement.isJsonObject());
        assertEquals(task.getId(), jsonId);
        assertEquals(task.getDescription(), jsonDescription);
    }

    @Test
    void shouldPOSTTask() throws IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/task/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(task.toString(), httpTaskManager.getTaskById(task.getId()).toString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldDELETETask() throws IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все задачи удалены.", response.body());
    }

    @Test
    void shouldGETEpic() throws  IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/epic/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        JsonElement jsonElement = JsonParser.parseString(response.body());

        Type taskType = new TypeToken<List<Epic>>(){}.getType();

        List<Task> epic = gson.fromJson(jsonElement, taskType);

        assertEquals(200, response.statusCode());
        assertEquals(1, epic.size());
    }

    @Test
    void shouldGETEpicById() throws  IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/epic/?id=" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int jsonId = jsonObject.get("id").getAsInt();
        String jsonDescription = jsonObject.get("description").getAsString();
        assertEquals(200, response.statusCode());
        assertTrue(jsonElement.isJsonObject());
        assertEquals(epic.getId(), jsonId);
        assertEquals(epic.getDescription(), jsonDescription);
    }

    @Test
    void shouldPOSTEpic() throws IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(epic.toString(), httpTaskManager.getEpicById(epic.getId()).toString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldDELETEEpic() throws IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все эпики удалены.", response.body());
    }

    @Test
    void shouldGETSubTask() throws  IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/subtask/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        JsonElement jsonElement = JsonParser.parseString(response.body());

        Type taskType = new TypeToken<List<Subtask>>(){}.getType();

        List<Task> subtask = gson.fromJson(jsonElement, taskType);

        assertEquals(200, response.statusCode());
        assertEquals(1, subtask.size());
    }

    @Test
    void shouldGETSubTaskById() throws  IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/subtask/epic/?id=" + subtask.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int jsonId = jsonObject.get("id").getAsInt();
        String jsonDescription = jsonObject.get("description").getAsString();
        assertEquals(200, response.statusCode());
        assertTrue(jsonElement.isJsonObject());
        assertEquals(subtask.getId(), jsonId);
        assertEquals(subtask.getDescription(), jsonDescription);
    }

    @Test
    void shouldPOSTSubTask() throws IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/subtask/");
        String json = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(subtask.toString(), httpTaskManager.getSubTaskById(subtask.getId()).toString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldDELETESubTask() throws IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все подзадачи удалены.", response.body());
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/history/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertTrue(jsonElement.isJsonArray());
        assertEquals(3, jsonArray.size());
    }

    @Test
    void shouldGETPrioritized() throws IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/tasks/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertEquals(200, response.statusCode());
        assertTrue(jsonElement.isJsonArray());
        assertEquals(2, jsonElement.getAsJsonArray().size());
    }


}