package ru.job4j.tracker.store;

import org.junit.Test;
import ru.job4j.tracker.model.City;
import ru.job4j.tracker.model.Item;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class HbmTrackerTest {

    @Test
    public void whenFindAll() {
        HbmTracker tracker = new HbmTracker();
        Item item1 = new Item("Task1");
        Item item2 = new Item("Task2");
        tracker.add(item1);
        tracker.add(item2);
        assertEquals(List.of(item1, item2), tracker.findAll());
    }

    @Test
    public void whenAdd() {
        HbmTracker tracker = new HbmTracker();
        Item item = new Item("Task1");
        tracker.add(item);

        List<Item> all = tracker.findAll();
        assertEquals(1, all.size());
        assertEquals(item, all.get(0));
    }

    @Test
    public void whenReplace() {
        String newName = "Task999";
        HbmTracker tracker = new HbmTracker();
        Item item = new Item("Task1");
        item = tracker.add(item);
        item.setName(newName);
        tracker.replace(item.getId(), item);

        List<Item> all = tracker.findAll();
        assertEquals(1, all.size());
        assertEquals(newName, all.get(0).getName());
    }

    @Test
    public void whenDelete() {
        HbmTracker tracker = new HbmTracker();
        Item item = new Item("Task1");
        item = tracker.add(item);
        tracker.delete(item.getId());

        List<Item> all = tracker.findAll();
        assertEquals(0, all.size());
    }

    @Test
    public void whenFindByName() {
        String key = "Task2";
        HbmTracker tracker = new HbmTracker();
        Item item1 = new Item("Task1");
        Item item2 = new Item(key);
        tracker.add(item1);
        tracker.add(item2);

        List<Item> founded = tracker.findByName(key);
        assertEquals(1, founded.size());
        assertEquals(key, founded.get(0).getName());
    }

    @Test
    public void whenFindById() {
        HbmTracker tracker = new HbmTracker();
        Item item1 = new Item("Task1");
        Item item2 = new Item("Task2");
        tracker.add(item1);
        tracker.add(item2);

        Item founded = tracker.findById(item1.getId());
        assertEquals(founded, item1);
    }

    @Test
    public void whenClear() {
        HbmTracker tracker = new HbmTracker();
        Item item1 = new Item("Task1");
        Item item2 = new Item("Task2");
        tracker.add(item1);
        tracker.add(item2);
        tracker.clear();

        List<Item> all = tracker.findAll();
        assertEquals(0, all.size());
    }
}