package g62221.labyrinthe.view;

import g62221.labyrinthe.model.Direction;
import g62221.labyrinthe.model.LabyrinthFacade;
import g62221.labyrinthe.model.Game;
import g62221.labyrinthe.model.Tile;
import g62221.labyrinthe.model.Position;

import java.util.Scanner;

public class ConsoleView {
    private final LabyrinthFacade facade;
    private final Scanner scanner;

    public ConsoleView(LabyrinthFacade facade) {
        this.facade = facade;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== LABYRINTHE CONSOLE ===");
        System.out.print("Entrez le nombre de joueurs (2-4): ");
        int nbPlayers = scanner.nextInt();
        facade.startGame(nbPlayers);

        while (true) { // Boucle principale du jeu
            displayBoard();
            displayStatus();

            try {
                if (facade.getGameState() == Game.State.WAITING_FOR_SLIDE) {
                    askInsertion();
                } else {
                    askMove();
                }
            } catch (Exception e) {
                System.out.println("ERREUR: " + e.getMessage());
                scanner.nextLine(); // Vider le buffer en cas d'erreur de saisie
            }
        }
    }

    private void displayBoard() {
        System.out.println("\n  0 1 2 3 4 5 6");
        for (int i = 0; i < 7; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 7; j++) {
                Tile t = facade.getTile(i, j);
                // Représentation ASCII simplifiée : I, L, T
                String shape = t.getShape().toString();
                // Affiche un 'P' si un joueur est dessus (simplifié pour l'exemple)
                boolean playerHere = false;
                for(int p=0; p<facade.getNbPlayers(); p++) {
                    Position pos = facade.getPlayerPosition(p);
                    if(pos.row() == i && pos.col() == j) {
                        System.out.print("P" + (p+1) + " ");
                        playerHere = true;
                        break;
                    }
                }
                if(!playerHere) System.out.print(shape + "  ");
            }
            System.out.println();
        }
        System.out.println("Tuile Extra: " + facade.getExtraTile().getShape());
    }

    private void displayStatus() {
        System.out.println("Joueur courant: " + (facade.getCurrentPlayerIndex() + 1));
        System.out.println("Phase: " + facade.getGameState());
    }

    private void askInsertion() {
        System.out.println("Insertion: (Direction: UP, DOWN, LEFT, RIGHT) puis (Index: 1, 3, 5)");
        System.out.print("> ");
        String dirStr = scanner.next();
        int index = scanner.nextInt();
        Direction dir = Direction.valueOf(dirStr.toUpperCase());
        facade.insertTile(dir, index);
    }

    private void askMove() {
        System.out.println("Déplacement: (Ligne) puis (Colonne)");
        System.out.print("> ");
        int r = scanner.nextInt();
        int c = scanner.nextInt();
        facade.movePlayer(r, c);
    }

    // Pour lancer la version console
    public static void main(String[] args) {
        LabyrinthFacade facade = new LabyrinthFacade();
        ConsoleView view = new ConsoleView(facade);
        view.start();
    }
}