package SpaceInvaders;

import java.awt.Color;
import java.awt.Graphics2D;

public class Shield3 extends ShieldPart {

      public Shield3 (Integer xStartBlockPosition, Integer yStartBlockPosition) {
        super(xStartBlockPosition, yStartBlockPosition);
    } // Shield

    public void draw1(){
        drawBlockRow(Color.GRAY, 0, 0, 3);
        drawBlockRow(Color.GRAY, 0, 1, 4);
        drawBlockRow(Color.GRAY, 0, 2, 5);
        drawBlockRow(Color.GRAY, 0, 3, 6);
        drawBlockRow(Color.GRAY, 0, 4, 7);

    }
} // Shield
