package com.spacetravel.cli;

import com.spacetravel.config.FlywayConfig;
import com.spacetravel.config.HibernateUtil;
import com.spacetravel.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.File;
import java.util.Scanner;

public class App {
    private static final Logger logger = LoggerUtil.getLogger(App.class);
    private static final String DB_FILE_PATH = "./data/spacetravel.mv.db";
    private static boolean isRunning = false;

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                logger.warn("No command provided. Please use 'start' to begin.");
                System.out.println("Please run the application with the 'start' command.");
                return;
            }

            String command = args[0].toLowerCase();

            if ("start".equals(command)) {
                if (!isDatabaseInitialized()) {
                    logger.info("Starting database migration...");
                    FlywayConfig.migrate();
                    logger.info("Migration finished.");
                    System.out.println("Database migration completed.");
                }
                logger.info("Entering interactive CLI mode. Type 'help' for list of commands.");
                isRunning = true;
                runInteractiveCLI();
            } else {
                logger.error("Application not started. Please use 'start' to begin.");
                System.out.println("Error: Application not started. Please use 'start' to begin.");
            }

        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
        } finally {
            shutdown();
        }
    }

    public static boolean isRunning() {
        return isRunning;
    }

    private static boolean isDatabaseInitialized() {
        File dbFile = new File(DB_FILE_PATH);
        return dbFile.exists();
    }

    private static void runInteractiveCLI() {
        try (Scanner scanner = new Scanner(System.in)) {
            CommandParser commandParser = new CommandParser();

            while (isRunning) {
                System.out.print("> ");
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) continue;

                String[] args = line.split("\\s+");
                if ("exit".equalsIgnoreCase(args[0])) {
                    logger.info("Exiting interactive CLI.");
                    isRunning = false;
                    break;
                }

                try {
                    int result = commandParser.executeCommand(args);
                    if (result == 2) {
                        isRunning = false;
                        break;
                    }
                } catch (Exception e) {
                    logger.error("Error executing command: " + String.join(" ", args), e);
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error in interactive CLI mode", e);
            System.out.println("Error: " + e.getMessage());
        } finally {
            logger.info("Interactive CLI session ended.");
        }
    }

    private static void shutdown() {
        try {
            HibernateUtil.shutdown();
            logger.info("Application shutdown completed.");
            System.out.println("Application shutdown. Goodbye!");
        } catch (Exception e) {
            logger.error("Error during shutdown", e);
            System.out.println("Error during shutdown: " + e.getMessage());
        }
    }
}