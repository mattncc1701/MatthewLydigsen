package SpaceInvaders;

import java.awt.Color;
import java.awt.Graphics2D;

// TODO Explain this class, not coding needed

public abstract class ShieldPart extends BlockSprite {

    private static final Integer BLOCK_WIDTH = 8; // blocks
    private static final Integer BLOCK_HEIGHT = 6; // blocks
    public Boolean killed;
    public int clearOnce = 0;
    
    public enum ExplosionState {
        NOT_DRAWN,
        EXPLOSION_1_DRAWN,
        EXPLOSION_2_DRAWN,
        EXPLOSION_3_DRAWN,
        EXPLOSION_DONE
    } // enum

    ExplosionState explosionState = ExplosionState.NOT_DRAWN;
    int changeExplosionState = 0;
    
    public ShieldPart (Integer xStartBlockPosition, Integer yStartBlockPosition) {
        super(xStartBlockPosition, yStartBlockPosition, BLOCK_WIDTH, BLOCK_HEIGHT);
        killed = false;
    } // Shield

     public abstract void draw1();

     public void kill() {
        killed = true;
    } // Kill

     @Override
     public Boolean collides(Sprite otherSprite) {
         if (killed) {
            return false;
        } // if

        else { // Not killed
            return super.collides(otherSprite);
        } // else
     } // shotHit
     
      protected void drawShieldExploded1(){
        drawBlock(Color.black, 0, 0);
        drawBlock(Color.black, 4, 0);
        drawBlock(Color.black, 1, 1);
        drawBlock(Color.black, 3, 1);
        drawBlock(Color.black, 6, 1);
        drawBlock(Color.black, 5, 2);
        drawBlock(Color.black, 0, 4);
        drawBlock(Color.black, 2, 6);
        drawBlock(Color.black, 5, 6);
        drawBlock(Color.black, 3, 5);
        drawBlock(Color.black, 1, 6);
      }
      protected void drawShieldExploded2(){
        drawBlock(Color.black, 1, 0);
        drawBlock(Color.black, 3, 0);
        drawBlock(Color.black, 1, 2);
        drawBlock(Color.black, 3, 2);
        drawBlock(Color.black, 2, 3);
        drawBlock(Color.black, 3, 3);
        drawBlock(Color.black, 6, 3);
        drawBlock(Color.black, 1, 5);
        drawBlock(Color.black, 3, 6);
      }
      protected void drawShieldExploded3(){
        drawBlock(Color.black, 2, 0);
        drawBlock(Color.black, 5, 0);
        drawBlock(Color.black, 5, 3);
        drawBlock(Color.black, 1, 3);
        drawBlock(Color.black, 3, 4);
        drawBlock(Color.black, 6, 5);
        drawBlock(Color.black, 2, 5);
        drawBlock(Color.black, 5, 5);
        drawBlock(Color.black, 4, 6);
          
      }
      protected void drawShieldExploded4(){
        drawBlock(Color.black, 2, 1);
        drawBlock(Color.black, 4, 1);
        drawBlock(Color.black, 5, 1);
        drawBlock(Color.black, 2, 2);
        drawBlock(Color.black, 4, 2);
        drawBlock(Color.black, 6, 2);
        drawBlock(Color.black, 4, 3);
        drawBlock(Color.black, 6, 5);
        drawBlock(Color.black, 0, 6); 
      }
     

    @Override
    public void draw(Graphics2D g2d) {
       super.draw(g2d);

//       if(killed == false){
//          draw1();
//       }
//       else{
          if (!killed  && changeExplosionState == 0) { // Not killed so draw alien
              // Call superclass to draw alien
              draw1();
              super.draw(g2d);
          } // if

          // Check if explosion should be drawn
          else if (changeExplosionState >= 1) {

             switch (explosionState) {

                case NOT_DRAWN:
                    if (changeExplosionState == 2) {
                        explosionState = ExplosionState.EXPLOSION_1_DRAWN;
                    }
                    drawShieldExploded1();
                    super.draw(g2d);
                    break;

                case EXPLOSION_1_DRAWN:
                    if (changeExplosionState == 3) {
                        explosionState = ExplosionState.EXPLOSION_2_DRAWN;
                    }

                    drawShieldExploded2();
                    super.draw(g2d);
                    break;

                case EXPLOSION_2_DRAWN:
                    if (changeExplosionState == 4) {
                        explosionState = ExplosionState.EXPLOSION_3_DRAWN;
                    } // if
                    
                    drawShieldExploded3();
                    super.draw(g2d);
                    break;

                case EXPLOSION_3_DRAWN:
                    if (changeExplosionState == 5) {
                         explosionState = ExplosionState.EXPLOSION_DONE; 
                    }
                    
                    drawShieldExploded4();
                    super.draw(g2d);
                    break;
                    
                case EXPLOSION_DONE:
                    clear();
                    break;
            } // switch
        } // if
       //}//else

    }

} // Shield
