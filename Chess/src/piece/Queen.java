package piece;

import main.GamePanel;
import main.Type;

public class Queen extends Piece{

    public Queen(int colour, int col, int row) {
        super(colour, col, row);
        type = Type.QUEEN;
        
        if(colour == GamePanel.WHITE){
            image = getImage("/Resources/w-queen");
        }else{
            image = getImage("/Resources/b-queen");
        }
    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){
            //vertical and horizontal
            if(targetCol == preCol || targetRow == preRow){
                if(isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol, targetRow)){
                    return true;
                }
            }
            
            //Diagonal
            if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)){
                if(isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow)){
                    return true;
                }
            }
        }
        return false;
    }

}
