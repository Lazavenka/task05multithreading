package by.lozovenko.hookahbar.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class LoungeManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private HookahLounge hookahLounge;
    private int managerId;


    public LoungeManager(HookahLounge hookahLounge, int managerId) {
        this.hookahLounge = hookahLounge;
        this.managerId = managerId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void serveClientGroup(ClientGroup clientGroup) {
        Optional<Hookah> optionalFreeHookah = hookahLounge.getFreeHookah();
        optionalFreeHookah.ifPresentOrElse(hookah -> {
            LOGGER.log(Level.INFO, "Manager #{} find free hookah #{}.", managerId, hookah.getHookahId());
            hookah.serveClientGroup(clientGroup, this);
        }, () -> {
            LOGGER.log(Level.INFO, "No free hookah. Group #{} moved to queues.", clientGroup.getClientGroupId());
            putClientGroupInLine(clientGroup);
        });
    }

    private void putClientGroupInLine(ClientGroup clientGroup) {
        WaitingQueue insideWaitingQueue = hookahLounge.getInsideWaitingQueue();
        int insideQueueFreePlaces = insideWaitingQueue.getFreePlaces();
        if (insideQueueFreePlaces > 0) {
            LOGGER.log(Level.INFO, "Manager #{} put group #{} into inside queue.", managerId, clientGroup.getClientGroupId());
            insideWaitingQueue.offer(clientGroup);
            clientGroup.setState(ClientGroupState.WAITING_INSIDE);

        } else {
            LOGGER.log(Level.INFO, "Manager #{}  put group #{} into outside queue.", managerId, clientGroup.getClientGroupId());
            hookahLounge.getOutsideWaitingQueue().offer(clientGroup);
            clientGroup.setState(ClientGroupState.WAITING_OUTSIDE);
        }
        hookahLounge.releaseManager(this);
    }

    public void checkWaitingLines() {
        LOGGER.log(Level.DEBUG, "Manager #{} checks queues.", this.managerId);
        WaitingQueue insideWaitingQueue = hookahLounge.getInsideWaitingQueue();
        boolean isInsideQueueEmpty = insideWaitingQueue.isEmpty();
        WaitingQueue outsideWaitingQueue = hookahLounge.getOutsideWaitingQueue();
        boolean isOutsideQueueEmpty = outsideWaitingQueue.isEmpty();
        if (!isInsideQueueEmpty) {
            ClientGroup firstInQueueGroup = insideWaitingQueue.poll();
            LOGGER.log(Level.INFO, "Manager #{} polls group #{} from inside queue.", managerId, firstInQueueGroup);
            if (!isOutsideQueueEmpty) {
                ClientGroup group = outsideWaitingQueue.poll();
                boolean success = insideWaitingQueue.offer(group);
                group.setState(ClientGroupState.WAITING_INSIDE);
                LOGGER.log(Level.INFO, "Manager #{} moved group #{} from outside to inside queue - {}"
                        , managerId, group, success);
            }
            serveClientGroup(firstInQueueGroup);
        } else {
            hookahLounge.releaseManager(this);
        }
    }
}
