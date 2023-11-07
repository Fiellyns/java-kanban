package ru.yandex.practicum.manager.taskmanager;

import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.historymanager.HistoryManager;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> taskHashMap;
    protected final Map<Integer, Epic> epicHashMap;
    protected final Map<Integer, Subtask> subtaskHashMap;
    protected static HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.taskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();
        this.subtaskHashMap = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    protected int id;



    // генератор ID
    public int getNextId(){
        return ++id;
    }


    // получение задач
    @Override
    public Collection<Task> getTaskList() {
        return taskHashMap.values();
    }

    // получение эпиков
    @Override
    public Collection<Epic> getEpicList() {
        return epicHashMap.values();
    }

    // получение сабтасков
    @Override
    public Collection<Subtask> getSubTaskList() {
        return subtaskHashMap.values();
    }

    // получение задачи по индентификатору
    @Override
    public Task getTaskById(int idNumber) {
        Task task = taskHashMap.get(idNumber);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    // получение эпика по индентификатору
    @Override
    public Epic getEpicById(int idNumber) {
        Epic epic = epicHashMap.get(idNumber);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    // получение сабтаска по индентификатору
    @Override
    public Subtask getSubTaskById(int idNumber) {
        Subtask subtask = subtaskHashMap.get(idNumber);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    //получение истории
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //создание задачи
    @Override
    public Task createTask(Task task) {
        task.setId(getNextId());
        taskHashMap.put(task.getId(), task);
        return task;
    }

    // создание эпика
    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epicHashMap.put(epic.getId(), epic);

        return epic;
    }

    // создание сабтаска
    @Override
    public Subtask createSubTask(Subtask subtask) {
        subtask.setId(getNextId());
        subtaskHashMap.put(subtask.getId(), subtask);

        Epic epic = epicHashMap.get(subtask.getEpicID());
        epic.addSubtask(subtask);
        updateEpicStatus(subtask.getEpicID());

        return subtask;
    }

    // удаление всех тасков
    @Override
    public void deleteTasks() {
        for (Integer taskId : taskHashMap.keySet()) { // очистка из истории
            historyManager.remove(taskId);
        }
        taskHashMap.clear();
    }

    // удаление всех сабтасков
    @Override
    public void deleteSubTasks() {
        for (Epic epic : epicHashMap.values()) {
            epic.getSubtasks().clear(); // очищение подзадач
            updateEpicStatus(epic.getId()); // обновление статуса
        }

        for (Integer subTaskId : subtaskHashMap.keySet()) { // очистка из истории
            historyManager.remove(subTaskId);
        }
        subtaskHashMap.clear(); // очищается мапа
    }

    // удаление всех эпиков
    @Override
    public void deleteEpics() {
        for (Integer subTaskId : subtaskHashMap.keySet()) { // очистка из истории
            historyManager.remove(subTaskId);
        }
        for (Integer epicId : epicHashMap.keySet()) { // очистка из истории
            historyManager.remove(epicId);
        }
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    // удаление задачи по идентификатору
    @Override
    public void deleteTaskById(int id) {
        taskHashMap.remove(id);
        historyManager.remove(id); // очистка из истории
    }

    // удаление сабтаска по идентификатору
    @Override
    public void deleteSubTaskById(int id) {
        Subtask subtask = subtaskHashMap.get(id);

        if (subtask == null) {
            return;
        }

        Epic epic = epicHashMap.get(subtask.getEpicID());

        epic.removeSubtask(subtask);
        subtaskHashMap.remove(id);
        updateEpicStatus(subtask.getEpicID());
        historyManager.remove(id); // очистка из истории
    }

    // удаление эпика по идентификатору
    @Override
    public void deleteEpicById(int epicID) {
        Epic epic = epicHashMap.get(epicID);

        if (epic == null) {
            return;
        }

        for (Integer taskId : epic.getSubtasks()) {
            subtaskHashMap.remove(taskId);
            historyManager.remove(taskId);
        }
        epicHashMap.remove(epicID);
        historyManager.remove(epicID); // очистка из истории
    }

    // обновление задачи
    @Override
    public void updateTask(Task task) {
        if (taskHashMap.containsKey(task.getId())) {
            taskHashMap.put(task.getId(), task);
        }
    }

    // обновление сабтаска
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtaskHashMap.containsKey(subtask.getId())) {
            subtaskHashMap.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicID());
        }
    }

    // обновление эпика
    @Override
    public void updateEpic(Epic epic) {
        if (epicHashMap.containsKey(epic.getId())) {
            epicHashMap.put(epic.getId(), epic);
        }
    }


    // обновление эпик статуса
    private void updateEpicStatus(int id) {
        Epic epic = epicHashMap.get(id);
        boolean isNew = true;
        boolean isDone = true;

        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (Integer taskId : epic.getSubtasks()) {
            Status status = subtaskHashMap.get(taskId).getStatus();

            if (status != Status.NEW) {
                isNew = false;
            }

            if (status != Status.DONE) {
                isDone = false;
            }
        }

        if (isNew) {
            epic.setStatus(Status.NEW);
        } else if (isDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
