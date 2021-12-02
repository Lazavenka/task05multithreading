package by.lozovenko.hookahbar.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HookahLounge {
    private static final Logger LOGGER = LogManager.getLogger();

    private static HookahLounge instance;
    public static final int WAITING_QUEUE_SIZE = 10;
    public static final int HOOKAHS_NUMBER = 5;
    public static final int MANAGERS_NUMBER = 6;
    private final List<Hookah> hookahs;
    private final Deque<LoungeManager> loungeManagers;
    private final WaitingQueue insideWaitingQueue;
    private final WaitingQueue outsideWaitingQueue;

    private static AtomicBoolean isCreated = new AtomicBoolean(false);
    private static Lock lock = new ReentrantLock();
    private Lock hookahsLock = new ReentrantLock();
    private Lock managersLock = new ReentrantLock();

    private Semaphore semaphore = new Semaphore(MANAGERS_NUMBER); // fixme INITIALIZATION!

    private HookahLounge(){
        hookahs = new ArrayList<>(HOOKAHS_NUMBER);
        loungeManagers = new ArrayDeque<>(MANAGERS_NUMBER);
        insideWaitingQueue = new WaitingQueue(WAITING_QUEUE_SIZE);
        outsideWaitingQueue = new WaitingQueue();
        for (int i = 0; i< HOOKAHS_NUMBER; i++) {        //TODO reader and parser for initialization
            hookahs.add(new Hookah(i+1));

        }
        for (int i = 0; i < MANAGERS_NUMBER; i++) {
            loungeManagers.add(new LoungeManager(this,i+1));
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
        Optional<Hookah> optionalHookah;
        try {
            hookahsLock.lock();
            optionalHookah = hookahs.stream().filter(h -> !h.isBusy()).findFirst();
        }finally {
            hookahsLock.unlock();
        }
        return optionalHookah;
    }
    public LoungeManager callManager(){
        LoungeManager manager = null;
        try {
            semaphore.acquire();
            managersLock.lock();
            manager = loungeManagers.poll();
            LOGGER.log(Level.INFO, "Manager #{} is start working.", manager.getManagerId());
        } catch (InterruptedException e) {
            LOGGER.log(Level.ERROR, "InterruptedException in callManager method.", e);
        } finally {
            managersLock.unlock();
        }
        return manager;
    }
    public void releaseManager(LoungeManager manager){ //TODO make something with that
        try {
            managersLock.lock();
            loungeManagers.addLast(manager);
            LOGGER.log(Level.INFO, "Manager #{} is released.", manager.getManagerId());
        }finally {
            managersLock.unlock();
        }
        semaphore.release();
    }
    public WaitingQueue getInsideWaitingQueue() {
        return insideWaitingQueue;
    }

    public WaitingQueue getOutsideWaitingQueue() {
        return outsideWaitingQueue;
    }
}
