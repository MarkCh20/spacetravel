package com.spacetravel.config;

import com.spacetravel.entity.Client;
import com.spacetravel.entity.Planet;
import com.spacetravel.entity.Ticket;
import com.spacetravel.exception.ConfigurationException;
import com.spacetravel.util.LoggerUtil;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {
    private static final Logger LOGGER = LoggerUtil.getLogger(HibernateUtil.class);
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static SessionFactory buildSessionFactory() {
        LOGGER.info("Building Hibernate SessionFactory...");

        try {
            String propertiesFile = System.getProperty("config.file", "application.properties");
            Properties properties = loadProperties(propertiesFile);


            Configuration configuration = new Configuration();
            configuration.setProperties(properties);
            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Planet.class);
            configuration.addAnnotatedClass(Ticket.class);

            LOGGER.info("Hibernate SessionFactory created successfully.");
            return configuration.buildSessionFactory();
        } catch (ConfigurationException e) {
            LOGGER.error("Configuration failed: {}", e.getMessage(), e);
            throw new ExceptionInInitializerError(e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error during SessionFactory creation", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private static Properties loadProperties(String filename) {
        LOGGER.info("Loading Hibernate configuration from {}", filename);
        try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                throw new ConfigurationException("Properties file not found: " + filename);
            }
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new ConfigurationException("Failed to load properties from file: " + filename, e);
        }
    }


    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        LOGGER.info("Shutting down Hibernate SessionFactory...");
        getSessionFactory().close();
        LOGGER.info("SessionFactory shutdown complete.");
    }
}
