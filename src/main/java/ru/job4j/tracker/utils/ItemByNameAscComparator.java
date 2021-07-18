package ru.job4j.tracker.utils;

import ru.job4j.tracker.model.Item;

import java.util.Comparator;

public class ItemByNameAscComparator implements Comparator<Item> {
    @Override
    public int compare(Item o1, Item o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
