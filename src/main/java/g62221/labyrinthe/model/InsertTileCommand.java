package g62221.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;

public class InsertTileCommand implements Command {
    private final Game game;
    private final Direction dir;
    private final int index;

    // NOUVEAU : Sauvegarde des positions de tous les joueurs
    private final List<Position> savedPositions;

    public InsertTileCommand(Game game, Direction dir, int index) {
        this.game = game;
        this.dir = dir;
        this.index = index;
        this.savedPositions = new ArrayList<>();
    }

    @Override
    public void execute() {
        // 1. Sauvegarde préventive des positions AVANT le mouvement
        savedPositions.clear();
        for (int i = 0; i < game.getPlayersCount(); i++) {
            savedPositions.add(game.getPlayerPosition(i));
        }

        // 2. Action
        game.insertTile(dir, index);
    }

    @Override
    public void undo() {
        // 1. Annulation du glissement du plateau
        game.getBoard().slide(dir.opposite(), index);

        // 2. RESTAURATION EXACTE DES JOUEURS (Correction du bug de téléportation)
        for (int i = 0; i < savedPositions.size(); i++) {
            game.teleportPlayer(i, savedPositions.get(i));
        }

        // 3. Rétablissement de l'état
        game.forceState(Game.State.WAITING_FOR_SLIDE);
    }
}