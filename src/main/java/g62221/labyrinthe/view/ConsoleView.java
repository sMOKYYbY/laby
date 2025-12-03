package g62221.labyrinthe.view;

import g62221.labyrinthe.model.*;

import java.util.List;
import java.util.Scanner;

public class ConsoleView {
    private final LabyrinthFacade facade;
    private final Scanner scanner;

    // Couleurs ANSI
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String GRAY = "\u001B[90m";

    public ConsoleView(LabyrinthFacade facade) {
        this.facade = facade;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println(CYAN + "=== LABYRINTHE ===" + RESET);

        int nbPlayers = 0;
        while (nbPlayers < 2 || nbPlayers > 4) {
            System.out.print("Nombre de joueurs (2-4) : ");
            if (scanner.hasNextInt()) nbPlayers = scanner.nextInt();
            scanner.nextLine();
        }
        facade.startGame(nbPlayers);

        while (facade.getGameState() != Game.State.GAME_OVER) {
            int currentPlayer = facade.getCurrentPlayerIndex();

            System.out.println("\n--------------------------------------------------");
            System.out.println("TOUR DU JOUEUR " + (currentPlayer + 1) + " (" + getPlayerColor(currentPlayer) + "P" + (currentPlayer+1) + RESET + ")");

            displayHUD(currentPlayer);
            displayBoardSimple(); // Méthode simplifiée sans degrés

            if (facade.getGameState() == Game.State.WAITING_FOR_SLIDE) {
                askAndPerformInsertion();
            }

            System.out.println("\n[Mise à jour plateau...]");
            displayBoardSimple();

            if (facade.getGameState() == Game.State.WAITING_FOR_MOVE) {
                askAndPerformMove();
            }
        }

        System.out.println(YELLOW + "\nVICTOIRE DU JOUEUR " + (facade.getWinnerId() + 1) + " !!!" + RESET);
    }

    private void askAndPerformInsertion() {
        System.out.println("Action : [rotate] ou [Haut/Bas/Gauche/Droite] [1,3,5]");
        System.out.print("> ");

        try {
            String input = scanner.next();

            if (input.equalsIgnoreCase("rotate") || input.equalsIgnoreCase("r")) {
                facade.rotateExtraTile();
                System.out.println("Tuile tournée !");
                return;
            }

            String dirStr = input;
            int index = scanner.nextInt();
            Direction dir = parseDirection(dirStr);

            boolean success = facade.insertTile(dir, index);
            if (!success) {
                System.out.println(RED + "Coup interdit (Règle anti-retour) !" + RESET);
                askAndPerformInsertion();
            }
        } catch (Exception e) {
            System.out.println(RED + "Saisie invalide." + RESET);
            scanner.nextLine();
        }
    }

    private void askAndPerformMove() {
        System.out.println("Déplacement : [Ligne] [Colonne]");
        System.out.print("> ");
        try {
            int r = scanner.nextInt();
            int c = scanner.nextInt();
            facade.movePlayer(r, c);
        } catch (Exception e) {
            System.out.println(RED + "Erreur : " + e.getMessage() + RESET);
            scanner.nextLine();
        }
    }

    private void displayHUD(int pIndex) {
        // Suppression de l'affichage de la cible/objectif
        // String objective = facade.getPlayerCurrentObjective(pIndex); ... (Supprimé)

        Tile extra = facade.getExtraTile();
        System.out.println("En main : [" + getTileSymbol(extra) + "]"); // Plus de degrés
    }

    /**
     * Affiche le plateau simplifié (Symbole uniquement).
     */
    private void displayBoardSimple() {
        // Largeur de case réduite car plus de degrés
        System.out.println("\n      0   1   2   3   4   5   6");
        System.out.println("    ┌───┬───┬───┬───┬───┬───┬───┐");

        for (int r = 0; r < 7; r++) {
            System.out.print(r + "   │");
            for (int c = 0; c < 7; c++) {
                Tile tile = facade.getTile(r, c);
                String symbol = getTileSymbol(tile);

                String content = " " + symbol + " ";

                // Vérifier présence joueur
                boolean playerHere = false;
                for (int p = 0; p < facade.getNbPlayers(); p++) {
                    Position pos = facade.getPlayerPosition(p);
                    if (pos.row() == r && pos.col() == c) {
                        content = getPlayerColor(p) + "P" + (p + 1) + RESET + " "; // Juste P1
                        playerHere = true;
                    }
                }

                System.out.print(content + "│");
            }
            System.out.println();
            if (r < 6) System.out.println("    ├───┼───┼───┼───┼───┼───┼───┤");
        }
        System.out.println("    └───┴───┴───┴───┴───┴───┴───┘");
    }

    private String getTileSymbol(Tile tile) {
        List<Direction> dirs = tile.getConnectors();
        boolean u = dirs.contains(Direction.UP);
        boolean d = dirs.contains(Direction.DOWN);
        boolean l = dirs.contains(Direction.LEFT);
        boolean r = dirs.contains(Direction.RIGHT);

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

    private String getPlayerColor(int index) {
        return switch (index) {
            case 0 -> GREEN;
            case 1 -> BLUE;
            case 2 -> YELLOW;
            case 3 -> RED;
            default -> RESET;
        };
    }

    private Direction parseDirection(String s) {
        s = s.toLowerCase();
        if (s.startsWith("h") || s.equals("up")) return Direction.DOWN;
        if (s.startsWith("b") || s.equals("down")) return Direction.UP;
        if (s.startsWith("g") || s.equals("left")) return Direction.RIGHT;
        if (s.startsWith("d") || s.equals("right")) return Direction.LEFT;
        throw new IllegalArgumentException("Direction inconnue");
    }

    public static void main(String[] args) {
        LabyrinthFacade facade = new LabyrinthFacade();
        ConsoleView view = new ConsoleView(facade);
        view.start();
    }
}