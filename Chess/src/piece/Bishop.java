package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece{

    public Bishop(int colour, int col, int row) {
        super(colour, col, row);
        type = Type.BISHOP;
        
        if(colour == GamePanel.WHITE){
            image = getImage("/Resources/w-bishop");
        }else{
            image = getImage("/Resources/b-bishop");
        }
    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){
            if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)){
                if(isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow)){
                    return true;
                }
            }
        }
        return false;
    }

}
