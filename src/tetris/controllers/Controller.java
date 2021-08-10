package tetris.controllers;

import tetris.models.Game;
import tetris.views.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


/**
 * TetrisController will control the game board for Tetris.
 */
public class Controller implements ActionListener {

    /**
     * Related to Tetris models
     */
    private Game game;

    /**
     * Related to Tetris views
     */
    private final int LevelRate = 100;
    private final int scoreToLevel = 1000;
    private Frame frame;
    private int score = 0;
    private int level = 1;
    private int old_level = 1;
    private int removedLines = 0;

    /**
     * Related to user input
     */
    private KeyBoardHandler keyBoardHandler;
    private boolean isPaused;

    private SoundEffect BGM;
    private String BGMFileName = "Tetris.wav";
    private Timer timer;
    private int timeDelay = 1000;
    private int startTimeDelay = 1000;
    private final int levelUpConstant = 100;
    private final int minTimeDelay = 100;



    /**
     * If we call Controller, the game will start (init board)
     */
    public Controller() {
        keyBoardHandler = new KeyBoardHandler(this);
        frame = new Frame(keyBoardHandler);
        game = new Game();
        game.newPiece();
        timer = new Timer(timeDelay, this);
        BGM = new SoundEffect(BGMFileName, true);
        timer.start();
        this.isPaused = true;
    }

    public void pause() {
        this.isPaused = true;
        updateView();
        BGM.stop();
        timer.stop();
    }

    public void start() {
        this.isPaused = false;
        updateView();
        BGM.resume();
        timer.start();
    }

    public void replay() {
        frame = new Frame(keyBoardHandler);
        game = new Game();
        game.newPiece();
        timer = new Timer(timeDelay, this);
        BGM = new SoundEffect(BGMFileName, true);
        timer.start();
        score = 0;
        level = 1;
        old_level = 1;
        removedLines = 0;
        isPaused = true;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public void updateView() {
        this.frame.update(this.game.isGameOver(),
                this.isPaused,
                this.game.getBoard(),
                this.game.getColor(),
                this.game.currentCoords(),
                this.score,
                this.level,
                this.removedLines);
    }

    public void updateRecord() {
        int addRemoveLine = this.game.countFullLines();
        this.removedLines += addRemoveLine;
        this.score += addRemoveLine * this.level * this.LevelRate;
        this.level = 1 + this.removedLines / this.scoreToLevel;
    }

    private boolean isLevelUp() {
        if (this.level > this.old_level) {
            this.old_level = this.level;
            return true;
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // We reach here every time the alarm fires.
        // if not gameOver and not isPaused, we need to call models
        if (!this.game.isGameOver() && !this.isPaused()) {
            // if current piece is settled
            if (this.game.isFallingFinished()) {
                this.updateRecord(); // remove full line and update records
            }
            else {
                // keep move current piece one line down
                this.move(Action.DOWNONE);
            }
        }
        // keep update view, records and timer
        updateView();
        if (this.isLevelUp()) {
//            BGM.play(this.level);
        }
//        timer.setDelay(this.updateTimeDelay());

    }

    public void move(Action action) {
        if (action == Action.LEFT) {
            game.moveLeft();
        } else if (action == Action.RIGHT) {
            game.moveRight();
        } else if (action == Action.ROTATE) {
            game.rotateLeft();
        } else if (action == Action.DOWNONE) {
            game.dropOneLine();
        } else if (action == Action.ALLDOWN) {
            game.dropDown();
        }
        updateView();
    }

    public int updateTimeDelay() {
        if (this.timeDelay <= minTimeDelay) {
            return minTimeDelay;
        }
        this.timeDelay = startTimeDelay - levelUpConstant * (this.level - 1);
        return this.timeDelay;
    }

    public static void main(String[] args) {
        Controller c = new Controller();
    }


}