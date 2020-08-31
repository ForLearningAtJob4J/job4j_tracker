package ru.job4j.tracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(Parameterized.class)
public class TrackerTest {
    private Store tracker;

    public TrackerTest(String s, Store store) {
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
    public void whenAddNewItemThenTrackerHasSameItem() {
        tracker.clear();
        Item item = new Item(tracker.generateId(), "test1");
        tracker.add(item);
        Item result = tracker.findById(item.getId());
        assertThat(result.getName(), is(item.getName()));
    }

    @Test
    public void whenFindAllThenReturnAll() {
        tracker.clear();
        ArrayList<Item> expected = new ArrayList<>();
        Item item = new Item(tracker.generateId(), "test1");
        tracker.add(item);
        expected.add(item);
        item = new Item(tracker.generateId(), "test2");
        tracker.add(item);
        expected.add(item);
        item = new Item(tracker.generateId(), "test3");
        tracker.add(item);
        expected.add(item);
        assertThat(tracker.findAll(), is(expected));
    }

    @Test
    public void whenFindByNameThenReturnWithSameName() {
        tracker.clear();
        ArrayList<Item> expected = new ArrayList<>();
        Item item = new Item(tracker.generateId(), "google");
        tracker.add(item);
        expected.add(item);
        item = new Item(tracker.generateId(), "google");
        tracker.add(item);
        expected.add(item);
        item = new Item(tracker.generateId(), "test3");
        tracker.add(item);
        assertThat(tracker.findByName("google"), is(expected));
    }

    @Test
    public void whenFindItemById() {
        tracker.clear();
        Item item = new Item(tracker.generateId(), "test1");
        tracker.add(item);
        Item result = tracker.findById(item.getId());
        assertThat(result.getName(), is(item.getName()));
    }

    @Test
    public void whenReplace() {
        tracker.clear();
        Item bug = new Item(tracker.generateId(), "Bug");
        tracker.add(bug);
        String id = bug.getId();
        Item bugWithDesc = new Item(id, "Bug with description");
        tracker.replace(id, bugWithDesc);
        assertThat(tracker.findById(id).getName(), is("Bug with description"));
    }

    @Test
    public void whenDelete() {
        tracker.clear();
        Item bug = new Item(tracker.generateId(), "Bug");
        tracker.add(bug);
        String id = bug.getId();
        tracker.delete(id);
        assertThat(tracker.findById(id), is(nullValue()));
    }
}
