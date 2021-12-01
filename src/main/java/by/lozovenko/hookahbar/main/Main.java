package by.lozovenko.hookahbar.main;

import by.lozovenko.hookahbar.entity.ClientGroup;
import by.lozovenko.hookahbar.factory.ClientGroupFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        IntStream.range(0, 25).mapToObj(i -> new Thread(ClientGroupFactory.createRandomClientGroup())).forEach(Thread::start);

    }
}
