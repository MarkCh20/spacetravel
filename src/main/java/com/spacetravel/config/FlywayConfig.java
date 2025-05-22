package com.spacetravel.config;

import com.spacetravel.util.LoggerUtil;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;

public class FlywayConfig {
    private static final Logger logger = LoggerUtil.getLogger(FlywayConfig.class);

    public static void migrate() {
        try {
            logger.info("Starting database migration...");
            Flyway flyway = Flyway.configure()
                    .dataSource("jdbc:h2:file:./data/spacetravel", "sa", "")
                    .locations("classpath:db/migration")
                    .load();
            flyway.migrate();
            logger.info("Database migration completed successfully.");
        } catch (Exception e) {
            logger.error("Database migration failed: ", e);

        }
    }
}
