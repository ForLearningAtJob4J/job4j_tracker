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
public class FindByNameActionTest {
    private Store tracker;

    public FindByNameActionTest(String s, Store store) {
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
        tracker.add(new Item(tracker.generateId(), "i'll be back"));
        Item item3 = new Item(tracker.generateId(), "fix bug");
        tracker.add(item3);
        FindByNameAction act = new FindByNameAction();
        act.execute(new StubInput(new String[] {"fix bug"}), tracker);
        String expect = new StringJoiner(System.lineSeparator(), "", System.lineSeparator())
                .add("=== Begin ===")
                .add(item.toString())
                .add(item3.toString())
                .add("=== End ===")
                .toString();
        assertThat(new String(out.toByteArray()), is(expect));
        System.setOut(def);
    }
}