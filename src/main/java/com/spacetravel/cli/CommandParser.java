package com.spacetravel.cli;

import com.spacetravel.entity.Client;
import com.spacetravel.entity.Planet;
import com.spacetravel.exception.ClientNotFoundException;
import com.spacetravel.exception.DuplicatePlanetIdException;
import com.spacetravel.exception.PlanetNotFoundException;
import com.spacetravel.service.ClientCrudService;
import com.spacetravel.service.PlanetCrudService;
import com.spacetravel.util.LoggerUtil;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class CommandParser {

    private final ClientCrudService clientService = new ClientCrudService();
    private final PlanetCrudService planetService = new PlanetCrudService();
    private final Logger logger = LoggerUtil.getLogger(CommandParser.class);

    /**
     * Processes a command from CLI arguments and returns a completion code:
     * 0 - success, 1 - error, 2 - exit command (to terminate)
     */
    public int executeCommand(String[] args) {
        if (!App.isRunning()) {
            System.out.println("Application not running. Please use 'start' to begin.");
            return 1;
        }

        if (args.length == 0) {
            printHelp();
            return 0;
        }

        String entity = args[0].toLowerCase();
        String action = args.length > 1 ? args[1].toLowerCase() : "";

        try {
            switch (entity) {
                case "help":
                    printHelp();
                    return 0;

                case "exit":
                    System.out.println("Exiting...");
                    return 2;

                // Client commands
                case "client":
                    return handleClientCommand(action, args);

                // Planet commands
                case "planet":
                    return handlePlanetCommand(action, args);

                default:
                    System.out.println("Unknown command. Type 'help' for list.");
                    return 1;
            }
        } catch (Exception e) {
            logger.error("Error executing command", e);
            System.out.println("Error: " + e.getMessage());
            return 1;
        }
    }

        private int handleClientCommand(String action, String[] args) {
            try {
                switch (action) {
                    case "create":
                        if (args.length < 3) {
                            System.out.println("Usage: client create <name>");
                            return 1;
                        }
                        String clientName = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
                        Client createdClient = clientService.create(clientName);
                        System.out.println("Created client with ID: " + createdClient.getId());
                        return 0;

                    case "list":
                        List<Client> clients = clientService.findAll();
                        if (clients.isEmpty()) {
                            System.out.println("No clients found.");
                        } else {
                            clients.forEach(c -> System.out.println(c.getId() + ": " + c.getName()));
                        }
                        return 0;

                    case "get":
                        if (args.length != 3) {
                            System.out.println("Usage: client get <id>");
                            return 1;
                        }
                        Long clientIdGet = Long.parseLong(args[2]);
                        Optional<Client> client = clientService.findById(clientIdGet);
                        client.ifPresentOrElse(
                                c -> System.out.println("Client: " + c.getId() + " - " + c.getName()),
                                () -> System.out.println("Client not found with ID: " + clientIdGet)
                        );
                        return 0;

                    case "update":
                        if (args.length < 4) {
                            System.out.println("Usage: client update <id> <new_name>");
                            return 1;
                        }
                        Long clientIdUpdate = Long.parseLong(args[2]);
                        String newClientName = String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length));
                        Client updatedClient = clientService.update(clientIdUpdate, newClientName);
                        System.out.println("Updated client with ID: " + updatedClient.getId());
                        return 0;

                    case "delete":
                        if (args.length != 3) {
                            System.out.println("Usage: client delete <id>");
                            return 1;
                        }
                        Long clientIdDel = Long.parseLong(args[2]);
                        clientService.delete(clientIdDel);
                        System.out.println("Deleted client " + clientIdDel);
                        return 0;

                    default:
                        System.out.println("Unknown client action. Type 'help' for list.");
                        return 1;
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid client ID format: {}", e.getMessage());
                System.out.println("Client ID must be a number.");
                return 1;
            } catch (IllegalArgumentException e) {
                logger.error("Client command error: {}", e.getMessage());
                System.out.println("Invalid input:" + e.getMessage());
                return 1;
            } catch (ClientNotFoundException e) {
                logger.warn("Client operation failed: {}", e.getMessage());
                System.out.println(e.getMessage());
                return 1;

            } catch (Exception e) {
                logger.error("Client command error", e);
                System.out.println("Error: " + e.getMessage());
                return 1;
            }
        }

        private int handlePlanetCommand(String action, String[] args) {
            try {
                switch (action) {
                    case "create":
                        if (args.length < 4) {
                            System.out.println("Usage: planet create <id> <name>");
                            return 1;
                        }
                        String planetId = args[2];
                        String planetName = String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length));
                        Planet createdPlanet = planetService.create(planetId, planetName);
                        System.out.println("Created planet with ID: " + createdPlanet.getId());
                        return 0;

                    case "list":
                        List<Planet> planets = planetService.findAll();
                        if (planets.isEmpty()) {
                            System.out.println("No planets found.");
                        } else {
                            planets.forEach(p -> System.out.println(p.getId() + ": " + p.getName()));
                        }
                        return 0;

                    case "get":
                        if (args.length != 3) {
                            System.out.println("Usage: planet get <id>");
                            return 1;
                        }
                        String planetIdGet = args[2];
                        Optional<Planet> planet = planetService.findById(planetIdGet);
                        planet.ifPresentOrElse(
                                p -> System.out.println("Planet: " + p.getId() + " - " + p.getName()),
                                () -> System.out.println("Planet not found with ID: " + planetIdGet)
                        );
                        return 0;

                    case "update":
                        if (args.length < 4) {
                            System.out.println("Usage: planet update <id> <new_name>");
                            return 1;
                        }
                        String planetIdUpdate = args[2];
                        String newPlanetName = String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length));
                        Planet updatedPlanet = planetService.update(planetIdUpdate, newPlanetName);
                        System.out.println("Updated planet with ID: " + updatedPlanet.getId());
                        return 0;

                    case "delete":
                        if (args.length != 3) {
                            System.out.println("Usage: planet delete <id>");
                            return 1;
                        }
                        String planetIdDel = args[2];
                        planetService.delete(planetIdDel);
                        System.out.println("Deleted planet " + planetIdDel);
                        return 0;

                    default:
                        System.out.println("Unknown planet action. Type 'help' for list.");
                        return 1;
                }
            } catch (IllegalArgumentException e) {
                logger.error("Planet command error: {}", e.getMessage());
                System.out.println("Invalid input:" + e.getMessage());
                return 1;
            } catch (PlanetNotFoundException e) {
                logger.warn("Planet operation failed: {}", e.getMessage());
                System.out.println(e.getMessage());
                return 1;
            } catch (DuplicatePlanetIdException e) {
                logger.error("Duplicate planet ID: {}", e.getMessage());
                System.out.println("Error: " + e.getMessage());
                return 1;
            } catch (Exception e) {
                logger.error("Planet command error", e);
                System.out.println("Error: " + e.getMessage());
                return 1;
            }
        }



    private void printHelp() {
        System.out.println("""
            Commands:
             help                          - Show this help
             exit                          - Exit program

             client create <name>          - Create new client
             client list                   - List all clients
             client get <id>               - Get client by ID
             client update <id> <new_name> - Update client by ID
             client delete <id>            - Delete client by ID

             planet create <id> <name>     - Create new planet
             planet list                   - List all planets
             planet get <id>               - Get planet by ID
             planet update <id> <new_name> - Update planet by ID
             planet delete <id>            - Delete planet by ID
            """);
    }
}