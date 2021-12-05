package by.lozovenko.hookahbar.parser.impl;

import by.lozovenko.hookahbar.parser.CustomLoungeInitializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLoungeInitializerImpl implements CustomLoungeInitializer {
    private static final String EQUALS_DELIMITER = "=";
    private static final int PARAMETER_NAME = 0;
    private static final int PARAMETER_VALUE = 1;

    public static final String HOOKAHS_PARAMETER_NAME = "hookahs";
    public static final String MANAGERS_PARAMETER_NAME = "loungeManagers";
    public static final String WAITING_QUEUE_PARAMETER_NAME = "insideWaitingQueueSize";

    @Override
    public Map<String, Integer> parseInitData(List<String> lines) {
        Map<String, Integer> initializationParameters = new HashMap<>();
        for (String line : lines) {
            String[] parameterSubData = line.split(EQUALS_DELIMITER);
            Integer value = Integer.parseInt(parameterSubData[PARAMETER_VALUE]);
            initializationParameters.put(parameterSubData[PARAMETER_NAME], value);
        }
        return initializationParameters;
    }
}
