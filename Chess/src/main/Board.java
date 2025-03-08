package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Board {

    final int COL = 8;
    final int ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;


    public void draw(Graphics2D g2){

        int c = 0;

        for (int row = 0; row < ROW; row++) {
            if(c == 0){
                c = 1;
            }else{
                c = 0;
            }
            for (int col = 0; col < COL; col++) {
 
                if(c == 0){
                    g2.setColor(new Color(175,115,70));
                    c = 1;
                }else{
                    g2.setColor(new Color(210,165,125));
                    c = 0;
                }

                g2.fillRect(col*SQUARE_SIZE, row*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}
