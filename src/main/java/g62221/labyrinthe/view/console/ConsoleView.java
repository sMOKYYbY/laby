package g62221.labyrinthe.view.console;

import g62221.labyrinthe.model.*;
import g62221.labyrinthe.model.facade.LabyrinthFacade;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for the Labyrinth game.
 * <p>
 * This class provides a text-only representation of the game using ASCII characters and ANSI colors.
 * It manages the main game loop, user input parsing, and board rendering within the terminal.
 * Unlike the JavaFX view, this view operates on a procedural loop rather than an event-driven model.
 * </p>
 */
public class ConsoleView {

    private final LabyrinthFacade facade;
    private final Scanner scanner;

    // --- ANSI Color Codes for Terminal ---
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";

    /**
     * Constructs a new ConsoleView.
     *
     * @param facade The facade interface to interact with the game model.
     */
    public ConsoleView(LabyrinthFacade facade) {
        this.facade = facade;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the main game loop.
     * <p>
     * Handles player setup, turn management, and game over condition.
     * This method blocks until the game is finished.
     * </p>
     */
    public void start() {
        System.out.println(CYAN + "=== LABYRINTHE (Version Console) ===" + RESET);

        // 1. Configuration du nombre de joueurs
        int nbPlayers = 0;
        while (nbPlayers < 2 || nbPlayers > 4) {
            System.out.print("Nombre de joueurs (2-4) : ");
            if (scanner.hasNextInt()) {
                nbPlayers = scanner.nextInt();
            } else {
                scanner.next(); // Consomme l'entrée invalide
            }
        }
        facade.startGame(nbPlayers);

        // 2. Boucle principale du jeu
        while (facade.getGameState() != Game.State.GAME_OVER) {
            int currentPlayer = facade.getCurrentPlayerIndex();

            System.out.println("\n--------------------------------------------------");
            System.out.println("TOUR DU JOUEUR " + (currentPlayer + 1) + " (" + getPlayerColor(currentPlayer) + "P" + (currentPlayer + 1) + RESET + ")");

            displayHUD();
            displayBoardSimple();

            // Phase 1 : Insertion de la tuile (Glissement)
            if (facade.getGameState() == Game.State.WAITING_FOR_SLIDE) {
                handleInsertionPhase();
            }

            // Ré-affichage après modification du plateau
            System.out.println("\n[Mise à jour plateau...]");
            displayBoardSimple();

            // Phase 2 : Déplacement du pion
            if (facade.getGameState() == Game.State.WAITING_FOR_MOVE) {
                handleMovePhase();
            }
        }

        // Fin de partie
        System.out.println(YELLOW + "\n🏆 VICTOIRE DU JOUEUR " + (facade.getWinnerId() + 1) + " !!! 🏆" + RESET);
    }

    /**
     * Manages the user input for the tile insertion phase.
     * Loops until a valid move is performed.
     */
    private void handleInsertionPhase() {
        boolean turnComplete = false;
        while (!turnComplete) {
            System.out.println("Action : [rotate] ou [Haut/Bas/Gauche/Droite] [Index 1,3,5]");
            System.out.print("> ");

            String input = scanner.next();

            // Option : Rotation de la tuile en main
            if (input.equalsIgnoreCase("rotate") || input.equalsIgnoreCase("r")) {
                facade.rotateExtraTile();
                System.out.println("Tuile tournée ! État actuel : " + getTileSymbol(facade.getExtraTile()));
                continue; // On redemande une action
            }

            // Tentative d'insertion
            try {
                String dirStr = input;
                if (!scanner.hasNextInt()) {
                    System.out.println(RED + "Erreur : Index manquant ou invalide." + RESET);
                    scanner.nextLine(); // Nettoyage buffer
                    continue;
                }
                int index = scanner.nextInt();
                Direction dir = parseDirection(dirStr);

                boolean success = facade.insertTile(dir, index);
                if (success) {
                    turnComplete = true;
                } else {
                    System.out.println(RED + "Coup impossible (Règle anti-retour ou index invalide)." + RESET);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(RED + "Direction inconnue. Utilisez H, B, G, D." + RESET);
            } catch (Exception e) {
                System.out.println(RED + "Erreur de saisie." + RESET);
                scanner.nextLine();
            }
        }
    }

    /**
     * Manages the user input for the player movement phase.
     */
    private void handleMovePhase() {
        boolean moveComplete = false;
        while (!moveComplete) {
            System.out.println("Déplacement : [Ligne] [Colonne] (ex: 2 4)");
            System.out.print("> ");
            try {
                if (scanner.hasNextInt()) {
                    int r = scanner.nextInt();
                    int c = scanner.nextInt();
                    facade.movePlayer(r, c); // Lève une exception si chemin bloqué
                    moveComplete = true;
                } else {
                    scanner.next(); // Ignore l'entrée texte
                    System.out.println(RED + "Veuillez entrer des coordonnées chiffrées." + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Déplacement impossible : " + e.getMessage() + RESET);
                // On boucle pour redemander
            }
        }
    }

    /**
     * Displays the current player's hand (Extra tile).
     */
    private void displayHUD() {
        Tile extra = facade.getExtraTile();
        System.out.println("En main : [" + getTileSymbol(extra) + "]");
    }

    /**
     * Renders the game board using ASCII box-drawing characters.
     * <p>
     * Displays tiles as paths (═, ║, ╗, etc.) and overlays player positions using P1, P2...
     * </p>
     */
    private void displayBoardSimple() {
        System.out.println("\n      0   1   2   3   4   5   6");
        System.out.println("    ┌───┬───┬───┬───┬───┬───┬───┐");

        for (int r = 0; r < 7; r++) {
            System.out.print(r + "   │"); // Numéro de ligne à gauche
            for (int c = 0; c < 7; c++) {
                Tile tile = facade.getTile(r, c);
                String symbol = getTileSymbol(tile);
                String content = " " + symbol + " ";

                // Vérification si un joueur est sur cette case
                // Si oui, on remplace le symbole de la tuile par l'ID du joueur
                for (int p = 0; p < facade.getNbPlayers(); p++) {
                    Position pos = facade.getPlayerPosition(p);
                    if (pos.row() == r && pos.col() == c) {
                        content = getPlayerColor(p) + "P" + (p + 1) + RESET + " ";
                    }
                }
                System.out.print(content + "│");
            }
            System.out.println();
            // Ligne de séparation entre les rangées
            if (r < 6) System.out.println("    ├───┼───┼───┼───┼───┼───┼───┤");
        }
        System.out.println("    └───┴───┴───┴───┴───┴───┴───┘");
    }

    /**
     * Converts a Tile object into an ASCII character based on its open connectors.
     *
     * @param tile The tile to convert.
     * @return A single unicode character string (e.g., "╗").
     */
    private String getTileSymbol(Tile tile) {
        List<Direction> dirs = tile.getConnectors();
        boolean u = dirs.contains(Direction.UP);
        boolean d = dirs.contains(Direction.DOWN);
        boolean l = dirs.contains(Direction.LEFT);
        boolean r = dirs.contains(Direction.RIGHT);

        // Mapping des connexions vers les caractères de dessin de boîtes (Box Drawing)
        if (u && d && l && r) return "╬";
        if (u && d && l) return "╣";
        if (u && d && r) return "╠";
        if (u && l && r) return "╩";
        if (d && l && r) return "╦";
        if (u && d) return "║";
        if (l && r) return "═";
        if (u && r) return "╚";
        if (u && l) return "╝";
        if (d && r) return "╔";
        if (d && l) return "╗";
        return " ";
    }

    /**
     * Gets the ANSI color code associated with a player index.
     */
    private String getPlayerColor(int index) {
        return switch (index) {
            case 0 -> GREEN;
            case 1 -> BLUE;
            case 2 -> YELLOW;
            case 3 -> RED;
            default -> RESET;
        };
    }

    /**
     * Parses a string input into a Direction enum.
     *
     * @param s The input string (e.g., "Haut", "up", "h").
     * @return The corresponding Direction.
     * @throws IllegalArgumentException if the string is not a valid direction.
     */
    private Direction parseDirection(String s) {
        s = s.toLowerCase();
        // Logique inversée pour l'insertion :
        // Si je suis en HAUT du plateau et que je pousse, la colonne descend (DOWN).
        if (s.startsWith("h") || s.equals("up")) return Direction.DOWN;
        if (s.startsWith("b") || s.equals("down")) return Direction.UP;
        if (s.startsWith("g") || s.equals("left")) return Direction.RIGHT;
        if (s.startsWith("d") || s.equals("right")) return Direction.LEFT;
        throw new IllegalArgumentException("Direction inconnue");
    }

    /**
     * Entry point for the Console version of the game.
     */
    public static void main(String[] args) {
        LabyrinthFacade facade = new LabyrinthFacade();
        ConsoleView view = new ConsoleView(facade);
        view.start();
    }
}