package ru.yandex.practicum.manager.historymanager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyManagersList;

    public InMemoryHistoryManager () {
        this.historyManagersList = new ArrayList<>();
    }

    // добавление таска
    @Override
    public void add(Task task) {
        historyManagersList.add(task);
        if (historyManagersList.size() > 10) {
            historyManagersList.remove(0);
        }
    }

    // получение истории
    @Override
    public List<Task> getHistory() {
        return historyManagersList;
    }
}
