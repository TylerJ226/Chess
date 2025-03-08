package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece{

    public Rook(int colour, int col, int row) {
        super(colour, col, row);
        type = Type.ROOK;
        
        if(colour == GamePanel.WHITE){
            image = getImage("/Resources/w-rook");
        }else{
            image = getImage("/Resources/b-rook");
        }
    
    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){
            if(targetCol == preCol || targetRow == preRow){
                if(isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol, targetRow)){
                    return true;
                }
            }
        }
        return false;
    }

}
