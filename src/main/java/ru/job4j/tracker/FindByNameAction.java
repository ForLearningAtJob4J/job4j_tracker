package ru.job4j.tracker;

public class FindByNameAction implements UserAction {
    @Override
    public String name() {
        return "Find Item by Name";
    }

    @Override
    public boolean execute(Input input, Tracker tracker) {
        System.out.println("=== Begin ===");
        String name = input.askStr("Enter name: ");
        for (Item item: tracker.findByName(name)) {
            System.out.println(item);
        }
        System.out.println("=== End ===");
        return true;
    }
}
