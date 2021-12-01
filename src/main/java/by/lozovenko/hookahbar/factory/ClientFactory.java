package by.lozovenko.hookahbar.factory;

import by.lozovenko.hookahbar.entity.Client;
import by.lozovenko.hookahbar.util.ClientIdGenerator;
import by.lozovenko.hookahbar.util.ClientNameGenerator;

public class ClientFactory {
    public static Client generateClient(){
        return new Client(ClientIdGenerator.generateNewId(), ClientNameGenerator.getRandomName());
    }
}
