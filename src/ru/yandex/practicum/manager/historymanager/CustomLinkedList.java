package ru.yandex.practicum.manager.historymanager;

import ru.yandex.practicum.tasks.Task;
import java.util.ArrayList;
import java.util.List;


class CustomLinkedList {

    private Node<Task> first;
    private Node<Task> last;

    public Node<Task> linkLast(Task task) {
        Node<Task> l = last;
        Node<Task> newNode = new Node<>(l, task, null);

        if (l == null)
            first = newNode;
        else
            l.next = newNode;

        last = newNode;

        return newNode;
    }

    public List<Task> getTasks() {

        List<Task> tasks = new ArrayList<>();

        Node<Task> element = first;

        while (element != null) {
            tasks.add(element.item);
            element = element.next;
        }
        return tasks;
    }

    public void removeNode(Node<Task> node) {

        if (node == null)
            return;

        if (node.equals(first)) {
            first = node.next;

            if (node.next != null)
                node.next.prev = null;

        } else {
            node.prev.next = node.next;

            if (node.next != null)
                node.next.prev = node.prev;
        }
    }

}
