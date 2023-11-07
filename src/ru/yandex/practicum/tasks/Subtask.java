package ru.yandex.practicum.tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int epicID;

    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask (int id,
                    String name,
                    Status status,
                    String description,
                    int epicID) {
        super(name, description);
        this.id = id;
        this.status = status;
        this.taskType = TaskType.SUBTASK;
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }


    public String getAllDescription() {
        return id + ","
                + taskType + ","
                + name + ","
                + status + ","
                + description + ","
                + epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;

        Subtask that = (Subtask) o;

        return Objects.equals(this.epicID, that.epicID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicID);
    }
}