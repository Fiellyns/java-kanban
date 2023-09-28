package ru.yandex.practicum.manager.historymanager;

import ru.yandex.practicum.tasks.Task;
import java.util.List;

public interface HistoryManager {

    // добавление таска
    void add(Task task);

    // получение истории
    List<Task> getHistory();
}
