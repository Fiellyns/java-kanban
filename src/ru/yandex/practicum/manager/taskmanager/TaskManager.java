package ru.yandex.practicum.manager.taskmanager;

import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    int getNextId();


    // получение задач
     ArrayList<Task> getTaskList();

    // получение эпиков
     ArrayList<Epic> getEpicList();

    // получение сабтасков
     ArrayList<Subtask> getSubTaskList();

    // получение задачи по индентификатору
     Task getTaskById(int idNumber);

    // получение эпика по индентификатору
     Epic getEpicById(int idNumber);

    // получение сабтаска по индентификатору
     Subtask getSubTaskById(int idNumber);

     // получение истории
    List<Task> getHistory();

    //создание задачи
     Task createTask(Task task);

    // создание эпика
     Epic createEpic(Epic epic);

    // создание сабтаска
     Subtask createSubTask(Subtask subtask);
    // удаление всех тасков
     void deleteTasks();

    // удаление всех сабтасков
     void deleteSubTasks();

    // удаление всех эпиков
     void deleteEpics();

    // удаление задачи по идентификатору
     void deleteTaskById(int id);

    // удаление сабтаска по идентификатору
    void deleteSubTaskById(int id);

    // удаление эпика по идентификатору
    void deleteEpicById(int epicID);

    // обновление задачи
    void updateTask(Task task);

    // обновление сабтаска
    void updateSubtask(Subtask subtask);

    // обновление эпика
    void updateEpic(Epic epic);

}
