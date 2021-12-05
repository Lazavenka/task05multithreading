package by.lozovenko.hookahbar.parser.impl;

import by.lozovenko.hookahbar.entity.Client;
import by.lozovenko.hookahbar.entity.ClientGroup;
import by.lozovenko.hookahbar.factory.ClientFactory;
import by.lozovenko.hookahbar.parser.CustomClientGroupParser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CustomClientGroupParserImpl implements CustomClientGroupParser {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String DELIMITER_REGEX = " ";
    private static final String EQUALS_REGEX = "=";
    private static final int ID_PARAMETER = 0;
    private static final int CLIENTS_NUMBER_PARAMETER = 1;
    private static final int RELAX_TIME_PARAMETER = 2;

    @Override
    public List<ClientGroup> parseClientGroupList(List<String> lines) {
        List<ClientGroup> clientGroups = new ArrayList<>();
        for (String line : lines) {
            ClientGroup clientGroup = parseClientGroup(line);
            clientGroups.add(clientGroup);
        }
        return clientGroups;
    }

    @Override
    public ClientGroup parseClientGroup(String line) {
        String[] clientGroupParameters = line.split(DELIMITER_REGEX);
        List<String> parameterValues = new ArrayList<>();
        for (String clientGroupParameter : clientGroupParameters) {
            String[] subData = clientGroupParameter.split(EQUALS_REGEX);
            parameterValues.add(subData[1]);
        }
        int clientGroupId = Integer.parseInt(parameterValues.get(ID_PARAMETER));
        int clientsNumber = Integer.parseInt(parameterValues.get(CLIENTS_NUMBER_PARAMETER));
        int relaxTime = Integer.parseInt(parameterValues.get(RELAX_TIME_PARAMETER));

        List<Client> clients = new ArrayList<>();
        IntStream.range(0, clientsNumber).forEach(i -> clients.add(ClientFactory.generateClient()));

        LOGGER.log(Level.INFO, "ClientGroup #{}, clients={}, relaxTime={} is created.",
                clientGroupId, clientsNumber, relaxTime);
        return new ClientGroup(clients, clientGroupId, relaxTime);
    }
}
