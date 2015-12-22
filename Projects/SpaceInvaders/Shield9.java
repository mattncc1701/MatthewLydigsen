package SpaceInvaders;

import java.awt.Color;
import java.awt.Graphics2D;

public class Shield9 extends ShieldPart {

       public Shield9 (Integer xStartBlockPosition, Integer yStartBlockPosition) {
        super(xStartBlockPosition, yStartBlockPosition);
    } // Shield

    public void draw1(){
        drawBlockRow(Color.GRAY, 0, 0, 8);
        drawBlockRow(Color.GRAY, 1, 1, 7);
        drawBlockRow(Color.GRAY, 2, 2, 6);
        drawBlockRow(Color.GRAY, 2, 3, 6);
        drawBlockRow(Color.GRAY, 2, 4, 6);

    }
    
} // Shield
