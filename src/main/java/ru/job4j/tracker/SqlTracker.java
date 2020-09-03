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

    SqlTracker(Connection connection) {
        cn = connection;
    }

    public Connection init() {
        if (cn == null) {
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
        return cn;
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    @Override
    public Item add(Item aItem) {
        try (PreparedStatement st =
                     cn.prepareStatement("INSERT INTO items (name) values (?)", Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, aItem.getName());
            st.executeUpdate();
            ResultSet rs  = st.getGeneratedKeys();
            if (rs.next()) {
                aItem.setId(rs.getString(1));
            }
            return aItem;
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean replace(String id, Item item) {
        try (PreparedStatement st = cn.prepareStatement("UPDATE items SET name = ? WHERE id = ?")) {
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
        try (PreparedStatement st = cn.prepareStatement("DELETE FROM items WHERE id = ?")) {
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
        try (PreparedStatement st = cn.prepareStatement("SELECT id, name FROM items")) {
            List<Item> res = new ArrayList<>();
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Item i = new Item(rs.getString(2));
                i.setId(rs.getString(1));
                res.add(i);
            }
            return res;
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Item> findByName(String key) {
        try (PreparedStatement st = cn.prepareStatement("SELECT id, name FROM items WHERE name = ?")) {
            List<Item> res = new ArrayList<>();
            st.setString(1, key);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Item i = new Item(rs.getString(2));
                i.setId(rs.getString(1));
                res.add(i);
            }
            return res;
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Item findById(String id) {
        try (PreparedStatement st = cn.prepareStatement("SELECT id, name FROM items WHERE id = ?")) {
            st.setInt(1, Integer.parseInt(id));
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Item i = new Item(rs.getString("name"));
                i.setId(rs.getString("id"));
                return i;
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void clear() {
        try (Statement st = cn.createStatement()) {
            st.addBatch("TRUNCATE TABLE items");
            st.addBatch("SELECT setval('public.items_id_seq', 1, false)");
            st.executeBatch();
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}