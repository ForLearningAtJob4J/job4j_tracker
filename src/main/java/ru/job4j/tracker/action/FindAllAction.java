package ru.job4j.tracker.action;

import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.store.Store;
import ru.job4j.tracker.model.Item;

public class FindAllAction implements UserAction {
    @Override
    public String name() {
        return "Show all items";
    }

    @Override
    public boolean execute(Input input, Store tracker) {
        System.out.println("=== Begin ===");
        for (Item item: tracker.findAll()) {
            System.out.println(item);
        }
        System.out.println("=== End ===");
        return true;
    }
}