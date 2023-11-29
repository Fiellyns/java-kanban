package ru.yandex.practicum.manager;

import ru.yandex.practicum.manager.historymanager.HistoryManager;
import ru.yandex.practicum.manager.historymanager.InMemoryHistoryManager;
import ru.yandex.practicum.manager.taskmanager.FileBackedTasksManager;
import ru.yandex.practicum.manager.taskmanager.HttpTaskManager;
import ru.yandex.practicum.manager.taskmanager.TaskManager;
import ru.yandex.practicum.manager.taskmanager.InMemoryTaskManager;
import ru.yandex.practicum.server.KVTaskClient;

import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault(String url) {
        return new HttpTaskManager(url, new KVTaskClient(url));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static InMemoryTaskManager getDefaultInMemory() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager getDefaultFileBackedTasksManager(String url) {
        return new FileBackedTasksManager(url);
    }

}
