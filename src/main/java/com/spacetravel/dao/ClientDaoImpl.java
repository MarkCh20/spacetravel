package com.spacetravel.dao;

import com.spacetravel.config.HibernateUtil;
import com.spacetravel.entity.Client;
import com.spacetravel.exception.DataProcessingException;
import com.spacetravel.util.LoggerUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class ClientDaoImpl implements ClientDao {
    private static final Logger LOGGER = LoggerUtil.getLogger(ClientDaoImpl.class);

    public Client save(Client client) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(client);
            tx.commit();
            LOGGER.info("Client saved: {}", client.getName());
            return client;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error saving client: " + client.getName();
            throw new DataProcessingException(msg, e);
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
            LOGGER.info("Client deleted: {}", client.getName());
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error deleting client: " + client.getName();
            throw new DataProcessingException(msg, e);
        }
    }

    public Client update(Client client) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Client merged = session.merge(client);
            tx.commit();
            LOGGER.info("Client updated: {}", merged.getName());
            return merged;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error updating client: " + client.getName();
            throw new DataProcessingException(msg, e);
        }
    }
}
