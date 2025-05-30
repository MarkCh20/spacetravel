package com.spacetravel.dao;

import com.spacetravel.config.HibernateUtil;
import com.spacetravel.entity.Planet;
import com.spacetravel.entity.Ticket;
import com.spacetravel.exception.DataProcessingException;
import com.spacetravel.util.LoggerUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;


public class TicketDaoImpl implements TicketDao {
    private static final Logger LOGGER = LoggerUtil.getLogger(TicketDaoImpl.class);

    public Ticket save(Ticket ticket) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(ticket);
            tx.commit();
            LOGGER.info("Ticket saved for client: {}", ticket.getClient().getId());
            return ticket;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error saving ticket: " + ticket.getClient().getId();
            throw new DataProcessingException(msg, e);
        }
    }

    public Optional<Ticket> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Ticket t " +
                                    "JOIN FETCH t.client " +
                                    "JOIN FETCH t.fromPlanet " +
                                    "JOIN FETCH t.toPlanet " +
                                    "WHERE t.id = :id", Ticket.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        } catch (Exception e) {
            String msg = "Error retrieving ticket by ID: " + id;
            throw new DataProcessingException(msg, e);
        }
    }

    public List<Ticket> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Ticket", Ticket.class).list();
        }
    }

    public List<Ticket> findAllByClient(Long clientId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<Ticket> tickets = session.createQuery(
                            "FROM Ticket t WHERE t.client.id = :clientId", Ticket.class)
                    .setParameter("clientId", clientId)
                    .getResultList();
            tx.commit();
            return tickets;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error retrieving tickets for client ID: " + clientId;
            throw new DataProcessingException(msg, e);
        }
    }

    public List<Ticket> findAllByFromPlanet(String planetId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<Ticket> tickets = session.createQuery(
                            "FROM Ticket t WHERE t.fromPlanet.id = :planetId", Ticket.class)
                    .setParameter("planetId", planetId)
                    .getResultList();

            tx.commit();
            return tickets;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error retrieving tickets from planet ID: " + planetId;
            throw new DataProcessingException(msg, e);
        }
    }

    public List<Ticket> findAllByToPlanet(String planetId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<Ticket> tickets = session.createQuery(
                            "FROM Ticket t WHERE t.toPlanet.id = :planetId", Ticket.class)
                    .setParameter("planetId", planetId)
                    .getResultList();

            tx.commit();
            return tickets;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error retrieving tickets to planet ID: " + planetId;
            throw new DataProcessingException(msg, e);
        }
    }

    public List<Ticket> findAllByDate(LocalDate createdAt) {
        Instant startOfDay = createdAt.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = createdAt.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<Ticket> tickets = session.createQuery(
                            "FROM Ticket t WHERE t.createdAt >= :startOfDay AND t.createdAt < :endOfDay",
                            Ticket.class)
                    .setParameter("startOfDay", startOfDay)
                    .setParameter("endOfDay", endOfDay)
                    .getResultList();

            tx.commit();
            return tickets;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error retrieving tickets by createdAt: " + createdAt;
            throw new DataProcessingException(msg, e);
        }
    }

    public Ticket updateFromPlanet(Long ticketId, Planet newFromPlanet) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Ticket ticket = session.get(Ticket.class, ticketId);
            if (ticket == null) {
                throw new DataProcessingException("Ticket not found for ID: " + ticketId, new NullPointerException());
            }
            ticket.setFromPlanet(newFromPlanet);
            session.merge(ticket);
            tx.commit();
            LOGGER.info("Updated fromPlanet of ticket {} to {}", ticketId, newFromPlanet.getId());
            return ticket;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Failed to update fromPlanet for ticket " + ticketId;
            throw new DataProcessingException(msg, e);
        }
    }

    public Ticket updateToPlanet(Long ticketId, Planet newToPlanet) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Ticket ticket = session.get(Ticket.class, ticketId);
            if (ticket == null) {
                throw new DataProcessingException("Ticket not found for ID: " + ticketId, new NullPointerException());
            }
            ticket.setToPlanet(newToPlanet);
            session.merge(ticket);
            tx.commit();
            LOGGER.info("Updated toPlanet of ticket {} to {}", ticketId, newToPlanet.getId());
            return ticket;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Failed to update toPlanet for ticket " + ticketId;
            throw new DataProcessingException(msg, e);
        }
    }

    public void delete(Ticket ticket) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(ticket);
            tx.commit();
            LOGGER.info("Ticket deleted: {}", ticket.getId());
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Error deleting client: " + ticket.getClient().getId();
            throw new DataProcessingException(msg, e);
        }
    }

    public void deleteAllByClientId(Long clientId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Ticket t WHERE t.client.id = :clientId")
                    .setParameter("clientId", clientId)
                    .executeUpdate();
            tx.commit();
            LOGGER.info("Deleted all tickets with clientId = {}", clientId);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Failed to delete tickets by clientId: " + clientId;
            throw new DataProcessingException(msg, e);
        }
    }

    public void deleteAllByFromPlanetId(String fromPlanetId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Ticket t WHERE t.fromPlanet.id = :fromPlanetId")
                    .setParameter("fromPlanetId", fromPlanetId)
                    .executeUpdate();
            tx.commit();
            LOGGER.info("Deleted all tickets with fromPlanetId = {}", fromPlanetId);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Failed to delete tickets by fromPlanetId: " + fromPlanetId;
            throw new DataProcessingException(msg, e);
        }

    }

    public void deleteAllByToPlanetId(String toPlanetId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM Ticket t WHERE t.toPlanet.id = :toPlanetId")
                    .setParameter("toPlanetId", toPlanetId)
                    .executeUpdate();
            tx.commit();
            LOGGER.info("Deleted all tickets with toPlanetId = {}", toPlanetId);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            String msg = "Failed to delete tickets by toPlanetId: " + toPlanetId;
            throw new DataProcessingException(msg, e);
        }

    }

}
