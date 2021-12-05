package by.lozovenko.hookahbar.entity;

import by.lozovenko.hookahbar.parser.CustomLoungeInitializer;
import by.lozovenko.hookahbar.parser.impl.CustomLoungeInitializerImpl;
import by.lozovenko.hookahbar.reader.CustomFileReader;
import by.lozovenko.hookahbar.reader.impl.CustomFileReaderImpl;
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

    public static final String INIT_FILEPATH = "data/Lounge.ini";

    public static final int INSIDE_WAITING_QUEUE_SIZE;
    public static final int HOOKAHS_NUMBER;
    public static final int MANAGERS_NUMBER;

    private static HookahLounge instance;

    private static AtomicBoolean isCreated = new AtomicBoolean(false);
    private static Lock singletonLock = new ReentrantLock();

    private final List<Hookah> hookahs;
    private final Deque<LoungeManager> loungeManagers;
    private final WaitingQueue insideWaitingQueue;
    private final WaitingQueue outsideWaitingQueue;
    private Lock hookahsLock = new ReentrantLock();
    private Lock managersLock = new ReentrantLock();
    private Semaphore semaphore = new Semaphore(MANAGERS_NUMBER);

    static {
        CustomFileReader fileReader = new CustomFileReaderImpl();
        List<String> initData = fileReader.readLinesFromFile(INIT_FILEPATH);
        CustomLoungeInitializer initializer = new CustomLoungeInitializerImpl();
        Map<String, Integer> initParameters = initializer.parseInitData(initData);
        int tempManagersNumber;
        int tempWaitingQueueSize;
        int tempHookahsNumber;
        if (!initParameters.isEmpty()) {
            tempHookahsNumber = initParameters.get(CustomLoungeInitializerImpl.HOOKAHS_PARAMETER_NAME);
            tempWaitingQueueSize = initParameters.get(CustomLoungeInitializerImpl.WAITING_QUEUE_PARAMETER_NAME);
            tempManagersNumber = initParameters.get(CustomLoungeInitializerImpl.MANAGERS_PARAMETER_NAME);
            LOGGER.log(Level.INFO, "Loaded initialization parameters for HookahLounge class:\n" +
                            "hookahs={}, insideWaitingQueueSize={}, loungeManagers={}", tempHookahsNumber,
                    tempWaitingQueueSize, tempManagersNumber);
        } else {
            tempHookahsNumber = 5;
            tempManagersNumber = 4;
            tempWaitingQueueSize = 10;
            LOGGER.log(Level.INFO, "INI file not found, preloaded default values:\n" +
                            "hookahs={}, insideWaitingQueueSize={}, loungeManagers={}", tempHookahsNumber,
                    tempWaitingQueueSize, tempManagersNumber);
        }

        MANAGERS_NUMBER = tempManagersNumber;
        INSIDE_WAITING_QUEUE_SIZE = tempWaitingQueueSize;
        HOOKAHS_NUMBER = tempHookahsNumber;
    }

    private HookahLounge() {
        hookahs = new ArrayList<>(HOOKAHS_NUMBER);
        loungeManagers = new ArrayDeque<>(MANAGERS_NUMBER);
        insideWaitingQueue = new WaitingQueue(INSIDE_WAITING_QUEUE_SIZE);
        outsideWaitingQueue = new WaitingQueue();
        for (int i = 0; i < HOOKAHS_NUMBER; i++) {
            hookahs.add(new Hookah(i + 1));
        }
        for (int i = 0; i < MANAGERS_NUMBER; i++) {
            loungeManagers.add(new LoungeManager(this, i + 1));
        }
    }

    public static HookahLounge getInstance() {
        if (!isCreated.get()) {
            try {
                singletonLock.lock();
                if (instance == null) {
                    instance = new HookahLounge();
                }
            } finally {
                singletonLock.unlock();
            }
        }
        return instance;
    }

    public Optional<Hookah> getFreeHookah() {
        Optional<Hookah> optionalHookah;
        try {
            hookahsLock.lock();
            optionalHookah = hookahs.stream().filter(h -> !h.isBusy()).findFirst();
        } finally {
            hookahsLock.unlock();
        }
        return optionalHookah;
    }

    public LoungeManager callManager() {
        LoungeManager manager = null;
        try {
            semaphore.acquire();
            managersLock.lock();
            manager = loungeManagers.poll();
            int managerId = manager.getManagerId();
            LOGGER.log(Level.INFO, "Manager #{} started working.", managerId);
        } catch (InterruptedException e) {
            LOGGER.log(Level.ERROR, "InterruptedException in callManager method.", e);
        } finally {
            managersLock.unlock();
        }
        return manager;
    }

    public void releaseManager(LoungeManager manager) {
        try {
            managersLock.lock();
            loungeManagers.addLast(manager);
            LOGGER.log(Level.INFO, "Manager #{} released.", manager.getManagerId());
        } finally {
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
