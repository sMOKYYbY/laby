package g62221.labyrinthe.model.facade;

import g62221.labyrinthe.model.Direction;
import g62221.labyrinthe.model.Game;
import g62221.labyrinthe.model.Position;
import g62221.labyrinthe.model.Tile;
import g62221.labyrinthe.model.command.Command;
import g62221.labyrinthe.model.command.CommandManager;
import g62221.labyrinthe.model.command.InsertTileCommand;
import g62221.labyrinthe.model.command.MovePlayerCommand;
import g62221.labyrinthe.model.observer.Observable;
import g62221.labyrinthe.model.observer.Observer;

import java.util.*;

/**
 * Facade pattern entry point for the Model layer.
 * <p>
 * This class provides a simplified interface to the complex logic of the game (Game, Board, Players)
 * and the command history (CommandManager). It acts as the main point of interaction for
 * the Controller and the View.
 * </p>
 * <p>
 * It also encapsulates the Artificial Intelligence (AI) logic for bot players, including
 * move simulation and pathfinding strategies.
 * </p>
 */
public class LabyrinthFacade extends Observable implements Observer {
    private final Game game;
    private final CommandManager commandManager;
    private boolean isSimulating = false;

    /**
     * Constructs a new LabyrinthFacade.
     * Initializes the game and command manager, and sets up the observer relationship.
     */
    public LabyrinthFacade() {
        this.game = new Game();
        this.commandManager = new CommandManager();
        // La façade écoute les changements du jeu pour les relayer à la vue
        this.game.addObserver(this);
    }

    /**
     * Starts a new game with the specified number of players.
     * Clears the command history.
     *
     * @param nbPlayers The number of players participating.
     */
    public void startGame(int nbPlayers) {
        // On vide l'historique (Undo/Redo) au début d'une nouvelle partie
        commandManager.clear();
        game.start(nbPlayers);
    }

    /**
     * Attempts to insert the extra tile into the board.
     * Executes the action via a command to support Undo/Redo.
     *
     * @param direction The direction of the slide.
     * @param index     The index of the row or column.
     * @return true if the move was successful, false if it was forbidden (anti-return rule).
     */
    public boolean insertTile(Direction direction, int index) {
        try {
            // On encapsule l'action dans une commande pour permettre l'annulation
            Command cmd = new InsertTileCommand(game, direction, index);
            commandManager.execute(cmd);
            return true; // L'action a réussi
        } catch (Exception e) {
            // Si le coup est interdit (règle anti-retour), on retourne false
            // Cela permet à l'IA de savoir que ce coup n'est pas jouable
            return false;
        }
    }

    /**
     * Moves the current player to the specified coordinates.
     * Executes the action via a command to support Undo/Redo.
     *
     * @param row The target row index.
     * @param col The target column index.
     */
    public void movePlayer(int row, int col) {
        try {
            Position target = new Position(row, col);
            Command cmd = new MovePlayerCommand(game, target);
            commandManager.execute(cmd);
        } catch (Exception e) {
            // On ignore silencieusement les erreurs ici (ex: clic sur un mur)
            // La vue gère généralement l'affichage des erreurs via le contrôleur
        }
    }

    /**
     * Rotates the extra tile currently in hand.
     * Notifies observers to update the view.
     */
    public void rotateExtraTile() {
        // Rotation simple de la tuile en main
        game.getBoard().getExtraTile().rotate();
        notifyObservers();
    }
    /**
     * Rotates the extra tile currently in hand counter-clockwise.
     */
    public void rotateExtraTileCCW() {
        game.getBoard().getExtraTile().rotateCounterClockwise();
        notifyObservers();
    }

    /**
     * Undoes the last executed command.
     */
    public void undo() {
        commandManager.undo();
        notifyObservers();
    }

    /**
     * Redoes the last undone command.
     */
    public void redo() {
        commandManager.redo();
        notifyObservers();
    }

    /**
     * Updates the facade when the observed Game model changes.
     * <p>
     * This method filters notifications: if the AI is currently simulating moves (`isSimulating` is true),
     * the notification is suppressed to prevent the UI from flickering. Otherwise, it propagates the notification.
     * </p>
     */
    @Override
    public void update() {
        // "Mode Silencieux" : Si l'IA réfléchit, on ne prévient pas la vue
        // pour éviter que l'interface ne clignote à chaque simulation de coup.
        if (!isSimulating) {
            notifyObservers();
        }
    }

    /**
     * Executes the AI logic for the current bot player.
     * <p>
     * The AI strategy is "Greedy" (Level 1):
     * 1. Identify the target (current objective or start position if finished).
     * 2. Simulate all possible tile insertions (12 combinations).
     * 3. For each valid insertion, check if a path exists to the target.
     * 4. If a winning move is found, execute it for real and end turn.
     * 5. If no winning move is found, perform a random move to avoid stalling.
     * </p>
     */
    public void playBot() {
        // Vérification de sécurité : si c'est à l'humain, le bot ne fait rien
        if (!game.isCurrentPlayerBot()) return;

        // 1. Activation du mode silencieux pour les simulations
        isSimulating = true;

        String objective = getCurrentPlayerObjective();
        // Si le joueur a un objectif, il le vise. Sinon, il vise sa case de départ pour gagner.
        Position targetPos = (objective != null)
                ? findTreasurePosition(objective)
                : game.getPlayerStartPosition(game.getCurrentPlayerIndex());

        // Cas rare : l'objectif est sur la tuile en main (hors plateau), on ne peut pas l'atteindre
        if (targetPos == null) {
            isSimulating = false; // On réactive l'affichage
            playRandomMove();
            return;
        }

        int[] indices = {1, 3, 5};
        Direction[] dirs = Direction.values();

        // 2. Boucle de simulation : On teste TOUTES les insertions possibles
        for (int idx : indices) {
            for (Direction dir : dirs) {
                // A. On joue le coup "pour de faux" (simulation)
                boolean success = insertTile(dir, idx);

                if (success) {
                    // B. Si l'insertion est valide, on vérifie si le chemin est ouvert
                    Position botPos = game.getPlayerPosition(game.getCurrentPlayerIndex());
                    Set<Position> reachable = game.getBoard().getReachablePositions(botPos);

                    // Attention : l'insertion a peut-être déplacé la cible, on la recalcule
                    Position currentTarget = (objective != null)
                            ? findTreasurePosition(objective)
                            : game.getPlayerStartPosition(game.getCurrentPlayerIndex());

                    // C. VICTOIRE ? Si la cible est dans les cases accessibles
                    if (currentTarget != null && reachable.contains(currentTarget)) {
                        // 1. On annule le coup simulé (Undo silencieux)
                        undo();

                        // 2. On désactive le mode silencieux pour que le joueur voie l'action
                        isSimulating = false;

                        // 3. On joue le coup "pour de vrai"
                        insertTile(dir, idx);
                        movePlayer(currentTarget.row(), currentTarget.col());
                        return; // Fin du tour
                    }

                    // D. Échec de la simulation : On annule pour tester la suivante
                    undo();
                }
            }
        }

        // 3. Aucune solution immédiate trouvée : Fallback sur un coup aléatoire
        isSimulating = false; // On réactive l'affichage
        playRandomMove();
    }

    /**
     * Executes a random move (insertion + movement).
     * Used as a fallback strategy when the AI cannot reach its objective immediately.
     */
    private void playRandomMove() {
        Random rand = new Random();
        int[] indices = {1, 3, 5};

        boolean inserted = false;
        // On essaie d'insérer au hasard jusqu'à trouver un coup autorisé (non interdit)
        while (!inserted) {
            inserted = insertTile(Direction.values()[rand.nextInt(4)], indices[rand.nextInt(3)]);
        }

        // Une fois inséré, on déplace le pion sur une case accessible au hasard
        Position botPos = game.getPlayerPosition(game.getCurrentPlayerIndex());
        List<Position> reachable = new ArrayList<>(game.getBoard().getReachablePositions(botPos));

        if (!reachable.isEmpty()) {
            Position dest = reachable.get(rand.nextInt(reachable.size()));
            movePlayer(dest.row(), dest.col());
        } else {
            // Si bloqué, on reste sur place
            movePlayer(botPos.row(), botPos.col());
        }
    }

    /**
     * Locates the position of a specific treasure on the board.
     *
     * @param treasureName The name of the treasure to find.
     * @return The Position of the tile containing the treasure, or null if not found (e.g., if on extra tile).
     */
    private Position findTreasurePosition(String treasureName) {
        // Parcours complet du plateau pour trouver la tuile
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                Tile t = game.getBoard().getTile(r, c);
                if (t.hasTreasure() && t.getTreasure().equals(treasureName)) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    /**
     * Checks if the current player is a bot.
     * @return true if bot.
     */
    public boolean isCurrentPlayerBot() { return game.isCurrentPlayerBot(); }

    /**
     * Gets the tile at specific coordinates.
     * @param r Row index.
     * @param c Column index.
     * @return The tile.
     */
    public Tile getTile(int r, int c) { return game.getBoard().getTile(r, c); }

    /**
     * Gets the extra tile.
     * @return The extra tile.
     */
    public Tile getExtraTile() { return game.getBoard().getExtraTile(); }

    /**
     * Gets a player's position.
     * @param index The player's index.
     * @return The position.
     */
    public Position getPlayerPosition(int index) { return game.getPlayerPosition(index); }

    /**
     * Gets the current player's index.
     * @return The index.
     */
    public int getCurrentPlayerIndex() { return game.getCurrentPlayerIndex(); }

    /**
     * Gets the current game state.
     * @return The state.
     */
    public Game.State getGameState() { return game.getState(); }

    /**
     * Gets the number of players.
     * @return The count.
     */
    public int getNbPlayers() { return game.getPlayersCount(); }

    /**
     * Gets the current player's objective name.
     * @return The objective name.
     */
    public String getCurrentPlayerObjective() { return game.getPlayerCurrentObjective(game.getCurrentPlayerIndex()); }

    /**
     * Gets a specific player's current objective.
     * @param index The player index.
     * @return The objective name.
     */
    public String getPlayerCurrentObjective(int index) { return game.getPlayerCurrentObjective(index); }

    /**
     * Gets the current player's remaining card count.
     * @return The count.
     */
    public int getCurrentPlayerCardsCount() { return game.getPlayerCardsCount(game.getCurrentPlayerIndex()); }

    /**
     * Gets a specific player's remaining card count.
     * @param index The player index.
     * @return The count.
     */
    public int getPlayerCardsCount(int index) { return game.getPlayerCardsCount(index); }

    /**
     * Gets the list of objectives found by a specific player.
     * @param index The player index.
     * @return The list of found treasures.
     */
    public List<String> getPlayerFoundObjectives(int index) { return game.getPlayerFoundObjectives(index); }

    /**
     * Gets the winner's ID.
     * @return The ID or -1.
     */
    public int getWinnerId() { return (game.getWinner() != null) ? game.getWinner().getId() : -1; }

    /**
     * Gets the forbidden direction (Anti-Return rule).
     * @return The direction.
     */
    public Direction getForbiddenDirection() { return game.getForbiddenDirection(); }

    /**
     * Gets the forbidden index (Anti-Return rule).
     * @return The index.
     */
    public int getForbiddenIndex() { return game.getForbiddenIndex(); }

    /**
     * Gets a player's starting position.
     * @param index The player index.
     * @return The start position.
     */
    public Position getPlayerStartPosition(int index) { return game.getPlayerStartPosition(index); }
}