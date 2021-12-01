package by.lozovenko.hookahbar.util;

import java.util.Random;

public class ClientNameGenerator {
    private static final String[] NAMES;

    static {
        NAMES = new String[]{"John","Jack","Joseph","Julia","Jason","Joana","Jeff","Jessica","Jasmine"};
    }

    public static String getRandomName(){
        Random random = new Random();
        return NAMES[random.nextInt(NAMES.length)];
    }
}
