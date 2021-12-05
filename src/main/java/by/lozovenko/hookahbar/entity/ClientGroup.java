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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{clientGroupId=").append(clientGroupId);
        sb.append(", relaxTime=").append(relaxTime);
        sb.append(", clientsNumber=").append(clients.size());
        sb.append(", state=").append(state);
        sb.append('}');
        return sb.toString();
    }
}
