package g62221.labyrinthe.model.command;

import g62221.labyrinthe.model.Direction;
import g62221.labyrinthe.model.Game;
import g62221.labyrinthe.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Command responsible for inserting a tile into the game board.
 * <p>
 * This command encapsulates the logic of sliding a row or a column.
 * Crucially, it saves the state of the game (player positions and game rules) before execution
 * to ensure a perfect restoration during an {@code undo()} operation.
 * </p>
 */
public class InsertTileCommand implements Command {

    /**
     * The game instance.
     */
    private final Game game;

    /**
     * The direction of the insertion.
     */
    private final Direction dir;

    /**
     * The index of the row or column to insert the tile into.
     */
    private final int index;

    /**
     * Snapshot of player positions before the insertion.
     * Used to restore players to their exact spots during undo.
     */
    private final List<Position> savedPositions;

    /**
     * Snapshot of the forbidden direction rule before the insertion.
     */
    private Direction prevForbiddenDir;

    /**
     * Snapshot of the forbidden index rule before the insertion.
     */
    private int prevForbiddenIndex;

    /**
     * Constructs a new command to insert a tile.
     *
     * @param game  The game instance.
     * @param dir   The direction to slide.
     * @param index The row or column index.
     */
    public InsertTileCommand(Game game, Direction dir, int index) {
        this.game = game;
        this.dir = dir;
        this.index = index;
        this.savedPositions = new ArrayList<>();
    }

    /**
     * Executes the tile insertion.
     * <p>
     * Before modifying the board, this method captures the current state (forbidden moves and player positions)
     * to support the undo functionality.
     * </p>
     */
    @Override
    public void execute() {
        // 1. Sauvegarde de la règle "Anti-Retour" actuelle.
        // C'est crucial pour l'IA : quand elle annule son test, le jeu doit "oublier" que l'IA a joué
        // et se souvenir de la contrainte imposée par le joueur précédent.
        this.prevForbiddenDir = game.getForbiddenDirection();
        this.prevForbiddenIndex = game.getForbiddenIndex();

        // 2. Sauvegarde des positions de tous les joueurs.
        // Si l'insertion pousse un joueur hors du plateau (wrap-around), le simple glissement inverse
        // ne suffit pas toujours à le remettre au bon endroit. On sauvegarde donc les positions exactes.
        savedPositions.clear();
        for (int i = 0; i < game.getPlayersCount(); i++) {
            savedPositions.add(game.getPlayerPosition(i));
        }

        // 3. Application de l'insertion dans le modèle.
        // Cela va modifier le plateau, déplacer les joueurs affectés et mettre à jour la règle anti-retour.
        game.insertTile(dir, index);
    }

    /**
     * Reverts the tile insertion.
     * <p>
     * This method slides the board in the opposite direction and rigorously restores
     * the players and game rules to their state prior to execution.
     * </p>
     */
    @Override
    public void undo() {
        // 1. On annule le changement physique du plateau.
        // On pousse simplement dans la direction opposée pour remettre les tuiles en place.
        game.getBoard().slide(dir.opposite(), index);

        // 2. Restauration chirurgicale des joueurs.
        // On replace chaque joueur exactement là où il était avant l'execute().
        for (int i = 0; i < savedPositions.size(); i++) {
            game.teleportPlayer(i, savedPositions.get(i));
        }

        // 3. Restauration de la règle "Anti-Retour".
        // On remet l'interdit tel qu'il était avant ce coup.
        game.setForbiddenState(prevForbiddenDir, prevForbiddenIndex);

        // 4. On force le jeu à revenir dans l'état d'attente d'insertion.
        // Cela permet au joueur (ou à l'IA) de rejouer cette phase.
        game.forceState(Game.State.WAITING_FOR_SLIDE);
    }
}