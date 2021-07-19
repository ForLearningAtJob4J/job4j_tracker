package ru.job4j.tracker.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.tracker.StartUI;
import ru.job4j.tracker.model.Item;

import java.sql.Connection;
import java.util.List;
import java.util.function.Function;

public class HbmTracker implements Store, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(StartUI.class);

    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    @Override
    public Connection init() {
        return null;
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            LOG.warn(e.getMessage(), e);
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Item add(Item item) {
        tx(session -> session.save(item));
        return item;
    }

    @Override
    public boolean replace(Integer id, Item item) {
        try {
            tx(session -> {
                session.update(item);
                return null;
            });
            return true;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try {
            tx(session -> {
                Item item = new Item(null);
                item.setId(id);
                session.delete(item);
                return null;
            });
            return true;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Item> findAll() {
        return tx(session -> session.createQuery("from Item", Item.class).list());
    }

    @Override
    public List<Item> findByName(String key) {
        return tx(session -> session.createQuery("from Item where name = :name", Item.class)
                .setParameter("name", key).list());
    }

    @Override
    public Item findById(Integer id) {
        return tx(session -> session.get(Item.class, id));
    }

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @Override
    public void clear() {
        tx(session -> {
            session.createSQLQuery("DELETE FROM items").executeUpdate();
            return null;
        });
    }
}