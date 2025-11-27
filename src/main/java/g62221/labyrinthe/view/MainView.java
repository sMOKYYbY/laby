package g62221.labyrinthe.view;

import g62221.labyrinthe.controller.Controller;
import g62221.labyrinthe.model.Direction;
import g62221.labyrinthe.model.Game;
import g62221.labyrinthe.model.LabyrinthFacade;
import g62221.labyrinthe.model.Observer;
import g62221.labyrinthe.model.Tile;
import g62221.labyrinthe.model.Position;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;

public class MainView implements Observer {
    private final LabyrinthFacade facade;
    private final Stage stage;

    // Composants de la Vue Jeu
    private GridPane mainGrid;
    private TileView[][] tileViews;
    private TileView extraTileView;
    private Label statusLabel;
    private Label objectiveLabel;

    // ContrÃ´les
    private Button btnRotate;
    private Button btnUndo;
    private Button btnRedo;
    private final List<Button> insertButtons = new ArrayList<>();

    private Controller controller;

    public MainView(Stage stage, LabyrinthFacade facade) {
        this.stage = stage;
        this.facade = facade;

        facade.addObserver(this);

        // Au dÃ©marrage, on affiche le menu de sÃ©lection
        showMenu();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Affiche l'Ã©cran de sÃ©lection du nombre de joueurs.
     */
    private void showMenu() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b2b2b;");

        // Titre
        Label title = new Label("LABYRINTHE");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0);");

        Label subTitle = new Label("Choisissez le nombre de joueurs :");
        subTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #cccccc;");

        // Boutons de sÃ©lection
        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);

        Button btn2 = createMenuButton("2 Joueurs", 2);
        Button btn3 = createMenuButton("3 Joueurs", 3);
        Button btn4 = createMenuButton("4 Joueurs", 4);

        buttonsBox.getChildren().addAll(btn2, btn3, btn4);
        root.getChildren().addAll(title, subTitle, buttonsBox);

        Scene menuScene = new Scene(root, 1200, 800);
        stage.setTitle("Labyrinth - Menu");
        stage.setScene(menuScene);
        stage.show();
    }

    private Button createMenuButton(String text, int nbPlayers) {
        Button btn = new Button(text);
        btn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-base: #444; -fx-text-fill: white; -fx-cursor: hand;");
        btn.setOnAction(e -> launchGame(nbPlayers));
        return btn;
    }

    /**
     * Lance la partie avec le nombre de joueurs choisi.
     */
    private void launchGame(int nbPlayers) {
        // 1. Initialiser les conteneurs graphiques
        this.mainGrid = new GridPane();
        this.tileViews = new TileView[7][7];
        this.extraTileView = new TileView();
        this.statusLabel = new Label("Initialisation...");
        this.objectiveLabel = new Label("Mission: ?");
        this.insertButtons.clear();

        // 2. Construire l'interface de jeu (Grille, Boutons...)
        initializeGameUI();

        // 3. DÃ©marrer le modÃ¨le (ceci dÃ©clenchera update() via l'observer)
        facade.startGame(nbPlayers);
    }

    /**
     * Construit la scÃ¨ne principale du jeu.
     */
    private void initializeGameUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        // --- TOP: HUD ---
        VBox topBox = new VBox(5);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(15));
        topBox.setStyle("-fx-background-color: #333333; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

        statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        objectiveLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #cccccc;");

        topBox.getChildren().addAll(statusLabel, objectiveLabel);
        root.setTop(topBox);

        // --- CENTER: Plateau ---
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setHgap(5);
        mainGrid.setVgap(5);
        mainGrid.setPadding(new Insets(20));

        // CrÃ©ation des tuiles (7x7)
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                TileView tv = new TileView();
                tileViews[r][c] = tv;
                // DÃ©calage +1 pour laisser place aux flÃ¨ches
                mainGrid.add(tv, c + 1, r + 1);

                int finalR = r;
                int finalC = c;
                tv.setOnMouseClicked(e -> {
                    if (controller != null) controller.handleMove(finalR, finalC);
                });
            }
        }

        // CrÃ©ation des flÃ¨ches d'insertion
        int[] mobileIndices = {1, 3, 5};
        for (int idx : mobileIndices) {
            addInsertButton(Direction.DOWN, idx, idx + 1, 0, "â–¼");  // Haut
            addInsertButton(Direction.UP, idx, idx + 1, 8, "â–²");    // Bas
            addInsertButton(Direction.RIGHT, idx, 0, idx + 1, "â–¶"); // Gauche
            addInsertButton(Direction.LEFT, idx, 8, idx + 1, "â—€");  // Droite
        }

        root.setCenter(mainGrid);

        // --- BOTTOM: ContrÃ´les ---
        HBox controls = new HBox(20);
        controls.setPadding(new Insets(15));
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-background-color: #333333;");

        VBox extraBox = new VBox(5, new Label("Tuile Bonus"), extraTileView);
        extraBox.setAlignment(Pos.CENTER);
        ((Label)extraBox.getChildren().get(0)).setTextFill(Color.WHITE);

        btnRotate = new Button("Rotation âŸ³");
        btnRotate.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        btnRotate.setOnAction(e -> { if (controller != null) controller.handleRotate(); });

        Button btnAI = new Button("Coup Auto (IA)");
        btnAI.setOnAction(e -> { if (controller != null) controller.handleAIPlay(); });

        btnUndo = new Button("Annuler");
        btnUndo.setOnAction(e -> { if (controller != null) controller.handleUndo(); });

        btnRedo = new Button("Refaire");
        btnRedo.setOnAction(e -> { if (controller != null) controller.handleRedo(); });

        controls.getChildren().addAll(extraBox, btnRotate, btnAI, btnUndo, btnRedo);
        root.setBottom(controls);

        // Changement de la scÃ¨ne
        Scene gameScene = new Scene(root, 1000, 850);
        stage.setTitle("Projet Labyrinthe - Jeu en cours");
        stage.setScene(gameScene);
        // stage.show() est dÃ©jÃ  actif, la scÃ¨ne change instantanÃ©ment
    }

    private void addInsertButton(Direction dir, int logicIndex, int gridCol, int gridRow, String text) {
        Button btn = new Button(text);
        btn.setPrefSize(40, 40);
        btn.setStyle("-fx-font-size: 16px; -fx-base: #444; -fx-text-fill: white;");
        btn.setOnAction(e -> {
            if (controller != null) controller.handleInsert(dir, logicIndex);
        });
        insertButtons.add(btn);
        mainGrid.add(btn, gridCol, gridRow);
    }

    @Override
    public void update() {
        // Si les vues ne sont pas initialisÃ©es (ex: update appelÃ© avant launchGame), on ne fait rien
        if (tileViews == null) return;

        // 1. Mise Ã  jour des tuiles
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                tileViews[r][c].update(facade.getTile(r, c));
                tileViews[r][c].getChildren().removeIf(node -> node instanceof Circle);
            }
        }
        extraTileView.update(facade.getExtraTile());

        // 2. Affichage des joueurs
        Color[] colors = {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE};
        for (int i = 0; i < facade.getNbPlayers(); i++) {
            Position pos = facade.getPlayerPosition(i);
            Circle pawn = new Circle(10, colors[i % colors.length]);


            pawn.setOpacity(0.45);

            pawn.setStroke(Color.BLACK);
            pawn.setStrokeWidth(2);
            pawn.setEffect(new javafx.scene.effect.DropShadow(5, Color.BLACK));
            tileViews[pos.row()][pos.col()].getChildren().add(pawn);
        }

        // --- NOUVEAU : 3. Gestion de la VICTOIRE ---
        if (facade.getGameState() == Game.State.GAME_OVER) {
            // On rÃ©cupÃ¨re l'ID du gagnant (+1 pour l'affichage humain 1-4)
            int winnerId = facade.getWinnerId() + 1;
            statusLabel.setText("VICTOIRE DU JOUEUR " + winnerId + " !");
            statusLabel.setTextFill(Color.GOLD);
            statusLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: gold;");

            // On dÃ©sactive tout le plateau pour empÃªcher de jouer
            mainGrid.setDisable(true);
            if (btnRotate != null) btnRotate.setDisable(true);
            setInsertButtonsEnabled(false);
            return; // On arrÃªte l'update ici
        }

        // --- NOUVEAU : 4. Gestion de l'Objectif (HUD) ---
        // On rÃ©cupÃ¨re l'objectif du joueur dont c'est le tour
        String objName = facade.getCurrentPlayerObjective();
        int cardsLeft = facade.getCurrentPlayerCardsCount();

        if (objName != null) {
            // Astuce : On crÃ©e une tuile temporaire juste pour demander son image Ã  la Factory
            Tile fakeTileForImage = new Tile(Tile.Shape.T, 0, objName, false);
            ImageView objImg = new ImageView(ImageFactory.getImage(fakeTileForImage));

            // Taille de l'image de l'objectif
            objImg.setFitWidth(50);
            objImg.setFitHeight(50);

            objectiveLabel.setGraphic(objImg);
            objectiveLabel.setText("Objectif (" + cardsLeft + " restants)");
            objectiveLabel.setTextFill(Color.WHITE);
        } else {
            // Si objName est null, c'est que le joueur a fini ses cartes
            objectiveLabel.setGraphic(null);
            objectiveLabel.setText("RETOUR Ã€ LA CASE DÃ‰PART !");
            objectiveLabel.setTextFill(Color.ORANGE);
        }

        // 5. Gestion des Ã‰tats du jeu (Texte et Boutons)
        Game.State state = facade.getGameState();
        int currentPlayer = facade.getCurrentPlayerIndex() + 1;
        String colorName = getColorName(currentPlayer - 1);

        statusLabel.setText("Tour du Joueur " + currentPlayer + " (" + colorName + ")");
        statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        if (state == Game.State.WAITING_FOR_SLIDE) {
            statusLabel.setText(statusLabel.getText() + " : InsÃ©rez une tuile !");
            if (btnRotate != null) btnRotate.setDisable(false);
            setInsertButtonsEnabled(true);
            mainGrid.setStyle("-fx-border-color: yellow; -fx-border-width: 2;");
        } else {
            statusLabel.setText(statusLabel.getText() + " : DÃ©placez votre pion !");
            if (btnRotate != null) btnRotate.setDisable(true);
            setInsertButtonsEnabled(false);
            mainGrid.setStyle("-fx-border-color: green; -fx-border-width: 2;");
        }
    }

    private void setInsertButtonsEnabled(boolean enabled) {
        for (Button btn : insertButtons) {
            btn.setDisable(!enabled);
            btn.setVisible(enabled);
        }
    }

    private String getColorName(int index) {
        String[] names = {"Rouge", "Jaune", "Vert", "Bleu"};
        return (index >= 0 && index < names.length) ? names[index] : "Inconnu";
    }

    public void showError(String message) {
        statusLabel.setText("ERREUR : " + message);
        statusLabel.setTextFill(Color.RED);
    }
}