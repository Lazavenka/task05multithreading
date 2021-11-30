package by.lozovenko.hookahbar.entity;

import java.util.List;


public class ClientGroup implements Runnable {
    private List<Client> clients;

    private final int clientGroupId;
    private final int relaxTime;
    private ClientGroupState state;

    public ClientGroup(List<Client> clients, int clientGroupId, int relaxTime){
        this.clients = clients;
        this.clientGroupId= clientGroupId;
        this.relaxTime = relaxTime;
        state = ClientGroupState.NEW;
    }
    @Override
    public void run(){
        HookahLounge lounge = HookahLounge.getInstance();
        LoungeManager loungeManager = lounge.callManager();
        loungeManager.serveClientGroup(this);
        if (state == ClientGroupState.WAITING_INSIDE){

        }
        if (state == ClientGroupState.WAITING_OUTSIDE){

        }
    }

    public int getClientGroupId() {
        return clientGroupId;
    }

    public int getRelaxTime(){
        return relaxTime;
    }

    public ClientGroupState getState() {
        return state;
    }

    public void setState(ClientGroupState state) {
        this.state = state;
    }
}
