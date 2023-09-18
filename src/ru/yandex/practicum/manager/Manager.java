package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;

import java.util.HashMap;

public class Manager {
    protected final HashMap<Integer, Task> taskHashMap = new HashMap<>();
    protected final HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();

    private int idGenerator;


    // получение задач
    public HashMap<Integer, Task> getTaskHashMap() {
        return taskHashMap;
    }

    // получение эпиков
    public HashMap<Integer, Epic> getEpicHashMap() {
        return epicHashMap;
    }

    // получение сабтасков
    public HashMap<Integer, Subtask> getSubtaskHashMap() {
        return subtaskHashMap;
    }

    // получение задачи по индентификатору
    public Task getTaskByIdNumber(int idNumber) {
        return taskHashMap.get(idNumber);
    }

    // получение эпика по индентификатору
    public Epic getEpicByIdNumber(int idNumber) {
        return epicHashMap.get(idNumber);
    }

    // получение сабтаска по индентификатору
    public Subtask getSubTaskByIdNumber(int idNumber) {
        return subtaskHashMap.get(idNumber);
    }

    //создание задачи
    public Task createTask(Task task) {
        task.setId(++idGenerator);
        taskHashMap.put(task.getId(), task);

        return task;
    }

    // создание эпика
    public Epic createEpic(Epic epic) {
        epic.setId(++idGenerator);
        epicHashMap.put(epic.getId(), epic);

        return epic;
    }

    // создание сабтаска
    public Subtask createSubTask(Subtask subtask) {
        Epic epic = epicHashMap.get(subtask.getEpicID());

        subtask.setId(++idGenerator);
        subtaskHashMap.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        updateEpicStatus(subtask.getEpicID());

        return subtask;
    }

    // удаление задачи
    public void deleteTask(int id) {
        taskHashMap.remove(id);
        System.out.println("Задача №" + id + " удалёна");
    }

    // удаление сабтаска
    public void deleteSubtask(int id) {
        Subtask subtask = subtaskHashMap.get(id);
        Epic epic = epicHashMap.get(subtask.getEpicID());

        epic.removeSubtask(subtask);
        subtaskHashMap.remove(id);
        updateEpicStatus(subtask.getEpicID());
        System.out.println("Подзадача №" + id + " удалёна");
    }

    // удаление эпика
    public void deleteEpic(int epicID) {
        Epic epic = epicHashMap.get(epicID);

        for (Integer subtasks : epic.getSubtasks()) {
            this.subtaskHashMap.remove(subtasks);
        }
        epicHashMap.remove(epicID);
        System.out.println("Эпик №" + epicID + " удалён");
    }

    // удаление всех тасков, эпиков и сабтасков
    public void deleteAllTasksEpicsSubtasks() {
        taskHashMap.clear();
        epicHashMap.clear();
        subtaskHashMap.clear();
        System.out.println("Удалены все задачи, эпики и подзадачи");
    }

    // печать списка всех тасков
    public void printAllTasks() {
        if (taskHashMap.isEmpty()) {
            System.out.println("Список задач пуст.");
        }
        for (int id : taskHashMap.keySet()) {
            Task value = taskHashMap.get(id);
            System.out.println("№" + id + " " + value);
        }
    }

    // печать списка всех сабтасков
    public void printAllSubtasks() {
        if (subtaskHashMap.isEmpty()) {
            System.out.println("Список подзадач пуст.");
        }
        for (int id : subtaskHashMap.keySet()) {
            Subtask value = subtaskHashMap.get(id);
            System.out.println("№" + id + " " + value);
        }
    }

    // печать списка всех эпиков
    public void printAllEpics() {
        if (epicHashMap.isEmpty()) {
            System.out.println("Список эпиков пуст.");
        }
        for (int id : epicHashMap.keySet()) {
            Epic value = epicHashMap.get(id);
            System.out.println("№" + id + " " + value);
        }
    }


    // обновление задачи
    public void updateTask(Task task) {
        int id = task.getId();
        if (taskHashMap.containsKey(id)) {
            taskHashMap.put(task.getId(), task);
        }
    }

    // обновление сабтаска
    public void updateSubtask(Subtask subtask) {
        Epic epic = epicHashMap.get(subtask.getEpicID());
        int id = subtask.getId();
        if (subtaskHashMap.containsKey(id)) {
            subtaskHashMap.put(subtask.getId(), subtask);
            epicHashMap.put(epic.getId(), epic);
            updateEpicStatus(subtask.getEpicID());
        }
    }

    // обновление эпика
    public void updateEpic(Epic epic) {
        Subtask subtask = subtaskHashMap.get(epic.getId());
        int id = epic.getId();
        if (epicHashMap.containsKey(id)) {
            epicHashMap.put(epic.getId(), epic);
            subtaskHashMap.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicID());
        }
    }


    private void updateEpicStatus(int id) {
        Epic epic = epicHashMap.get(id);
        int isNew = 0;
        int isDone = 0;
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        for (Integer iDsOfSubtask : epic.getSubtasks()) {
            if (subtaskHashMap.get(iDsOfSubtask).getStatus() == Status.NEW) {
                isNew += 1;
            } else if (subtaskHashMap.get(iDsOfSubtask).getStatus() == Status.DONE) {
                isDone += 1;
            }
        }
        if (epic.getSubtasks().size() == isNew) {
            epic.setStatus(Status.NEW);
            return;
        } else if (epic.getSubtasks().size() == isDone) {
            epic.setStatus(Status.DONE);
            return;
        }
        epic.setStatus(Status.IN_PROGRESS);
    }

}
