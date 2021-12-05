package by.lozovenko.hookahbar.parser;

import java.util.List;
import java.util.Map;

public interface CustomLoungeInitializer {
    Map<String, Integer> parseInitData(List<String> lines);
}
