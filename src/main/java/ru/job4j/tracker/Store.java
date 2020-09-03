package ru.job4j.tracker;

import java.sql.Connection;
import java.util.List;

public interface Store extends AutoCloseable {
    Connection init();
    Item add(Item item);
    boolean replace(String id, Item item);
    boolean delete(String id);
    void clear();
    List<Item> findAll();
    List<Item> findByName(String key);
    Item findById(String id);
}