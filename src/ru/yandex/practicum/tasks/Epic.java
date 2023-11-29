package ru.yandex.practicum.tasks;

import ru.yandex.practicum.utils.Status;
import ru.yandex.practicum.utils.TaskType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasks = new ArrayList<>();
    protected Instant endTime;


    public Epic(String name,
                String description,
                Status status,
                Long duration,
                Instant startTime) {

        super(name, description, status, duration, startTime);
        this.taskType = TaskType.EPIC;
        this.endTime = super.getEndTime();
    }

    public Epic(int id,
                String name,
                String description,
                Status status,
                Long duration,
                Instant startTime) {

        super(id, name, description, status, duration, startTime);
        this.taskType = TaskType.EPIC;
        this.endTime = super.getEndTime();
    }


    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubtasks() {
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
                + description + ","
                + getStartTime() + ","
                + duration;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
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
