# Java-Kanban
Этот проект представляет собой простое приложение Kanban, написанное на Java. Оно позволяет организовывать задачи в виде карточек на доске с тремя столбцами: "To Do", "In Progress" и "Done".
# Описание

Технологии: Java Core

Основная идея проекта заключается в возможности создавать три вида задач:
1. Обычные задачи - **Task**;
2. Большие задачи, включающие в себя другие подзадачи, - **Epic**;
3. Подзадачи, входящие в состав больших задач, - **Subtask**.

# Функциональные возможности
- Получение списка всех задач.
- Удаление всех задач.
- Получение по идентификатору.
- Создание.
- Обновление.
- Удаление по идентификатору.
- История просмотренных задач.

# Запуск проекта
Для запуска проекта необходимо:

1. Клонировать репозиторий.
2. Перейти в директорию проекта.
3. Собрать проект с помощью Maven.
```sh
git clone https://github.com/Fiellyns/java-kanban.git
cd java-kanban
mvn clean install
```
4. Запустить приложение.
``java -jar target/java-kanban-1.0-SNAPSHOT.jar``
