package ru.yandex.practicum.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.manager.historymanager.HistoryManager;
import ru.yandex.practicum.manager.taskmanager.TaskManager;
import ru.yandex.practicum.tasks.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Formatting {


    public static String tasksToString(TaskManager taskManager) {
        List<Task> allTasks = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        allTasks.addAll(taskManager.getTaskList());
        allTasks.addAll(taskManager.getEpicList());
        allTasks.addAll(taskManager.getSubTaskList());

        for (Task task : allTasks) {
            result.append(task.getAllDescription()).append("\n");
        }

        return result.toString();
    }

    public static Task tasksFromString(String value) {

        int epicID = 0;
        String[] values = value.split(",");
        int id = Integer.parseInt(values[0]);
        String type = values[1];
        String name = values[2];
        Status status = Status.valueOf(values[3]);
        String description = values[4];
        Instant startTime = Instant.parse(values[5]);
        Long duration = Long.parseLong(values[6]);

        if (TaskType.valueOf(type).equals(TaskType.SUBTASK))
            epicID = Integer.parseInt(values[7]);

        if (TaskType.valueOf(type).equals(TaskType.TASK))
            return new Task(id, name, description, status, duration, startTime);

        if (TaskType.valueOf(type).equals(TaskType.EPIC))
            return new Epic(id, name, description, status, duration, startTime);

        if (TaskType.valueOf(type).equals(TaskType.SUBTASK))
            return new Subtask(id, name, description, status, duration, startTime, epicID);

        else
            throw new IllegalArgumentException("Данный формат таска не поддерживается");

    }


    public static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();

        for (Task task : manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }

        return sb.toString();
    }

    public static List<Integer> historyFromString(String value) {

        List<Integer> history = new ArrayList<>();

        for (var line : value.split(","))
            history.add(Integer.parseInt(line));

        return history;
    }

    public static Gson createGson() {

        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }
}