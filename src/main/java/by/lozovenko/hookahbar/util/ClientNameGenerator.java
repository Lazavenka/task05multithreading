package by.lozovenko.hookahbar.util;

import java.util.Random;

public class ClientNameGenerator {
    private static final String[] NAMES;
    private static final Random random = new Random();

    static {
        NAMES = new String[]{"John", "Jack", "Joseph", "Julia", "Jason", "Joana", "Jeff", "Jessica", "Jasmine",
                "Samuel", "Harry", "Alfie", "Thomas", "Charlie", "Oscar", "William", "George", "Ethan", "Leo", "Dexter",
                "Olivia", "Amelia", "Emily", "Mia", "Sophie", "Chloe", "Scarlett", "Freya", "Alice", "Ivy", "Rose"};
    }

    public static String getRandomName() {
        return NAMES[random.nextInt(NAMES.length)];
    }
}
