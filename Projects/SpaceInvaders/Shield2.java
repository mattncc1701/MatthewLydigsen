package SpaceInvaders;

import java.awt.Color;
import java.awt.Graphics2D;

public class Shield2 extends ShieldPart {

    public Shield2 (Integer xStartBlockPosition, Integer yStartBlockPosition) {
        super(xStartBlockPosition, yStartBlockPosition);
    } // Shield

     public void draw1(){
        drawBlockRow(Color.GRAY, 0, 0, 6);
        drawBlockRow(Color.GRAY, 0, 1, 6);
        drawBlockRow(Color.GRAY, 0, 2, 6);
        drawBlockRow(Color.GRAY, 0, 3, 6);
        drawBlockRow(Color.GRAY, 0, 4, 6);

    }
}
