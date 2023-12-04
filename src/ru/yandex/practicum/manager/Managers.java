package ru.yandex.practicum.manager;

import ru.yandex.practicum.manager.historymanager.HistoryManager;
import ru.yandex.practicum.manager.historymanager.InMemoryHistoryManager;
import ru.yandex.practicum.manager.taskmanager.FileBackedTasksManager;
import ru.yandex.practicum.manager.taskmanager.HttpTaskManager;
import ru.yandex.practicum.manager.taskmanager.TaskManager;
import ru.yandex.practicum.manager.taskmanager.InMemoryTaskManager;

public class Managers {

    public static TaskManager getDefault(String url) {
        return new HttpTaskManager(url);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultFileBackedTasksManager(String url) {
        return new FileBackedTasksManager(url);
    }

}
