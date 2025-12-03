import g62221.labyrinthe.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
        game.start(2); // Partie à 2 joueurs
    }

    @Test
    void testStartGame() {
        assertEquals(2, game.getPlayersCount());
        assertEquals(0, game.getCurrentPlayerIndex()); // C'est au joueur 0 (P1)
        assertEquals(Game.State.WAITING_FOR_SLIDE, game.getState());

        // Vérification des positions de départ
        assertEquals(new Position(6, 0), game.getPlayerPosition(0)); // P1 Bas-Gauche
        assertEquals(new Position(6, 6), game.getPlayerPosition(1)); // P2 Bas-Droite
    }

    @Test
    void testInsertTileValid() {
        // Le joueur 1 insère une tuile
        game.insertTile(Direction.RIGHT, 1);

        // L'état doit passer à WAITING_FOR_MOVE
        assertEquals(Game.State.WAITING_FOR_MOVE, game.getState());

        // Le joueur interdit doit être mis à jour (Interdit de faire LEFT sur 1)
        assertEquals(Direction.LEFT, game.getForbiddenDirection());
        assertEquals(1, game.getForbiddenIndex());
    }

    @Test
    void testInsertTileForbiddenMove() {
        // 1. Joueur 1 joue
        game.insertTile(Direction.RIGHT, 1);
        // On force le passage au tour suivant (simulé pour le test) pour revenir à l'insertion
        game.movePlayer(game.getPlayerPosition(0));

        // 2. Joueur 2 essaie d'annuler le coup (LEFT sur 1) -> INTERDIT
        assertThrows(IllegalArgumentException.class, () -> {
            game.insertTile(Direction.LEFT, 1);
        });
    }

    @Test
    void testPlayerExpulsion() {
        // On place un joueur sur une ligne mobile pour le test
        // Disons ligne 1, col 6.
        game.teleportPlayer(0, new Position(1, 6));

        // On pousse la ligne 1 vers la Droite -> Le joueur doit être expulsé à Gauche (1, 0)
        game.insertTile(Direction.RIGHT, 1);

        assertEquals(new Position(1, 0), game.getPlayerPosition(0), "Le joueur expulsé à droite doit réapparaître à gauche");
    }
}