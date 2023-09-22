package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private final HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();

    private int id;

    public int getNextId(){
        return ++id;
    }


    // получение задач
    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskHashMap.values());
    }

    // получение эпиков
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epicHashMap.values());
    }

    // получение сабтасков
    public ArrayList<Subtask> getSubTaskList() {
        return new ArrayList<>(subtaskHashMap.values());
    }

    // получение задачи по индентификатору
    public Task getTaskById(int idNumber) {
        return taskHashMap.get(idNumber);
    }

    // получение эпика по индентификатору
    public Epic getEpicById(int idNumber) {
        return epicHashMap.get(idNumber);
    }

    // получение сабтаска по индентификатору
    public Subtask getSubTaskById(int idNumber) {
        return subtaskHashMap.get(idNumber);
    }

    //создание задачи
    public Task createTask(Task task) {
        task.setId(getNextId());
        taskHashMap.put(task.getId(), task);

        return task;
    }

    // создание эпика
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epicHashMap.put(epic.getId(), epic);

        return epic;
    }

    // создание сабтаска
    public Subtask createSubTask(Subtask subtask) {
        Epic epic = epicHashMap.get(subtask.getEpicID());

        subtask.setId(getNextId());
        subtaskHashMap.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        updateEpicStatus(subtask.getEpicID());

        return subtask;
    }

    // удаление всех тасков
    public void deleteTasks() {
        taskHashMap.clear();
    }

    // удаление всех сабтасков
    public void deleteSubTasks() {
        // Если я правильно понял, то метод должен быть таким
        for (Epic epic : epicHashMap.values()) {
                epic.getSubtasks().clear();
                updateEpicStatus(epic.getId());
        }
//        for (Subtask sub : subtaskHashMap.values()) {
//                Epic epic = epicHashMap.get(sub.getEpicID());
//                epic.getSubtasks().clear(); // очищается список сабтасков в эпике
//                updateEpicStatus(sub.getEpicID()); // по итогу, ставится статус эпику NEW
//        }
        subtaskHashMap.clear(); // очищается мапа
    }

    // удаление всех эпиков
    public void deleteEpics() {
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    // удаление задачи по идентификатору
    public void deleteTaskById(int id) {
        taskHashMap.remove(id);
    }

    // удаление сабтаска по идентификатору
    public void deleteSubTaskById(int id) {
        Subtask subtask = subtaskHashMap.get(id);
        Epic epic = epicHashMap.get(subtask.getEpicID());

        epic.removeSubtask(subtask);
        subtaskHashMap.remove(id);
        updateEpicStatus(subtask.getEpicID());
    }

    // удаление эпика по идентификатору
    public void deleteEpicById(int epicID) {
        Epic epic = epicHashMap.get(epicID);

        for (Integer taskId : epic.getSubtasks()) {
            subtaskHashMap.remove(taskId);
        }
        epicHashMap.remove(epicID);
    }

    // обновление задачи
    public void updateTask(Task task) {
        if (taskHashMap.containsKey(task.getId())) {
            taskHashMap.put(task.getId(), task);
        }
    }

    // обновление сабтаска
    public void updateSubtask(Subtask subtask) {
        if (subtaskHashMap.containsKey(subtask.getId())) {
            subtaskHashMap.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicID());
        }
    }

    // обновление эпика
    public void updateEpic(Epic epic) {
        if (epicHashMap.containsKey(epic.getId())) {
            epicHashMap.put(epic.getId(), epic);
        }
    }


    // обновление эпик статуса
    private void updateEpicStatus(int id) {
        Epic epic = epicHashMap.get(id);
        int isNew = 0;
        int isDone = 0;
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (Integer taskId : epic.getSubtasks()) {

            if (subtaskHashMap.get(taskId).getStatus() == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            } else if (subtaskHashMap.get(taskId).getStatus() == Status.NEW) {
                isNew++;
            } else if (subtaskHashMap.get(taskId).getStatus() == Status.DONE) {
                isDone++;
            }
        }

        if (epic.getSubtasks().size() == isNew) {
            epic.setStatus(Status.NEW);
        } else if (epic.getSubtasks().size() == isDone) {
            epic.setStatus(Status.DONE);
        }
    }
}
