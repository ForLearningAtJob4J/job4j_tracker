package ru.job4j.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SqlTracker implements Store {
    private static final Logger LOG = LoggerFactory.getLogger(StartUI.class);
    private Connection cn;

    public void init() {
        try (InputStream in = SqlTracker.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            assert in != null;
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String generateId() {
        Statement st;
        try {
            st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT nextval('public.items_id_seq')");
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    @Override
    public Item add(Item aItem) {
        PreparedStatement st;
        Item item = new Item(aItem.getId() == null ? generateId() : aItem.getId(), aItem.getName());
        try {
            st = cn.prepareStatement("INSERT INTO items (id, name) values (?, ?)");
            st.setInt(1, Integer.parseInt(item.getId()));
            st.setString(2, item.getName());
            st.executeUpdate();
            return item;
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean replace(String id, Item item) {
        PreparedStatement st;
        try {
            st = cn.prepareStatement("UPDATE items SET name = ? WHERE id = ?");
            st.setString(1, item.getName());
            st.setInt(2, Integer.parseInt(id));
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        PreparedStatement st;
        try {
            st = cn.prepareStatement("DELETE FROM items WHERE id = ?");
            st.setInt(1, Integer.parseInt(id));
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<Item> findAll() {
        PreparedStatement st;
        try {
            List<Item> res = new ArrayList<>();
            st = cn.prepareStatement("SELECT id, name FROM items");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                res.add(new Item(rs.getString(1), rs.getString(2)));
            }
            return res;
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Item> findByName(String key) {
        PreparedStatement st;
        try {
            List<Item> res = new ArrayList<>();
            st = cn.prepareStatement("SELECT id, name FROM items WHERE name = ?");
            st.setString(1, key);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                res.add(new Item(rs.getString(1), rs.getString(2)));
            }
            return res;
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Item findById(String id) {
        PreparedStatement st;
        try {
            st = cn.prepareStatement("SELECT id, name FROM items WHERE id = ?");
            st.setInt(1, Integer.parseInt(id));
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Item(rs.getString("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void clear() {
        Statement st;
        try {
            st = cn.createStatement();
            st.addBatch("TRUNCATE TABLE items");
            st.addBatch("SELECT setval('public.items_id_seq', 1, false)");
            st.executeBatch();
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}