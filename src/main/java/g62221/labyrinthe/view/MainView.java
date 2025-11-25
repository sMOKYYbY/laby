package g62221.labyrinthe.view;

import g62221.labyrinthe.controller.Controller;
import g62221.labyrinthe.model.Game;
import g62221.labyrinthe.model.LabyrinthFacade;
import g62221.labyrinthe.model.Observer;
import g62221.labyrinthe.model.Position;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Main View.
 */
public class MainView implements Observer {
    private final LabyrinthFacade facade;
    private final Stage stage;
    private final GridPane boardGrid;
    private final TileView[][] tileViews;
    private final TileView extraTileView;
    private final Label statusLabel;
    private Controller controller;

    public MainView(Stage stage, LabyrinthFacade facade) {
        this.stage = stage;
        this.facade = facade;
        this.boardGrid = new GridPane();
        this.tileViews = new TileView[7][7];
        this.extraTileView = new TileView();
        this.statusLabel = new Label("Welcome");

        initializeUI();
        facade.addObserver(this);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void initializeUI() {
        BorderPane root = new BorderPane();

        // Top: Status
        HBox topBox = new HBox(statusLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        root.setTop(topBox);

        // Center: Board
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setHgap(2);
        boardGrid.setVgap(2);
        boardGrid.setStyle("-fx-background-color: #333; -fx-padding: 10;");

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                TileView tv = new TileView();
                tileViews[r][c] = tv;
                boardGrid.add(tv, c, r);

                int finalR = r;
                int finalC = c;
                tv.setOnMouseClicked(e -> {
                    if (controller != null) controller.handleMove(finalR, finalC);
                });
            }
        }
        root.setCenter(boardGrid);

        // Bottom: Controls
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(15));
        controls.setAlignment(Pos.CENTER);

        VBox extraBox = new VBox(5, new Label("Extra:"), extraTileView);
        extraBox.setAlignment(Pos.CENTER);

        Button btnRotate = new Button("Rotate");
        btnRotate.setOnAction(e -> { if (controller != null) controller.handleRotate(); });

        Button btnInsert = new Button("Insert Random (AI)");
        btnInsert.setOnAction(e -> { if (controller != null) controller.handleInsert(); });

        Button btnUndo = new Button("Undo");
        btnUndo.setOnAction(e -> { if (controller != null) controller.handleUndo(); });

        Button btnRedo = new Button("Redo");
        btnRedo.setOnAction(e -> { if (controller != null) controller.handleRedo(); });

        controls.getChildren().addAll(extraBox, btnRotate, btnInsert, btnUndo, btnRedo);
        root.setBottom(controls);

        Scene scene = new Scene(root, 800, 700);
        stage.setTitle("Labyrinth - 3dev3a");
        stage.setScene(scene);
        stage.show();

        // Init game with 2 players for demo
        facade.startGame(2);
        update();
    }

    @Override
    public void update() {
        // 1. Update Tiles
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                tileViews[r][c].update(facade.getTile(r, c));
                tileViews[r][c].getChildren().removeIf(node -> node instanceof Circle); // Remove old pawns
            }
        }
        extraTileView.update(facade.getExtraTile());

        // 2. Draw Players
        Color[] colors = {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE};
        for (int i = 0; i < facade.getNbPlayers(); i++) {
            Position pos = facade.getPlayerPosition(i);
            Circle pawn = new Circle(15, colors[i % colors.length]);
            pawn.setStroke(Color.BLACK);
            pawn.setStrokeWidth(2);
            tileViews[pos.row()][pos.col()].getChildren().add(pawn);
        }

        // 3. Update Status
        String stateTxt = (facade.getGameState() == Game.State.WAITING_FOR_SLIDE)
                ? "Insert a Tile" : "Move your Pawn";
        statusLabel.setText("Player " + (facade.getCurrentPlayerIndex() + 1) + " turn: " + stateTxt);
    }
}