package com.spacetravel.dao;

import com.spacetravel.config.HibernateUtil;
import com.spacetravel.entity.Planet;
import com.spacetravel.util.LoggerUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class PlanetDao {
    private static final Logger logger = LoggerUtil.getLogger(PlanetDao.class);

    public Planet save(Planet planet) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(planet);
            tx.commit();
            logger.info("Planet saved: {}", planet.getName());
            return planet;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error saving planet: ", e);
            throw e;
        }
    }

    public Optional<Planet> findById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Planet planet = session.get(Planet.class, id);
            return Optional.ofNullable(planet);
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
            logger.info("Planet deleted: {}", planet.getName());
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error deleting planet: ", e);
            throw e;
        }
    }

    public Planet update(Planet planet) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Planet merged = (Planet) session.merge(planet);
            tx.commit();
            logger.info("Planet updated: {}", merged.getName());
            return merged;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error updating planet: ", e);
            throw e;
        }
    }
}