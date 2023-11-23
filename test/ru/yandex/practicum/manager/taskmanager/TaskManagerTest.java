package ru.yandex.practicum.manager.taskmanager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected Task newTask() {
        return new Task("Отдых", "поехать на море",
                Status.NEW, 1L,
                Instant.ofEpochMilli(1700492000L));
    }

    protected Epic newEpic() {
        return new Epic("Дом", "любимый дом",
                Status.NEW, 1L,
                Instant.ofEpochMilli(1700492500L));
    }

    protected Subtask newSubtask(Epic epic) {
        return new Subtask("Купить молоко", "3.2%",
                Status.NEW, 1L,
                Instant.ofEpochMilli(1700683200L), epic.getId());
    }

    protected Subtask newSubtask2(Epic epic) {
        return new Subtask("Купить молоко", "3.2%",
                Status.NEW, 1L,
                Instant.ofEpochMilli(1700769600L), epic.getId());
    }


    // тесты для задач
    @Test
    void shouldCreateTask() {
        Task task1 = taskManager.createTask(newTask());
        List<Task> taskList = new ArrayList<>(taskManager.getTaskList());

        assertNotNull(task1.getStatus());
        assertEquals(Status.NEW, task1.getStatus());
        assertEquals(List.of(task1), taskList);
    }

    @Test
    void shouldNotCreateATaskIfEmpty() {
        Task task1 = taskManager.createTask(null);
        assertNull(task1);
    }

    @Test
    void shouldReturnCreatedGetTaskById() {
        taskManager.createTask(newTask());
        Task task1 = taskManager.getTaskById(1);
        List<Task> taskList = new ArrayList<>(taskManager.getTaskList());

        assertNotNull(task1.getStatus());
        assertEquals(1, task1.getId());
        assertEquals(List.of(task1), taskList);
    }

    @Test
    void shouldReturnNullIfCreatedGetTaskEmpty() {
        taskManager.createTask(null);
        Task task1 = taskManager.getTaskById(0);
        List<Task> taskList = new ArrayList<>(taskManager.getTaskList());

        assertNull(task1);
        assertEquals(Collections.EMPTY_LIST, taskList);
    }

    @Test
    void shouldDeleteTasks(){
        taskManager.createTask(newTask());
        taskManager.deleteTasks();

        assertEquals(Collections.EMPTY_LIST, taskManager.getTaskList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getPrioritizedTasks());
    }

    @Test
    void shouldNotDeleteTasksIfEmpty(){
        taskManager.createTask(null);
        taskManager.deleteTasks();
        List<Task> taskList = new ArrayList<>(taskManager.getTaskList());
        List<Task> prioritizedList = new ArrayList<>(taskManager.getPrioritizedTasks());


        assertEquals(0, taskList.size());
        assertEquals(0, prioritizedList.size());
    }

    @Test
    void shouldDeleteTaskById() {
        Task task1 = taskManager.createTask(newTask());
        taskManager.deleteTaskById(task1.getId());
        List<Task> taskList = new ArrayList<>(taskManager.getTaskList());
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();

        assertEquals(0, taskList.size());
        assertEquals(0, prioritizedList.size());
    }

    @Test
    void shouldNotDeleteTaskByIdIfIncorrectId() {
        Task task1 = taskManager.createTask(newTask());
        taskManager.deleteTaskById(20);
        List<Task> taskList = new ArrayList<>(taskManager.getTaskList());

        assertEquals(List.of(task1), taskList);
    }

    @Test
    void shouldReturnListOfTasks() {
        taskManager.createTask(newTask());

        List<Task> taskList = new ArrayList<>(taskManager.getTaskList());

        assertNotNull(taskList);
        assertEquals(1, taskList.size());
    }

    @Test
    void shouldUpdateTaskToStatusIN_PROGRESS(){
        Task task1 = taskManager.createTask(newTask());
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);

        assertEquals(Status.IN_PROGRESS, task1.getStatus());
    }

    @Test
    void shouldUpdateTaskToStatusDONE(){
        Task task1 = taskManager.createTask(newTask());
        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1);

        assertEquals(Status.DONE, task1.getStatus());
    }

    @Test
    void shouldNotUpdateTaskIfEmpty(){
        Task task1 = taskManager.createTask(newTask());
        taskManager.updateTask(null);

        assertNotNull(task1);
    }

    //тесты для сабтасков

    @Test
    void shouldCreateSubTask() {
        Epic epic1 = taskManager.createEpic(newEpic());
        Subtask subtask1 = taskManager.createSubTask(newSubtask(epic1));

        List<Task> subtaskList = new ArrayList<>(taskManager.getSubTaskList());

        assertNotNull(subtask1.getStatus());
        assertEquals(Status.NEW, subtask1.getStatus());
        assertEquals(epic1.getId(), subtask1.getEpicID());
        assertEquals(List.of(subtask1), subtaskList);
        assertEquals(List.of(subtask1.getId()), epic1.getSubtasks());
    }

    @Test
    void shouldNotCreateASubtaskIfEmpty() {
        taskManager.createEpic(null);
        Subtask subtask = taskManager.createSubTask(null);

        List<Task> subtaskList = new ArrayList<>(taskManager.getSubTaskList());

        assertNull(subtask);
        assertEquals(Collections.EMPTY_LIST, subtaskList);
    }

    @Test
    void shouldReturnCreatedGetSubtaskById() {
        Epic epic = taskManager.createEpic(newEpic());
        taskManager.createSubTask(newSubtask(epic));
        Subtask subtask1 = taskManager.getSubTaskById(2);


        List<Task> subtaskList = new ArrayList<>(taskManager.getSubTaskList());

        assertNotNull(subtask1.getStatus());
        assertEquals(2, subtask1.getId());
        assertEquals(List.of(subtask1), subtaskList);
    }

    @Test
    void shouldReturnNullIfCreatedGetSubtaskEmpty() {
        taskManager.createSubTask(null);
        Subtask subTask = taskManager.getSubTaskById(0);
        List<Task> subtaskList = new ArrayList<>(taskManager.getSubTaskList());

        assertNull(subTask);
        assertEquals(Collections.EMPTY_LIST, subtaskList);
    }

    @Test
    void shouldDeleteSubtasks(){
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));
        taskManager.deleteSubTasks();

        assertEquals(Collections.EMPTY_LIST, taskManager.getSubTaskList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getPrioritizedTasks());
    }

    @Test
    void shouldNotDeleteSubtasksIfEmpty(){
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));
        taskManager.deleteSubTasks();

        List<Task> subtaskList = new ArrayList<>(taskManager.getSubTaskList());
        List<Task> prioritizedList = new ArrayList<>(taskManager.getPrioritizedTasks());


        assertEquals(0, subtaskList.size());
        assertEquals(0, prioritizedList.size());
    }

    @Test
    void shouldDeleteSubtaskById() {
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));

        taskManager.deleteSubTaskById(subtask.getId());

        List<Task> subtaskList = new ArrayList<>(taskManager.getSubTaskList());
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();

        assertEquals(0, subtaskList.size());
        assertEquals(0, prioritizedList.size());
    }

    @Test
    void shouldNotDeleteSubtaskByIdIfIncorrectId() {
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));

        taskManager.deleteSubTaskById(20);
        List<Task> subtaskList = new ArrayList<>(taskManager.getSubTaskList());

        assertEquals(List.of(subtask), subtaskList);
    }

    @Test
    void shouldReturnListOfSubtasks() {
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));

        List<Task> epicList = new ArrayList<>(taskManager.getEpicList());
        List<Task> subtaskList = new ArrayList<>(taskManager.getSubTaskList());

        assertNotNull(subtaskList);
        assertEquals(1, subtaskList.size());
        assertTrue(subtaskList.contains(subtask));
        assertTrue(epicList.contains(epic));
    }

    @Test
    void shouldUpdateSubtaskToStatusIN_PROGRESS(){
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));

        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);

        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
    }

    @Test
    void shouldUpdateSubtaskToStatusDONE(){
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));

        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);

        assertEquals(Status.DONE, subtask.getStatus());
    }

    @Test
    void shouldNotUpdateSubtaskIfEmpty(){
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));

        taskManager.updateSubtask(null);

        assertNotNull(subtask);
    }

    // тесты для эпиков

    @Test
    void shouldCreateEpic() {
        Epic epic1 = taskManager.createEpic(newEpic());

        List<Task> epicList = new ArrayList<>(taskManager.getEpicList());

        assertNotNull(epic1.getStatus());
        assertEquals(Status.NEW, epic1.getStatus());
        assertEquals(List.of(epic1), epicList);
        assertEquals(Collections.EMPTY_LIST, epic1.getSubtasks());
    }

    @Test
    void shouldNotCreateAEpicIfEmpty() {
        Epic epic1 = taskManager.createEpic(null);

        List<Task> epicList = new ArrayList<>(taskManager.getEpicList());

        assertNull(epic1);
        assertEquals(Collections.EMPTY_LIST, epicList);
    }

    @Test
    void shouldReturnCreatedGetEpicById() {
        taskManager.createEpic(newEpic());
        Epic epic1 = taskManager.getEpicById(1);

        List<Task> epicList = new ArrayList<>(taskManager.getEpicList());

        assertNotNull(epic1.getStatus());
        assertEquals(1, epic1.getId());
        assertEquals(List.of(epic1), epicList);
    }

    @Test
    void shouldReturnNullIfCreatedGetEpicEmpty() {
        taskManager.createEpic(null);
        Epic epic = taskManager.getEpicById(0);
        List<Task> epicList = new ArrayList<>(taskManager.getEpicList());

        assertNull(epic);
        assertEquals(Collections.EMPTY_LIST, epicList);
    }

    @Test
    void shouldDeleteEpic(){
        Epic epic = taskManager.createEpic(newEpic());
        taskManager.deleteEpics();

        assertEquals(Collections.EMPTY_LIST, taskManager.getEpicList());
    }

    @Test
    void shouldNotDeleteEpicIfEmpty(){
        Epic epic = taskManager.createEpic(newEpic());
        taskManager.deleteEpics();

        List<Task> epicList = new ArrayList<>(taskManager.getEpicList());
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();

        assertEquals(0, epicList.size());
        assertEquals(0, prioritizedList.size());
    }

    @Test
    void shouldDeleteEpicById() {
        Epic epic = taskManager.createEpic(newEpic());

        taskManager.deleteEpicById(epic.getId());

        List<Task> epicList = new ArrayList<>(taskManager.getEpicList());
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();

        assertEquals(0, epicList.size());
        assertEquals(0, prioritizedList.size());
    }

    @Test
    void shouldNotDeleteEpicByIdIfIncorrectId() {
        Epic epic = taskManager.createEpic(newEpic());

        taskManager.deleteEpicById(20);
        List<Task> epicList = new ArrayList<>(taskManager.getEpicList());

        assertEquals(List.of(epic), epicList);
    }

    @Test
    void shouldReturnListOfEpic() {
        Epic epic = taskManager.createEpic(newEpic());

        List<Task> epicList = new ArrayList<>(taskManager.getEpicList());

        assertNotNull(epicList);
        assertEquals(1, epicList.size());
    }

    @Test
    void shouldUpdateEpicToStatusIN_PROGRESS(){
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));

        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldUpdateEpicToStatusDONE(){
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));

        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldUpdateEpicToStatusIN_PROGRESSWithTwoSubtasksDoneAndInProgress(){
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));
        Subtask subtask2 = taskManager.createSubTask(newSubtask2(epic));

        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);


        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldUpdateEpicToStatusIN_PROGRESSWithTwoSubtasksNewAndDone(){
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));
        Subtask subtask2 = taskManager.createSubTask(newSubtask2(epic));

        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        subtask2.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask2);


        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldUpdateEpicToStatusNewWithTwoSubtasksNew(){
        Epic epic = taskManager.createEpic(newEpic());
        Subtask subtask = taskManager.createSubTask(newSubtask(epic));
        Subtask subtask2 = taskManager.createSubTask(newSubtask2(epic));

        assertEquals(Status.NEW, epic.getStatus());
    }



    @Test
    void shouldNotUpdateEpicIfEmpty(){
        Epic epic = taskManager.createEpic(newEpic());

        taskManager.updateEpic(null);

        assertNotNull(epic);
    }


}