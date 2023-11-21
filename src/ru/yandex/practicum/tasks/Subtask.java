package ru.yandex.practicum.tasks;

import java.time.Instant;
import java.util.Objects;

public class Subtask extends Task {
    private int epicID;


    public Subtask (String name,
                    String description,
                    Status status,
                    Long duration,
                    Instant startTime,
                    int epicID) {
        super(name, description, status, duration, startTime);
        this.taskType = TaskType.SUBTASK;
        this.epicID = epicID;
    }

    public Subtask (int id,
                    String name,
                    String description,
                    Status status,
                    Long duration,
                    Instant startTime,
                    int epicID) {
        super(id, name, description, status, duration, startTime);
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
                + getStartTime() + ","
                + duration + ","
                + epicID;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "epicID=" + epicID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", taskType=" + taskType +
                ", startTime=" + getStartTime().toEpochMilli() +
                ", duration=" + duration +
                ", endTime=" + getEndTime().toEpochMilli() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(this.epicID, that.epicID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicID);
    }
}