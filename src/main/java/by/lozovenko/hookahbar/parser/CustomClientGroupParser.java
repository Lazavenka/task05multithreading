package by.lozovenko.hookahbar.parser;

import by.lozovenko.hookahbar.entity.ClientGroup;

import java.util.List;

public interface CustomClientGroupParser {
    List<ClientGroup> parseClientGroupList(List<String> lines);

    ClientGroup parseClientGroup(String line);
}
