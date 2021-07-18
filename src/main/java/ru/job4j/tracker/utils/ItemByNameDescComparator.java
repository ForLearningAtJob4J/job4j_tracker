package ru.job4j.tracker.utils;

import ru.job4j.tracker.model.Item;

import java.util.Comparator;

public class ItemByNameDescComparator implements Comparator<Item> {
    @Override
    public int compare(Item o1, Item o2) {
        return o2.getName().compareTo(o1.getName());
    }
}
