package by.lozovenko.hookahbar.reader.impl;

import by.lozovenko.hookahbar.reader.CustomFileReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CustomFileReaderImpl implements CustomFileReader {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public List<String> readLinesFromFile(String stringFilePath) {
        List<String> lines = new ArrayList<>();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(stringFilePath);
        if (is != null) {
            try (Stream<String> lineStream = new BufferedReader(new InputStreamReader(is))
                    .lines()) {
                lineStream.map(String::strip).forEach(lines::add);
            }
        } else {
            LOGGER.log(Level.ERROR, "File {} not found!", stringFilePath);
        }
        return lines;
    }
}
