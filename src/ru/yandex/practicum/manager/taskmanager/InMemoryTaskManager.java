package ru.yandex.practicum.manager.taskmanager;

import ru.yandex.practicum.exceptions.IntersectionException;
import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.historymanager.HistoryManager;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> taskHashMap;
    protected final Map<Integer, Epic> epicHashMap;
    protected final Map<Integer, Subtask> subtaskHashMap;
    protected static HistoryManager historyManager;
    private final Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.taskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();
        this.subtaskHashMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    protected int id;


    // генератор ID
    public int getNextId() {
        return ++id;
    }


    // получение списка и форматирование его в ArrayList
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // добавление таска в список + проверка
    public void addToPrioritizedTasks(Task task) {
        boolean isIntersection = checkIntersections(task);
        if (!isIntersection) {
            prioritizedTasks.add(task);
        } else {
            throw new IntersectionException("Найдено пересечение во времени");
        }
    }

    public boolean checkIntersections(Task task) {
        boolean isValidate = false;

        Instant startOfTask = task.getStartTime();
        Instant endOfTask = task.getEndTime();

        for (Task taskValue : prioritizedTasks) {
            if (taskValue.getStartTime() == null) {
                continue;
            }

            Instant startTime = taskValue.getStartTime();
            Instant endTime = taskValue.getEndTime();

            isValidate = endOfTask.isAfter(startTime) && startOfTask.isBefore(endTime);
            if (isValidate) {
                return isValidate;
            }
        }
        return isValidate;
    }

    // получение задач
    @Override
    public Collection<Task> getTaskList() {
        if (!taskHashMap.isEmpty()) {
            return taskHashMap.values();
        } else {
            return Collections.emptyList();
        }
    }

    // получение эпиков
    @Override
    public Collection<Epic> getEpicList() {
        if (!epicHashMap.isEmpty()) {
            return epicHashMap.values();
        } else {
            return Collections.emptyList();
        }
    }

    // получение сабтасков
    @Override
    public Collection<Subtask> getSubTaskList() {
        if (!subtaskHashMap.isEmpty()) {
            return subtaskHashMap.values();
        } else {
            return Collections.emptyList();
        }
    }

    // получение задачи по индентификатору
    @Override
    public Task getTaskById(int idNumber) {
        Task task = taskHashMap.get(idNumber);
        if (task != null && taskHashMap.containsKey(id)) {
            historyManager.add(task);
        } else {
            return null;
        }
        return task;
    }

    // получение эпика по индентификатору
    @Override
    public Epic getEpicById(int idNumber) {
        Epic epic = epicHashMap.get(idNumber);
        if (epic != null && epicHashMap.containsKey(id)) {
            historyManager.add(epic);
        } else {
            return null;
        }
        return epic;
    }

    // получение сабтаска по индентификатору
    @Override
    public Subtask getSubTaskById(int idNumber) {
        Subtask subtask = subtaskHashMap.get(idNumber);
        if (subtask != null && subtaskHashMap.containsKey(id)) {
            historyManager.add(subtask);
        } else {
            return null;
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
        if (task != null && !taskHashMap.containsKey(task.getId())) {
            task.setId(getNextId());
            taskHashMap.put(task.getId(), task);
            addToPrioritizedTasks(task);
        } else {
            return null;
        }
        return task;
    }

    // создание эпика
    @Override
    public Epic createEpic(Epic epic) {
        if (epic != null && !epicHashMap.containsKey(epic.getId())) {
            epic.setId(getNextId());
            epicHashMap.put(epic.getId(), epic);
        } else {
            return null;
        }
        return epic;
    }

    // создание сабтаска
    @Override
    public Subtask createSubTask(Subtask subtask) {
        if (subtask != null && !subtaskHashMap.containsKey(subtask.getId())) {
            subtask.setId(getNextId());

            Epic epic = epicHashMap.get(subtask.getEpicID());
            if (epic != null) {
                subtaskHashMap.put(subtask.getId(), subtask);
                addToPrioritizedTasks(subtask);
                epic.addSubtask(subtask);
                updateEpicStatus(subtask.getEpicID());
                updateEpicTime(subtask.getEpicID());
            }
        } else {
            return null;
        }
        return subtask;
    }

    // удаление всех тасков
    @Override
    public void deleteTasks() {
        for (Integer taskId : taskHashMap.keySet()) { // очистка из истории
            historyManager.remove(taskId);
        }
        prioritizedTasks.removeIf(task -> task.getTaskType() == TaskType.TASK);
        taskHashMap.clear();
    }

    // удаление всех сабтасков
    @Override
    public void deleteSubTasks() {
        for (Epic epic : epicHashMap.values()) {
            updateEpicTime(epic.getId());
            epic.getSubtasks().clear(); // очищение подзадач
            updateEpicStatus(epic.getId()); // обновление статуса
        }

        for (Integer subTaskId : subtaskHashMap.keySet()) { // очистка из истории
            historyManager.remove(subTaskId);
        }
        prioritizedTasks.removeIf(task -> task.getTaskType() == TaskType.SUBTASK);
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
        prioritizedTasks.removeIf(task -> task.getTaskType() == TaskType.SUBTASK);
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    // удаление задачи по идентификатору
    @Override
    public void deleteTaskById(int id) {
        taskHashMap.remove(id);
        historyManager.remove(id); // очистка из истории
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }

    // удаление сабтаска по идентификатору
    @Override
    public void deleteSubTaskById(int id) {
        Subtask subtask = subtaskHashMap.get(id);

        if (subtask == null) {
            return;
        }

        Epic epic = epicHashMap.get(subtask.getEpicID());

        updateEpicTime(subtask.getEpicID());
        epic.removeSubtask(subtask);
        subtaskHashMap.remove(id);
        updateEpicStatus(subtask.getEpicID());
        historyManager.remove(id); // очистка из истории
        prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), id));

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
            prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), taskId));
        }
        epicHashMap.remove(epicID);
        historyManager.remove(epicID); // очистка из истории
    }

    // обновление задачи
    @Override
    public void updateTask(Task task) {
        if (task != null && taskHashMap.containsKey(task.getId())) {
            taskHashMap.put(task.getId(), task);
            prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.equals(task));
            addToPrioritizedTasks(task);
        }
    }

    // обновление сабтаска
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtaskHashMap.containsKey(subtask.getId())) {
            subtaskHashMap.put(subtask.getId(), subtask);
            prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.equals(subtask));
            addToPrioritizedTasks(subtask);
            updateEpicStatus(subtask.getEpicID());
            updateEpicTime(subtask.getEpicID());
        }
    }

    // обновление эпика
    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epicHashMap.containsKey(epic.getId())) {
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

    private void updateEpicTime(int id) {
        Epic epic = epicHashMap.get(id);
        List<Integer> subtasks = epic.getSubtasks();
        Instant startTime = subtaskHashMap.get(subtasks.get(0)).getStartTime();
        Instant endTime = subtaskHashMap.get(subtasks.get(0)).getEndTime();

        for (Integer taskId : subtasks) {
            Subtask subtask = subtaskHashMap.get(taskId);

            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            } else if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        long duration = Duration.between(startTime, endTime).toMinutes();
        epic.setDuration(duration);
    }
}
