package SpaceInvaders;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JFrame {

    // Maintains which key is currently pressed
    public Boolean leftKeyPressed = false;
    public Boolean rightKeyPressed = false;
    public Boolean fireKeyPressed = false;
    public Boolean escKeyPressed = false;

    // This class handle keybroad input from the window
    private class KeyInputHandler extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftKeyPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightKeyPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                fireKeyPressed = true;
            }
        } // keyPressed

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftKeyPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightKeyPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                fireKeyPressed = false;
            }
        } // keyReleased

        public void keyTyped(KeyEvent e) {
            // if we hit escape, then quit the game
            if (e.getKeyChar() == 27) {
                escKeyPressed = true;
            }
        } // keyTypes
    } // KeyInputHandler

    public GameWindow() {
        super("Space Invaders");
        
        // Add listner for keybroad inputs
        addKeyListener(new KeyInputHandler());
    } // ctor

} // SpaceInvadersPanel

