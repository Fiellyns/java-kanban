package ru.yandex.practicum.manager.taskmanager;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.manager.historymanager.HistoryManager;
import ru.yandex.practicum.server.KVTaskClient;
import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.utils.Formatting;

import java.lang.reflect.Type;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient taskClient;
    private final Gson gson;

    public HttpTaskManager(String url, KVTaskClient taskClient) {
        super(url);
        this.taskClient = taskClient;
        gson = Formatting.createGson();
    }

    @Override
    public void save() {
        taskClient.put("task", gson.toJson(getTaskList()));
        taskClient.put("subtask", gson.toJson(getSubTaskList()));
        taskClient.put("epic", gson.toJson(getEpicList()));
        taskClient.put("tasks", gson.toJson(getPrioritizedTasks()));
        taskClient.put("history", gson.toJson(getHistory()));
    }

    public void loadFromServer() {
        loadTasks("task");
        loadTasks("subtask");
        loadTasks("epic");
        loadHistory();
    }

    private void loadTasks(String key) {
        JsonElement jsonElement = JsonParser.parseString(taskClient.load(key));
        JsonArray jsonTasksArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonTasksArray) {
            Task task;
            Epic epic;
            Subtask subtask;
            switch (key) {
                case "task":
                    task = gson.fromJson(element.getAsJsonObject(), Task.class);
                    createTask(task);
                    break;
                case "subtask":
                    subtask = gson.fromJson(element.getAsJsonObject(), Subtask.class);
                    createSubTask(subtask);
                    break;
                case "epic":
                    epic = gson.fromJson(element.getAsJsonObject(), Epic.class);
                    createEpic(epic);
                    break;
                default:
                    System.out.println("Невозможно загрузить задачи");
                    return;
            }
        }
    }

    private void loadHistory() {
        String gsonHistory = taskClient.load("history");
        Type historyType = new TypeToken<List<Task>>(){}.getType();

        List<Task> history = gson.fromJson(gsonHistory, historyType);

        for (Task task : history) {
            historyManager.add(findTaskById(task.getId()));
        }
    }

    private Task findTaskById(Integer taskId) {
        for (Task task : getTaskList()) {
            if (task.getId() == taskId) {
                return task;
            }
        }
        for (Epic epic : getEpicList()) {
            if (epic.getId() == taskId) {
                return epic;
            }
        }
        for (Subtask subtask : getSubTaskList()) {
            if (subtask.getId() == taskId) {
                return subtask;
            }
        }
        return null;
    }
}
