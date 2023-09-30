package ru.yandex.practicum.manager.historymanager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyManagersList;
    private final static int MAX_TASKS_NUMBERS = 10;

    public InMemoryHistoryManager () {
        // Мы ещё не проходили LinkedList, но воспользуюсь советом.
        this.historyManagersList = new LinkedList<>();
    }

    // добавление таска
    @Override
    public void add(Task task) {
        historyManagersList.add(task);
        if (historyManagersList.size() > MAX_TASKS_NUMBERS) {
            historyManagersList.remove(0);
        }
    }

    // получение истории
    @Override
    public List<Task> getHistory() {
        return List.copyOf(historyManagersList);
    }
}
