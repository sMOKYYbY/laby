package g62221.labyrinthe.view;

import g62221.labyrinthe.controller.Controller;
import g62221.labyrinthe.model.Direction;
import g62221.labyrinthe.model.Game;
import g62221.labyrinthe.model.facade.LabyrinthFacade;
import g62221.labyrinthe.model.observer.Observer;
import g62221.labyrinthe.model.Position;
import g62221.labyrinthe.view.sound.SoundManager;
import javafx.animation.FadeTransition;
import javafx.scene.control.Slider;
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
import java.util.Map;
import java.util.HashMap;

/**
 * Main View class for the Labyrinth game application.
 * <p>
 * This class handles the entire graphical user interface using JavaFX.
 * It implements the {@link Observer} pattern to react to model changes and provides
 * a rich, immersive "Dark Fantasy" themed UI with custom window controls,
 * sound management, and smooth animations.
 * </p>
 */
public class MainView implements Observer {

    // ==============================================================================================
    //                                      STYLE CONSTANTS (CSS)
    // ==============================================================================================

    /** Global background style: Deep dark gradient (Anthracite to Abyss Black). */
    private static final String THEME_BACKGROUND = "-fx-background-color: linear-gradient(to bottom, #141414, #0a0a0a);";

    /** Main button style: Gradient grey/black with Gold text and 3D effects. */
    private static final String BTN_MAIN_STYLE =
            "-fx-background-color: linear-gradient(to bottom, #555, #333); " +
                    "-fx-text-fill: #ffd700; " + // Gold Text
                    "-fx-font-family: 'Segoe UI', sans-serif; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 16px; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-color: #222; " +
                    "-fx-border-radius: 8; " +
                    "-fx-border-width: 1; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(three-pass-box, black, 5, 0, 0, 3);";

    /** Hover style for main buttons: Lighter gradient with glowing gold border. */
    private static final String BTN_MAIN_HOVER =
            "-fx-background-color: linear-gradient(to bottom, #666, #444); " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: #ffd700; " +
                    "-fx-effect: dropshadow(three-pass-box, #ffd700, 10, 0.3, 0, 0);";

    /** Style for arrow buttons (insertion): Circular, subtle transparency. */
    private static final String BTN_ARROW_STYLE =
            "-fx-background-color: rgba(255,255,255,0.05); " +
                    "-fx-text-fill: rgba(255,255,255,0.7); " +
                    "-fx-font-size: 20px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 50; " +
                    "-fx-border-color: rgba(255,255,255,0.2); " +
                    "-fx-border-radius: 50; " +
                    "-fx-cursor: hand; " +
                    "-fx-min-width: 40px; -fx-min-height: 40px;";

    /** Main Title style: Large serif font with gold gradient effect. */
    private static final String TITLE_STYLE =
            "-fx-font-size: 72px; " +
                    "-fx-font-family: 'Times New Roman', serif; " +
                    "-fx-font-weight: bold; " +
                    "-fx-text-fill: linear-gradient(to bottom, #ffd700, #b8860b); " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 5);";

    private static final int TILE_SIZE = 85;

    // ==============================================================================================
    //                                      FIELDS
    // ==============================================================================================

    private final LabyrinthFacade facade;
    private final Stage stage;
    private final SoundManager soundManager; // Audio Management System

    // Layout Containers
    private StackPane rootStack;
    private BorderPane gameLayout;
    private GridPane mainGrid;

    // UI Components
    private TileView[][] tileViews;
    private TileView extraTileView;
    private Label statusLabel;
    private final List<PlayerInfoPanel> playerPanels = new ArrayList<>();
    private final List<Button> insertButtons = new ArrayList<>();

    // Control Buttons references (kept to enable/disable them during bot turns)
    private Button btnRotateLeft;
    private Button btnRotateRight;
    private Button btnUndo;
    private Button btnRedo;

    private Controller controller;

    // Game State Flags
    private boolean isBotPlaying = false;
    private boolean gameEnded = false;
    private Game.State lastState = Game.State.WAITING_FOR_SLIDE;

    // Window Dragging Variables (for custom undecorated window feeling)
    private double xOffset = 0;
    private double yOffset = 0;

    // ==============================================================================================
    //                                      CONSTRUCTOR
    // ==============================================================================================

    /**
     * Constructs the MainView.
     * Initializes the stage, sound manager, and displays the main menu.
     *
     * @param stage  The primary JavaFX stage.
     * @param facade The game facade (Model).
     */
    public MainView(Stage stage, LabyrinthFacade facade) {
        this.stage = stage;
        this.facade = facade;
        this.soundManager = new SoundManager(); // Initialisation du système audio

        // Chargement de l'icône de l'application
        try {
            javafx.scene.image.Image icon = new javafx.scene.image.Image(getClass().getResourceAsStream("/icon.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            // Ignore if icon is missing, not critical
        }

        stage.setTitle("Labyrinthe - Projet 3dev3a");

        // ABONNEMENT OBSERVER : La Vue écoute le Modèle
        facade.addObserver(this);

        // Initial Display: Show the Menu
        showMenu();
    }

    /**
     * Links the Controller to this View.
     * Essential for the MVC pattern so user inputs can trigger controller actions.
     *
     * @param controller The controller instance.
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    // ==============================================================================================
    //                                      MENU & RULES
    // ==============================================================================================

    /**
     * Displays the Main Menu.
     * Includes buttons to start the game, read rules, and window controls.
     */
    private void showMenu() {
        // Sauvegarde de l'état plein écran pour le restaurer après
        boolean wasFullScreen = stage.isFullScreen();

        // 2. Build Content
        VBox content = new VBox(40);
        content.setAlignment(Pos.CENTER);

        Label title = new Label("LABYRINTHE");
        title.setStyle(TITLE_STYLE);

        Label subTitle = new Label("⚜ PROJET 3DEV3A - FORA YASSIR 62221 ⚜");
        subTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: rgba(255,255,255,0.5); -fx-letter-spacing: 2px; -fx-font-weight: bold;");

        // Boîte du menu avec effet vitré (Glassmorphism)
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 20; -fx-padding: 40; -fx-border-color: rgba(255,215,0,0.1); -fx-border-radius: 20;");
        menuBox.setMaxWidth(900);

        // Game Mode Buttons
        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(
                createStyledButton("⚔  Vs 1 Bot", 2),
                createStyledButton("⚔  Vs 2 Bots", 3),
                createStyledButton("⚔  Vs 3 Bots", 4)
        );

        // Rules Button
        Button btnRules = new Button("📜 Lire le Grimoire des Règles");
        btnRules.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaa; -fx-font-size: 16px; -fx-underline: true; -fx-cursor: hand;");
        btnRules.setOnMouseEntered(e -> btnRules.setTextFill(Color.GOLD));
        btnRules.setOnMouseExited(e -> btnRules.setTextFill(Color.web("#aaa")));
        btnRules.setOnAction(e -> showRules());

        menuBox.getChildren().addAll(buttonsBox, btnRules);
        content.getChildren().addAll(title, subTitle, menuBox);

        // 3. Root Container
        StackPane rootStack = new StackPane();
        rootStack.setStyle(THEME_BACKGROUND);
        makeDraggable(rootStack); // Rend la fenêtre déplaçable à la souris

        // 4. Window Controls (Top-Right)
        HBox windowControls = createWindowControls();
        windowControls.setMaxHeight(40);
        StackPane.setAlignment(windowControls, Pos.TOP_RIGHT);

        rootStack.getChildren().addAll(content, windowControls);

        // 5. Set Scene & Restore State
        stage.setScene(new Scene(rootStack, 1400, 1000));
        if (wasFullScreen) {
            stage.setFullScreen(true);
        }
        stage.show();
    }

    /**
     * Displays the Rules page with a styled scrollable layout.
     */
    private void showRules() {
        boolean wasFullScreen = stage.isFullScreen();

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        // ScrollPane pour faire défiler les règles si l'écran est petit
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);

        VBox contentBox = new VBox(25);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(20));

        Label title = new Label("🏰 LABYRINTHE - Règles du jeu");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: gold; -fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0);");

        // Création des sections de règles
        VBox secObjectif = createRuleSection("🎯 OBJECTIF", "Être le premier joueur à collecter tous ses trésors et revenir à sa case de départ !", Color.LIGHTGREEN);
        VBox secSetup = createRuleSection("🎲 MISE EN PLACE", "🟦 Plateau 7x7 avec des tuiles de couloir fixes et mobiles\n🎴 Chaque joueur reçoit une pile secrète de cartes trésor\n🔴🔵🟢🟡 Chaque joueur commence dans un coin du plateau\n➕ Il reste 1 tuile en main (hors du plateau)", Color.LIGHTBLUE);
        VBox secTurn = createRuleSection("⚙️ DÉROULEMENT D'UN TOUR", "1️⃣ POUSSER UNE RANGÉE\n📥 Insère la tuile libre sur le côté du plateau\n➡️⬅️⬆️⬇️ Pousse une rangée ou colonne entière\n🚫 Interdit de défaire le coup du joueur précédent !\n📤 Une tuile sort de l'autre côté → devient la nouvelle tuile libre\n\n2️⃣ DÉPLACER SON PION\n🚶 Déplace ton pion sur le chemin créé\n🛤️ Tu ne peux suivre que les couloirs ouverts\n⏸️ Tu peux aussi choisir de ne pas bouger\n\n3️⃣ RÉCUPÉRER UN TRÉSOR\n💎 Si tu atteins ta case objectif actuelle → tu récupères ce trésor !\n👀 Révèle ta prochaine carte trésor\n🏁 Une fois tous tes trésors collectés → retourne à ta case de départ pour gagner !", Color.ORANGE);
        VBox secTiles = createRuleSection("🎨 TYPES DE COULOIRS", "┗━ Couloir en L (virages)\n┃━ Couloir droit (lignes)\n┣━ Couloir en T (intersections)", Color.VIOLET);
        VBox secSpecial = createRuleSection("⭐ RÈGLES SPÉCIALES", "🌀 Si ton pion est poussé hors du plateau, il réapparaît de l'autre côté\n👥 Plusieurs pions peuvent occuper la même case\n🔄 Tu peux tourner la tuile libre avant de l'insérer", Color.YELLOW);
        VBox secVictory = createRuleSection("🏆 VICTOIRE", "Le premier joueur qui collecte tous ses trésors ET revient à sa case de départ gagne la partie ! 🎉", Color.GOLD);

        contentBox.getChildren().addAll(title, secObjectif, secSetup, secTurn, secTiles, secSpecial, secVictory);

        StackPane contentWrapper = new StackPane(contentBox);
        contentWrapper.setAlignment(Pos.TOP_CENTER);
        contentWrapper.setPadding(new Insets(20));
        scrollPane.setContent(contentWrapper);

        Button btnContinue = new Button("J'AI COMPRIS, JOUONS ! ▶");
        btnContinue.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 30; -fx-border-color: gold; -fx-border-radius: 30; -fx-border-width: 2; -fx-cursor: hand;");
        btnContinue.setOnMouseEntered(e -> btnContinue.setStyle("-fx-background-color: #666; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 30; -fx-border-color: gold; -fx-border-radius: 30; -fx-border-width: 2; -fx-cursor: hand;"));
        btnContinue.setOnMouseExited(e -> btnContinue.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 30; -fx-border-color: gold; -fx-border-radius: 30; -fx-border-width: 2; -fx-cursor: hand;"));
        btnContinue.setOnAction(e -> {
            soundManager.playClick();
            showMenu();
        });

        root.getChildren().addAll(scrollPane, btnContinue);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        StackPane rootStack = new StackPane();
        rootStack.setStyle(THEME_BACKGROUND);
        makeDraggable(rootStack);

        HBox windowControls = createWindowControls();
        StackPane.setAlignment(windowControls, Pos.TOP_RIGHT);

        rootStack.getChildren().addAll(root, windowControls);

        stage.setScene(new Scene(rootStack, 1400, 1000));
        if (wasFullScreen) stage.setFullScreen(true);
    }

    // ==============================================================================================
    //                                      GAME UI INITIALIZATION
    // ==============================================================================================

    /**
     * Initializes and launches the Game Loop.
     * Sets up the board, players, and GUI components.
     *
     * @param nbPlayers The number of players for this session.
     */
    private void launchGame(int nbPlayers) {
        // Initialisation des composants graphiques
        this.mainGrid = new GridPane();
        this.tileViews = new TileView[7][7];
        this.extraTileView = new TileView();
        this.statusLabel = new Label("Initialisation...");
        this.insertButtons.clear();
        this.playerPanels.clear();
        this.gameEnded = false;
        this.lastState = Game.State.WAITING_FOR_SLIDE;

        initializeGameUI(nbPlayers);
        facade.startGame(nbPlayers); // Démarrage côté Modèle
    }

    /**
     * Builds the main Game Interface.
     * Configures the BorderPane layout with Top (Status), Center (Grid), and Bottom (Controls) sections.
     * Wraps everything in a ScrollPane for adaptability.
     *
     * @param nbPlayers Number of players to create panels for.
     */
    private void initializeGameUI(int nbPlayers) {
        boolean wasFullScreen = stage.isFullScreen();

        rootStack = new StackPane();
        rootStack.setStyle(THEME_BACKGROUND);
        makeDraggable(rootStack);

        gameLayout = new BorderPane();
        gameLayout.setStyle("-fx-background-color: transparent;");

        // --- TOP BAR (Status) ---
        VBox topBox = new VBox(5);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        topBox.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-border-color: rgba(255,215,0,0.3); -fx-border-width: 0 0 1 0;");

        statusLabel.setStyle("-fx-font-size: 28px; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(one-pass-box, black, 3, 0, 0, 1);");
        topBox.getChildren().add(statusLabel);
        gameLayout.setTop(topBox);

        // --- SIDE PANELS (Players) ---
        // Création dynamique des panneaux des joueurs à gauche et à droite
        VBox leftPanel = new VBox(20);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(10, 20, 10, 20));
        leftPanel.setPrefWidth(240);

        VBox rightPanel = new VBox(20);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(10, 20, 10, 20));
        rightPanel.setPrefWidth(240);

        PlayerInfoPanel p1 = new PlayerInfoPanel(0, true);
        playerPanels.add(p1);
        leftPanel.getChildren().add(p1);

        if (nbPlayers >= 2) {
            PlayerInfoPanel p2 = new PlayerInfoPanel(1, false);
            playerPanels.add(p2);
            rightPanel.getChildren().add(p2);
        }
        if (nbPlayers >= 3) {
            Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
            leftPanel.getChildren().addAll(spacer, new PlayerInfoPanel(2, false));
            playerPanels.add((PlayerInfoPanel) leftPanel.getChildren().get(2));
        }
        if (nbPlayers >= 4) {
            Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
            rightPanel.getChildren().addAll(spacer, new PlayerInfoPanel(3, false));
            playerPanels.add((PlayerInfoPanel) rightPanel.getChildren().get(2));
        }

        gameLayout.setLeft(leftPanel);
        gameLayout.setRight(rightPanel);

        // --- CENTER GRID (Board) ---
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setHgap(2);
        mainGrid.setVgap(2);
        mainGrid.setPadding(new Insets(15));
        mainGrid.setStyle("-fx-background-color: #000; -fx-background-radius: 5; -fx-border-color: #333; -fx-border-width: 5; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, black, 30, 0, 0, 10);");

        // Remplissage de la grille avec les TileViews
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                TileView tv = new TileView();
                tileViews[r][c] = tv;
                mainGrid.add(tv, c + 1, r + 1);
                int finalR = r; int finalC = c;
                // Gestion du clic pour le déplacement
                tv.setOnMouseClicked(e -> {
                    if (!facade.isCurrentPlayerBot() && !isBotPlaying && !gameEnded && controller != null)
                        controller.handleMove(finalR, finalC);
                });
            }
        }

        // Ajout des boutons d'insertion (Flèches jaunes) autour de la grille
        int[] mobileIndices = {1, 3, 5};
        for (int idx : mobileIndices) {
            addInsertButton(Direction.DOWN, idx, idx + 1, 0, "▼");
            addInsertButton(Direction.UP, idx, idx + 1, 8, "▲");
            addInsertButton(Direction.RIGHT, idx, 0, idx + 1, "▶");
            addInsertButton(Direction.LEFT, idx, 8, idx + 1, "◀");
        }
        gameLayout.setCenter(mainGrid);

        // --- BOTTOM BAR (Controls) ---
        HBox controls = new HBox(20);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-background-color: rgba(20, 20, 20, 0.9); -fx-border-color: #444; -fx-border-width: 1 0 0 0; -fx-effect: dropshadow(three-pass-box, black, 20, 0, 0, -5);");

        // Affichage de la Tuile en Main
        VBox extraBox = new VBox(5, new Label("TUILE EN MAIN"), extraTileView);
        extraBox.setAlignment(Pos.CENTER);
        Label extraLabel = (Label)extraBox.getChildren().get(0);
        extraLabel.setStyle("-fx-text-fill: #aaa; -fx-font-weight: bold; -fx-font-size: 12px;");
        extraTileView.setStyle("-fx-effect: dropshadow(three-pass-box, gold, 15, 0, 0, 0);");

        // Boutons Rotation
        btnRotateLeft = createControlButton("⟲");
        btnRotateLeft.setStyle(BTN_MAIN_STYLE + " -fx-font-size: 18px; -fx-padding: 8 15;");
        btnRotateLeft.setOnAction(e -> { if (controller != null) controller.handleRotateLeft(); });

        btnRotateRight = createControlButton("⟳");
        btnRotateRight.setStyle(BTN_MAIN_STYLE + " -fx-font-size: 18px; -fx-padding: 8 15;");
        btnRotateRight.setOnAction(e -> { if (controller != null) controller.handleRotate(); });

        HBox rotateBox = new HBox(5, btnRotateLeft, btnRotateRight);
        rotateBox.setAlignment(Pos.CENTER);

        // Boutons Undo / Redo
        btnUndo = createControlButton("Annuler ↩");
        btnUndo.setOnAction(e -> { if (controller != null) controller.handleUndo(); });

        btnRedo = createControlButton("Refaire ↪");
        btnRedo.setOnAction(e -> { if (controller != null) controller.handleRedo(); });

        // Bouton Menu (Rouge)
        Button btnMenu = new Button("MENU 🏠");
        String redStyle = BTN_MAIN_STYLE.replace("#555", "#8b0000").replace("#333", "#500000").replace("#ffd700", "#ffcccc");
        String redHover = BTN_MAIN_HOVER.replace("#666", "#a00000").replace("#444", "#700000");
        btnMenu.setStyle(redStyle + " -fx-padding: 10 20;");
        btnMenu.setOnMouseEntered(e -> btnMenu.setStyle(redHover + " -fx-padding: 10 20;"));
        btnMenu.setOnMouseExited(e -> btnMenu.setStyle(redStyle + " -fx-padding: 10 20;"));
        btnMenu.setOnAction(e -> {
            showMenu();
        });

        controls.getChildren().addAll(extraBox, rotateBox, btnUndo, btnRedo, btnMenu);
        gameLayout.setBottom(controls);

        // --- FINAL WRAPPER: SCROLLPANE ---
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(gameLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-width: 0;");
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);

        HBox windowControls = createWindowControls();
        StackPane.setAlignment(windowControls, Pos.TOP_RIGHT);

        rootStack.getChildren().addAll(scrollPane, windowControls);
        stage.getScene().setRoot(rootStack);

        if (wasFullScreen) stage.setFullScreen(true);
    }

    // ==============================================================================================
    //                                      OBSERVER UPDATE & GAME LOGIC
    // ==============================================================================================

    /**
     * Called by the Model whenever the game state changes.
     * Updates the board, players, and UI status.
     * <p>
     * This is the core method of the Observer pattern. It triggers re-rendering of tiles,
     * checks for game over conditions, and handles AI turn logic.
     * </p>
     */
    @Override
    public void update() {
        if (tileViews == null) return;

        // Mise à jour visuelle des composants
        updateTiles();
        updatePlayers();
        updatePlayerPanels();

        // 1. Détection d'animation (si on vient d'insérer une tuile)
        Game.State currentState = facade.getGameState();
        if (lastState == Game.State.WAITING_FOR_SLIDE && currentState == Game.State.WAITING_FOR_MOVE) {
            animateSlide(facade.getForbiddenDirection(), facade.getForbiddenIndex());
        }
        lastState = currentState;

        // 2. Vérification de victoire
        if (facade.getGameState() == Game.State.GAME_OVER) {
            if (!gameEnded) {
                gameEnded = true;
                showVictoryScreen(facade.getWinnerId());
            }
            return;
        }

        // 3. Gestion du tour (Humain vs Bot)
        handleTurnLogic();
    }

    /**
     * Manages turn sequence, UI status messages, and AI triggering.
     */
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

        // Si c'est au tour du Bot et qu'il n'est pas déjà en train de jouer
        if (isBot && !isBotPlaying) {
            setControlsEnabled(false); // Désactive les boutons pour l'humain
            isBotPlaying = true;

            // Pause artificielle pour simuler la "réflexion" du bot
            PauseTransition pause = new PauseTransition(Duration.millis(1500));
            pause.setOnFinished(e -> {
                controller.handleAIPlay(); // Le contrôleur déclenche l'IA
                isBotPlaying = false;
                Platform.runLater(this::update);
            });
            pause.play();
        } else if (!isBot) {
            // Tour de l'humain : Mise à jour des indices visuels
            Game.State state = facade.getGameState();
            if (state == Game.State.WAITING_FOR_SLIDE) {
                statusLabel.setText(statusLabel.getText() + " : Insérez une tuile");
                setControlsEnabled(true);
                mainGrid.setStyle("-fx-border-color: #ffd700; -fx-border-width: 3; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, gold, 10, 0, 0, 0);");
            } else {
                statusLabel.setText(statusLabel.getText() + " : Déplacez votre pion");
                setControlsEnabled(false); // On désactive les insertions, seul le clic plateau est actif
                mainGrid.setStyle("-fx-border-color: #55ff55; -fx-border-width: 3; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, lime, 10, 0, 0, 0);");
            }
        }
    }

    /**
     * Displays a victory overlay with animations and option to return to menu.
     *
     * @param winnerId The ID of the winning player.
     */
    private void showVictoryScreen(int winnerId) {
        // Flou d'arrière-plan pour focus sur la victoire
        gameLayout.setEffect(new GaussianBlur(10));

        VBox victoryBox = new VBox(20);
        victoryBox.setAlignment(Pos.CENTER);
        victoryBox.setMaxSize(600, 400);

        boolean isHumanWinner = (winnerId == 0);

        String mainTitleText = isHumanWinner ? "VICTOIRE !" : "DÉFAITE...";
        String subTitleText = isHumanWinner
                ? "BRAVO ! VOUS AVEZ GAGNÉ !"
                : "LE BOT " + (winnerId + 1) + " A GAGNÉ !";

        String colorHex = isHumanWinner ? "#00FF00" : "#FF0000";
        Color glowColor = isHumanWinner ? Color.LIME : Color.RED;

        victoryBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.95); -fx-border-color: " + colorHex + "; -fx-border-width: 4; -fx-background-radius: 20; -fx-border-radius: 20;");
        victoryBox.setEffect(new DropShadow(50, glowColor));

        Label title = new Label(mainTitleText);
        title.setStyle("-fx-font-size: 60px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + "; -fx-effect: dropshadow(three-pass-box, " + colorHex + ", 10, 0, 0, 0);");

        Label subTitle = new Label(subTitleText);
        subTitle.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnBackToMenu = new Button("MENU PRINCIPAL 🏠");
        String btnColor = isHumanWinner ? "#005500" : "#550000";
        String btnStyleBase = "-fx-background-color: " + btnColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-padding: 15 40; -fx-background-radius: 30; -fx-cursor: hand; -fx-border-color: white; -fx-border-width: 2; -fx-font-weight: bold;";
        String btnStyleHover = "-fx-background-color: " + colorHex + "; -fx-text-fill: black; -fx-font-size: 20px; -fx-padding: 15 40; -fx-background-radius: 30; -fx-cursor: hand; -fx-border-color: black; -fx-border-width: 2; -fx-font-weight: bold;";

        btnBackToMenu.setStyle(btnStyleBase);
        btnBackToMenu.setOnMouseEntered(e -> btnBackToMenu.setStyle(btnStyleHover));
        btnBackToMenu.setOnMouseExited(e -> btnBackToMenu.setStyle(btnStyleBase));

        btnBackToMenu.setOnAction(e -> showMenu());

        victoryBox.getChildren().addAll(title, subTitle, btnBackToMenu);

        // Animation d'apparition (Zoom In)
        victoryBox.setScaleX(0);
        victoryBox.setScaleY(0);
        rootStack.getChildren().add(victoryBox);

        ScaleTransition st = new ScaleTransition(Duration.millis(600), victoryBox);
        st.setFromX(0); st.setFromY(0); st.setToX(1); st.setToY(1);
        st.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        st.play();
    }

    // ==============================================================================================
    //                                      UTILITIES & HELPERS
    // ==============================================================================================

    /**
     * Creates the custom window controls (Minimize, Maximize, Close) + Volume Slider.
     *
     * @return The HBox containing the controls.
     */
    private HBox createWindowControls() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(10));
        box.setPickOnBounds(false);

        String btnStyle = "-fx-background-color: transparent; -fx-text-fill: gray; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;";
        String btnHover = "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;";

        // 1. Bouton Mute
        Button btnSound = new Button("🔊");
        btnSound.setStyle(btnStyle);

        // 2. Slider de Volume
        box.setMaxHeight(40);
        Slider volumeSlider = new Slider(0, 1, soundManager.getMusicVolume());
        volumeSlider.setPrefWidth(80);
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setStyle("-fx-control-inner-background: #333; -fx-background-color: transparent;");

        // Listener pour le changement de volume en temps réel
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            soundManager.setMusicVolume(newVal.doubleValue());
            if (newVal.doubleValue() > 0 && soundManager.isMuted()) {
                soundManager.toggleMute();
                btnSound.setText("🔊");
            }
        });

        // Action Mute
        btnSound.setOnAction(e -> {
            soundManager.toggleMute();
            boolean isMuted = soundManager.isMuted();
            btnSound.setText(isMuted ? "🔇" : "🔊");
            volumeSlider.setDisable(isMuted);
        });
        btnSound.setOnMouseEntered(e -> btnSound.setStyle(btnHover));
        btnSound.setOnMouseExited(e -> btnSound.setStyle(btnStyle));

        // Boutons Fenêtre (Min, Max, Close)
        Button btnMin = new Button("_");
        btnMin.setStyle(btnStyle);
        btnMin.setOnAction(e -> stage.setIconified(true));
        btnMin.setOnMouseEntered(e -> btnMin.setStyle(btnHover));
        btnMin.setOnMouseExited(e -> btnMin.setStyle(btnStyle));

        Button btnFull = new Button("⛶");
        btnFull.setStyle(btnStyle);
        btnFull.setOnAction(e -> stage.setFullScreen(!stage.isFullScreen()));
        btnFull.setOnMouseEntered(e -> btnFull.setStyle(btnHover));
        btnFull.setOnMouseExited(e -> btnFull.setStyle(btnStyle));

        Button btnClose = new Button("✕");
        btnClose.setStyle(btnStyle);
        btnClose.setOnAction(e -> Platform.exit());
        btnClose.setOnMouseEntered(e -> btnClose.setStyle("-fx-background-color: #cc0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;"));
        btnClose.setOnMouseExited(e -> btnClose.setStyle(btnStyle));

        box.getChildren().addAll(btnSound, volumeSlider, btnMin, btnFull, btnClose);
        return box;
    }

    /**
     * Enables window dragging on a specific pane (for custom undecorated window style).
     *
     * @param root The pane to make draggable.
     */
    private void makeDraggable(javafx.scene.layout.Pane root) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            if (!stage.isFullScreen()) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
    }

    // --- Helpers for Button Creation ---

    private Button createStyledButton(String text, int nbPlayers) {
        Button btn = new Button(text);
        btn.setStyle(BTN_MAIN_STYLE + " -fx-padding: 15 30;");
        btn.setOnMouseEntered(e -> btn.setStyle(BTN_MAIN_HOVER + " -fx-padding: 15 30;"));
        btn.setOnMouseExited(e -> btn.setStyle(BTN_MAIN_STYLE + " -fx-padding: 15 30;"));
        btn.setOnAction(e -> launchGame(nbPlayers));
        return btn;
    }

    private Button createControlButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(BTN_MAIN_STYLE + " -fx-padding: 10 25; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle(BTN_MAIN_HOVER + " -fx-padding: 10 25; -fx-font-size: 14px;"));
        btn.setOnMouseExited(e -> btn.setStyle(BTN_MAIN_STYLE + " -fx-padding: 10 25; -fx-font-size: 14px;"));
        return btn;
    }

    private void addInsertButton(Direction dir, int logicIndex, int gridCol, int gridRow, String text) {
        Button btn = new Button(text);
        btn.setPrefSize(TILE_SIZE, TILE_SIZE);
        btn.setStyle(BTN_ARROW_STYLE);

        btn.setOnMouseEntered(e -> {
            if(!btn.isDisabled())
                btn.setStyle(BTN_ARROW_STYLE + "-fx-background-color: rgba(255,215,0,0.2); -fx-text-fill: gold; -fx-border-color: gold; -fx-effect: dropshadow(gaussian, gold, 10, 0, 0, 0);");
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

    private VBox createRuleSection(String headerTitle, String bodyText, Color headerColor) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 20; -fx-background-radius: 15; -fx-border-color: #444; -fx-border-radius: 15;");
        box.setMaxWidth(900);
        box.setAlignment(Pos.CENTER);

        Label header = new Label(headerTitle);
        header.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 22));
        header.setTextFill(headerColor);
        header.setEffect(new DropShadow(5, Color.BLACK));
        header.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label body = new Label(bodyText);
        body.setFont(javafx.scene.text.Font.font("Arial", 16));
        body.setTextFill(Color.WHITE);
        body.setWrapText(true);
        body.setStyle("-fx-line-spacing: 5px;");
        body.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        body.setAlignment(Pos.CENTER);

        box.getChildren().addAll(header, body);
        return box;
    }

    /**
     * Animates the sliding of a row or column.
     * Uses ParallelTransition to move all affected tiles smoothly.
     *
     * @param forbiddenDir The direction the slide just came from.
     * @param index        The row or column index.
     */
    private void animateSlide(Direction forbiddenDir, int index) {
        // Lancement du son de glissement
        soundManager.playSlide();

        ParallelTransition animationGroup = new ParallelTransition();
        double fromX = 0;
        double fromY = 0;

        // Calcul du décalage initial pour simuler le mouvement
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

            // Animation de retour à la position (0,0) en 300ms
            TranslateTransition tt = new TranslateTransition(Duration.millis(300), tv);
            tt.setToX(0);
            tt.setToY(0);
            animationGroup.getChildren().add(tt);
        }

        // Arrêt du son à la fin de l'animation
        animationGroup.setOnFinished(e -> soundManager.stopSlide());

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
        Color[] colors = {Color.web("#55FF55"), Color.web("#5555FF"), Color.web("#FFFF55"), Color.web("#FF5555")};
        int nbPlayers = facade.getNbPlayers();
        int currentPlayerIndex = facade.getCurrentPlayerIndex();

        // Regroupement des joueurs par case (pour gérer les chevauchements)
        Map<Position, List<Integer>> playersOnTile = new HashMap<>();
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

            // Si plusieurs joueurs sur la même case, on les décale
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

            // Mise en valeur du joueur dont c'est le tour
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
            playerPanels.get(i).update(
                    facade.getPlayerCurrentObjective(i),
                    facade.getPlayerCardsCount(i),
                    facade.getPlayerFoundObjectives(i),
                    (i == currentPlayerIdx)
            );
        }
    }

    private void setControlsEnabled(boolean enableSlide) {
        if (btnRotateLeft != null) btnRotateLeft.setDisable(!enableSlide);
        if (btnRotateRight != null) btnRotateRight.setDisable(!enableSlide);
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

                // Règle Anti-Retour : on désactive la flèche opposée au dernier coup
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

    /**
     * Displays a temporary error message (Toast) on screen.
     * @param message The text to display.
     */
    public void showError(String message) {
        Label toast = new Label(message);
        toast.setStyle("-fx-background-color: rgba(255, 50, 50, 0.8); -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 20; -fx-background-radius: 10; -fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 10;");
        toast.setEffect(new DropShadow(10, Color.BLACK));

        rootStack.getChildren().add(toast);

        // Animation d'apparition/disparition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), toast);
        fadeOut.setFromValue(1); fadeOut.setToValue(0);

        SequentialTransition seq = new SequentialTransition(fadeIn, stay, fadeOut);
        seq.setOnFinished(e -> rootStack.getChildren().remove(toast));
        seq.play();
    }
}