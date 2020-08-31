package ru.job4j.tracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.StringJoiner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class FindAllActionTest {
    private Store tracker;

    public FindAllActionTest(String s, Store store) {
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
    public void whenCheckOutput() {
        tracker.clear();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream def = System.out;
        System.setOut(new PrintStream(out));
        Item item = new Item(tracker.generateId(), "fix bug");
        tracker.add(item);
        Item item2 = new Item(tracker.generateId(), "bla-bla");
        tracker.add(item2);
        Item item3 = new Item(tracker.generateId(), "don't jump you might fall");
        tracker.add(item3);
        FindAllAction act = new FindAllAction();
        act.execute(new StubInput(new String[] {}), tracker);
        String expect = new StringJoiner(System.lineSeparator(), "", System.lineSeparator())
                .add("=== Begin ===")
                .add(item.toString())
                .add(item2.toString())
                .add(item3.toString())
                .add("=== End ===")
                .toString();
        assertThat(new String(out.toByteArray()), is(expect));
        System.setOut(def);
    }
}