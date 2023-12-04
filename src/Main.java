import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.taskmanager.HttpTaskManager;
import ru.yandex.practicum.server.HttpTaskServer;
import ru.yandex.practicum.server.KVServer;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.utils.Status;

import java.io.IOException;
import java.time.Instant;

public class Main {
        public static void main(String[] args) throws IOException {

            KVServer kvServer = new KVServer();
            kvServer.start();
            HttpTaskManager httpTaskManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
            HttpTaskServer httpTaskServer = new HttpTaskServer(httpTaskManager);
            httpTaskServer.start();

            Task task = httpTaskManager.createTask(new Task("Отдых", "поехать на море",
                    Status.NEW, 1L, Instant.ofEpochMilli(1700492000L)));


            Epic epic1 = httpTaskManager.createEpic(new Epic("Дом", "любимый дом",
                    Status.NEW, 1L, Instant.ofEpochMilli(1700492500L)));


            Subtask subtask1 = httpTaskManager.createSubTask(new Subtask("Купить молоко", "3.2%",
                    Status.NEW, 1L, Instant.ofEpochMilli(1700683200L), epic1.getId()));


            Subtask subtask2 = httpTaskManager.createSubTask(new Subtask("Купить кофе", "черный молотый",
                    Status.NEW, 1L, Instant.ofEpochMilli(1700769600L), epic1.getId()));


            Subtask subtask3 = httpTaskManager.createSubTask(new Subtask("Купить корм", "Royal Canin до 6 месяцев",
                    Status.NEW, 1L, Instant.ofEpochMilli(1700856000L), epic1.getId()));

            httpTaskManager.getSubTaskById(subtask1.getId());
            httpTaskManager.getTaskById(task.getId());



            System.out.println("\n=========================================================");
            System.out.println(httpTaskManager.getTaskList());
            System.out.println(httpTaskManager.getSubTaskList());
            System.out.println(httpTaskManager.getEpicList());
            System.out.println(httpTaskManager.getHistory());
            System.out.println(httpTaskManager.getPrioritizedTasks());
            System.out.println("=========================================================");

            HttpTaskManager httpTaskManagerAfterLoad = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
            httpTaskManagerAfterLoad.loadFromServer();

            System.out.println("\n=========================================================");
            System.out.println(httpTaskManagerAfterLoad.getTaskList());
            System.out.println(httpTaskManagerAfterLoad.getSubTaskList());
            System.out.println(httpTaskManagerAfterLoad.getEpicList());
            System.out.println(httpTaskManagerAfterLoad.getHistory());
            System.out.println(httpTaskManagerAfterLoad.getPrioritizedTasks());
            System.out.println("=========================================================");
            httpTaskServer.stop();
            kvServer.stop();
        }
    }
