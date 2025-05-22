package com.spacetravel.config;

import com.spacetravel.entity.Client;
import com.spacetravel.entity.Planet;
import com.spacetravel.util.LoggerUtil;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;

import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {
    private static final Logger logger = LoggerUtil.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            logger.info("Building Hibernate SessionFactory...");
            Configuration configuration = new Configuration();

            String propertiesFile = System.getProperty("config.file", "application.properties");
            InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream(propertiesFile);
            if (input == null) {
                throw new IllegalStateException("Properties file not found: " + propertiesFile);
            }

            Properties properties = new Properties();
            properties.load(input);
            configuration.setProperties(properties);

            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Planet.class);

            logger.info("Hibernate SessionFactory created successfully.");
            return configuration.buildSessionFactory();
        } catch (Exception e) {
            logger.error("SessionFactory creation failed", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        logger.info("Shutting down Hibernate SessionFactory...");
        getSessionFactory().close();
        logger.info("SessionFactory shutdown complete.");
    }
}
