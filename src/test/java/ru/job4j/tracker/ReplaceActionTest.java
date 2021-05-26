package ru.job4j.tracker;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ReplaceActionTest {
    private static Store tracker;
    private static PrintStream def;
    private static ByteArrayOutputStream out;

    public ReplaceActionTest(String s, Store store) {
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

    @BeforeClass
    public static void saveSystemOut() {
        out = new ByteArrayOutputStream();
        def = System.out;
        System.setOut(new PrintStream(out));
    }

    @AfterClass
    public static void annihilation() throws Exception {
        tracker.close();
        System.setOut(def);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[][] gens() throws SQLException {
        return new Object[][] {
                {"MemTracker", new MemTracker()},
                {"SqlTracker", new SqlTracker(ConnectionRollback.create(init()))}
        };
    }

    @Test
    public void executeWhenReplacedItemIdIsFound() {
        tracker.clear();
        out.reset();
        Item item = new Item("Replaced item");
        tracker.add(item);
        String replacedName = "New item name";
        ReplaceAction rep = new ReplaceAction();

        Input input = mock(Input.class);

        when(input.askStr("Enter id: ")).thenReturn(item.getId());
        when(input.askStr("Enter new name: ")).thenReturn(replacedName);

        rep.execute(input, tracker);

        assertThat(out.toString(), is("Was successfully edited!" + System.lineSeparator()));
        assertThat(tracker.findAll().get(0).getName(), is(replacedName));
    }

    @Test
    public void executeWhenReplacedItemIdIsNotFound() {
        tracker.clear();
        out.reset();
        Item item = new Item("Replaced item");
        tracker.add(item);
        ReplaceAction rep = new ReplaceAction();

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn("0");

        rep.execute(input, tracker);

        assertThat(out.toString(), is("Task with specified ID was not found" + System.lineSeparator()));
        assertThat(tracker.findAll().get(0).getName(), is("Replaced item"));
    }
}