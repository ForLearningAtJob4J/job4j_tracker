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
import java.util.Properties;
import java.util.StringJoiner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class FindAllActionTest {
    private static Store tracker;

    public FindAllActionTest(String s, Store aTracker) {
        tracker = aTracker;
    }

    @AfterClass
    public static void annihilation() throws Exception {
        tracker.close();
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

    @Parameterized.Parameters(name = "{0}")
    public static Object[][] gens() throws SQLException {
        return new Object[][] {
                {"MemTracker", new MemTracker()},
                {"SqlTracker", new SqlTracker(ConnectionRollback.create(init()))}
        };
    }

    @Test
    public void whenCheckOutput() {
        tracker.clear();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream def = System.out;
        System.setOut(new PrintStream(out));
        Item item = new Item("fix bug");
        tracker.add(item);
        Item item2 = new Item("bla-bla");
        tracker.add(item2);
        Item item3 = new Item("don't jump you might fall");
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