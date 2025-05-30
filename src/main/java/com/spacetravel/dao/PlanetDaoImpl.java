package com.spacetravel.dao;

import com.spacetravel.config.HibernateUtil;
import com.spacetravel.entity.Planet;
import com.spacetravel.exception.DataProcessingException;
import com.spacetravel.util.LoggerUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class PlanetDaoImpl implements PlanetDao {
    private static final Logger LOGGER = LoggerUtil.getLogger(PlanetDaoImpl.class);

    public Planet save(Planet planet) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(planet);
            tx.commit();
            LOGGER.info("Planet saved: {}", planet.getName());
            return planet;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error saving planet: " + planet.getName();
            throw new DataProcessingException(msg, e);
        }
    }

    public Optional<Planet> findById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Planet planet = session.get(Planet.class, id);
            return Optional.ofNullable(planet);
        }
    }

    public Optional<Planet> findByName(String name) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Optional<Planet> planet = session.createQuery("FROM Planet p WHERE p.name = :name", Planet.class)
                    .setParameter("name", name)
                    .uniqueResultOptional();

            tx.commit();
            return planet;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error finding planet by name: " + name;
            throw new DataProcessingException(msg, e);

        }
    }

    public List<Planet> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Planet", Planet.class).list();
        }
    }

    public void delete(Planet planet) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(planet);
            tx.commit();
            LOGGER.info("Planet deleted: {}", planet.getName());
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error deleting planet: " + planet.getName();
            throw new DataProcessingException(msg, e);
        }
    }

    public Planet update(Planet planet) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Planet merged = session.merge(planet);
            tx.commit();
            LOGGER.info("Planet updated: {}", merged.getName());
            return merged;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error updating planet: " + planet.getName();
            throw new DataProcessingException(msg, e);
        }
    }
}