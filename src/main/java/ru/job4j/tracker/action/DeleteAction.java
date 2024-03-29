package ru.job4j.tracker.action;

import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.store.Store;

public class DeleteAction implements UserAction {
    @Override
    public String name() {
        return "Delete Item";
    }

    @Override
    public boolean execute(Input input, Store tracker) {
        Integer id = input.askInt("Enter id: ");
        if (tracker.delete(id)) {
            System.out.println("Was successfully deleted!");
        } else {
            System.out.println("Task with specified ID was not found");
        }
        return true;
    }
}
