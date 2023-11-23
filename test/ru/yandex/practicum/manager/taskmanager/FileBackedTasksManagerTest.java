package ru.yandex.practicum.manager.taskmanager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.taskmanager.FileBackedTasksManager;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final Path path = Path.of("resources/resultTest.csv");

    @BeforeEach
    void beforeEach() {
        taskManager = (FileBackedTasksManager) Managers.getDefault(path.toString());
    }

    @AfterEach
    void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            exception.getStackTrace();
        }
    }

    @Test
    void shouldSaveAndLoadCorrectly() {
        Task task = taskManager.createTask(newTask());
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subtask.getId());

        FileBackedTasksManager fileManagerAfterLoading = FileBackedTasksManager.loadFromFile(path.toString());

        List<Task> taskList = new ArrayList<>(fileManagerAfterLoading.getTaskList());
        List<Task> subtaskList = new ArrayList<>(fileManagerAfterLoading.getSubTaskList());
        List<Task> epicList = new ArrayList<>(fileManagerAfterLoading.getEpicList());
        List<Task> historyList = new ArrayList<>(fileManagerAfterLoading.getHistory());

        assertEquals(List.of(task), taskList);
        assertEquals(List.of(subtask), subtaskList);
        assertEquals(List.of(epic), epicList);
        assertEquals(List.of(task, epic, subtask), historyList);
    }

    @Test
    void shouldSaveAndLoadEmpty() {
        taskManager.save();
        FileBackedTasksManager fileManagerAfterLoading = FileBackedTasksManager.loadFromFile(path.toString());
        assertEquals(Collections.EMPTY_LIST, fileManagerAfterLoading.getTaskList());
        assertEquals(Collections.EMPTY_LIST, fileManagerAfterLoading.getSubTaskList());
        assertEquals(Collections.EMPTY_LIST, fileManagerAfterLoading.getEpicList());
    }

    @Test
    void shouldSaveAndLoadEmptyHistory() {
        taskManager.save();
        FileBackedTasksManager fileManagerAfterLoading = FileBackedTasksManager.loadFromFile(path.toString());

        assertEquals(Collections.EMPTY_LIST, fileManagerAfterLoading.getHistory());
    }

}