package piece;

import main.GamePanel;
import main.Type;

public class Knight extends Piece{

    public Knight(int colour, int col, int row) {
        super(colour, col, row);
        type = Type.KNIGHT;
        
        if(colour == GamePanel.WHITE){
            image = getImage("/Resources/w-knight");
        }else{
            image = getImage("/Resources/b-knight");
        }
    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow)){
            if(Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2){
                if(isValidSquare(targetCol, targetRow)){
                    return true;
                }
            }
        }
        return false;
    }

}
