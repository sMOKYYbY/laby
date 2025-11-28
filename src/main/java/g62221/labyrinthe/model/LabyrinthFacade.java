package g62221.labyrinthe.model;

import java.util.*;

public class LabyrinthFacade extends Observable implements Observer {
    private final Game game;
    private final CommandManager commandManager;

    // NOUVEAU : Drapeau pour le mode silencieux
    private boolean isSimulating = false;

    public LabyrinthFacade() {
        this.game = new Game();
        this.commandManager = new CommandManager();
        this.game.addObserver(this);
    }

    public void startGame(int nbPlayers) {
        commandManager.clear();
        game.start(nbPlayers);
    }

    public boolean insertTile(Direction direction, int index) {
        try {
            Command cmd = new InsertTileCommand(game, direction, index);
            commandManager.execute(cmd);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void movePlayer(int row, int col) {
        try {
            Position target = new Position(row, col);
            Command cmd = new MovePlayerCommand(game, target);
            commandManager.execute(cmd);
        } catch (Exception e) {
            // Ignorer
        }
    }

    public void rotateExtraTile() {
        game.getBoard().getExtraTile().rotate();
        notifyObservers();
    }

    public void undo() { commandManager.undo(); notifyObservers(); }
    public void redo() { commandManager.redo(); notifyObservers(); }

    /**
     * C'est ici que l'on bloque les notifications si l'IA est en train de réfléchir.
     */
    @Override
    public void update() {
        if (!isSimulating) {
            notifyObservers();
        }
    }

    // --- IA INTELLIGENTE (Mode Silencieux) ---
    public void playBot() {
        if (!game.isCurrentPlayerBot()) return;

        // 1. On active le mode silencieux AVANT de commencer les tests
        isSimulating = true;

        String objective = getCurrentPlayerObjective();
        Position targetPos = (objective != null)
                ? findTreasurePosition(objective)
                : game.getPlayerStartPosition(game.getCurrentPlayerIndex());

        if (targetPos == null) {
            isSimulating = false; // On réactive pour le coup aléatoire
            playRandomMove();
            return;
        }

        int[] indices = {1, 3, 5};
        Direction[] dirs = Direction.values();

        // Simulation
        for (int idx : indices) {
            for (Direction dir : dirs) {
                boolean success = insertTile(dir, idx);
                if (success) {
                    Position botPos = game.getPlayerPosition(game.getCurrentPlayerIndex());
                    Set<Position> reachable = game.getBoard().getReachablePositions(botPos);

                    // Recalcul cible
                    Position currentTarget = (objective != null)
                            ? findTreasurePosition(objective)
                            : game.getPlayerStartPosition(game.getCurrentPlayerIndex());

                    if (currentTarget != null && reachable.contains(currentTarget)) {
                        // VICTOIRE TROUVÉE !

                        // A. On annule le coup de simulation (silencieusement)
                        undo();

                        // B. On désactive le mode silencieux
                        isSimulating = false;

                        // C. On rejoue le coup "pour de vrai" (la Vue le verra cette fois)
                        insertTile(dir, idx);
                        movePlayer(currentTarget.row(), currentTarget.col());
                        return;
                    }
                    undo(); // Annulation silencieuse
                }
            }
        }

        // Si aucune solution trouvée
        isSimulating = false; // On réactive l'affichage
        playRandomMove();     // On joue un coup visible
    }

    private void playRandomMove() {
        Random rand = new Random();
        int[] indices = {1, 3, 5};

        boolean inserted = false;
        while (!inserted) {
            inserted = insertTile(Direction.values()[rand.nextInt(4)], indices[rand.nextInt(3)]);
        }

        Position botPos = game.getPlayerPosition(game.getCurrentPlayerIndex());
        List<Position> reachable = new ArrayList<>(game.getBoard().getReachablePositions(botPos));

        if (!reachable.isEmpty()) {
            Position dest = reachable.get(rand.nextInt(reachable.size()));
            movePlayer(dest.row(), dest.col());
        } else {
            movePlayer(botPos.row(), botPos.col());
        }
    }

    private Position findTreasurePosition(String treasureName) {
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                Tile t = game.getBoard().getTile(r, c);
                if (t.hasTreasure() && t.getTreasure().equals(treasureName)) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    // --- Getters ---
    public boolean isCurrentPlayerBot() { return game.isCurrentPlayerBot(); }
    public Tile getTile(int r, int c) { return game.getBoard().getTile(r, c); }
    public Tile getExtraTile() { return game.getBoard().getExtraTile(); }
    public Position getPlayerPosition(int index) { return game.getPlayerPosition(index); }
    public int getCurrentPlayerIndex() { return game.getCurrentPlayerIndex(); }
    public Game.State getGameState() { return game.getState(); }
    public int getNbPlayers() { return game.getPlayersCount(); }

    public String getCurrentPlayerObjective() { return game.getPlayerCurrentObjective(game.getCurrentPlayerIndex()); }
    public String getPlayerCurrentObjective(int index) { return game.getPlayerCurrentObjective(index); }

    public int getCurrentPlayerCardsCount() { return game.getPlayerCardsCount(game.getCurrentPlayerIndex()); }
    public int getPlayerCardsCount(int index) { return game.getPlayerCardsCount(index); }

    public List<String> getPlayerFoundObjectives(int index) { return game.getPlayerFoundObjectives(index); }
    public int getWinnerId() { return (game.getWinner() != null) ? game.getWinner().getId() : -1; }
    public Direction getForbiddenDirection() { return game.getForbiddenDirection(); }
    public int getForbiddenIndex() { return game.getForbiddenIndex(); }
    public Position getPlayerStartPosition(int index) { return game.getPlayerStartPosition(index); }
}