package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.manager.taskmanager.TaskManager;
import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.utils.Endpoint;
import ru.yandex.practicum.utils.Formatting;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final Gson gson;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", new Handler(taskManager));
        gson = Formatting.createGson();
    }

    public void start() {
        System.out.println("Запускаем сервер на " + PORT + " порту");
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    private class Handler implements HttpHandler {
        private final TaskManager taskManager;

        public Handler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            String url = exchange.getRequestURI().toString();
            String[] urlParts = url.split("/");

            Endpoint endpoint = getEndpoint(requestMethod, url);

            switch (endpoint) {
                case GET_TASKS:
                    handleGetTasks(exchange, urlParts[2]);
                    break;
                case GET_PRIORITIZED:
                    handleGetPrioritized(exchange);
                    break;
                case GET_HISTORY:
                    handleGetHistory(exchange);
                    break;
                case GET_BY_ID:
                    handleGetById(exchange, urlParts);
                    break;
                case DELETE_TASKS:
                    handleDeleteTasks(exchange, urlParts[2]);
                    break;
                case DELETE_BY_ID:
                    handleDeleteById(exchange, urlParts);
                    break;
                case POST_TASK:
                    handlePostTask(exchange);
                    break;
                default:
                    writeResponse(exchange, "Неправильный запрос", 404);
            }
        }

        private Endpoint getEndpoint(String requestMethod, String url) {
            String[] urlParts = url.split("/");
            if (requestMethod.equals("GET")) {
                switch (url) {
                    case "/tasks/":
                        return Endpoint.GET_PRIORITIZED;
                    case "/tasks/history/":
                        return Endpoint.GET_HISTORY;
                    case "/tasks/task/":
                    case "/tasks/subtask/":
                    case "/tasks/epic/":
                        return Endpoint.GET_TASKS;
                }
                if (urlParts[urlParts.length - 1].startsWith("?id")) {
                    return Endpoint.GET_BY_ID;
                }
            }
            if (requestMethod.equals("DELETE")) {
                switch (url) {
                    case "/tasks/task":
                    case "/tasks/subtask":
                    case "/tasks/epic":
                        return Endpoint.DELETE_TASKS;
                }
                if (urlParts[urlParts.length - 1].startsWith("?id")) {
                    return Endpoint.DELETE_BY_ID;
                }
            }
            if (requestMethod.equals("POST") && urlParts[1].equals("tasks") && urlParts.length == 3) {
                return Endpoint.POST_TASK;
            }
            return Endpoint.UNKNOWN;
        }

        private Optional<Integer> getTaskId(String[] urlParts) {
            try {
                return Optional.of(Integer.parseInt(urlParts[urlParts.length-1].split("=")[1]));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }

        private void handleGetTasks(HttpExchange exchange, String type) throws IOException {
            switch (type) {
                case "task":
                    writeResponse(exchange, gson.toJson(taskManager.getTaskList()), 200);
                    return;
                case "subtask":
                    writeResponse(exchange, gson.toJson(taskManager.getSubTaskList()), 200);
                    return;
                case "epic":
                    writeResponse(exchange, gson.toJson(taskManager.getEpicList()), 200);
                    return;
            }
            writeResponse(exchange, "Неправильный запрос", 400);
        }

        private void handleGetPrioritized(HttpExchange exchange) throws IOException {
            Collection<Task> prioritized = taskManager.getPrioritizedTasks();
            if (prioritized.isEmpty()) {
                writeResponse(exchange, "Список приоритетных задач пуст", 404);
                return;
            }
            writeResponse(exchange, gson.toJson(prioritized), 200);
        }

        private void handleGetById(HttpExchange exchange, String[] urlParts) throws IOException {
            Optional<Integer> optionalId = getTaskId(urlParts);
            if (optionalId.isEmpty()) {
                writeResponse(exchange, "Неверный идентификатор задачи", 400);
                return;
            }
            int id = optionalId.get();
            if (urlParts.length == 4 && urlParts[2].equals("task")) {
                try {
                    writeResponse(exchange, gson.toJson(taskManager.getTaskById(id)), 200);
                } catch (NoSuchElementException e) {
                    writeResponse(exchange, e.getMessage(), 404);
                }
            } else if (urlParts.length == 4 && urlParts[2].equals("epic")) {
                try {
                    writeResponse(exchange, gson.toJson(taskManager.getEpicById(id)), 200);
                } catch (NoSuchElementException e) {
                    writeResponse(exchange, e.getMessage(), 404);
                }
            } else if (urlParts.length == 5 && urlParts[2].equals("subtask") && urlParts[3].equals("epic")) {
                try {
                    writeResponse(exchange, gson.toJson(taskManager.getSubTaskById(id)), 200);
                } catch (NoSuchElementException e) {
                    writeResponse(exchange, e.getMessage(), 404);
                }
            } else {
                writeResponse(exchange, "Неправильный запрос", 400);
            }
        }

        private void handlePostTask(HttpExchange exchange) throws IOException {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task task;
                Subtask subtask;
                Epic epic;
                switch (exchange.getRequestURI().getPath()) {
                    case "/tasks/task/":
                        task = gson.fromJson(body, Task.class);
                        taskManager.createTask(task);
                        writeResponse(exchange, "Задача создана", 201);
                        break;
                    case "/tasks/epic/":
                        epic = gson.fromJson(body, Epic.class);
                        taskManager.createEpic(epic);
                        writeResponse(exchange, "Эпик создан", 201);
                        break;
                    case "/tasks/subtask/":
                        subtask = gson.fromJson(body, Subtask.class);
                        taskManager.createSubTask(subtask);
                        writeResponse(exchange, "Подзадача создана", 201);
                        break;
                    default:
                        writeResponse(exchange, "Неправильный запрос", 400);
                        break;
                }
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Неправильный JSON", 400);
            }
        }

        private void handleDeleteTasks(HttpExchange exchange, String type) throws IOException {
            switch (type) {
                case "task":
                    taskManager.deleteTasks();
                    writeResponse(exchange, "Все задачи удалены.", 200);
                    return;
                case "epic":
                    taskManager.deleteEpics();
                    writeResponse(exchange, "Все эпики удалены.", 200);
                    return;
                case "subtask":
                    taskManager.deleteSubTasks();
                    writeResponse(exchange, "Все подзадачи удалены.", 200);
                    return;
            }
            writeResponse(exchange, "Неправильный запрос", 400);
        }

        private void handleDeleteById(HttpExchange exchange, String[] urlParts) throws IOException {
            Optional<Integer> optionalId = getTaskId(urlParts);
            if (optionalId.isEmpty()) {
                writeResponse(exchange, "Неверный идентификатор задачи", 400);
                return;
            }
            int id = optionalId.get();
            switch (urlParts[2]) {
                case "task": {
                    try {
                        taskManager.deleteTaskById(id);
                        writeResponse(exchange, "Задача удалена", 200);
                    } catch (NoSuchElementException e) {
                        writeResponse(exchange, e.getMessage(), 404);
                    }
                }
                case "subtask": {
                    try {
                        taskManager.deleteSubTaskById(id);
                        writeResponse(exchange, "Подзадача удалена", 200);
                    } catch (NoSuchElementException e) {
                        writeResponse(exchange, e.getMessage(), 404);
                    }
                }
                case "epic": {
                    try {
                        taskManager.deleteEpicById(id);
                        writeResponse(exchange, "Эпик удален", 200);
                    } catch (NoSuchElementException e) {
                        writeResponse(exchange, e.getMessage(), 404);
                    }
                }
                default: {
                    writeResponse(exchange, "Неправильный запрос", 400);
                }
            }
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            Collection<Task> history = taskManager.getHistory();
            if (history.isEmpty()) {
                writeResponse(exchange, "Список истории пуст", 404);
                return;
            }
            writeResponse(exchange, gson.toJson(taskManager.getHistory()), 200);
        }

        private void writeResponse(HttpExchange exchange, String response, int code) throws IOException {
            if (response.isBlank()) {
                exchange.sendResponseHeaders(code, 0);
            } else {
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(code, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }
    }
}
