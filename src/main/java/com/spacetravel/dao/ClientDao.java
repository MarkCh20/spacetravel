package com.spacetravel.dao;

import com.spacetravel.config.HibernateUtil;
import com.spacetravel.entity.Client;
import com.spacetravel.util.LoggerUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class ClientDao {
    private static final Logger logger = LoggerUtil.getLogger(ClientDao.class);

    public Client save(Client client) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(client);
            tx.commit();
            logger.info("Client saved: {}", client.getName());
            return client;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error saving client: ", e);
            throw e;
        }
    }

    public Optional<Client> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Client client = session.get(Client.class, id);
            return Optional.ofNullable(client);
        }
    }

    public List<Client> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Client", Client.class).list();
        }
    }

    public void delete(Client client) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(client);
            tx.commit();
            logger.info("Client deleted: {}", client.getName());
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error deleting client: ", e);
            throw e;
        }
    }

    public Client update(Client client) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Client merged = (Client) session.merge(client);
            tx.commit();
            logger.info("Client updated: {}", merged.getName());
            return merged;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error updating client: ", e);
            throw e;
        }
    }
}
