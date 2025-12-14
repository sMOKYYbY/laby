package g62221.labyrinthe.model;

import g62221.labyrinthe.model.observer.Observable;
import java.util.*;

/**
 * Core game logic class.
 * <p>
 * This class manages the central state of the game, including the board, the list of players,
 * the current turn, and the game phase (sliding or moving). It enforces the rules of Labyrinth,
 * such as valid moves, player expulsion, objective collection, and victory conditions.
 * </p>
 * <p>
 * It extends {@link Observable} to notify registered observers (like the View)
 * whenever the game state changes.
 * </p>
 */
public class Game extends Observable {

    private final Board board;
    private final List<Player> players;
    private int currentPlayerIndex;
    private Player winner;

    /**
     * Represents the possible states of a game turn.
     */
    public enum State {
        /** Phase 1: The player must insert a tile into the board. */
        WAITING_FOR_SLIDE,
        /** Phase 2: The player must move their pawn (or stay put). */
        WAITING_FOR_MOVE,
        /** The game has ended. */
        GAME_OVER
    }
    private State currentState;

    // Fields to track the last move to enforce the "Anti-Return" rule.
    private Direction forbiddenDirection = null;
    private int forbiddenIndex = -1;

    /**
     * Constructs a new Game instance.
     * Initializes the board and the player list.
     */
    public Game() {
        this.board = new Board();
        this.players = new ArrayList<>();
        this.currentState = State.WAITING_FOR_SLIDE;
    }

    /**
     * Starts a new game with the specified number of players.
     * <p>
     * Resets the board, creates players at their starting positions, distributes objectives,
     * and sets the initial game state.
     * </p>
     *
     * @param nbPlayers The number of players (between 2 and 4).
     */
    public void start(int nbPlayers) {
        // Nettoyage de la liste des joueurs pour garantir une nouvelle partie propre
        players.clear();
        winner = null;
        forbiddenDirection = null;
        forbiddenIndex = -1;
        board.initializeBoard();

        // Définition des positions de départ dans les 4 coins du plateau
        // P1: Bas-Gauche (6,0), P2: Bas-Droite (6,6), P3: Haut-Droite (0,6), P4: Haut-Gauche (0,0)
        Position[] starts = {
                new Position(6, 0),
                new Position(6, 6),
                new Position(0, 6),
                new Position(0, 0)
        };

        // Création des objets Joueurs
        for (int i = 0; i < nbPlayers; i++) {
            players.add(new Player(i, starts[i]));
        }

        // Mélange et distribution des cartes objectifs
        distributeCards();

        // Le premier joueur commence, phase d'insertion
        currentPlayerIndex = 0;
        currentState = State.WAITING_FOR_SLIDE;
        notifyObservers();
    }

    /**
     * Checks if the current player is an AI bot.
     * <p>
     * By convention in this project, Player 0 is the human user, and all other players are bots.
     * </p>
     *
     * @return true if the current player is a bot, false otherwise.
     */
    public boolean isCurrentPlayerBot() {
        return currentPlayerIndex != 0;
    }

    /**
     * Distributes the 24 treasure cards randomly and equally among players.
     */
    private void distributeCards() {
        // Liste complète des 24 trésors disponibles dans le jeu
        List<String> allTreasures = new ArrayList<>(Arrays.asList(
                "goal_bat", "goal_butteryfly", "goal_dragon", "goal_ghost", "goal_ghost2", "goal_hibou",
                "goal_insecte", "goal_lezard", "goal_mouse", "goal_pig", "goal_spider", "goal_witch",
                "goal_book", "goal_candleholder", "goal_coffre", "goal_crown", "goal_helmet", "goal_keys",
                "goal_map", "goal_money", "goal_ring", "goal_saphir", "goal_skull", "goal_sword"
        ));

        // Mélange aléatoire du paquet
        Collections.shuffle(allTreasures);

        // Calcul du nombre de cartes par joueur
        int cardsPerPlayer = allTreasures.size() / players.size();
        int deckIndex = 0;

        // Distribution des piles de cartes à chaque joueur
        for (Player p : players) {
            Stack<String> hand = new Stack<>();
            for (int i = 0; i < cardsPerPlayer; i++) {
                hand.push(allTreasures.get(deckIndex++));
            }
            p.setObjectives(hand);
        }
    }

    /**
     * Inserts the extra tile into the board at the specified location.
     * <p>
     * This action shifts the tiles in the given row or column. It enforces the game phase
     * and the anti-return rule (preventing the immediate reversal of the previous move).
     * </p>
     *
     * @param dir   The direction to push the tile (UP, DOWN, LEFT, RIGHT).
     * @param index The index of the row or column to insert into.
     * @throws IllegalArgumentException if the move is forbidden (anti-return rule) or index is invalid.
     */
    public void insertTile(Direction dir, int index) {
        // Vérifie qu'on est bien dans la phase d'insertion
        if (currentState != State.WAITING_FOR_SLIDE) return;

        // Règle Anti-Retour : on empêche le joueur d'annuler immédiatement le coup précédent
        if (dir == forbiddenDirection && index == forbiddenIndex) {
            throw new IllegalArgumentException("Forbidden move! You cannot reverse the previous slide.");
        }

        // Modification physique du plateau (glissement)
        board.slide(dir, index);

        // Gestion des joueurs qui seraient poussés hors du plateau (Pac-Man effect)
        handlePlayerExpulsion(dir, index);

        // Mise à jour de l'interdit pour le prochain tour (l'opposé de ce mouvement)
        this.forbiddenDirection = dir.opposite();
        this.forbiddenIndex = index;

        // Changement de phase : le joueur doit maintenant se déplacer
        currentState = State.WAITING_FOR_MOVE;
        notifyObservers();
    }

    /**
     * Moves the current player's pawn to the target destination.
     * <p>
     * Validates the path using BFS. If the move is valid, it updates the player's position,
     * checks if an objective was reached, checks for victory, and passes the turn to the next player.
     * </p>
     *
     * @param destination The target position on the board.
     * @throws IllegalArgumentException if the path to the destination is blocked.
     */
    public void movePlayer(Position destination) {
        // Vérifie qu'on est bien dans la phase de déplacement
        if (currentState != State.WAITING_FOR_MOVE) return;

        Player currentP = players.get(currentPlayerIndex);

        // Vérification qu'un chemin existe entre la position actuelle et la destination (via BFS)
        if (!board.getReachablePositions(currentP.getPosition()).contains(destination)) {
            throw new IllegalArgumentException("Path is blocked!");
        }

        // Mise à jour de la position du joueur
        currentP.setPosition(destination);

        // Vérifie si le joueur est arrivé sur son trésor
        checkObjective(currentP);

        // Vérifie si le joueur a gagné la partie
        if (checkVictory(currentP)) {
            currentState = State.GAME_OVER;
            this.winner = currentP;
        } else {
            // Fin du tour, passage au joueur suivant
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentState = State.WAITING_FOR_SLIDE;
        }

        notifyObservers();
    }

    /**
     * Checks if the player is currently standing on their objective tile.
     * If so, the objective is marked as found and the next one is revealed.
     *
     * @param p The player to check.
     */
    private void checkObjective(Player p) {
        String target = p.getCurrentObjective();
        Tile currentTile = board.getTile(p.getPosition().row(), p.getPosition().col());

        // Si la tuile contient un trésor et que c'est celui recherché par le joueur
        if (target != null && currentTile.hasTreasure() && target.equals(currentTile.getTreasure())) {
            p.objectiveFound();
        }
    }

    /**
     * Checks victory conditions for a player.
     *
     * @param p The player to check.
     * @return true if the player has found all objectives and returned to their start position.
     */
    private boolean checkVictory(Player p) {
        // Condition de Victoire : Tous les objectifs trouvés ET retour à la case départ
        return p.hasFinishedObjectives() && p.getPosition().equals(p.getStartPosition());
    }

    /**
     * Handles the logic for players being pushed off the board during a slide.
     * Players are wrapped around to the opposite side of the board.
     *
     * @param dir   The direction of the slide.
     * @param index The row or column index being slid.
     */
    private void handlePlayerExpulsion(Direction dir, int index) {
        for (Player p : players) {
            Position pos = p.getPosition();

            // Si on pousse vers la Droite et que le joueur est au bout (col 6) -> retour à 0
            if (dir == Direction.RIGHT && pos.row() == index) {
                p.setPosition(new Position(pos.row(), (pos.col() + 1) % 7));
            }
            // Si on pousse vers la Gauche et que le joueur est au bout (col 0) -> retour à 6
            else if (dir == Direction.LEFT && pos.row() == index) {
                p.setPosition(new Position(pos.row(), (pos.col() - 1 + 7) % 7));
            }
            // Si on pousse vers le Bas et que le joueur est en bas (row 6) -> retour à 0
            else if (dir == Direction.DOWN && pos.col() == index) {
                p.setPosition(new Position((pos.row() + 1) % 7, pos.col()));
            }
            // Si on pousse vers le Haut et que le joueur est en haut (row 0) -> retour à 6
            else if (dir == Direction.UP && pos.col() == index) {
                p.setPosition(new Position((pos.row() - 1 + 7) % 7, pos.col()));
            }
        }
    }

    // --- Getters and Accessors ---
    // (Standard accessors used by Facade/View)

    public void setForbiddenState(Direction dir, int index) {
        this.forbiddenDirection = dir;
        this.forbiddenIndex = index;
    }

    public Board getBoard() { return board; }
    public Position getPlayerPosition(int index) { return players.get(index).getPosition(); }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public State getState() { return currentState; }
    public int getPlayersCount() { return players.size(); }
    public String getPlayerCurrentObjective(int index) { return players.get(index).getCurrentObjective(); }
    public int getPlayerCardsCount(int index) { return players.get(index).getCardsRemaining(); }
    public List<String> getPlayerFoundObjectives(int index) { return players.get(index).getFoundObjectives(); }
    public Player getWinner() { return winner; }
    public Direction getForbiddenDirection() { return forbiddenDirection; }
    public int getForbiddenIndex() { return forbiddenIndex; }
    public Position getPlayerStartPosition(int index) { return players.get(index).getStartPosition(); }

    public int getPreviousPlayerIndex() {
        return (currentPlayerIndex - 1 + players.size()) % players.size();
    }

    public void previousPlayer() {
        this.currentPlayerIndex = getPreviousPlayerIndex();
    }

    public void forceState(State state) { this.currentState = state; }

    public void teleportPlayer(int index, Position pos) {
        players.get(index).setPosition(pos);
    }

    // --- Advanced Undo/Redo Helpers (Memento Pattern) ---

    /**
     * Captures a snapshot of a specific player's state.
     * <p>
     * This method creates a Memento object containing the player's current objectives,
     * card stack, and collected treasures. It is used by commands to save the state
     * before executing an action that might modify the player's inventory.
     * </p>
     *
     * @param index The index of the player to save.
     * @return A {@link Player.PlayerState} record representing the player's current data.
     */
    public Player.PlayerState getPlayerState(int index) {
        // Capture un instantané (Memento) de l'état du joueur pour le Undo/Redo
        return players.get(index).saveState();
    }

    /**
     * Restores a specific player's state from a saved snapshot.
     * <p>
     * This method reverts the player's internal data (objectives, stack, found items)
     * to the values stored in the provided Memento object.
     * </p>
     *
     * @param index The index of the player to restore.
     * @param state The {@link Player.PlayerState} snapshot to apply.
     */
    public void restorePlayerState(int index, Player.PlayerState state) {
        // Restaure l'état du joueur (si on annule la découverte d'un trésor par exemple)
        players.get(index).restoreState(state);
    }

    /**
     * Resets the winner of the game.
     * <p>
     * This method clears the winner field. It is essential when undoing the move
     * that triggered the victory condition, effectively resuming the game.
     * </p>
     */
    public void resetWinner() {
        // Réinitialise le gagnant à null (nécessaire si on annule le coup de la victoire)
        this.winner = null;
    }
}