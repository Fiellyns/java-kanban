package ru.yandex.practicum.manager.historymanager;

import ru.yandex.practicum.tasks.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();
}
