package g62221.dev.ascii.controller;

import g62221.dev.ascii.model.AsciiPaint;
import g62221.dev.ascii.view.AsciiView;
import java.util.*;
import java.util.regex.*;

public class AsciiController {

    private final AsciiPaint paint;
    private final AsciiView view;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Initialise le contrôleur avec le modèle et la vue.
     */
    public AsciiController(AsciiPaint paint, AsciiView view) {
        this.paint = paint;
        this.view = view;
    }

    /**
     * Lance la boucle principale de l'application.
     */
    public void start() {
        System.out.println("=== AsciiPaint ===");
        System.out.println("Commandes : add, show, list, move, color, delete, group, ungroup, undo, redo, quit");
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }
            executeCommand(line);
        }
    }

    /**
     * Analyse et exécute une ligne de commande.
     */
    private void executeCommand(String line) {
        try {
            if (line.equals("show")) {
                view.display();
            }
            else if (line.equals("list")) {
                listShapes();
            }
            else if (line.startsWith("add circle")) {
                Pattern p = Pattern.compile("add circle (\\d+) (\\d+) (\\d+) (\\S)");
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    int x = Integer.parseInt(m.group(1));
                    int y = Integer.parseInt(m.group(2));
                    int r = Integer.parseInt(m.group(3));
                    char c = m.group(4).charAt(0);
                    paint.addCircle(x, y, r, c);
                }
            }
            else if (line.startsWith("add rectangle")) {
                Pattern p = Pattern.compile("add rectangle (\\d+) (\\d+) (\\d+) (\\d+) (\\S)");
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    int x = Integer.parseInt(m.group(1));
                    int y = Integer.parseInt(m.group(2));
                    int w = Integer.parseInt(m.group(3));
                    int h = Integer.parseInt(m.group(4));
                    char c = m.group(5).charAt(0);
                    paint.addRectangle(x, y, w, h, c);
                }
            }
            else if (line.startsWith("add square")) {
                Pattern p = Pattern.compile("add square (\\d+) (\\d+) (\\d+) (\\S)");
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    int x = Integer.parseInt(m.group(1));
                    int y = Integer.parseInt(m.group(2));
                    int side = Integer.parseInt(m.group(3));
                    char c = m.group(4).charAt(0);
                    paint.addSquare(x, y, side, c);
                }
            }
            else if (line.startsWith("move")) {
                Pattern p = Pattern.compile("move (\\d+) (-?\\d+) (-?\\d+)");
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    int index = Integer.parseInt(m.group(1));
                    int dx = Integer.parseInt(m.group(2));
                    int dy = Integer.parseInt(m.group(3));
                    paint.moveShape(index, dx, dy);
                }
            }
            else if (line.startsWith("color")) {
                Pattern p = Pattern.compile("color (\\d+) (\\S)");
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    int index = Integer.parseInt(m.group(1));
                    char c = m.group(2).charAt(0);
                    paint.setColor(index, c);
                }
            }
            else if (line.startsWith("delete")) {
                Pattern p = Pattern.compile("delete (\\d+)");
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    int index = Integer.parseInt(m.group(1));
                    paint.removeShape(index);
                }
            }
            else if (line.startsWith("group")) {
                String[] parts = line.split(" ");
                List<Integer> indices = new ArrayList<>();
                for (int i = 1; i < parts.length; ++i) {
                    indices.add(Integer.parseInt(parts[i]));
                }
                paint.group(indices);
            }
            else if (line.startsWith("ungroup")) {
                Pattern p = Pattern.compile("ungroup (\\d+)");
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    int index = Integer.parseInt(m.group(1));
                    paint.ungroup(index);
                }
            }
            else if (line.equals("undo")) {
                paint.undo();
            }
            else if (line.equals("redo")) {
                paint.redo();
            }
            else {
                System.out.println("Unknown command: " + line);
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    /**
     * Affiche la liste des formes.
     */
    private void listShapes() {
        var shapes = paint.getDrawing().getShapes();
        for (int i = 0; i < shapes.size(); i++) {
            System.out.println(i + " : " + shapes.get(i).getClass().getSimpleName() +
                    " (" + shapes.get(i).getColor() + ")");
        }
    }
}
