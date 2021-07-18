package ru.job4j.tracker.action;

import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.store.Store;
import ru.job4j.tracker.model.Item;

public class FindByNameAction implements UserAction {
    @Override
    public String name() {
        return "Find Item by Name";
    }

    @Override
    public boolean execute(Input input, Store tracker) {
        System.out.println("=== Begin ===");
        String name = input.askStr("Enter name: ");
        for (Item item: tracker.findByName(name)) {
            System.out.println(item);
        }
        System.out.println("=== End ===");
        return true;
    }
}
