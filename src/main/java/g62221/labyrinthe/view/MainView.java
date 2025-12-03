package g62221.labyrinthe.view;

import g62221.labyrinthe.controller.Controller;
import g62221.labyrinthe.model.Direction;
import g62221.labyrinthe.model.Game;
import g62221.labyrinthe.model.LabyrinthFacade;
import g62221.labyrinthe.model.Observer;
import g62221.labyrinthe.model.Position;
import g62221.labyrinthe.model.Tile;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MainView implements Observer {
    private final LabyrinthFacade facade;
    private final Stage stage;

    private StackPane rootStack;
    private BorderPane gameLayout;

    private GridPane mainGrid;
    private TileView[][] tileViews;
    private TileView extraTileView;
    private Label statusLabel;

    private final List<PlayerInfoPanel> playerPanels = new ArrayList<>();
    private final List<Button> insertButtons = new ArrayList<>();

    private Button btnRotate;
    private Button btnUndo;
    private Button btnRedo;

    private Controller controller;
    private boolean isBotPlaying = false;
    private boolean gameEnded = false;

    private Game.State lastState = Game.State.WAITING_FOR_SLIDE;

    private static final String BTN_STYLE_NORMAL = "-fx-background-color: #444; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String BTN_STYLE_HOVER = "-fx-background-color: #666; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String BTN_ARROW_STYLE = "-fx-background-color: transparent; -fx-text-fill: #DDD; -fx-font-size: 24px; -fx-font-weight: bold; -fx-border-color: #555; -fx-border-radius: 50; -fx-border-width: 2; -fx-cursor: hand;";

    private static final int TILE_SIZE = 85;

    public MainView(Stage stage, LabyrinthFacade facade) {
        this.stage = stage;
        this.facade = facade;


        try {
            javafx.scene.image.Image icon = new javafx.scene.image.Image(getClass().getResourceAsStream("/icon.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {

        }

        stage.setTitle("Labyrinthe - Projet 3dev3a");
        facade.addObserver(this);
        showMenu();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void showMenu() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1a1a, #2b2b2b);");

        Label title = new Label("LABYRINTHE");
        title.setStyle("-fx-font-size: 60px; -fx-font-weight: bold; -fx-text-fill: gold; -fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0);");

        // --- MISE À JOUR DU TEXTE : VOUS ÊTES LE JOUEUR VERT ---
        Label subTitle = new Label("Projet 3dev3a - Fora Yassir 62221");
        subTitle.setStyle("-fx-font-size: 20px; -fx-text-fill: #ffffff;");

        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(
                createStyledButton("Vs 1 Bot", 2),
                createStyledButton("Vs 2 Bots", 3),
                createStyledButton("Vs 3 Bots", 4)
        );
        root.getChildren().addAll(title, subTitle, buttonsBox);

        stage.setScene(new Scene(root, 1400, 1000));
        stage.show();
    }

    private Button createStyledButton(String text, int nbPlayers) {
        Button btn = new Button(text);
        btn.setStyle(BTN_STYLE_NORMAL + " -fx-font-size: 18px; -fx-padding: 15 30;");
        btn.setOnMouseEntered(e -> btn.setStyle(BTN_STYLE_HOVER + " -fx-font-size: 18px; -fx-padding: 15 30;"));
        btn.setOnMouseExited(e -> btn.setStyle(BTN_STYLE_NORMAL + " -fx-font-size: 18px; -fx-padding: 15 30;"));
        btn.setOnAction(e -> launchGame(nbPlayers));
        return btn;
    }

    private void launchGame(int nbPlayers) {
        this.mainGrid = new GridPane();
        this.tileViews = new TileView[7][7];
        this.extraTileView = new TileView();
        this.statusLabel = new Label("Initialisation...");
        this.insertButtons.clear();
        this.playerPanels.clear();
        this.gameEnded = false;
        this.lastState = Game.State.WAITING_FOR_SLIDE;

        initializeGameUI(nbPlayers);
        facade.startGame(nbPlayers);
    }

    private void initializeGameUI(int nbPlayers) {
        rootStack = new StackPane();
        rootStack.setStyle("-fx-background-color: black;");

        gameLayout = new BorderPane();
        gameLayout.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 100%, #2b2b2b, #000000);");

        // TOP
        VBox topBox = new VBox(5);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(15));
        topBox.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-border-color: #444; -fx-border-width: 0 0 2 0;");
        statusLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(one-pass-box, black, 5, 0, 0, 1);");
        topBox.getChildren().add(statusLabel);
        gameLayout.setTop(topBox);

        // PANELS
        VBox leftPanel = new VBox(50);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(20));
        leftPanel.setPrefWidth(220);

        VBox rightPanel = new VBox(50);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setPrefWidth(220);

        PlayerInfoPanel p1 = new PlayerInfoPanel(0, true);
        playerPanels.add(p1);
        leftPanel.getChildren().add(p1);

        if (nbPlayers >= 2) {
            PlayerInfoPanel p2 = new PlayerInfoPanel(1, false);
            playerPanels.add(p2);
            rightPanel.getChildren().add(p2);
        }
        if (nbPlayers >= 3) {
            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);
            leftPanel.getChildren().addAll(spacer, new PlayerInfoPanel(2, false));
            playerPanels.add((PlayerInfoPanel) leftPanel.getChildren().get(2));
        }
        if (nbPlayers >= 4) {
            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);
            rightPanel.getChildren().addAll(spacer, new PlayerInfoPanel(3, false));
            playerPanels.add((PlayerInfoPanel) rightPanel.getChildren().get(2));
        }

        gameLayout.setLeft(leftPanel);
        gameLayout.setRight(rightPanel);

        // GRID
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setHgap(0);
        mainGrid.setVgap(0);
        mainGrid.setPadding(new Insets(20));
        mainGrid.setEffect(new DropShadow(30, Color.BLACK));

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                TileView tv = new TileView();
                tileViews[r][c] = tv;
                mainGrid.add(tv, c + 1, r + 1);
                int finalR = r;
                int finalC = c;
                tv.setOnMouseClicked(e -> {
                    if (!facade.isCurrentPlayerBot() && !isBotPlaying && !gameEnded && controller != null)
                        controller.handleMove(finalR, finalC);
                });
            }
        }

        int[] mobileIndices = {1, 3, 5};
        for (int idx : mobileIndices) {
            addInsertButton(Direction.DOWN, idx, idx + 1, 0, "▼");
            addInsertButton(Direction.UP, idx, idx + 1, 8, "▲");
            addInsertButton(Direction.RIGHT, idx, 0, idx + 1, "▶");
            addInsertButton(Direction.LEFT, idx, 8, idx + 1, "◀");
        }
        gameLayout.setCenter(mainGrid);

        // BOTTOM
        HBox controls = new HBox(20);
        controls.setPadding(new Insets(15));
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-border-color: #444; -fx-border-width: 2 0 0 0;");

        VBox extraBox = new VBox(5, new Label("Tuile en main"), extraTileView);
        extraBox.setAlignment(Pos.CENTER);
        ((Label)extraBox.getChildren().get(0)).setTextFill(Color.LIGHTGRAY);
        extraTileView.setEffect(new DropShadow(10, Color.BLACK));

        btnRotate = createControlButton("Rotation ⟳");
        btnRotate.setOnAction(e -> { if (controller != null) controller.handleRotate(); });

        btnUndo = createControlButton("Annuler");
        btnUndo.setOnAction(e -> { if (controller != null) controller.handleUndo(); });

        btnRedo = createControlButton("Refaire");
        btnRedo.setOnAction(e -> { if (controller != null) controller.handleRedo(); });

        controls.getChildren().addAll(extraBox, btnRotate, btnUndo, btnRedo);
        gameLayout.setBottom(controls);

        rootStack.getChildren().add(gameLayout);
        stage.getScene().setRoot(rootStack);
    }

    private Button createControlButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(BTN_STYLE_NORMAL + " -fx-padding: 10 20; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle(BTN_STYLE_HOVER + " -fx-padding: 10 20; -fx-font-size: 14px;"));
        btn.setOnMouseExited(e -> btn.setStyle(BTN_STYLE_NORMAL + " -fx-padding: 10 20; -fx-font-size: 14px;"));
        return btn;
    }

    private void addInsertButton(Direction dir, int logicIndex, int gridCol, int gridRow, String text) {
        Button btn = new Button(text);
        btn.setPrefSize(TILE_SIZE, TILE_SIZE);
        btn.setStyle(BTN_ARROW_STYLE);

        btn.setOnMouseEntered(e -> {
            if(!btn.isDisabled()) btn.setStyle(BTN_ARROW_STYLE + "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-border-color: white;");
        });
        btn.setOnMouseExited(e -> {
            if(!btn.isDisabled()) btn.setStyle(BTN_ARROW_STYLE);
        });

        btn.setUserData(new Object[]{dir, logicIndex});
        btn.setOnAction(e -> {
            if (!facade.isCurrentPlayerBot() && !isBotPlaying && !gameEnded && controller != null)
                controller.handleInsert(dir, logicIndex);
        });
        insertButtons.add(btn);
        mainGrid.add(btn, gridCol, gridRow);
    }

    @Override
    public void update() {
        if (tileViews == null) return;

        updateTiles();
        updatePlayers();
        updatePlayerPanels();

        // 1. Détection Animation
        Game.State currentState = facade.getGameState();
        if (lastState == Game.State.WAITING_FOR_SLIDE && currentState == Game.State.WAITING_FOR_MOVE) {
            animateSlide(facade.getForbiddenDirection(), facade.getForbiddenIndex());
        }
        lastState = currentState;

        // 2. Victoire
        if (facade.getGameState() == Game.State.GAME_OVER) {
            if (!gameEnded) {
                gameEnded = true;
                showVictoryScreen(facade.getWinnerId());
            }
            return;
        }

        // 3. Logique Tour / Bots
        handleTurnLogic();
    }

    private void handleTurnLogic() {
        boolean isBot = facade.isCurrentPlayerBot();
        int playerNum = facade.getCurrentPlayerIndex() + 1;

        if (isBot) {
            statusLabel.setText("L'IA (Joueur " + playerNum + ") réfléchit...");
            statusLabel.setTextFill(Color.CYAN);
        } else {
            statusLabel.setText("À vous de jouer ! (Joueur " + playerNum + ")");
            statusLabel.setTextFill(Color.WHITE);
        }

        if (isBot && !isBotPlaying) {
            setControlsEnabled(false);
            isBotPlaying = true;

            PauseTransition pause = new PauseTransition(Duration.millis(1500));
            pause.setOnFinished(e -> {
                controller.handleAIPlay();
                isBotPlaying = false;
                Platform.runLater(this::update);
            });
            pause.play();
        } else if (!isBot) {
            Game.State state = facade.getGameState();
            if (state == Game.State.WAITING_FOR_SLIDE) {
                statusLabel.setText(statusLabel.getText() + " : Insérez une tuile");
                setControlsEnabled(true);
                mainGrid.setStyle("-fx-border-color: #ffd700; -fx-border-width: 3; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, gold, 10, 0, 0, 0);");
            } else {
                statusLabel.setText(statusLabel.getText() + " : Déplacez votre pion");
                setControlsEnabled(false);
                mainGrid.setStyle("-fx-border-color: #55ff55; -fx-border-width: 3; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, lime, 10, 0, 0, 0);");
            }
        }
    }

    private void animateSlide(Direction forbiddenDir, int index) {
        ParallelTransition animationGroup = new ParallelTransition();
        double fromX = 0;
        double fromY = 0;

        if (forbiddenDir == Direction.LEFT)  fromX = -TILE_SIZE;
        if (forbiddenDir == Direction.RIGHT) fromX = TILE_SIZE;
        if (forbiddenDir == Direction.UP)    fromY = -TILE_SIZE;
        if (forbiddenDir == Direction.DOWN)  fromY = TILE_SIZE;

        for (int i = 0; i < 7; i++) {
            TileView tv = (forbiddenDir == Direction.LEFT || forbiddenDir == Direction.RIGHT)
                    ? tileViews[index][i]
                    : tileViews[i][index];

            tv.setTranslateX(fromX);
            tv.setTranslateY(fromY);

            TranslateTransition tt = new TranslateTransition(Duration.millis(300), tv);
            tt.setToX(0);
            tt.setToY(0);
            animationGroup.getChildren().add(tt);
        }
        animationGroup.play();
    }

    private void updateTiles() {
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                tileViews[r][c].update(facade.getTile(r, c));
                tileViews[r][c].getChildren().removeIf(node -> node instanceof Circle);
            }
        }
        extraTileView.update(facade.getExtraTile());
    }

    private void updatePlayers() {
        // --- MISE À JOUR DES COULEURS (Vert, Bleu, Jaune, Rouge) ---
        Color[] colors = {Color.web("#55FF55"), Color.web("#5555FF"), Color.web("#FFFF55"), Color.web("#FF5555")};

        int currentPlayerIndex = facade.getCurrentPlayerIndex();
        int nbPlayers = facade.getNbPlayers();

        java.util.Map<Position, List<Integer>> playersOnTile = new java.util.HashMap<>();
        for (int i = 0; i < nbPlayers; i++) {
            Position pos = facade.getPlayerPosition(i);
            playersOnTile.putIfAbsent(pos, new ArrayList<>());
            playersOnTile.get(pos).add(i);
        }

        for (int i = 0; i < nbPlayers; i++) {
            Position pos = facade.getPlayerPosition(i);
            List<Integer> buddies = playersOnTile.get(pos);

            double radius;
            double offsetX = 0;
            double offsetY = 0;

            if (buddies.size() > 1) {
                radius = 18;
                int rank = buddies.indexOf(i);
                switch (rank) {
                    case 0 -> { offsetX = -20; offsetY = -20; }
                    case 1 -> { offsetX = 20; offsetY = -20; }
                    case 2 -> { offsetX = -20; offsetY = 20; }
                    case 3 -> { offsetX = 20; offsetY = 20; }
                }
            } else {
                radius = 30;
            }

            Circle pawn = new Circle(radius, colors[i % colors.length]);
            pawn.setOpacity(0.45);
            pawn.setStroke(Color.BLACK);
            pawn.setStrokeWidth(2);
            pawn.setTranslateX(offsetX);
            pawn.setTranslateY(offsetY);

            if (i == currentPlayerIndex) {
                pawn.setEffect(new Glow(0.8));
                pawn.setStroke(Color.WHITE);
                pawn.setViewOrder(-1);
            } else {
                pawn.setEffect(new DropShadow(5, Color.BLACK));
            }

            tileViews[pos.row()][pos.col()].getChildren().add(pawn);
        }
    }

    private void updatePlayerPanels() {
        int currentPlayerIdx = facade.getCurrentPlayerIndex();
        for (int i = 0; i < playerPanels.size() && i < facade.getNbPlayers(); i++) {
            String obj = facade.getPlayerCurrentObjective(i);
            int count = facade.getPlayerCardsCount(i);
            List<String> found = facade.getPlayerFoundObjectives(i);
            boolean isTurn = (i == currentPlayerIdx);
            playerPanels.get(i).update(obj, count, found, isTurn);
        }
    }

    private void setControlsEnabled(boolean enableSlide) {
        if (btnRotate != null) btnRotate.setDisable(!enableSlide);
        if (btnUndo != null) btnUndo.setDisable(isBotPlaying);
        if (btnRedo != null) btnRedo.setDisable(isBotPlaying);

        Direction forbiddenDir = facade.getForbiddenDirection();
        int forbiddenIdx = facade.getForbiddenIndex();

        for (Button btn : insertButtons) {
            if (!enableSlide) {
                btn.setDisable(true);
                btn.setOpacity(0.15);
            } else {
                Object[] data = (Object[]) btn.getUserData();
                Direction btnDir = (Direction) data[0];
                int btnIdx = (Integer) data[1];

                if (btnDir == forbiddenDir && btnIdx == forbiddenIdx) {
                    btn.setDisable(true);
                    btn.setStyle(BTN_ARROW_STYLE + "-fx-text-fill: #552222; -fx-border-color: #552222;");
                    btn.setOpacity(0.5);
                } else {
                    btn.setDisable(false);
                    btn.setOpacity(1.0);
                    btn.setStyle(BTN_ARROW_STYLE);
                }
            }
        }
    }

    public void showError(String message) {
        Label toast = new Label(message);
        toast.setStyle("-fx-background-color: rgba(255, 50, 50, 0.8); -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 20; -fx-background-radius: 10; -fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 10;");
        toast.setEffect(new DropShadow(10, Color.BLACK));

        rootStack.getChildren().add(toast);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition seq = new SequentialTransition(fadeIn, stay, fadeOut);
        seq.setOnFinished(e -> rootStack.getChildren().remove(toast));
        seq.play();
    }

    private void showVictoryScreen(int winnerId) {
        gameLayout.setEffect(new GaussianBlur(10));

        VBox victoryBox = new VBox(20);
        victoryBox.setAlignment(Pos.CENTER);
        victoryBox.setMaxSize(500, 350);

        boolean isHumanWinner = (winnerId == 0);

        String mainTitleText = isHumanWinner ? "VICTOIRE !" : "DÉFAITE...";
        String subTitleText = isHumanWinner
                ? "BRAVO ! VOUS AVEZ GAGNÉ !"
                : "LE BOT " + (winnerId + 1) + " A GAGNÉ !";

        String colorHex = isHumanWinner ? "#00FF00" : "#FF0000";
        Color glowColor = isHumanWinner ? Color.LIME : Color.RED;

        victoryBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: " + colorHex + "; -fx-border-width: 4; -fx-background-radius: 20; -fx-border-radius: 20;");
        victoryBox.setEffect(new DropShadow(30, glowColor));

        Label title = new Label(mainTitleText);
        title.setStyle("-fx-font-size: 60px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + "; -fx-effect: dropshadow(three-pass-box, " + colorHex + ", 10, 0, 0, 0);");

        Label subTitle = new Label(subTitleText);
        subTitle.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnQuit = new Button("Quitter");
        String btnColor = isHumanWinner ? "#005500" : "#550000";
        btnQuit.setStyle("-fx-background-color: " + btnColor + "; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10 40; -fx-background-radius: 10; -fx-cursor: hand; -fx-border-color: white; -fx-border-width: 1;");

        btnQuit.setOnMouseEntered(e -> btnQuit.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: black; -fx-font-size: 18px; -fx-padding: 10 40; -fx-background-radius: 10; -fx-cursor: hand;"));
        btnQuit.setOnMouseExited(e -> btnQuit.setStyle("-fx-background-color: " + btnColor + "; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10 40; -fx-background-radius: 10; -fx-cursor: hand; -fx-border-color: white;"));

        btnQuit.setOnAction(e -> Platform.exit());

        victoryBox.getChildren().addAll(title, subTitle, btnQuit);

        victoryBox.setScaleX(0);
        victoryBox.setScaleY(0);
        rootStack.getChildren().add(victoryBox);

        ScaleTransition st = new ScaleTransition(Duration.millis(600), victoryBox);
        st.setFromX(0);
        st.setFromY(0);
        st.setToX(1);
        st.setToY(1);
        st.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        st.play();
    }
}