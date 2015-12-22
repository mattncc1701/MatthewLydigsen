package SpaceInvaders;

import java.awt.Color;
import java.awt.Graphics2D;

public class PlayerTank extends Tank {

    // Indicates if the tank has been destroyed
    public Boolean destroyed;
    public Boolean killed = false;
    
    protected static final Long EXPLOSION_CHANGE_RATE = (long) 400; // milli seconds

    protected Timer explosionChangeRateTimer = new Timer(EXPLOSION_CHANGE_RATE);

    public LivesArea livesArea;

    public enum ExplosionState {
        NO_EXPLOSION,
        NOT_DRAWN,
        EXPLOSION_1_DRAWN,
        EXPLOSION_2_DRAWN,
        EXPLOSION_DONE
    } // enum

    ExplosionState explosionState;

    public PlayerTank (
        Integer xStartBlockPosition, Integer yStartBlockPosition, GamePanel gamePanel) {
        super(xStartBlockPosition, yStartBlockPosition, gamePanel);

        destroyed = false;
        explosionState = ExplosionState.NO_EXPLOSION;
        livesArea = new LivesArea(0,0,3,gamePanel);
    } // ctor

    public TankShot shoot() {
       return new TankShot(xBlockPosition() + (blockWidth/2), yBlockPosition(), gamePanel);
    } // TankShot

    public void kill() {
        explosionState = ExplosionState.NOT_DRAWN;
    } // Kill

    public Boolean atRightScreenEdge() {
        // Check if position of tank is at left edge of screen
        if (xPosition >= (gamePanel.getWidth() - width())) {
            return true;
        } // if

        else { // Not at edge
            return false;
        } // else
    } // atRightScreenEdge

     public Boolean atLeftScreenEdge() {
        // Check if position of tank is at left edge of screen
        if (xPosition <= 0) {
            return true;
        } // if

        else { // Not at edge
            return false;
        } // else
    } // atLeftScreenEdge

     public void drawExplodedTank1(){
        clear();

        drawBlock(Color.yellow, 4, 0);
        drawBlock(Color.yellow, 6, 0);
        drawBlock(Color.yellow, 1, 1);
        drawBlock(Color.yellow, 3, 1);
        drawBlock(Color.yellow, 8, 1);
        drawBlock(Color.yellow, 10, 1);
        drawBlock(Color.yellow, 5, 2);
        drawBlock(Color.yellow, 7, 2);
        drawBlock(Color.yellow, 9, 2);
        drawBlock(Color.yellow, 12, 2);
        drawBlock(Color.yellow, 0, 3);
        drawBlock(Color.yellow, 2, 3);
        drawBlock(Color.yellow, 10, 3);
        drawBlock(Color.green, 4, 4);
        drawBlock(Color.yellow, 7, 4);
        drawBlock(Color.yellow, 9, 4);
        drawBlock(Color.yellow, 1, 5);
        drawBlock(Color.yellow, 3, 5);
        drawBlock(Color.green, 4, 5);
        drawBlock(Color.yellow, 5, 5);
        drawBlock(Color.green, 6, 5);
        drawBlock(Color.yellow, 12, 5);
        drawBlock(Color.yellow, 2, 6);
        drawBlockRow(Color.green, 3, 6, 2);
        drawBlock(Color.yellow, 5, 6);
        drawBlockRow(Color.green, 6, 6, 2);
        drawBlock(Color.yellow, 9, 6);
        drawBlock(Color.green, 0, 7);
        drawBlock(Color.green, 2, 7);
        drawBlock(Color.yellow, 1, 7);
        drawBlock(Color.yellow, 3, 7);
        drawBlockRow(Color.green, 4, 7, 4);
        drawBlockRow(Color.green, 10, 7, 2);
        drawBlockRow(Color.green, 0, 8, 12);
    }

    public void drawExplodedTank2(){
        clear();

        drawBlock(Color.yellow, 5, 0);
        drawBlock(Color.yellow, 0, 1);
        drawBlock(Color.yellow, 2, 1);
        drawBlock(Color.yellow, 7, 1);
        drawBlock(Color.yellow, 9, 1);
        drawBlock(Color.yellow, 4, 2);
        drawBlock(Color.yellow, 11, 2);
        drawBlock(Color.yellow, 1, 3);
        drawBlock(Color.yellow, 6, 3);
        drawBlock(Color.yellow, 8, 3);
        drawBlock(Color.yellow, 12, 3);
        drawBlock(Color.yellow, 4, 4);
        drawBlock(Color.yellow, 10, 4);
        drawBlock(Color.yellow, 0, 5);
        drawBlock(Color.yellow, 3, 5);
        drawBlockRow(Color.green, 4, 5, 2);
        drawBlock(Color.yellow, 6, 5);
        drawBlock(Color.yellow, 8, 5);
        drawBlock(Color.yellow, 11, 5);
        drawBlockRow(Color.green, 2, 6, 2);
        drawBlock(Color.yellow, 4, 6);
        drawBlockRow(Color.green, 5, 6, 2);
        drawBlock(Color.yellow, 7, 6);
        drawBlock(Color.yellow, 0, 7);
        drawBlock(Color.green, 2, 7);
        drawBlock(Color.yellow, 3, 7);
        drawBlockRow(Color.green, 4, 7, 3);
        drawBlock(Color.yellow, 7, 7);
        drawBlockRow(Color.green, 10, 7, 2);
        drawBlockRow(Color.green, 0, 8, 12);
    }

    @Override
    public void draw(Graphics2D g2d) {

        switch (explosionState) {

            case NO_EXPLOSION:
                super.draw(g2d);
                break;

            case NOT_DRAWN:
                explosionState =  ExplosionState.EXPLOSION_1_DRAWN;
                explosionChangeRateTimer.reset();
                killed = true;
                
                drawExplodedTank1();
                super.draw(g2d);
                break;

            case EXPLOSION_1_DRAWN:
                if (explosionChangeRateTimer.expired()) {
                    explosionState = ExplosionState.EXPLOSION_2_DRAWN;
                    explosionChangeRateTimer.reset();
                }; // if

                drawExplodedTank1();
                super.draw(g2d);
                break;

            case EXPLOSION_2_DRAWN:
                if (explosionChangeRateTimer.expired()) {
                    explosionState = ExplosionState.EXPLOSION_DONE;
                }; // if

                drawExplodedTank2();
                super.draw(g2d);
                break;

            case EXPLOSION_DONE:
                this.destroyed = true;
                killed = false;
                break;
        } // switch
    }; // draw

    @Override
    protected void move (MoveDirection direction, Integer delta) {
        if (!destroyed) {
            super.move(direction, delta);
        } // End if
    } // move
} // PlayerTank