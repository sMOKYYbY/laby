package g62221.labyrinthe.view;

import g62221.labyrinthe.model.Tile;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class PlayerInfoPanel extends VBox {
    private final int playerId;
    private final boolean isHuman;
    private final Label nameLabel;
    private final ImageView currentCardView;
    private final Label remainingCountLabel;
    private final FlowPane foundTreasuresPane;

    // Couleurs des joueurs pour le thème
    private static final String[] PLAYER_COLORS = {"#FF5555", "#FFFF55", "#55FF55", "#5555FF"}; // Rouge, Jaune, Vert, Bleu

    public PlayerInfoPanel(int playerId, boolean isHuman) {
        this.playerId = playerId;
        this.isHuman = isHuman;

        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(10);
        // Fond noir semi-transparent + Bords arrondis
        this.setStyle("-fx-background-color: rgba(30, 30, 30, 0.85); -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-width: 2; -fx-border-color: #444;");
        this.setPadding(new javafx.geometry.Insets(15));
        this.setPrefWidth(190);

        // Effet d'ombre sous le panneau
        this.setEffect(new DropShadow(10, Color.BLACK));

        // 1. Nom du joueur (Stylisé)
        String name = isHuman ? "VOUS (J1)" : "BOT " + (playerId + 1);
        this.nameLabel = new Label(name);
        this.nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        this.nameLabel.setTextFill(Color.web(PLAYER_COLORS[playerId % 4]));

        // 2. Zone Objectif (Encadrée)
        VBox targetBox = new VBox(5);
        targetBox.setAlignment(Pos.CENTER);
        targetBox.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10; -fx-padding: 10;");

        Label targetTitle = new Label("Objectif Actuel");
        targetTitle.setTextFill(Color.LIGHTGRAY);
        targetTitle.setFont(Font.font("Arial", 12));

        this.currentCardView = new ImageView();
        this.currentCardView.setFitWidth(70);
        this.currentCardView.setFitHeight(70);
        // Ombre sous la carte pour le relief
        this.currentCardView.setEffect(new DropShadow(5, Color.BLACK));

        this.remainingCountLabel = new Label("?");
        this.remainingCountLabel.setTextFill(Color.WHITE);
        this.remainingCountLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        targetBox.getChildren().addAll(targetTitle, currentCardView, remainingCountLabel);

        // 3. Zone Butin
        VBox foundSection = new VBox(5);
        foundSection.setAlignment(Pos.CENTER);

        Label foundLabel = new Label("Trésors Récoltés");
        foundLabel.setTextFill(Color.GRAY);
        foundLabel.setFont(Font.font("Arial", 10));

        this.foundTreasuresPane = new FlowPane();
        this.foundTreasuresPane.setAlignment(Pos.CENTER);
        this.foundTreasuresPane.setHgap(5);
        this.foundTreasuresPane.setVgap(5);
        this.foundTreasuresPane.setPrefWrapLength(160);

        foundSection.getChildren().addAll(foundLabel, foundTreasuresPane);

        this.getChildren().addAll(nameLabel, targetBox, foundSection);
    }

    public void update(String currentObjective, int cardsRemaining, List<String> foundObjectives, boolean isCurrentTurn) {
        // Mise en évidence du joueur actif
        if (isCurrentTurn) {
            // Bordure brillante de la couleur du joueur + Glow
            String pColor = PLAYER_COLORS[playerId % 4];
            this.setStyle("-fx-background-color: rgba(40, 40, 40, 0.95); -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-width: 3; -fx-border-color: " + pColor + ";");
            this.setEffect(new DropShadow(15, Color.web(pColor))); // Effet de néon
        } else {
            this.setStyle("-fx-background-color: rgba(30, 30, 30, 0.85); -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-width: 1; -fx-border-color: #555;");
            this.setEffect(new DropShadow(5, Color.BLACK));
        }

        this.remainingCountLabel.setText(cardsRemaining + " restante(s)");

        // Affichage carte
        if (currentObjective != null) {
            if (isHuman) {
                Tile fakeTile = new Tile(Tile.Shape.T, 0, currentObjective, false);
                this.currentCardView.setImage(ImageFactory.getImage(fakeTile));
            } else {
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
            this.currentCardView.setImage(null);
            this.remainingCountLabel.setText("RETOUR DÉPART !");
            this.remainingCountLabel.setStyle("-fx-text-fill: #FF5555; -fx-font-weight: bold;");
        }

        // Affichage Butin
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

    private javafx.scene.image.Image createPlaceholder(Color color) {
        WritableImage img = new WritableImage(50, 50);
        for (int i = 0; i < 50; i++)
            for (int j = 0; j < 50; j++)
                img.getPixelWriter().setColor(i, j, color);
        return img;
    }
}