package by.lozovenko.hookahbar.main;

import by.lozovenko.hookahbar.entity.ClientGroup;
import by.lozovenko.hookahbar.parser.CustomClientGroupParser;
import by.lozovenko.hookahbar.parser.impl.CustomClientGroupParserImpl;
import by.lozovenko.hookahbar.reader.CustomFileReader;
import by.lozovenko.hookahbar.reader.impl.CustomFileReaderImpl;

import java.util.List;

public class Main {
    private static final String CLIENTS_DATA_FILEPATH = "data/ClientGroups.txt";

    public static void main(String[] args) {
        CustomFileReader fileReader = new CustomFileReaderImpl();
        CustomClientGroupParser clientGroupParser = new CustomClientGroupParserImpl();
        List<String> fileDataLines = fileReader.readLinesFromFile(CLIENTS_DATA_FILEPATH);
        List<ClientGroup> clientGroups = clientGroupParser.parseClientGroupList(fileDataLines);
        clientGroups.forEach(r -> new Thread(r).start());
    }
}
