package by.lozovenko.hookahbar.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class LoungeManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static LoungeManager instance;
    private final HookahLounge hookahLounge;
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
            hookah.serveClientGroup(clientGroup, this);
            LOGGER.log(Level.INFO, "Manager #{} find free hookah #{}.",managerId ,hookah.getHookahId());
        }, () -> putClientGroupInLine(clientGroup));
    }

    private void putClientGroupInLine(ClientGroup clientGroup) {
        WaitingQueue insideWaitingQueue = hookahLounge.getInsideWaitingQueue();
        int insideQueueFreePlaces = insideWaitingQueue.getFreePlaces();
        if (insideQueueFreePlaces > 0) {
            insideWaitingQueue.offer(clientGroup);
            clientGroup.setState(ClientGroupState.WAITING_INSIDE);
            LOGGER.log(Level.INFO, "Manager #{} put group #{} into inside queue.", managerId,clientGroup.getClientGroupId());

        } else {
            hookahLounge.getOutsideWaitingQueue().offer(clientGroup);
            clientGroup.setState(ClientGroupState.WAITING_OUTSIDE);
            LOGGER.log(Level.INFO, "Manager #{}  put group #{} into outside queue.", managerId, clientGroup.getClientGroupId());

        }
        hookahLounge.releaseManager(this);
    }

    public void checkWaitingLines() {
        WaitingQueue insideWaitingQueue = hookahLounge.getInsideWaitingQueue();
        boolean isInsideQueueEmpty = insideWaitingQueue.isEmpty();
        WaitingQueue outsideWaitingQueue = hookahLounge.getOutsideWaitingQueue();
        boolean isOutsideQueueEmpty = outsideWaitingQueue.isEmpty();
        LOGGER.log(Level.DEBUG,"inside {} outside {}", isInsideQueueEmpty, isOutsideQueueEmpty);
        if (!isInsideQueueEmpty) {
            ClientGroup firstInQueueGroup = insideWaitingQueue.poll();
            LOGGER.log(Level.DEBUG, "Polled group {} inside", firstInQueueGroup);
            if(!isOutsideQueueEmpty){
                ClientGroup group = outsideWaitingQueue.poll();
                LOGGER.log(Level.DEBUG, "Polled group {} outside", group);
                boolean success = insideWaitingQueue.offer(group);
                group.setState(ClientGroupState.WAITING_INSIDE);
                LOGGER.log(Level.INFO, "Manager #{} moved group #{} from outside to inside queue - {}"
                        , managerId,group.getClientGroupId(), success);
            }
            LOGGER.log(Level.INFO, "Manager #{} polls group #{} from inside queue.", managerId,firstInQueueGroup.getClientGroupId());
            serveClientGroup(firstInQueueGroup);

        }else {
            hookahLounge.releaseManager(this);
        }
    }
}
