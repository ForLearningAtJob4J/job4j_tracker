package ru.job4j.tracker;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ItemByNameAscComparatorTest {

    @Test
    public void compare() {
        ArrayList<Item> list = new ArrayList<>(List.of(
                new Item("Second"),
                new Item("First"),
                new Item("Third")));
        list.sort(new ItemByNameAscComparator());
        assertThat(new String[]{list.get(0).getName(), list.get(1).getName(), list.get(2).getName()},
                is(new String[]{"First", "Second", "Third"}));
    }

    @Test
    public void compareReversed() {
        ArrayList<Item> list = new ArrayList<>(List.of(
                new Item("Second"),
                new Item("First"),
                new Item("Third")));
        list.sort(new ItemByNameAscComparator().reversed());
        assertThat(new String[]{list.get(0).getName(), list.get(1).getName(), list.get(2).getName()},
                is(new String[]{"Third", "Second", "First"}));
    }
}