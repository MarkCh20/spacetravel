package com.spacetravel.config;

import com.spacetravel.util.LoggerUtil;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;

public class FlywayConfig {
    private static final Logger LOGGER = LoggerUtil.getLogger(FlywayConfig.class);

    public static void migrate() {
        try {
            LOGGER.info("Starting database migration...");
            Flyway flyway = Flyway.configure()
                    .dataSource("jdbc:h2:file:./data/spacetravel", "sa", "")
                    .locations("classpath:db/migration")
                    .load();
            flyway.migrate();
            LOGGER.info("Database migration completed successfully.");
        } catch (Exception e) {
            LOGGER.error("Database migration failed: ", e);

        }
    }
}
