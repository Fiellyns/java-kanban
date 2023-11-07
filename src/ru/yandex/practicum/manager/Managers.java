package ru.yandex.practicum.manager;

import ru.yandex.practicum.manager.historymanager.HistoryManager;
import ru.yandex.practicum.manager.historymanager.InMemoryHistoryManager;
import ru.yandex.practicum.manager.taskmanager.FileBackedTasksManager;
import ru.yandex.practicum.manager.taskmanager.TaskManager;
import ru.yandex.practicum.manager.taskmanager.InMemoryTaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static InMemoryTaskManager getDefaultInMemory() {

        return new InMemoryTaskManager();

    }
}
