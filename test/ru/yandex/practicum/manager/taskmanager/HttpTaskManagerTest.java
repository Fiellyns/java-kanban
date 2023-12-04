package ru.yandex.practicum.manager.taskmanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.server.KVServer;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>{
    protected KVServer server;
    private static final String url = "http://localhost:8078";

    @BeforeEach
    void startServer() throws IOException {
        server = new KVServer();
        server.start();
        taskManager = (HttpTaskManager) Managers.getDefault(url);
    }

    @AfterEach
    void serverStop() {
        server.stop();
    }

    @Test
    void shouldLoadFromServer(){
        Task task = taskManager.createTask(newTask());
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subtask.getId());
        HttpTaskManager httpTaskManagerAfterLoad = (HttpTaskManager) Managers.getDefault(url);
        httpTaskManagerAfterLoad.loadFromServer();

        List<Task> taskList = new ArrayList<>(httpTaskManagerAfterLoad.getTaskList());
        List<Task> subtaskList = new ArrayList<>(httpTaskManagerAfterLoad.getSubTaskList());
        List<Task> epicList = new ArrayList<>(httpTaskManagerAfterLoad.getEpicList());
        List<Task> historyList = new ArrayList<>(httpTaskManagerAfterLoad.getHistory());

        assertEquals(List.of(task), taskList);
        assertEquals(List.of(subtask), subtaskList);
        assertEquals(List.of(epic), epicList);
        assertEquals(List.of(task, epic, subtask), historyList);
    }


}