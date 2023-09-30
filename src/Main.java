import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.taskmanager.TaskManager;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;

    public class Main {
        public static void main(String[] args) {
            TaskManager taskManager = Managers.getDefault();

            Task task1 = taskManager.createTask(new Task("Отдых", "поехать на море"));
            Task task2 = taskManager.createTask(new Task("Дом", "вынести мусор"));

            Epic epic1 = taskManager.createEpic(new Epic("Дом", "любимый дом"));
            Epic epic2 = taskManager.createEpic(new Epic("Кот", "любимый кот"));

            Subtask subtask1 = taskManager.createSubTask(new Subtask("Купить молоко", "3.2%", epic1.getId()));
            Subtask subtask2 = taskManager.createSubTask(new Subtask("Купить кофе", "черный, молотый", epic1.getId()));
            Subtask subtask3 = taskManager.createSubTask(new Subtask("Купить корм", "Royal Canin, до 6 месяцев", epic2.getId()));

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

            task2.setStatus(Status.DONE);
            taskManager.updateTask(task2);

            System.out.println("Обновление статусов завершено.");

            // Вывод всего
            System.out.println(taskManager.getTaskList());
            System.out.println(taskManager.getEpicList());
            System.out.println(taskManager.getSubTaskList());

            // Удаление тасков
            taskManager.deleteTasks();
            System.out.println("Все задачи удалены");


            // Вывод всего
            System.out.println(taskManager.getTaskList());
            System.out.println(taskManager.getEpicList());
            System.out.println(taskManager.getSubTaskList());

            // Удаление всех сабтасков
            taskManager.deleteSubTasks();
            System.out.println("Все подзадачи удалены");
            System.out.println(taskManager.getEpicList());


            Subtask subtask4 = taskManager.createSubTask(new Subtask("Купить корм", "Royal Canin, до 6 месяцев", epic1.getId()));
            subtask4.setStatus(Status.IN_PROGRESS);
            taskManager.updateSubtask(subtask4);
            Subtask subtask5 = taskManager.createSubTask(new Subtask("Купить корм", "Royal Canin, до 6 месяцев", epic2.getId()));
            subtask5.setStatus(Status.IN_PROGRESS);
            taskManager.updateSubtask(subtask5);
            System.out.println("Добавлены новые подзадачи для эпиков.");
            System.out.println(taskManager.getEpicList());

            // get history
            System.out.println("Просматриваем задачи для истории.");
            taskManager.getEpicById(3);
            taskManager.getEpicById(3);
            taskManager.getEpicById(4);
            taskManager.getEpicById(3);
            taskManager.getEpicById(3);
            taskManager.getEpicById(3);
            taskManager.getEpicById(3);
            taskManager.getEpicById(3);
            taskManager.getEpicById(3);
            taskManager.getEpicById(3);
            taskManager.getEpicById(4);
            taskManager.getTaskById(1);
            System.out.println("Просмотр завершён.");

            System.out.println("Вывод истории по отдельности...");
            for (Task task : taskManager.getHistory()) {
                System.out.println(task);
            }
        }
    }
