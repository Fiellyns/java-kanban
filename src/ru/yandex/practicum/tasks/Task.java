package ru.yandex.practicum.tasks;

import ru.yandex.practicum.utils.Status;
import ru.yandex.practicum.utils.TaskType;

import java.time.Instant;
import java.util.Objects;
import java.time.temporal.ChronoUnit;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected TaskType taskType;
    protected Instant startTime;
    protected long duration;

    public Task (String name,
                 String description,
                 Status status,
                 Long duration,
                 Instant startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.taskType = TaskType.TASK;
    }

    public Task (int id,
                 String name,
                 String description,
                 Status status,
                 Long duration,
                 Instant startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.taskType = TaskType.TASK;
    }

    public Instant getEndTime() {
        return startTime.plus(duration, ChronoUnit.MINUTES);
    }

    public Instant getStartTime() {
        return startTime;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAllDescription() {
        return id + ","
                + taskType + ","
                + name + ","
                + status + ","
                + description + ","
                + getStartTime() + ","
                + duration;
    }


    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
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
        if (!(o instanceof Task)) return false;
        Task that = (Task) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.description, that.description)
                && Objects.equals(this.id, that.id)
                && Objects.equals(this.status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

}
