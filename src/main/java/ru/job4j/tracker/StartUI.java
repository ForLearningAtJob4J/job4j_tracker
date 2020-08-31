package ru.job4j.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StartUI {

    private static final Logger LOG = LoggerFactory.getLogger(StartUI.class);

    public void init(Input input, Store tracker, List<UserAction> actions) {
        boolean run = true;
        LOG.info("Started");
        while (run) {
            this.showMenu(actions);
            int select = input.askInt("Select: ", actions.size());
            UserAction action = actions.get(select);
            run = action.execute(input, tracker);
        }
        LOG.info("Finished");
    }

    private void showMenu(List<UserAction> actions) {
        System.out.println("Menu.");
        for (int index = 0; index < actions.size(); index++) {
            System.out.println(index + ". " + actions.get(index).name());
        }
    }

    public static void main(String[] args) {
        Input validate = new ValidateInput(
                new ConsoleInput()
        );
        try (Store tracker = new SqlTracker()) {
            tracker.init();

            new StartUI().init(validate, tracker, new ArrayList<>(List.of(
                    new CreateAction(),
                    new ReplaceAction(),
                    new DeleteAction(),
                    new FindAllAction(),
                    new FindByIdAction(),
                    new FindByNameAction(),
                    new ExitAction()
            )));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}