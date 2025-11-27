package g62221.labyrinthe.model;

import java.util.*;

public class Game extends Observable {

    private final Board board;
    private final List<Player> players; // Changé de List<Position> à List<Player>
    private int currentPlayerIndex;
    private Player winner;

    public enum State { WAITING_FOR_SLIDE, WAITING_FOR_MOVE, GAME_OVER }
    private State currentState;

    public Game() {
        this.board = new Board();
        this.players = new ArrayList<>();
        this.currentState = State.WAITING_FOR_SLIDE;
    }

    public void start(int nbPlayers) {
        players.clear();
        winner = null;

        // 1. Création des joueurs
        Position[] starts = { new Position(0,0), new Position(0,6), new Position(6,0), new Position(6,6) };
        for (int i = 0; i < nbPlayers; i++) {
            players.add(new Player(i, starts[i]));
        }

        // 2. Distribution des cartes
        distributeCards();

        currentPlayerIndex = 0;
        currentState = State.WAITING_FOR_SLIDE;
        notifyObservers();
    }

    private void distributeCards() {
        // Liste de tous les trésors (24 au total)
        List<String> allTreasures = new ArrayList<>(Arrays.asList(
                "goal_bat", "goal_butteryfly", "goal_dragon", "goal_ghost", "goal_ghost2", "goal_hibou",
                "goal_insecte", "goal_lezard", "goal_mouse", "goal_pig", "goal_spider", "goal_witch",
                "goal_book", "goal_candleholder", "goal_coffre", "goal_crown", "goal_helmet", "goal_keys",
                "goal_map", "goal_money", "goal_ring", "goal_saphir", "goal_skull", "goal_sword"
        ));
        Collections.shuffle(allTreasures);

        // Répartition équitable
        int cardsPerPlayer = allTreasures.size() / players.size();
        int deckIndex = 0;

        for (Player p : players) {
            Stack<String> hand = new Stack<>();
            for (int i = 0; i < cardsPerPlayer; i++) {
                hand.push(allTreasures.get(deckIndex++));
            }
            p.setObjectives(hand);
        }
    }

    public void insertTile(Direction dir, int index) {
        if (currentState != State.WAITING_FOR_SLIDE) return;

        board.slide(dir, index);
        handlePlayerExpulsion(dir, index);
        currentState = State.WAITING_FOR_MOVE;
        notifyObservers();
    }

    public void movePlayer(Position destination) {
        if (currentState != State.WAITING_FOR_MOVE) return;

        Player currentP = players.get(currentPlayerIndex);
        Position start = currentP.getPosition();

        // --- DÉBUT DIAGNOSTIC (DEBUG) ---
        // On récupère les tuiles pour inspecter leur état interne
        Tile startTile = board.getTile(start.row(), start.col());
        Tile destTile = board.getTile(destination.row(), destination.col());

        System.out.println("\n--- DIAGNOSTIC DÉPLACEMENT ---");
        System.out.println("Joueur " + (currentPlayerIndex + 1) + " : Tentative de " + start + " vers " + destination);

        System.out.println("Tuile DÉPART (" + start.row() + "," + start.col() + ") Type: " + startTile.getShape() + " - Rotation: " + startTile.getRotation() + "°");
        System.out.println("   -> Connecteurs (Sorties possibles) : " + startTile.getConnectors());

        System.out.println("Tuile ARRIVÉE (" + destination.row() + "," + destination.col() + ") Type: " + destTile.getShape() + " - Rotation: " + destTile.getRotation() + "°");
        System.out.println("   -> Connecteurs (Entrées possibles) : " + destTile.getConnectors());

        // Calcul de la direction demandée pour vérifier manuellement
        Direction moveDir = null;
        if (destination.row() == start.row() && destination.col() == start.col() - 1) moveDir = Direction.LEFT;
        else if (destination.row() == start.row() && destination.col() == start.col() + 1) moveDir = Direction.RIGHT;
        else if (destination.col() == start.col() && destination.row() == start.row() - 1) moveDir = Direction.UP;
        else if (destination.col() == start.col() && destination.row() == start.row() + 1) moveDir = Direction.DOWN;

        if (moveDir != null) {
            boolean startHasOutput = startTile.getConnectors().contains(moveDir);
            boolean destHasInput = destTile.getConnectors().contains(moveDir.opposite());

            System.out.println("Analyse Direction " + moveDir + " :");
            System.out.println("   1. Sortie Départ OK ? " + (startHasOutput ? "OUI" : "NON (Mur)"));
            System.out.println("   2. Entrée Arrivée OK ? " + (destHasInput ? "OUI" : "NON (Mur)"));

            if (startHasOutput && destHasInput) {
                System.out.println("=> RÉSULTAT : Le chemin devrait être VALIDE.");
            } else {
                System.out.println("=> RÉSULTAT : BLOQUÉ !");
            }
        } else {
            System.out.println("Déplacement non adjacent ou complexe (plus d'une case).");
        }
        System.out.println("------------------------------\n");
        // --- FIN DIAGNOSTIC ---

        // Vérification du chemin (Logique réelle)
        if (!board.getReachablePositions(currentP.getPosition()).contains(destination)) {
            throw new IllegalArgumentException("Chemin bloqué !");
        }

        // Déplacement
        currentP.setPosition(destination);

        // --- LOGIQUE OBJECTIFS ET VICTOIRE ---
        checkObjective(currentP);

        if (checkVictory(currentP)) {
            currentState = State.GAME_OVER;
            this.winner = currentP;
        } else {
            // Tour suivant
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentState = State.WAITING_FOR_SLIDE;
        }

        notifyObservers();
    }

    private void checkObjective(Player p) {
        String target = p.getCurrentObjective();
        Tile currentTile = board.getTile(p.getPosition().row(), p.getPosition().col());

        // Si le joueur est sur la case de son trésor
        if (target != null && currentTile.hasTreasure() && target.equals(currentTile.getTreasure())) {
            p.revealNextObjective(); // Objectif trouvé ! Suivant.
        }
    }

    private boolean checkVictory(Player p) {
        // Gagne si : Plus d'objectifs ET retour à la case départ
        return p.hasFinishedObjectives() && p.getPosition().equals(p.getStartPosition());
    }

    private void handlePlayerExpulsion(Direction dir, int index) {
        for (Player p : players) {
            Position pos = p.getPosition();
            // Logique d'exclusion (inchangée, mais utilise p.setPosition)
            if (dir == Direction.RIGHT && pos.row() == index) {
                p.setPosition(new Position(pos.row(), (pos.col() + 1) % 7));
            } else if (dir == Direction.LEFT && pos.row() == index) {
                p.setPosition(new Position(pos.row(), (pos.col() - 1 + 7) % 7));
            } else if (dir == Direction.DOWN && pos.col() == index) {
                p.setPosition(new Position((pos.row() + 1) % 7, pos.col()));
            } else if (dir == Direction.UP && pos.col() == index) {
                p.setPosition(new Position((pos.row() - 1 + 7) % 7, pos.col()));
            }
        }
    }

    // Getters adaptés pour la Façade
    public Board getBoard() { return board; }
    public Position getPlayerPosition(int index) { return players.get(index).getPosition(); }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public State getState() { return currentState; }
    public int getPlayersCount() { return players.size(); }

    // Nouveaux Getters
    public String getPlayerCurrentObjective(int index) { return players.get(index).getCurrentObjective(); }
    public int getPlayerCardsCount(int index) { return players.get(index).getCardsRemaining(); }
    public Player getWinner() { return winner; }

    // Pour Undo/Redo (à adapter plus tard avec Player, simplifié ici)
    public void previousPlayer() {
        // On réutilise la méthode de calcul pour éviter la duplication
        this.currentPlayerIndex = getPreviousPlayerIndex();
    }
    public void forceState(State state) { this.currentState = state; }
    public void teleportPlayer(int index, Position pos) {
        players.get(index).setPosition(pos);
    }
    /**
     * Calcule l'index du joueur précédent (nécessaire pour le Undo).
     */
    public int getPreviousPlayerIndex() {
        return (currentPlayerIndex - 1 + players.size()) % players.size();
    }
    //Debug pour trouvé mon problème de merde sur les tuiles
    public void debugBoard() {
        System.out.println("\n=== DEBUG TOUTES LES TUILES ===\n");
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                Tile t = board.getTile(r, c);
                System.out.printf("(%d,%d) %s %d° -> %s%n",
                        r, c, t.getShape(), t.getRotation(), t.getConnectors());
            }
        }
        System.out.println("\n=== FIN DEBUG ===\n");
    }

}