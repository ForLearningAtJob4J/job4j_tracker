package ru.job4j.tracker;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class StartUITest {
    private static Store tracker;

    public StartUITest(String s, Store store) {
        tracker = store;
    }

    public static Connection init() {
        try (InputStream in = SqlTracker.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            assert in != null;
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            return DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")

            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @AfterClass
    public static void annihilation() throws Exception {
        tracker.close();
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[][] gens() throws SQLException {
        return new Object[][] {
                {"MemTracker", new MemTracker()},
                {"SqlTracker", new SqlTracker(ConnectionRollback.create(init()))}
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
        Item item = new Item("new item");
        tracker.add(item);
        new ReplaceAction().execute(new StubInput(new String[]{item.getId().toString(), "replaced item"}), tracker);
        Item replaced = tracker.findById(item.getId());
        assertThat(replaced.getName(), is("replaced item"));
    }

    @Test
    public void whenDeleteItem() {
        tracker.clear();
        Item item = new Item("new item");
        tracker.add(item);
        Integer id = item.getId();
        new DeleteAction().execute(new StubInput(new String[]{id.toString()}), tracker);
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
        assertThat(out.toString(), is(expect));
        System.setOut(def);
    }
}
