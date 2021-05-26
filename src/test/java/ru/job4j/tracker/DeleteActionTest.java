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
public class DeleteActionTest {
    private static Store tracker;
    private static PrintStream def;
    private static ByteArrayOutputStream out;

    public DeleteActionTest(String s, Store store) {
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
    public void executeWhenIdIsFound() {
        tracker.clear();
        out.reset();
        Item item = new Item("Replaced item");
        tracker.add(item);
        DeleteAction deleteAction = new DeleteAction();

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn(item.getId());

        deleteAction.execute(input, tracker);

        assertThat(out.toString(), is("Was successfully deleted!" + System.lineSeparator()));
        assertThat(tracker.findAll().size(), is(0));
    }

    @Test
    public void executeWhenIdIsNotFound() {
        tracker.clear();
        out.reset();
        Item item = new Item("Replaced item");
        tracker.add(item);
        DeleteAction deleteAction = new DeleteAction();

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn("0");

        deleteAction.execute(input, tracker);

        assertThat(out.toString(), is("Task with specified ID was not found" + System.lineSeparator()));
        assertThat(tracker.findAll().size(), is(1));
    }
}