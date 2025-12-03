import g62221.labyrinthe.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testSlideRowRight() {
        // On récupère la tuile en (1,6) avant le slide
        Tile tileBefore = board.getTile(1, 6);
        Tile extraBefore = board.getExtraTile();

        // On pousse la ligne 1 vers la droite
        board.slide(Direction.RIGHT, 1);

        // La tuile extra doit être entrée en (1,0)
        assertEquals(extraBefore, board.getTile(1, 0), "L'ancienne tuile extra doit être en début de ligne");

        // L'ancienne dernière tuile doit être devenue la nouvelle extra
        assertEquals(tileBefore, board.getExtraTile(), "La tuile expulsée doit être la nouvelle extra");
    }

    @Test
    void testSlideFixedLineThrowsException() {
        // La ligne 0 est fixe, on ne doit pas pouvoir la bouger
        assertThrows(IllegalArgumentException.class, () -> {
            board.slide(Direction.RIGHT, 0);
        });
    }

    @Test
    void testReachablePositions() {
        // Ce test est plus délicat car le plateau est aléatoire.
        // On va vérifier un cas trivial : une case est toujours accessible depuis elle-même.
        Position start = new Position(0, 0);
        Set<Position> reachable = board.getReachablePositions(start);

        assertTrue(reachable.contains(start), "La position de départ doit être accessible");

        // Vérification de la cohérence : si A peut aller à B, B doit pouvoir aller à A (graphe non orienté)
        // Note: Cela suppose que votre BFS gère bien la réciprocité.
        for (Position p : reachable) {
            assertTrue(board.getReachablePositions(p).contains(start), "Le chemin doit être symétrique");
        }
    }
}