package SpaceInvaders;

import java.awt.Color;

// TODO Explain this class, not coding needed

public class Shield1 extends ShieldPart {
   
    public Shield1 (Integer xStartBlockPosition, Integer yStartBlockPosition) {
        super(xStartBlockPosition, yStartBlockPosition);
    } // Shield

     public void draw1(){
        drawBlockRow(Color.GRAY, 4, 0, 3);
        drawBlockRow(Color.GRAY, 3, 1, 4);
        drawBlockRow(Color.GRAY, 2, 2, 5);
        drawBlockRow(Color.GRAY, 1, 3, 6);
        drawBlockRow(Color.GRAY, 0, 4, 7);
    } 
} // Shield
