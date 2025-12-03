import g62221.labyrinthe.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Stack;

/**
 * Teste la logique pure (Objectifs, Victoire) en isolant le Player.
 */
class GameLogicTest {

    @Test
    void testPlayerFindsObjective() {
        Position start = new Position(0,0);
        Player player = new Player(0, start);

        // On lui donne 2 objectifs manuellement
        Stack<String> cards = new Stack<>();
        cards.push("goal_sword"); // 2ème (caché)
        cards.push("goal_skull"); // 1er (visible)
        player.setObjectives(cards);

        // Vérif initiale
        assertEquals("goal_skull", player.getCurrentObjective());
        assertEquals(2, player.getCardsRemaining());

        // Action : Il trouve le crâne
        player.objectiveFound();

        // Vérif après
        assertEquals("goal_sword", player.getCurrentObjective());
        assertEquals(1, player.getCardsRemaining());
        assertTrue(player.getFoundObjectives().contains("goal_skull"));
    }

    @Test
    void testPlayerWins() {
        Position start = new Position(0,0);
        Player player = new Player(0, start);

        // 1 seul objectif
        Stack<String> cards = new Stack<>();
        cards.push("goal_gold");
        player.setObjectives(cards);

        // Il trouve l'objectif
        player.objectiveFound();
        assertNull(player.getCurrentObjective(), "Plus d'objectif actif");

        // Il est sur sa case départ ?
        player.setPosition(start);

        // La condition de victoire de Game.java est :
        // hasFinishedObjectives() && position.equals(start)
        assertTrue(player.hasFinishedObjectives());
        assertEquals(start, player.getPosition());
    }
}