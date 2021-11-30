package by.lozovenko.hookahbar.factory;

import by.lozovenko.hookahbar.entity.Client;
import by.lozovenko.hookahbar.entity.ClientGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientGroupFactory {
    public static ClientGroup createRandomGang(List<Client> clients, int relaxTime){ //TODO read from file

        return new ClientGroup(clients, 1, relaxTime);
    }
}
