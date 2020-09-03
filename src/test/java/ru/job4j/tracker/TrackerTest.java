package ru.job4j.tracker;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(Parameterized.class)
public class TrackerTest {
    private static Store tracker;

    public TrackerTest(String s, Store store) {
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
    public void whenAddNewItemThenTrackerHasSameItem() {
        tracker.clear();
        Item item = new Item("test1");
        tracker.add(item);
        Item result = tracker.findById(item.getId());
        assertThat(result.getName(), is(item.getName()));
    }

    @Test
    public void whenFindAllThenReturnAll() {
        tracker.clear();
        ArrayList<Item> expected = new ArrayList<>();
        Item item = new Item("test1");
        tracker.add(item);
        expected.add(item);
        item = new Item("test2");
        tracker.add(item);
        expected.add(item);
        item = new Item("test3");
        tracker.add(item);
        expected.add(item);
        assertThat(tracker.findAll(), is(expected));
    }

    @Test
    public void whenFindByNameThenReturnWithSameName() {
        tracker.clear();
        ArrayList<Item> expected = new ArrayList<>();
        Item item = new Item("google");
        tracker.add(item);
        expected.add(item);
        item = new Item("google");
        tracker.add(item);
        expected.add(item);
        item = new Item("test3");
        tracker.add(item);
        assertThat(tracker.findByName("google"), is(expected));
    }

    @Test
    public void whenFindItemById() {
        tracker.clear();
        Item item = new Item("test1");
        tracker.add(item);
        Item result = tracker.findById(item.getId());
        assertThat(result.getName(), is(item.getName()));
    }

    @Test
    public void whenReplace() {
        tracker.clear();
        Item bug = new Item("Bug");
        tracker.add(bug);
        String id = bug.getId();
        Item bugWithDesc = new Item("Bug with description");
        tracker.replace(id, bugWithDesc);
        assertThat(tracker.findById(id).getName(), is("Bug with description"));
    }

    @Test
    public void whenDelete() {
        tracker.clear();
        Item bug = new Item("Bug");
        tracker.add(bug);
        String id = bug.getId();
        tracker.delete(id);
        assertThat(tracker.findById(id), is(nullValue()));
    }
}
