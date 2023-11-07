package ru.yandex.practicum.manager.taskmanager;

import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.utils.Formatting;
import ru.yandex.practicum.manager.Managers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private static final Path filePath = Path.of("resources/result.csv");

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

            if (br.readLine() == null) {

                String header = "id,type,name,status,description,epic" + "\n";
                bw.write(header);

            }

            String values = Formatting.tasksToString(this) + "\n" + Formatting.historyToString(historyManager);
            bw.write(values);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл" + e.getMessage());
        }
    }

    public static FileBackedTasksManager loadFromFile (Path path) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();

        int initialID = 0;

        try {

            var fileName = Files.readString(path, StandardCharsets.UTF_8);

            var lines = fileName.split("\n");

            for (int i = 1; i < lines.length - 2; i++) {

                var task = Formatting.tasksFromString(lines[i]);
                var type = lines[i].split(",")[4];

                if (task.getId() > initialID)
                    initialID = task.getId();


                if (TaskType.valueOf(type).equals(TaskType.TASK)) {

                    fileBackedTasksManager.taskHashMap.put(task.getId(),task);
                    historyManager.add(fileBackedTasksManager.getTaskById(task.getId()));

                }

                if (TaskType.valueOf(type).equals(TaskType.EPIC)) {

                    var epic = (Epic) task;
                    fileBackedTasksManager.epicHashMap.put(epic.getId(),epic);
                    historyManager.add(fileBackedTasksManager.getEpicById(epic.getId()));

                }

                if (TaskType.valueOf(type).equals(TaskType.SUBTASK)) {

                    var subtask = (Subtask) task;
                    fileBackedTasksManager.subtaskHashMap.put(subtask.getId(),subtask);

                    if (!fileBackedTasksManager.epicHashMap.isEmpty()) {
                        Epic epicInSub = fileBackedTasksManager.epicHashMap.get(subtask.getEpicID());
                        epicInSub.getSubtasks().add(subtask.getId());
                    }
                    historyManager.add(fileBackedTasksManager.getSubTaskById(subtask.getId()));

                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла: " + e.getMessage());
        }

        fileBackedTasksManager.id = initialID;
        return fileBackedTasksManager;
    }

    public static void main(String[] args) {

        TaskManager fileManagerBeforeLoading = Managers.getDefault();

        Task task10 = fileManagerBeforeLoading.createTask(new Task("Отдых", "поехать на море"));
        Task task11 = fileManagerBeforeLoading.createTask(new Task("Дом", "вынести мусор"));

        Epic epic11 = fileManagerBeforeLoading.createEpic(new Epic("Дом", "любимый дом"));
        Epic epic12 = fileManagerBeforeLoading.createEpic(new Epic("Кот", "любимый кот"));

        Subtask subtask11 = fileManagerBeforeLoading.createSubTask(new Subtask("Купить молоко", "3.2%", epic11.getId()));
        Subtask subtask12 = fileManagerBeforeLoading.createSubTask(new Subtask("Купить кофе", "черный молотый", epic11.getId()));
        Subtask subtask13 = fileManagerBeforeLoading.createSubTask(new Subtask("Купить корм", "Royal Canin до 6 месяцев", epic11.getId()));

        // Обновления статусов
        System.out.println("Производится обновление статусов...");
        task10.setStatus(Status.IN_PROGRESS);
        fileManagerBeforeLoading.updateTask(task10);

        subtask11.setStatus(Status.DONE);
        fileManagerBeforeLoading.updateSubtask(subtask11);

        subtask13.setStatus(Status.DONE);
        fileManagerBeforeLoading.updateSubtask(subtask13);

        subtask12.setStatus(Status.IN_PROGRESS);
        fileManagerBeforeLoading.updateSubtask(subtask12);

        task11.setStatus(Status.IN_PROGRESS);
        fileManagerBeforeLoading.updateTask(task11);

        epic12.setStatus(Status.DONE);
        fileManagerBeforeLoading.updateEpic(epic12);

        System.out.println("Обновление статусов завершено.");

        System.out.println(fileManagerBeforeLoading.getTaskList());
        System.out.println(fileManagerBeforeLoading.getEpicList());
        System.out.println(fileManagerBeforeLoading.getSubTaskList());

        // get history
        System.out.println("Просматриваем задачи для истории.");
        fileManagerBeforeLoading.getTaskById(task10.getId());
        fileManagerBeforeLoading.getTaskById(task11.getId());
        fileManagerBeforeLoading.getEpicById(epic11.getId());
        fileManagerBeforeLoading.getEpicById(epic12.getId());
        fileManagerBeforeLoading.getSubTaskById(subtask11.getId());
        fileManagerBeforeLoading.getSubTaskById(subtask12.getId());
        fileManagerBeforeLoading.getSubTaskById(subtask13.getId());
        System.out.println("Просмотр завершён.");

        System.out.println("Вывод истории по отдельности...");
        for (Task task : fileManagerBeforeLoading.getHistory()) {
            System.out.println(task);
        }

        FileBackedTasksManager fileManagerAfterLoading = FileBackedTasksManager.loadFromFile(filePath);

        System.out.println("================================================");
        System.out.println("После загрузки");
        System.out.println(fileManagerAfterLoading.getTaskList());
        System.out.println(fileManagerAfterLoading.getEpicList());
        System.out.println(fileManagerAfterLoading.getSubTaskList());
    }
}
