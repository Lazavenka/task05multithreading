package by.lozovenko.hookahbar.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Hookah{
    private static final Logger LOGGER = LogManager.getLogger();
    private final int hookahId;
    private AtomicBoolean isBusy;
    public Hookah(int hookahId) {
        this.hookahId = hookahId;
        isBusy = new AtomicBoolean(false);
    }


    public boolean isBusy() {
        return isBusy.get();
    }

    public int getHookahId() {
        return hookahId;
    }

    public void serveClientGroup(ClientGroup clientGroup){
        int relaxTime = clientGroup.getRelaxTime();
        try {
            isBusy.set(true);
            clientGroup.setState(ClientGroupState.SMOKING);
            TimeUnit.SECONDS.sleep(relaxTime);
            LOGGER.log(Level.INFO, "Group #{}" );
        }catch (InterruptedException e){
            LOGGER.log(Level.ERROR, "Thread interrupt exception while sleeping: ", e);
        }finally {
            isBusy.set(false);
            clientGroup.setState(ClientGroupState.SERVED);
            LoungeManager manager = LoungeManager.getInstance();
            manager.checkWaitingLines();
        }
    }
}
