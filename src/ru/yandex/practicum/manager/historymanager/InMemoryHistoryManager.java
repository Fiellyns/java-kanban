package ru.yandex.practicum.manager.historymanager;

import ru.yandex.practicum.tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList customLinkedList;
    private final Map<Integer, Node<Task>> historyManagersMap;

    public InMemoryHistoryManager () {
        customLinkedList = new CustomLinkedList();
        historyManagersMap = new HashMap<>();
    }

    // добавление таска
    @Override
    public void add(Task task) {
        Node<Task> node = customLinkedList.linkLast(task);
        int id = task.getId();

        if (historyManagersMap.containsKey(id)) {
            remove(id);
        }

        historyManagersMap.put(id, node);

    }

    // очистка истории
    @Override
    public void remove(int id) {
        customLinkedList.removeNode(historyManagersMap.get(id));
        historyManagersMap.remove(id);
    }

    // получение истории
    @Override
    public List<Task> getHistory() {
        return List.copyOf(customLinkedList.getTasks());
    }
}
