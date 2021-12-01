package by.lozovenko.hookahbar.util;

public class ClientIdGenerator {
    private static int currentClientId = 0;
    private static int currentClientGroupId = 0;
    public static int generateNewId(){
        return ++currentClientId;
    }
    public static int generateNewGroupId(){
        return ++currentClientGroupId;
    }
}
