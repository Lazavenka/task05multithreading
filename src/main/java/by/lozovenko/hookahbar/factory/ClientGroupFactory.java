package by.lozovenko.hookahbar.factory;

import by.lozovenko.hookahbar.entity.Client;
import by.lozovenko.hookahbar.entity.ClientGroup;
import by.lozovenko.hookahbar.util.ClientIdGenerator;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ClientGroupFactory {
    public static ClientGroup createRandomClientGroup(){ //TODO read from file
        List<Client> clients = new ArrayList<>();
        Random random = new Random();
        int clientsNumber = 1 + random.nextInt(7);
        int relaxTime = 100 + random.nextInt(2500);
        int clientGroupId = ClientIdGenerator.generateNewGroupId();
        for (int i = 0; i < clientsNumber; i++){
            clients.add(ClientFactory.generateClient());
        }
        return new ClientGroup(clients, clientGroupId, relaxTime);
    }
}
