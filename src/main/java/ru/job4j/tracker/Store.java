package ru.job4j.tracker;

import java.sql.Connection;
import java.util.List;

public interface Store extends AutoCloseable {
    Connection init();
    Item add(Item item);
    boolean replace(Integer id, Item item);
    boolean delete(Integer id);
    void clear();
    List<Item> findAll();
    List<Item> findByName(String key);
    Item findById(Integer id);
}