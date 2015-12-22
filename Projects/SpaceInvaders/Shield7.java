package SpaceInvaders;

import java.awt.Color;
import java.awt.Graphics2D;

public class Shield7 extends ShieldPart {

      public Shield7 (Integer xStartBlockPosition, Integer yStartBlockPosition) {
        super(xStartBlockPosition, yStartBlockPosition);
    } // Shield

    public void draw1(){
        drawBlockRow(Color.GRAY, 0, 0, 5);
        drawBlockRow(Color.GRAY, 0, 1, 6);
        drawBlockRow(Color.GRAY, 0, 2, 6);
        drawBlockRow(Color.GRAY, 0, 3, 6);
        drawBlockRow(Color.GRAY, 0, 4, 6);
        drawBlockRow(Color.GRAY, 0, 5, 6);

    }


} // Shield
