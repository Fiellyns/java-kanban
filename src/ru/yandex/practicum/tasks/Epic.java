package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.taskType = TaskType.EPIC;
        this.subtasks = new ArrayList<>();
    }

    public Epic(int id,
                String name,
                Status status,
                String description) {

        super(name, description);
        this.subtasks = new ArrayList<>();
        this.taskType = TaskType.EPIC;
        this.status = status;
        this.id = id;

    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask.getId());
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(Integer.valueOf(subtask.getId()));
    }

    public String getAllDescription() {
        return id + ","
                + taskType + ","
                + name + ","
                + status + ","
                + description;

    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subtasks=" + subtasks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;

        Epic that = (Epic) o;

        return Objects.equals(this.subtasks, that.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtasks);
    }
}
