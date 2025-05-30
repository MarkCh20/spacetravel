package com.spacetravel.cli;

import com.spacetravel.dao.ClientDaoImpl;
import com.spacetravel.dao.PlanetDaoImpl;
import com.spacetravel.dao.TicketDaoImpl;
import com.spacetravel.entity.Client;
import com.spacetravel.entity.Planet;
import com.spacetravel.entity.Ticket;

import com.spacetravel.exception.ClientNotFoundException;
import com.spacetravel.exception.DuplicatePlanetIdException;
import com.spacetravel.exception.PlanetNotFoundException;
import com.spacetravel.exception.TicketNotFoundException;
import com.spacetravel.service.ClientCrudServiceImpl;
import com.spacetravel.service.PlanetCrudServiceImpl;
import com.spacetravel.service.TicketCrudServiceImpl;
import com.spacetravel.util.CommandActions;
import com.spacetravel.util.LoggerUtil;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class CommandParser {

    private final ClientCrudServiceImpl clientService;
    private final PlanetCrudServiceImpl planetService;
    private final TicketCrudServiceImpl ticketService;
    private final Logger logger = LoggerUtil.getLogger(CommandParser.class);

    public CommandParser() {
        this.clientService = new ClientCrudServiceImpl(new ClientDaoImpl(), new TicketDaoImpl());
        this.planetService = new PlanetCrudServiceImpl(new PlanetDaoImpl(), new TicketDaoImpl());
        this.ticketService = new TicketCrudServiceImpl(new TicketDaoImpl(), new PlanetDaoImpl());
    }

    /**
     * Processes a command from CLI arguments and returns a completion code:
     * 0 - success, 1 - error, 2 - exit command (to terminate)
     */
    public int executeCommand(String[] args) {
        if (!App.isRunning()) {
            logger.warn("Application not running. Please use 'start' to begin.");
            return 1;
        }

        if (args.length == 0) {
            printHelp();
            return 0;
        }

        String entity = args[0].toLowerCase();
        String action = args.length > 1 ? args[1].toLowerCase() : "";

        try {
            return switch (entity) {
                case "help" -> {
                    printHelp();
                    yield 0;
                }
                case "exit" -> {
                    logger.info("Exiting...");
                    yield 2;
                }

                // Client commands
                case "client" -> handleClientCommand(action, args);

                // Planet commands
                case "planet" -> handlePlanetCommand(action, args);

                // Ticket commands
                case "ticket" -> handleTicketCommand(action, args);
                default -> {
                    logger.warn("Unknown command. Type 'help' for list.");
                    yield 1;
                }
            };
        } catch (Exception e) {
            logger.error("Error executing command", e);
            return 1;
        }
    }

    private int handleClientCommand(String action, String[] args) {
        try {
            return switch (action) {
                case CommandActions.CREATE -> handleClientCreate(args);
                case CommandActions.LIST -> handleClientList();
                case CommandActions.GET -> handleClientGet(args);
                case CommandActions.UPDATE -> handleClientUpdate(args);
                case CommandActions.DELETE -> handleClientDelete(args);
                default -> {
                    logger.warn("Unknown client action. Type 'help' for list.");
                    yield 1;
                }
            };
        } catch (NumberFormatException e) {
            logger.error("Invalid client ID format: {}", e.getMessage());
            return 1;
        } catch (IllegalArgumentException e) {
            logger.error("Client command error: {}", e.getMessage());
            return 1;
        } catch (ClientNotFoundException e) {
            logger.warn("Client operation failed: {}", e.getMessage());
            return 1;
        } catch (Exception e) {
            logger.error("Client command error", e);
            return 1;
        }
    }

    private int handleClientCreate(String[] args) {
        if (args.length < 3) {
            logger.warn("Invalid usage of command create: Please use command as client create <name>");
            return 1;
        }
        String clientName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        Client createdClient = clientService.create(clientName);
        logger.info("Created client with ID: {}", createdClient.getId());
        return 0;
    }

    private int handleClientList() {
        List<Client> clients = clientService.findAll();
        if (clients.isEmpty()) {
            logger.info("No clients found.");
        } else {
            clients.forEach(c -> System.out.println(c.getId() + ": " + c.getName()));
        }
        return 0;
    }

    private int handleClientGet(String[] args) {
        if (args.length != 3) {
            logger.warn("Invalid usage of command get: Please use command as client get <id>");
            return 1;
        }
        Long clientId = Long.parseLong(args[2]);
        Client client = clientService.findById(clientId);
        logger.info("Client with id {}: - {}", client.getId(), client.getName());
        return 0;
    }

    private int handleClientUpdate(String[] args) {
        if (args.length < 4) {
            logger.warn("Invalid usage of command update: Please use command as client update <id> <new_name>");
            return 1;
        }
        Long clientId = Long.parseLong(args[2]);
        String newName = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        Client client = clientService.findById(clientId);

        if (client.getName().equals(newName)) {
            logger.warn("Entered name '{}' is the same as the current client name '{}'. Please enter a different name.",
                    newName, client.getName());
            return 1;
        }

        Client updatedClient = clientService.update(clientId, newName);
        logger.info("Updated client with ID: {}", updatedClient.getId());
        return 0;
    }

    private int handleClientDelete(String[] args) {
        if (args.length != 3) {
            logger.warn("Invalid usage of command delete: Please use command as client delete <id>");
            return 1;
        }
        Long clientId = Long.parseLong(args[2]);
        clientService.delete(clientId);
        logger.info("Deleted client with ID: {}", clientId);
        return 0;
    }


    private int handlePlanetCommand(String action, String[] args) {
        try {
            return switch (action) {
                case CommandActions.CREATE -> handlePlanetCreate(args);
                case CommandActions.LIST -> handlePlanetList();
                case CommandActions.GET -> handlePlanetGet(args);
                case CommandActions.UPDATE -> handlePlanetUpdate(args);
                case CommandActions.DELETE -> handlePlanetDelete(args);
                default -> {
                    logger.warn("Unknown planet action. Type 'help' for list.");
                    yield 1;
                }
            };
        } catch (IllegalArgumentException e) {
            logger.error("Planet command error: {}", e.getMessage());
            return 1;
        } catch (PlanetNotFoundException e) {
            logger.warn("Planet operation failed: {}", e.getMessage());
            return 1;
        } catch (DuplicatePlanetIdException e) {
            logger.error("Duplicate planet ID: {}", e.getMessage());
            return 1;
        } catch (Exception e) {
            logger.error("Planet command error", e);
            return 1;
        }
    }


    private int handlePlanetCreate(String[] args) {
        if (args.length < 4) {
            logger.warn("Invalid usage of command create: Please use command as planet create <id> <name>");
            return 1;
        }

        String planetId = args[2];
        String planetName = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        if (planetService.findOptionalByName(planetName).isPresent()) {
            logger.warn("A planet with the name '{}' already exists. Please choose a different name.", planetName);
            return 1;
        }

        Planet createdPlanet = planetService.create(planetId, planetName);
        logger.info("Created planet with ID: {}", createdPlanet.getId());
        return 0;
    }

    private int handlePlanetList() {
        List<Planet> planets = planetService.findAll();
        if (planets.isEmpty()) {
            logger.info("No planets found.");
        } else {
            planets.forEach(p -> System.out.println(p.getId() + ": " + p.getName()));
        }
        return 0;
    }

    private int handlePlanetGet(String[] args) {
        if (args.length != 3) {
            logger.warn("Invalid usage of command get: Please use command as planet get <input> (id or name)");
            return 1;
        }

        String input = args[2];

        try {
            Planet planet = planetService.findById(input);
            logger.info("Planet with id {}: - {}", planet.getId(), planet.getName());
            return 0;
        } catch (PlanetNotFoundException eById) {
            try {
                Planet planet = planetService.findByName(input);
                logger.info("Planet with name {}: has ID - {}", planet.getName(), planet.getId());
                return 0;
            } catch (PlanetNotFoundException eByName) {
                logger.warn("No planet found with ID or name: {}", input);
                return 1;
            }
        }
    }

    private int handlePlanetUpdate(String[] args) {
        if (args.length < 4) {
            logger.warn("Invalid usage of command update: Please use command as planet update <id> <new_name>");
            return 1;
        }

        String planetId = args[2];
        String newPlanetName = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        Planet planet = planetService.findById(planetId);

        if (planetService.findOptionalByName(newPlanetName).isPresent()) {
            logger.warn("The planet with the name '{}' already exists. Please choose a different name.", newPlanetName);
            return 1;
        }

        if (planet.getName().equals(newPlanetName)) {
            logger.warn("Entered name '{}' is the same as the current planet name '{}'. Please enter a different name.",
                    newPlanetName, planet.getName());
            return 1;
        }

        Planet updatedPlanet = planetService.update(planetId, newPlanetName);
        logger.info("Updated planet with ID: {}", updatedPlanet.getId());
        return 0;
    }

    private int handlePlanetDelete(String[] args) {
        if (args.length != 3) {
            logger.warn("Invalid usage of command delete: Please use command as planet delete <id>");
            return 1;
        }

        String planetIdDel = args[2];
        planetService.delete(planetIdDel);
        logger.info("Deleted planet with ID - {}", planetIdDel);
        return 0;
    }


    private int handleTicketCommand(String action, String[] args) {
        try {
            return switch (action) {
                case CommandActions.CREATE -> handleTicketCreate(args);
                case CommandActions.LIST -> handleTicketList(args);
                case CommandActions.GET -> handleTicketGet(args);
                case CommandActions.UPDATE_FROM -> handleTicketUpdateFrom(args);
                case CommandActions.UPDATE_TO -> handleTicketUpdateTo(args);
                case CommandActions.DELETE -> handleTicketDelete(args);
                case CommandActions.DELETE_CLIENT -> handleTicketDeleteClient(args);
                case CommandActions.DELETE_FROM -> handleTicketDeleteFrom(args);
                case CommandActions.DELETE_TO -> handleTicketDeleteTo(args);
                default -> {
                    logger.info("Unknown ticket action. Type 'help' for list.");
                    yield 1;
                }
            };
        } catch (IllegalArgumentException e) {
            logger.error("Ticket command error: {}", e.getMessage());
            return 1;
        } catch (TicketNotFoundException e) {
            logger.warn("Ticket operation failed: {}", e.getMessage());
            return 1;
        } catch (Exception e) {
            logger.error("Ticket command error", e);
            return 1;
        }
    }


    private int handleTicketCreate(String[] args) {
        if (args.length != 5) {
            logger.warn("Invalid usage of command create: Please use ticket create <client_id> <from_planet_id> <to_planet_id>");
            return 1;
        }

        Long clientId = Long.parseLong(args[2]);
        String fromPlanetId = args[3];
        String toPlanetId = args[4];

        if (fromPlanetId.equals(toPlanetId)) {
            logger.warn("You entered the same from and to planet ID. Please enter different planet IDs.");
            return 1;
        }

        Client client = clientService.findById(clientId);
        Planet fromPlanet = planetService.findById(fromPlanetId);
        Planet toPlanet = planetService.findById(toPlanetId);

        Ticket created = ticketService.create(new Ticket(client, fromPlanet, toPlanet));

        logger.info("Created: Ticket {}: client={}, from={}, to={}, createdAt={}",
                created.getId(), clientId, fromPlanetId, toPlanetId, created.getCreatedAt());
        return 0;
    }

    private int handleTicketList(String[] args) {
        if (args.length == 2) {
            return listAllTickets();
        }

        if (args.length == 3) {
            String arg = args[2];

            if (tryListTicketsByClientId(arg)) {
                return 0;
            }
            if (tryListTicketsByDate(arg)) {
                return 0;
            }
            if (tryListTicketsByDate(arg)) {
                return 0;
            }

            return listTicketsByPlanetId(arg);
        }
        logger.info("Invalid usage of command list: Please use ticket list [<client_id>|<planet_id>|<created_at>]");
        return 1;
    }

    private int listAllTickets() {
        List<Ticket> all = ticketService.findAll();
        if (all.isEmpty()) {
            logger.warn("No tickets found.");
        } else {
            all.forEach(this::logTicket);
        }
        return 0;
    }

    private boolean tryListTicketsByClientId(String arg) {
        try {
            Long clientId = Long.parseLong(arg);
            List<Ticket> byClient = ticketService.findAllByClient(clientId);
            if (byClient.isEmpty()) {
                logger.warn("No tickets found for client {}", clientId);
            } else {
                byClient.forEach(this::logTicket);
            }
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private boolean tryListTicketsByDate(String arg) {
        try {
            LocalDate date = LocalDate.parse(arg);
            List<Ticket> byDate = ticketService.findAllByDate(date);
            if (byDate.isEmpty()) {
                logger.warn("No tickets found on date {}", arg);
            } else {
                byDate.forEach(this::logTicket);
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private int listTicketsByPlanetId(String planetId) {
        List<Ticket> from = ticketService.findAllByFromPlanet(planetId);
        List<Ticket> to = ticketService.findAllByToPlanet(planetId);

        boolean anyFound = false;

        if (!from.isEmpty()) {
            System.out.println("Tickets from planet:" + planetId);
            from.forEach(this::logTicket);
            anyFound = true;
        }

        if (!to.isEmpty()) {
            System.out.println("Tickets to planet:" + planetId);
            to.forEach(this::logTicket);
            anyFound = true;
        }

        if (!anyFound) {
            logger.warn("No tickets found for planet {}", planetId);
            return 1;
        }

        return 0;
    }

    private void logTicket(Ticket t) {
        System.out.println("Ticket " + t.getId() +
                ": client=" + t.getClient().getId() +
                ", from=" + t.getFromPlanet().getId() +
                ", to=" + t.getToPlanet().getId() +
                ", createdAt=" + t.getCreatedAt());
    }

    private int handleTicketGet(String[] args) {
        if (args.length != 3) {
            logger.warn("Invalid usage of command get: Please use ticket get <id>");
            return 1;
        }

        Long ticketId = Long.parseLong(args[2]);
        Ticket t = ticketService.findById(ticketId);
        System.out.println("Ticket " + t.getId() +
                ": client=" + t.getClient().getId() +
                ", from=" + t.getFromPlanet().getId() +
                ", to=" + t.getToPlanet().getId() +
                ", createdAt=" + t.getCreatedAt());
        return 0;
    }

    private int handleTicketUpdateFrom(String[] args) {
        if (args.length != 4) {
            logger.warn("Invalid usage of command update-from: Please use ticket update-from <ticket_id> <from_planet_id>");
            return 1;
        }

        Long ticketId = Long.parseLong(args[2]);
        String newFrom = args[3];

        Ticket t = ticketService.findById(ticketId);
        if (t.getToPlanet().getId().equals(newFrom)) {
            logger.warn("Entered fromPlanet ID is identical to toPlanet ID.");
            return 1;
        }

        Ticket t2 = ticketService.findById(ticketId);
        if (t2.getFromPlanet().getId().equals(newFrom)) {
            logger.warn("You entered the same from planet ID. Please enter different planet IDs.");
            return 1;
        }

        Ticket updated = ticketService.updateFromPlanet(ticketId, newFrom);
        logger.info("Updated ticket {} from planet to {}", updated.getId(), newFrom);
        return 0;
    }

    private int handleTicketUpdateTo(String[] args) {
        if (args.length != 4) {
            logger.warn("Invalid usage of command update-to: Please use ticket update-to <ticket_id> <to_planet_id>");
            return 1;
        }

        Long ticketId = Long.parseLong(args[2]);
        String newTo = args[3];

        Ticket t = ticketService.findById(ticketId);
        if (t.getFromPlanet().getId().equals(newTo)) {
            logger.warn("Entered toPlanet ID is identical to fromPlanet ID.");
            return 1;
        }

        Ticket t2 = ticketService.findById(ticketId);
        if (t2.getToPlanet().getId().equals(newTo)) {
            logger.warn("You entered the same to planet ID. Please enter different planet IDs.");
            return 1;
        }

        Ticket updated = ticketService.updateToPlanet(ticketId, newTo);
        logger.info("Updated ticket {} to planet to {}", updated.getId(), newTo);
        return 0;
    }

    private int handleTicketDelete(String[] args) {
        if (args.length != 3) {
            logger.warn("Invalid usage of command delete: Please use ticket delete <ticket_id>");
            return 1;
        }

        Long id = Long.parseLong(args[2]);
        ticketService.delete(id);
        logger.info("Deleted ticket {}", id);
        return 0;
    }

    private int handleTicketDeleteClient(String[] args) {
        if (args.length != 3) {
            logger.warn("Invalid usage of command delete-client: Please use ticket delete-client <client_id>");
            return 1;
        }

        Long clientId = Long.parseLong(args[2]);
        ticketService.deleteAllByClientId(clientId);
        logger.info("Deleted all tickets for client {}", clientId);
        return 0;
    }

    private int handleTicketDeleteFrom(String[] args) {
        if (args.length != 3) {
            logger.warn("Invalid usage of command delete-from: Please use ticket delete-from <planet_id>");
            return 1;
        }

        ticketService.deleteAllByFromPlanetId(args[2]);
        logger.info("Deleted all tickets from planet {}", args[2]);
        return 0;
    }

    private int handleTicketDeleteTo(String[] args) {
        if (args.length != 3) {
            logger.warn("Invalid usage of command delete-to: Please use ticket delete-to <planet_id>");
            return 1;
        }

        ticketService.deleteAllByToPlanetId(args[2]);
        logger.info("Deleted all tickets to planet {}", args[2]);
        return 0;
    }


    private void printHelp() {
        logger.info("""
            Commands:
             help                                                       - Show this help
             exit                                                       - Exit program
            
             client create <name>                                       - Create new client
             client list                                                - List all clients
             client get <id>                                            - Get client by ID
             client update <id> <new_name>                              - Update client by ID
             client delete <id>                                         - Delete client by ID
            
             planet create <id> <name>                                  - Create new planet
             planet list                                                - List all planets
             planet get <id>                                            - Get planet by ID
             planet get <name>                                          - Get planet by Name
             planet update <id> <new_name>                              - Update planet by ID
             planet delete <id>                                         - Delete planet by ID
            
             ticket create <client_id> <from_planet_id> <to_planet_id>  - Create new Ticket
             ticket list                                                - List all tickets
             ticket list <client_id>                                    - List all tickets with Client ID
             ticket list <planet_id>                                    - List all tickets with Planet ID
             ticket list <created_at> (YYYY-MM-DD)                      - List all ticket with certain date
             ticket get <ticket_id>                                     - Get ticket by Ticket ID
             ticket update-from <ticket_id> <new_from_planet_id>        - Update ticket's FromPlanet by ID
             ticket update-to <ticket_id> <new_to_planet_id>            - Update ticket's ToPlanet by ID
             ticket delete <ticket_id>                                  - Delete ticket by Ticket ID
             ticket delete-client <client_id>                           - Delete ticket by Client ID
             ticket delete-from <from_planet_id>                        - Delete ticket by FromPlanet ID
             ticket delete-to <to_planet_id>                            - Delete ticket by ToPlanet ID
            """);
    }
}