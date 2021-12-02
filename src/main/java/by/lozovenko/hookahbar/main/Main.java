package by.lozovenko.hookahbar.main;

import by.lozovenko.hookahbar.factory.ClientGroupFactory;

import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        IntStream.range(0, 55).mapToObj(i -> new Thread(ClientGroupFactory.createRandomClientGroup())).forEach(Thread::start);

    }
}
