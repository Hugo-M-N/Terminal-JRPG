package game;

import java.awt.Canvas;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputManager {

    public enum NavAction { UP, DOWN, LEFT, RIGHT, CONFIRM, BACK }
    public enum MoveDirection { UP, DOWN, LEFT, RIGHT }

    private NavAction pendingNav = null;
    private MoveDirection pendingMove = null;
    private boolean escapePressed = false;
    private KeyEvent pendingKeyEvent = null;

    public InputManager(Canvas canvas) {
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!handleNav(e.getKeyCode())) pendingKeyEvent = e;
            }
        });
    }

    /** Returns true if the key was a nav key, false otherwise. */
    private boolean handleNav(int key) {
        switch (key) {
            case KeyEvent.VK_UP    -> { pendingNav = NavAction.UP;    pendingMove = MoveDirection.UP;    return true; }
            case KeyEvent.VK_DOWN  -> { pendingNav = NavAction.DOWN;  pendingMove = MoveDirection.DOWN;  return true; }
            case KeyEvent.VK_LEFT  -> { pendingNav = NavAction.LEFT;  pendingMove = MoveDirection.LEFT;  return true; }
            case KeyEvent.VK_RIGHT -> { pendingNav = NavAction.RIGHT; pendingMove = MoveDirection.RIGHT; return true; }
            case KeyEvent.VK_ENTER -> { pendingNav = NavAction.CONFIRM; return true; }
            case KeyEvent.VK_ESCAPE -> { pendingNav = NavAction.BACK; escapePressed = true; return true; }
        }
        return false;
    }

    public NavAction consumeNav() {
        NavAction nav = pendingNav;
        pendingNav = null;
        escapePressed = false;
        return nav;
    }

    public MoveDirection consumeMove() {
        MoveDirection move = pendingMove;
        pendingMove = null;
        return move;
    }

    public boolean consumeEscape() {
        boolean pressed = escapePressed;
        escapePressed = false;
        return pressed;
    }

    public boolean hasNav() { return pendingNav != null; }
    public boolean hasMove() { return pendingMove != null; }

    public KeyEvent consumeKeyEvent() {
        KeyEvent e = pendingKeyEvent;
        pendingKeyEvent = null;
        return e;
    }
}