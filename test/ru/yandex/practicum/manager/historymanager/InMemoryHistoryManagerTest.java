package ru.yandex.practicum.manager.historymanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    private final Task task = new Task(1,"Отдых", "поехать на море",
            Status.NEW, 1L,
            Instant.EPOCH);
    private final Epic epic = new Epic(2,"Дом", "любимый дом",
            Status.NEW, 1L,
            Instant.EPOCH);
    private final Subtask subtask = new Subtask(3,"Купить молоко", "3.2%",
            Status.NEW, 1L,
            Instant.EPOCH, 2);

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldAddTasksToHistoryList() {

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(List.of(task, epic, subtask), historyManager.getHistory());
    }

    @Test
    public void shouldReturnNullOfTaskIsEmpty() {
        historyManager.add(null);

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListIfNoHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void shouldRemoveTaskFromHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.remove(task.getId());

        assertEquals(List.of(epic), historyManager.getHistory());
    }

    @Test
    public void shouldDoNothingIfPassedIncorrect(){
        historyManager.add(task);
        historyManager.add(epic);

        historyManager.remove(3);
        assertEquals(List.of(task,epic), historyManager.getHistory());
    }

    @Test
    public void shouldNotDuplicateHistory(){
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }



}