package g62221.labyrinthe.model;

/**
 * Command to move a player.
 * Handles position update AND objective state restoration.
 */
public class MovePlayerCommand implements Command {
    private final Game game;
    private final Position target;
    private final Position startPos;
    private final int playerIndex;

    // NOUVEAU : On garde une copie de l'état des cartes du joueur
    private Player.PlayerState previousState;

    public MovePlayerCommand(Game game, Position target) {
        this.game = game;
        this.target = target;
        // On capture les infos au moment de la création de la commande
        this.playerIndex = game.getCurrentPlayerIndex();
        this.startPos = game.getPlayerPosition(playerIndex);
    }

    @Override
    public void execute() {
        // 1. Sauvegarde de l'état des cartes AVANT le mouvement
        // (Car le mouvement peut déclencher "objectiveFound")
        this.previousState = game.getPlayerState(playerIndex);

        // 2. Déplacement
        game.movePlayer(target);
    }

    @Override
    public void undo() {
        // 1. Restauration de la position
        game.teleportPlayer(playerIndex, startPos);

        // 2. Restauration des cartes (Objectifs et Trouvés)
        // C'est ça qui corrige votre bug !
        if (previousState != null) {
            game.restorePlayerState(playerIndex, previousState);
        }

        // 3. Restauration de l'état du jeu
        game.forceState(Game.State.WAITING_FOR_MOVE);
        game.previousPlayer();

        // 4. Si on annule un coup gagnant, on doit "annuler" la victoire
        game.resetWinner();
    }
}