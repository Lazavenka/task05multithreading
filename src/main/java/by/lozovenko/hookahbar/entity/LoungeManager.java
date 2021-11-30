package by.lozovenko.hookahbar.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoungeManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static LoungeManager instance;
    private final HookahLounge hookahLounge;
    private static AtomicBoolean isCreated = new AtomicBoolean(false);
    private static Lock lock = new ReentrantLock(true);

    private LoungeManager(HookahLounge hookahLounge) {
        this.hookahLounge = hookahLounge;
    }

    public static LoungeManager getInstance() {
        if (!isCreated.get()) {
            try {
                lock.lock();
                if (instance == null) {
                    instance = new LoungeManager(HookahLounge.getInstance());
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public void serveClientGroup(ClientGroup clientGroup) {
        Optional<Hookah> optionalFreeHookah = hookahLounge.getFreeHookah();
        optionalFreeHookah.ifPresentOrElse(hookah -> {
            hookah.serveClientGroup(clientGroup);
            LOGGER.log(Level.INFO, "Manager find free hookah #{}.", hookah.getHookahId());
        }, () -> putClientGroupInLine(clientGroup));
    }

    private void putClientGroupInLine(ClientGroup clientGroup) {
        WaitingQueue insideWaitingQueue = hookahLounge.getInsideWaitingQueue();
        int insideQueueFreePlaces = insideWaitingQueue.getFreePlaces();
        if (insideQueueFreePlaces > 0) {
            insideWaitingQueue.offer(clientGroup);
            clientGroup.setState(ClientGroupState.WAITING_INSIDE);
            LOGGER.log(Level.INFO, "Manager put group #{} into inside queue.", clientGroup.getClientGroupId());
        } else {
            hookahLounge.getOutsideWaitingQueue().offer(clientGroup);
            clientGroup.setState(ClientGroupState.WAITING_OUTSIDE);
            LOGGER.log(Level.INFO, "Manager put group #{} into outside queue.", clientGroup.getClientGroupId());
        }
    }

    public void checkWaitingLines() {
        WaitingQueue insideWaitingQueue = hookahLounge.getInsideWaitingQueue();
        boolean isInsideQueueEmpty = insideWaitingQueue.isEmpty();
        if (!isInsideQueueEmpty) {
            ClientGroup firstInQueueGroup = insideWaitingQueue.poll();
            serveClientGroup(firstInQueueGroup);
            LOGGER.log(Level.INFO, "Manager put group #{} into inside queue.", firstInQueueGroup.getClientGroupId());
        }
    }
}
