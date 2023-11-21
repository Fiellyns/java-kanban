package ru.yandex.practicum.manager.taskmanager;

import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.utils.Formatting;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final Path filePath;

    public FileBackedTasksManager(String path) {
        this.filePath = Path.of(path);
    }

    @Override
    public Task createTask(Task task) {
        var savedTask = super.createTask(task);
        save();
        return savedTask;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        var savedSubTask = super.createSubTask(subtask);
        save();
        return savedSubTask;
    }
    @Override
    public Epic createEpic(Epic epic) {
        var savedEpic = super.createEpic(epic);
        save();
        return savedEpic;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int epicID) {
        super.deleteEpicById(epicID);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }
    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    // обновление сабтаска
    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    // обновление эпика
    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }


    @Override
    public Task getTaskById(int idNumber) {
        var savedTask = super.getTaskById(idNumber);
        save();
        return savedTask;
    }


    @Override
    public Subtask getSubTaskById(int idNumber) {
        var savedSubTask = super.getSubTaskById(idNumber);
        save();
        return savedSubTask;
    }

    @Override
    public Epic getEpicById(int idNumber) {
        var savedEpic = super.getEpicById(idNumber);
        save();
        return savedEpic;
    }


    protected void save() {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.toFile(), StandardCharsets.UTF_8));
             BufferedReader br = new BufferedReader(new FileReader(filePath.toFile(), StandardCharsets.UTF_8))) {

            // BufferedReader нужен здесь, чтобы проверять пустой ли файл,
            // если файл пуст, то добавляется шапка id,type,name и др.

            if (br.readLine() == null) {
                String header = "id,type,name,status,description,startTime,duration,epic" + "\n";
                bw.write(header);
            }

            String values = Formatting.tasksToString(this) + "\n" + Formatting.historyToString(historyManager);
            bw.write(values);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл" + e.getMessage());
        }
    }

    protected static FileBackedTasksManager loadFromFile (String path) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(path);

        int initialID = 0;

        try {

            var fileName = Files.readString(Path.of(path), StandardCharsets.UTF_8);

            var lines = fileName.split("\n");

            for (int i = 1; i < lines.length - 2; i++) {

                var task = Formatting.tasksFromString(lines[i]);
                var type = lines[i].split(",")[1];

                if (task.getId() > initialID)
                    initialID = task.getId();


                if (TaskType.valueOf(type).equals(TaskType.TASK)) {

                    fileBackedTasksManager.createTask(task);
                    historyManager.add(fileBackedTasksManager.getTaskById(task.getId()));

                }

                if (TaskType.valueOf(type).equals(TaskType.EPIC)) {

                    var epic = (Epic) task;
                    fileBackedTasksManager.createEpic(epic);
                    historyManager.add(fileBackedTasksManager.getEpicById(epic.getId()));

                }

                if (TaskType.valueOf(type).equals(TaskType.SUBTASK)) {

                    var subtask = (Subtask) task;
                    fileBackedTasksManager.createSubTask(subtask);
                    historyManager.add(fileBackedTasksManager.getSubTaskById(subtask.getId()));

                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла: " + e.getMessage());
        }

        fileBackedTasksManager.id = initialID;
        return fileBackedTasksManager;
    }
}
