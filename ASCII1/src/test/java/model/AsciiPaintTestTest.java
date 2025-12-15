package model;

import static org.junit.jupiter.api.Assertions.*;

import g62221.dev.ascii.model.*;
import org.junit.jupiter.api.Test;

class AsciiPaintTest {

    @Test
    void testAddCircle() {
        AsciiPaint paint = new AsciiPaint(20, 10);
        paint.addCircle(5, 5, 3, 'o');

        assertEquals('o', paint.getColorAt(5, 5), "Le centre doit contenir la couleur du cercle");
    }

    @Test
    void testAddRectangle() {
        AsciiPaint paint = new AsciiPaint(20, 10);
        paint.addRectangle(2, 2, 4, 3, 'x');

        assertEquals('x', paint.getColorAt(3, 3));
        assertEquals(' ', paint.getColorAt(0, 0));
    }

    @Test
    void testMoveShape() {
        AsciiPaint paint = new AsciiPaint(20, 10);
        paint.addSquare(5, 5, 2, 'c');
        paint.moveShape(0, 5, 0);

        assertEquals('c', paint.getColorAt(10, 5));
    }

    @Test
    void testSetColor() {
        AsciiPaint paint = new AsciiPaint(20, 10);
        paint.addCircle(5, 5, 2, 'o');
        paint.setColor(0, 'z');

        assertEquals('z', paint.getColorAt(5, 5));
    }

    @Test
    void testRemoveShape() {
        AsciiPaint paint = new AsciiPaint(20, 10);
        paint.addCircle(5, 5, 2, 'o');
        paint.removeShape(0);

        assertEquals(' ', paint.getColorAt(5, 5));
    }

    @Test
    void testGetWidthAndHeight() {
        AsciiPaint paint = new AsciiPaint(30, 15);
        assertEquals(30, paint.getWidth());
        assertEquals(15, paint.getHeight());
    }

    // ✅ Tests supplémentaires

    @Test
    void testGetDrawingAndShapeList() {
        AsciiPaint paint = new AsciiPaint(20, 10);
        paint.addCircle(3, 3, 2, 'o');
        paint.addRectangle(1, 1, 2, 2, 'x');

        assertEquals(2, paint.getDrawing().getShapes().size(), "Deux formes devraient être dans le dessin");
    }

    @Test
    void testGetShapeAtCoordinates() {
        AsciiPaint paint = new AsciiPaint(20, 10);
        paint.addCircle(5, 5, 2, 'o');
        Shape s = paint.getDrawing().getShapeAt(new Point(5, 5));

        assertNotNull(s, "Une forme doit être trouvée à cette position");
        assertEquals('o', s.getColor());
    }

    @Test
    void testRemoveInvalidIndex() {
        AsciiPaint paint = new AsciiPaint(20, 10);
        paint.addCircle(5, 5, 2, 'o');

        assertDoesNotThrow(() -> paint.removeShape(5), "Suppression d'un index invalide ne doit pas planter");
    }

    @Test
    void testMoveInvalidIndex() {
        AsciiPaint paint = new AsciiPaint(20, 10);
        assertThrows(IndexOutOfBoundsException.class, () -> paint.moveShape(0, 1, 1));
    }
}
