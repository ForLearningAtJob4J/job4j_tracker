package ru.job4j.tracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class StartUITest {
    private Store tracker;

    public StartUITest(String s, Store store) {
        tracker = store;
    }

    @Before
    public void setUp() {
        tracker.init();
    }

    @After
    public void cleanUp() {
        try {
            tracker.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[][] primeNumbers() {
        return new Object[][] {
                {"MemTracker", new MemTracker()},
                {"SqlTracker", new SqlTracker()}
        };
    }

    @Test
    public void whenAddItem() {
        tracker.clear();
        new CreateAction().execute(new StubInput(new String[]{"Fix PC"}), tracker);
        Item created = tracker.findAll().get(0);
        assertThat(created.getName(), is("Fix PC"));
    }

    @Test
    public void whenReplaceItem() {
        tracker.clear();
        Item item = new Item(tracker.generateId(), "new item");
        tracker.add(item);
        new ReplaceAction().execute(new StubInput(new String[]{item.getId(), "replaced item"}), tracker);
        Item replaced = tracker.findById(item.getId());
        assertThat(replaced.getName(), is("replaced item"));
    }

    @Test
    public void whenDeleteItem() {
        tracker.clear();
        Item item = new Item(tracker.generateId(), "new item");
        tracker.add(item);
        String id = item.getId();
        new DeleteAction().execute(new StubInput(new String[]{id}), tracker);
        tracker.delete(id);
        assertThat(tracker.findById(id), is(nullValue()));
    }

    @Test
    public void whenExit() {
        tracker.clear();
        StubInput input = new StubInput(
                new String[] {"0"}
        );
        StubAction action = new StubAction();
        new StartUI().init(input, tracker, new ArrayList<>(List.of(action)));
        assertThat(action.isCall(), is(true));
    }

    @Test
    public void whenPrtMenu() {
        tracker.clear();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream def = System.out;
        System.setOut(new PrintStream(out));
        StubInput input = new StubInput(
                new String[] {"0"}
        );
        StubAction action = new StubAction();
        new StartUI().init(input, tracker, new ArrayList<>(List.of(action)));
        String expect = new StringJoiner(System.lineSeparator(), "", System.lineSeparator())
                .add("Menu.")
                .add("0. Stub action")
                .toString();
        assertThat(new String(out.toByteArray()), is(expect));
        System.setOut(def);
    }
}
