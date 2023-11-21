import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.taskmanager.FileBackedTasksManager;
import ru.yandex.practicum.manager.taskmanager.TaskManager;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;

import java.time.Instant;

public class Main {
        public static void main(String[] args) {
            TaskManager taskManager = Managers.getDefault("resources/result.csv");

            Task task1 = taskManager.createTask(new Task("Отдых", "поехать на море",
                    Status.NEW, 1L, Instant.ofEpochMilli(1700492000L)));


            Epic epic1 = taskManager.createEpic(new Epic("Дом", "любимый дом",
                    Status.NEW, 1L, Instant.ofEpochMilli(1700492500L)));


            Subtask subtask1 = taskManager.createSubTask(new Subtask("Купить молоко", "3.2%",
                    Status.NEW, 1L, Instant.ofEpochMilli(1700683200L), epic1.getId()));


            Subtask subtask2 = taskManager.createSubTask(new Subtask("Купить кофе", "черный молотый",
                    Status.NEW, 1L, Instant.ofEpochMilli(1700769600L), epic1.getId()));


            Subtask subtask3 = taskManager.createSubTask(new Subtask("Купить корм", "Royal Canin до 6 месяцев",
                    Status.NEW, 1L, Instant.ofEpochMilli(1700856000L), epic1.getId()));


            // Вывод всего

            System.out.println(taskManager.getTaskList());
            System.out.println(taskManager.getEpicList());
            System.out.println(taskManager.getSubTaskList());


            // Обновления статусов
            System.out.println("Производится обновление статусов...");
            subtask1.setStatus(Status.IN_PROGRESS);
            taskManager.updateSubtask(subtask1);

            subtask2.setStatus(Status.DONE);
            taskManager.updateSubtask(subtask2);

            subtask3.setStatus(Status.DONE);
            taskManager.updateSubtask(subtask3);

            task1.setStatus(Status.IN_PROGRESS);
            taskManager.updateTask(task1);


            System.out.println("Обновление статусов завершено.");

            // Вывод всего
            System.out.println(taskManager.getTaskList());
            System.out.println(taskManager.getEpicList());
            System.out.println(taskManager.getSubTaskList());


            // Вывод всего
            System.out.println(taskManager.getTaskList());
            System.out.println(taskManager.getEpicList());
            System.out.println(taskManager.getSubTaskList());


            // get history
            System.out.println("Просматриваем задачи для истории.");
            taskManager.getTaskById(task1.getId());
            taskManager.getTaskById(task1.getId());
            taskManager.getEpicById(epic1.getId());
            taskManager.getTaskById(task1.getId());
            taskManager.getTaskById(task1.getId());
            taskManager.getTaskById(task1.getId());
            taskManager.getSubTaskById(subtask2.getId());
            System.out.println("Просмотр завершён.");

            System.out.println("Вывод истории по отдельности...");
            for (Task task : taskManager.getHistory()) {
                System.out.println(task);
            }

            System.out.println("Вывод истории по отдельности...");
            for (Task task : taskManager.getHistory()) {
                System.out.println(task);
            }

            System.out.println("Вывод приоритетного списка по отдельности...");
            for (Task task : taskManager.getPrioritizedTasks()) {
                System.out.println(task);
            }
        }
    }
