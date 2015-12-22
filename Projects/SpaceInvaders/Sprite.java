package SpaceInvaders;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;

public class Sprite {

    public Integer xPosition = 0;
    public Integer yPosition = 0;
    public Boolean shieldDestroyed = false;
    
    // Represents the move direction
    public enum MoveDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    } // enum
    
    // Holds the predrawn sprite
    protected Image image;

    public Sprite (Integer xStartPosition, Integer yStartPosition,
        Integer width, Integer height) {
      
        this.xPosition = xStartPosition;
        this.yPosition = yStartPosition;

        // Create  an accelerated image of the right size to store our sprite in
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getDefaultConfiguration();
        image = gc.createCompatibleImage(width, height, Transparency.BITMASK);
    } // ctor

    public Integer width() {
        return image.getWidth(null);
    } // getWidth

    public Integer height() {
        return image.getHeight(null);
    } // getHeight

    protected void move (MoveDirection direction, Integer delta) {
         // Move the specific delta pixels in the specificed direction
         switch (direction) {
             case UP:
                     yPosition = yPosition - delta;
                 break;

             case DOWN:
                     yPosition = yPosition + delta;
                 break;

             case LEFT:
                     xPosition = xPosition - delta;
                 break;

             case RIGHT:
                     xPosition = xPosition + delta;
                 break;
         } // switch
     } // move

     // This method checks if the passed in sprite has a collision
     // with the specified sprite
     public Boolean collides(Sprite otherSprite) {

          Rectangle spriteArea = new Rectangle(xPosition, yPosition, width(), height());
          Rectangle otherSpriteArea = new Rectangle(otherSprite.xPosition,
              otherSprite.yPosition, otherSprite.width(), otherSprite.height());
          return spriteArea.intersects(otherSpriteArea);
     } // collision
     
     public void draw(Graphics2D g2d) {
         // Display image for this sprite
         g2d.drawImage(image, xPosition, yPosition, null);
     }; // draw
     
     public void clear() {
         Graphics2D g2d = (Graphics2D) image.getGraphics();
         g2d.setColor(Color.black);
         g2d.fillRect(0, 0, width(), height());
     } // clear
} // Sprite
