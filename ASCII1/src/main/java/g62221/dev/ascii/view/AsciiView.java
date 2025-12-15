package g62221.dev.ascii.view;

import g62221.dev.ascii.model.AsciiPaint;

/**
 * Consol view for AsciiPaint.
 * <p>
 * Parcourt le dessin ligne par ligne et affiche à la console le caractère renvoyé
 * par {@link AsciiPaint#getColorAt(int,int)}.
 * </p>
 */
public class AsciiView {
    private AsciiPaint paint;

    public AsciiView(AsciiPaint paint) {
        this.paint = paint;
    }

    /**
     * Affiche le dessin passé en paramètre dans la sortie standard.
     *
     *
     */
    public void display() {
        for (int y = 0; y < paint.getHeight(); y++) {
            for (int x = 0; x < paint.getWidth(); x++) {
                System.out.print(paint.getColorAt(x, y));
            }
            System.out.println();
        }
    }
}
