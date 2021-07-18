package ru.job4j.tracker.action;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.Test;
import ru.job4j.tracker.utils.ConnectionRollback;
import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.model.Item;
import ru.job4j.tracker.store.MemTracker;
import ru.job4j.tracker.store.SqlTracker;
import ru.job4j.tracker.store.Store;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class FindByIdActionTest {
    private static Store tracker;
    private static PrintStream def;
    private static ByteArrayOutputStream out;
    private static final String LS = System.lineSeparator();
    private Item item, item3;

    public FindByIdActionTest(String s, Store store) {
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

    @Before
    public void prepareTracker() {
        tracker.clear();
        out.reset();
        item = new Item("fix bug");
        tracker.add(item);
        tracker.add(new Item("i'll be back"));
        item3 = new Item("fix bug");
        tracker.add(item3);
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
    public void executeWhenItemsIsFoundById() {
        out.reset();
        FindByIdAction findByIdAction = new FindByIdAction();

        Input input = mock(Input.class);

        when(input.askInt(any(String.class))).thenReturn(item.getId());

        findByIdAction.execute(input, tracker);

        assertThat(out.toString(), is(item.toString() + LS));
    }

    @Test
    public void executeWhenItemsIsNotFoundById() {
        out.reset();
        FindByIdAction findByIdAction = new FindByIdAction();

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn("-1");

        findByIdAction.execute(input, tracker);

        assertThat(out.toString(), is("Task with specified ID was not found" + LS));
    }
}