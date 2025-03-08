package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece{

    public Pawn(int colour, int col, int row) {
        super(colour, col, row);

        type = Type.PAWN;
        
        if(colour == GamePanel.WHITE){
            image = getImage("/Resources/w-pawn");
        }else{
            image = getImage("/Resources/b-pawn");
        }
    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){
            
            int moveValue = -1;
            if(colour == GamePanel.WHITE){
                moveValue = -1;
            }else{
                moveValue = 1;
            }

            hittingP = getHittingP(targetCol, targetRow);

            //1 square movement
            if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null){
                return true;
            }
            // 2 quare movement for opening move
            if(targetCol == preCol && targetRow == preRow + moveValue*2 && hittingP == null && moved == false && !pieceIsOnStraightLine(targetCol, targetRow)){
                return true;
            }
            // for capturing pieces
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null && hittingP.colour != colour){
                return true;
            } 

            // for en passant
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue){
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == targetCol && piece.row == preRow && piece.twoStepped == true){
                        hittingP = piece;
                        return true;
                    }
                }
            }


        }
        return false;
    }

    
}
