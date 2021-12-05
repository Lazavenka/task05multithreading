package by.lozovenko.hookahbar.util;

import java.util.Random;

public class ClientNameGenerator {
    private static final String[] NAMES;
    private static final Random random = new Random();
    static {
        NAMES = new String[]{"John","Jack","Joseph","Julia","Jason","Joana","Jeff","Jessica","Jasmine"};
    }

    public static String getRandomName(){
        return NAMES[random.nextInt(NAMES.length)];
    }
}
