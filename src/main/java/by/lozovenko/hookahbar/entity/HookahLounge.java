package by.lozovenko.hookahbar.entity;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HookahLounge {
    private static HookahLounge instance;
    public static final int WAITING_QUEUE_SIZE = 10;
    private List<Hookah> hookahs;
    private WaitingQueue insideWaitingQueue;
    private WaitingQueue outsideWaitingQueue;

    private static AtomicBoolean isCreated = new AtomicBoolean(false);
    private static Lock lock = new ReentrantLock(true);

    private HookahLounge(){
        hookahs = new ArrayList<>();
        insideWaitingQueue = new WaitingQueue(WAITING_QUEUE_SIZE);
        outsideWaitingQueue = new WaitingQueue();
        for (int i = 0; i< 5; i++) {        //TODO reader and parser for initialization
            hookahs.add(new Hookah(i));
        }
    }

    public static HookahLounge getInstance(){
        if (!isCreated.get()){
            try {
                lock.lock();
                if (instance == null){
                    instance = new HookahLounge();
                }
            }
            finally {
                lock.unlock();
            }
        }
        return instance;
    }
    public Optional<Hookah> getFreeHookah(){
        return hookahs.stream().filter(h -> !h.isBusy()).findFirst();
    }
    public LoungeManager callManager(){
        return LoungeManager.getInstance();
    }

    public WaitingQueue getInsideWaitingQueue() {
        return insideWaitingQueue;
    }

    public WaitingQueue getOutsideWaitingQueue() {
        return outsideWaitingQueue;
    }
}
