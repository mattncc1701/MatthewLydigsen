package SpaceInvaders;

import java.awt.Color;
import java.awt.Graphics2D;

public class Shield8 extends ShieldPart {

      public Shield8 (Integer xStartBlockPosition, Integer yStartBlockPosition) {
        super(xStartBlockPosition, yStartBlockPosition);
    } // Shield

    public void draw1(){
        drawBlockRow(Color.GRAY, 0, 0, 8);
        drawBlockRow(Color.GRAY, 0, 1, 7);
        drawBlockRow(Color.GRAY, 0, 2, 6);
        drawBlockRow(Color.GRAY, 0, 3, 6);
        drawBlockRow(Color.GRAY, 0, 4, 6);

    }

    

} // Shield
