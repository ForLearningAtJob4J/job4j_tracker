package ru.job4j.tracker;

import org.junit.*;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class FindByNameActionTest {
    private static Store tracker;
    private static PrintStream def;
    private static ByteArrayOutputStream out;
    private static final String LS = System.lineSeparator();
    private Item item, item3;

    public FindByNameActionTest(String s, Store store) {
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
    public void whenCheckOutput() {
        out.reset();
        FindByNameAction act = new FindByNameAction();
        act.execute(new StubInput(new String[] {"fix bug"}), tracker);
        String expect = new StringJoiner(LS, "", LS)
                .add("=== Begin ===")
                .add(item.toString())
                .add(item3.toString())
                .add("=== End ===")
                .toString();
        assertThat(out.toString(), is(expect));
    }


    @Test
    public void executeWhenItemsIsFoundByName() {
        out.reset();
        FindByNameAction findByNameAction = new FindByNameAction();

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn("fix bug");

        findByNameAction.execute(input, tracker);

        String expect = new StringJoiner(LS, "", LS)
                .add("=== Begin ===")
                .add(item.toString())
                .add(item3.toString())
                .add("=== End ===")
                .toString();
        assertThat(out.toString(), is(expect));
    }

    @Test
    public void executeWhenItemsIsNotFoundByName() {
        out.reset();
        FindByNameAction rep = new FindByNameAction();

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn("0");

        rep.execute(input, tracker);

        String expect = new StringJoiner(LS, "", LS)
                .add("=== Begin ===")
                .add("=== End ===")
                .toString();
        assertThat(out.toString(), is(expect));
    }
}