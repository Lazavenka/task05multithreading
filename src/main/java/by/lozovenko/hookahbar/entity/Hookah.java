package by.lozovenko.hookahbar.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Hookah {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int hookahId;
    private final AtomicBoolean isBusy;

    public Hookah(int hookahId) {
        this.hookahId = hookahId;
        isBusy = new AtomicBoolean(false);
    }

    public boolean isBusy() {
        return isBusy.get();
    }
    public void setBusy(){
        isBusy.set(true);
    }
    public int getHookahId() {
        return hookahId;
    }

    public void serveClientGroup(ClientGroup clientGroup, LoungeManager manager) {
        int relaxTime = clientGroup.getRelaxTime();
        HookahLounge hookahLounge = HookahLounge.getInstance();
        try {
            clientGroup.setState(ClientGroupState.SMOKING);
            LOGGER.log(Level.INFO, "Group #{} served by manager #{} at hookah #{}.",
                    clientGroup, manager.getManagerId(), hookahId);
            hookahLounge.releaseManager(manager);
            TimeUnit.MILLISECONDS.sleep(relaxTime);
        } catch (InterruptedException e) {
            LOGGER.log(Level.ERROR, "Thread interrupt exception while sleeping in serveClientGroup: ", e);
            Thread.currentThread().interrupt();
        } finally {
            isBusy.set(false);
            clientGroup.setState(ClientGroupState.SERVED);
            LOGGER.log(Level.INFO, "Group #{} relaxed and leave hookah lounge.", clientGroup);

        }
        hookahLounge.callManager().checkWaitingLines();
    }
}
