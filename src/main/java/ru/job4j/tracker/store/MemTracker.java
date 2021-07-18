package ru.job4j.tracker.store;

import ru.job4j.tracker.model.Item;
import ru.job4j.tracker.store.Store;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class MemTracker implements Store {
    /**
     * Массив для хранения заявок.
     */
    private final List<Item> items = new ArrayList<>();

    @Override
    public Connection init() {
        return null;
    }

    /**
     * Метод добавления заявки в хранилище
     * @param item новая заявка
     */
    public Item add(Item item) {
        item.setId(generateId());
        items.add(item);
        return item;
    }

    /**
     * Метод генерирует уникальный ключ для заявки.
     * Так как у заявки нет уникальности полей, имени и описание. Для идентификации нам нужен уникальный ключ.
     * @return Уникальный ключ.
     */
    private Integer generateId() {
        Random rm = new Random(System.nanoTime());
        return abs(rm.nextInt());
    }

    private int indexOf(Integer id) {
        int rsl = -1;
        for (int index = 0; index < items.size(); index++) {
            if (items.get(index).getId().equals(id)) {
                rsl = index;
                break;
            }
        }
        return rsl;
    }

    public List<Item> findAll() {
        return new ArrayList<>(items);
    }

    public List<Item> findByName(String key) {
        ArrayList<Item> newArray = new ArrayList<>();
        for (Item item : items) {
            if (item.getName().equals(key)) {
                newArray.add(item);
            }
        }
        return newArray;
    }

    public Item findById(Integer id) {
        int index = indexOf(id);
        return index != -1 ? items.get(index) : null;
    }

    public boolean replace(Integer id, Item item) {
        boolean result = false;
        int index = indexOf(id);
        if (index != -1) {
            item.setId(id);
            items.set(index, item);
            result = true;
        }
        return result;
    }

    public boolean delete(Integer id) {
        boolean result = false;
        int index = indexOf(id);
        if (index != -1) {
            items.remove(index);
            result = true;
        }
        return result;
    }

    @Override
    public void close() {
    }

    @Override
    public void clear() {
        items.clear();
    }
}
