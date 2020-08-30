package ru.job4j.tracker;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

public class TrackerTest {
    @Test
    public void whenAddNewItemThenTrackerHasSameItem() {
        Tracker tracker = new Tracker();
        Item item = new Item("test1");
        tracker.add(item);
        Item result = tracker.findById(item.getId());
        assertThat(result.getName(), is(item.getName()));
    }

    @Test
    public void whenFindAllThenReturnAll() {
        ArrayList<Item> expected = new ArrayList<>();
        Tracker tracker = new Tracker();
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
        ArrayList<Item> expected = new ArrayList<>();
        Tracker tracker = new Tracker();
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
        Tracker tracker = new Tracker();
        Item item = new Item("test1");
        tracker.add(item);
        Item result = tracker.findById(item.getId());
        assertThat(result.getName(), is(item.getName()));
    }

    @Test
    public void whenReplace() {
        Tracker tracker = new Tracker();
        Item bug = new Item("Bug");
        tracker.add(bug);
        String id = bug.getId();
        Item bugWithDesc = new Item("Bug with description");
        tracker.replace(id, bugWithDesc);
        assertThat(tracker.findById(id).getName(), is("Bug with description"));
    }

    @Test
    public void whenDelete() {
        Tracker tracker = new Tracker();
        Item bug = new Item("Bug");
        tracker.add(bug);
        String id = bug.getId();
        tracker.delete(id);
        assertThat(tracker.findById(id), is(nullValue()));
    }
}
