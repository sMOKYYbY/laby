package g62221.labyrinthe.view;

import g62221.labyrinthe.model.Tile;
import g62221.labyrinthe.view.image.ImageFactory;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Custom JavaFX component that displays a player's status.
 * <p>
 * This panel shows the player's name, their current secret objective card (hidden if bot),
 * the number of remaining cards, and the list of treasures already collected.
 * It changes appearance when it is the player's turn to provide visual feedback.
 * </p>
 */
public class PlayerInfoPanel extends VBox {
    private final int playerId;
    private final boolean isHuman;
    private final Label nameLabel;
    private final ImageView currentCardView;
    private final Label remainingCountLabel;
    private final FlowPane foundTreasuresPane;

    // --- MISE À JOUR DES COULEURS (Vert, Bleu, Jaune, Rouge) ---
    // Couleurs hexadécimales néons pour correspondre au thème sombre
    private static final String[] PLAYER_COLORS = {"#55FF55", "#5555FF", "#FFFF55", "#FF5555"};

    /**
     * Constructs a new PlayerInfoPanel.
     *
     * @param playerId The ID of the player (0-3).
     * @param isHuman  True if the player is a human user, false for AI.
     */
    public PlayerInfoPanel(int playerId, boolean isHuman) {
        this.playerId = playerId;
        this.isHuman = isHuman;

        // Configuration du Layout vertical (VBox)
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(15);

        // Style Panneau Sombre et Élégant (CSS en ligne)
        // Fond semi-transparent, bords arrondis, bordure subtile
        this.setStyle("-fx-background-color: rgba(20, 20, 20, 0.9); -fx-background-radius: 15; -fx-border-color: #444; -fx-border-width: 1; -fx-border-radius: 15;");
        this.setPadding(new javafx.geometry.Insets(20));
        this.setPrefWidth(200);

        // Effet d'ombre portée globale pour donner de la profondeur
        this.setEffect(new DropShadow(10, Color.BLACK));

        // --- 1. Nom du Joueur ---
        String name = isHuman ? "👤 VOUS (J1)" : "🤖 BOT " + (playerId + 1);
        this.nameLabel = new Label(name);
        this.nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        this.nameLabel.setTextFill(Color.web(PLAYER_COLORS[playerId % 4]));
        // Petit effet de lueur (Glow) sur le nom pour rappeler la couleur du pion
        this.nameLabel.setEffect(new DropShadow(10, Color.web(PLAYER_COLORS[playerId % 4])));

        // --- 2. Boite Objectif (Carte à trouver) ---
        VBox targetBox = new VBox(10);
        targetBox.setAlignment(Pos.CENTER);
        targetBox.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 10; -fx-padding: 15; -fx-border-color: rgba(255,255,255,0.05); -fx-border-radius: 10;");

        Label targetTitle = new Label("🎯 OBJECTIF");
        targetTitle.setTextFill(Color.GRAY);
        targetTitle.setFont(Font.font("Arial", FontWeight.BOLD, 10));

        this.currentCardView = new ImageView();
        this.currentCardView.setFitWidth(80); // Un peu plus grand pour la lisibilité
        this.currentCardView.setFitHeight(80);
        // Ombre portée sous la carte pour effet 3D
        this.currentCardView.setEffect(new DropShadow(10, Color.BLACK));

        this.remainingCountLabel = new Label("?");
        this.remainingCountLabel.setTextFill(Color.WHITE);
        this.remainingCountLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        targetBox.getChildren().addAll(targetTitle, currentCardView, remainingCountLabel);

        // --- 3. Section Trésors trouvés (Inventaire) ---
        VBox foundSection = new VBox(5);
        foundSection.setAlignment(Pos.CENTER);
        Label foundLabel = new Label("🎒 INVENTAIRE");
        foundLabel.setTextFill(Color.GRAY);
        foundLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));

        this.foundTreasuresPane = new FlowPane();
        this.foundTreasuresPane.setAlignment(Pos.CENTER);
        this.foundTreasuresPane.setHgap(5);
        this.foundTreasuresPane.setVgap(5);
        this.foundTreasuresPane.setPrefWrapLength(160);

        foundSection.getChildren().addAll(foundLabel, foundTreasuresPane);

        this.getChildren().addAll(nameLabel, targetBox, foundSection);
    }

    /**
     * Updates the panel with the latest player data.
     *
     * @param currentObjective The name of the treasure to find (or null if finished).
     * @param cardsRemaining   Number of cards left in the stack.
     * @param foundObjectives  List of treasures already collected.
     * @param isCurrentTurn    True if it is this player's turn (triggers highlighting).
     */
    public void update(String currentObjective, int cardsRemaining, List<String> foundObjectives, boolean isCurrentTurn) {
        // Feedback visuel pour indiquer quel joueur est en train de jouer
        if (isCurrentTurn) {
            String pColor = PLAYER_COLORS[playerId % 4];
            // Bordure brillante et fond légèrement plus clair
            this.setStyle("-fx-background-color: rgba(40, 40, 40, 0.95); -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-width: 2; -fx-border-color: " + pColor + "; -fx-effect: dropshadow(three-pass-box, " + pColor + ", 15, 0, 0, 0);");
        } else {
            // Retour au style sombre et discret
            this.setStyle("-fx-background-color: rgba(20, 20, 20, 0.9); -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-width: 1; -fx-border-color: #444; -fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0);");
        }

        this.remainingCountLabel.setText(cardsRemaining + " restante(s)");

        // Gestion de l'affichage de la carte objectif
        if (currentObjective != null) {
            if (isHuman) {
                // Si c'est un humain, on montre le trésor
                // Astuce : On crée une tuile temporaire juste pour récupérer l'image du trésor via la Factory
                Tile fakeTile = new Tile(Tile.Shape.T, 0, currentObjective, false);
                this.currentCardView.setImage(ImageFactory.getImage(fakeTile));
            } else {
                // Si c'est un BOT, on cache l'objectif (Dos de carte)
                try {
                    String path = "/back_card.jpg";
                    if (getClass().getResource(path) != null) {
                        this.currentCardView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(path)));
                    } else {
                        this.currentCardView.setImage(createPlaceholder(Color.DARKRED));
                    }
                } catch (Exception e) {
                    this.currentCardView.setImage(createPlaceholder(Color.DARKRED));
                }
            }
        } else {
            // Plus d'objectifs : Le joueur doit retourner à sa case départ
            this.currentCardView.setImage(null);
            this.remainingCountLabel.setText("RETOUR DÉPART !");
            this.remainingCountLabel.setStyle("-fx-text-fill: #FF5555; -fx-font-weight: bold;");
        }

        // Mise à jour de l'inventaire visuel (petites icônes en bas)
        foundTreasuresPane.getChildren().clear();
        for (String treasure : foundObjectives) {
            Tile fakeTile = new Tile(Tile.Shape.T, 0, treasure, false);
            ImageView imgView = new ImageView(ImageFactory.getImage(fakeTile));
            imgView.setFitWidth(25);
            imgView.setFitHeight(25);
            imgView.setEffect(new DropShadow(2, Color.BLACK));
            foundTreasuresPane.getChildren().add(imgView);
        }
    }

    /**
     * Creates a solid color placeholder image.
     * Used as a fallback if the card back image is missing.
     *
     * @param color The color of the square.
     * @return A WritableImage of 50x50 pixels.
     */
    private javafx.scene.image.Image createPlaceholder(Color color) {
        WritableImage img = new WritableImage(50, 50);
        for (int i = 0; i < 50; i++)
            for (int j = 0; j < 50; j++)
                img.getPixelWriter().setColor(i, j, color);
        return img;
    }
}