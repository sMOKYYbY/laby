package g62221.labyrinthe.model.command;

import g62221.labyrinthe.model.Game;
import g62221.labyrinthe.model.Player;
import g62221.labyrinthe.model.Position;

/**
 * Command responsible for moving a player on the board.
 * <p>
 * This command encapsulates the logic for moving a player to a specific position.
 * It is crucial for the Undo/Redo mechanism as it captures not just the position change,
 * but also the state of the player's objectives (in case a treasure was found during the move).
 * </p>
 */
public class MovePlayerCommand implements Command {

    private final Game game;
    private final Position target;
    private final Position startPos;
    private final int playerIndex;

    /**
     * Snapshot of the player's state (objectives and cards) before the move.
     * Used to restore the exact state during an undo operation.
     */
    private Player.PlayerState previousState;

    /**
     * Constructs a new MovePlayerCommand.
     *
     * @param game   The game instance.
     * @param target The target position the player wants to move to.
     */
    public MovePlayerCommand(Game game, Position target) {
        this.game = game;
        this.target = target;
        // On capture l'index et la position de départ au moment de la création de la commande
        // Cela permet de savoir "qui" bouge et "d'où" il part sans ambiguïté.
        this.playerIndex = game.getCurrentPlayerIndex();
        this.startPos = game.getPlayerPosition(playerIndex);
    }

    /**
     * Executes the player's move.
     * <p>
     * Saves the player's current objective state before moving, then performs the move
     * in the game model.
     * </p>
     */
    @Override
    public void execute() {
        // 1. Sauvegarde préventive de l'état des cartes (Objectifs)
        // C'est CRUCIAL : si le joueur bouge et trouve un trésor, l'état change.
        // Pour annuler le coup, on doit pouvoir "oublier" qu'il a trouvé ce trésor.
        this.previousState = game.getPlayerState(playerIndex);

        // 2. Application du déplacement dans le modèle
        game.movePlayer(target);
    }

    /**
     * Reverts the player's move.
     * <p>
     * Restores the player to their starting position and reverts their objective state
     * (e.g., un-finding a treasure). Also resets the game turn state.
     * </p>
     */
    @Override
    public void undo() {
        // 1. Restauration physique de la position
        // On téléporte le joueur directement sur sa case de départ.
        game.teleportPlayer(playerIndex, startPos);

        // 2. Restauration logique des cartes (Objectifs et Trouvés)
        // Si le joueur avait ramassé un trésor pendant ce coup, on l'annule ici.
        if (previousState != null) {
            game.restorePlayerState(playerIndex, previousState);
        }

        // 3. Restauration de l'état du jeu
        // On force le jeu à revenir en attente de mouvement (WAITING_FOR_MOVE)
        // et on redonne la main au joueur actuel (car le tour était passé au suivant).
        game.forceState(Game.State.WAITING_FOR_MOVE);
        game.previousPlayer();

        // 4. Gestion de la victoire
        // Si ce coup avait déclenché la victoire, on l'annule pour continuer la partie.
        game.resetWinner();
    }
}