package ru.job4j.tracker;

import java.util.Objects;

public class Item {
    private String id;
    private String name;

    public Item(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Item(String id, Item otherItem) {
        this.id = id;
        this.name = otherItem.getName();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Item item = (Item) o;

        if (!id.equals(item.id)) {
            return false;
        }
        return Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Item {" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
    }
}