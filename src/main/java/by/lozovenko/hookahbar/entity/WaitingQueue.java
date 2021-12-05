package by.lozovenko.hookahbar.entity;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class WaitingQueue {
    private int freePlaces;
    private Queue<ClientGroup> clientGroups;
    private Lock lock = new ReentrantLock();

    public WaitingQueue() {
        freePlaces = Integer.MAX_VALUE;
        clientGroups = new ArrayDeque<>();
    }

    public WaitingQueue(int freePlaces) {
        clientGroups = new ArrayDeque<>(freePlaces);
        this.freePlaces = freePlaces;
    }

    public boolean offer(ClientGroup clientGroup) {
        boolean result;
        try {
            lock.lock();
            freePlaces--;
            result = clientGroups.offer(clientGroup);
        } finally {
            lock.unlock();
        }
        return result;
    }

    public ClientGroup poll() {
        ClientGroup clientGroup;
        try {
            lock.lock();
            freePlaces++;
            clientGroup = clientGroups.poll();
        } finally {
            lock.unlock();
        }
        return clientGroup;
    }

    public int getFreePlaces() {
        return freePlaces;
    }

    public boolean isEmpty() {
        return clientGroups.isEmpty();
    }

    public void forEach(Consumer<? super ClientGroup> action) {
        clientGroups.forEach(action);
    }
}
