package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;

public class Piece {

    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int colour;
    public Piece hittingP;
    public boolean moved;//for pawn and for castling
    public boolean twoStepped;


    public Piece(int colour, int col, int row){
        
        this.colour = colour;
        this.col = col;
        this.row = row;

        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public BufferedImage getImage (String imagePath){

        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        } catch (IOException e) {
            // handle exception
            e.printStackTrace();
        }
        return image;
    }

    public int getX(int col){
        return col * Board.SQUARE_SIZE;
    }
    public int getY(int row){
        return row * Board.SQUARE_SIZE;
    }

    public int getCol(int x){
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    public int getRow(int x){
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getIndex(){
        for(int index = 0; index < GamePanel.simPieces.size(); index++){
            if(GamePanel.simPieces.get(index) == this){
                return index;
            }
        }
        return 0;
    }

    public void updatePosition(){

        //for enpassent
        if(type == Type.PAWN){
            if(Math.abs(row - preRow) == 2){
                twoStepped = true;
            }
        }

        //for gridsnapping pieces
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }

    public void resetPosition(){
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetCol, int targetRow){
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow){
        if(targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7){
            return true;
        }
        return false;
    }

    public boolean isSameSquare(int targetCol, int targetRow){
        if(targetCol == preCol && targetRow == preRow){
            return true;
        }
        return false;
    }

    public Piece getHittingP(int targetCol, int targetRow){
        for(Piece piece : GamePanel.simPieces){
            if(piece.col == targetCol && piece.row == targetRow && piece != this){
                return piece;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow){
        hittingP = getHittingP(targetCol, targetRow);

        if(hittingP == null){
            return true;
        }else{
            if(hittingP.colour != this.colour){
                return true;
            }else{
                hittingP = null;
            }
        }

        return false;
    }

    public boolean pieceIsOnStraightLine(int targetCol, int targetRow){
        // moving left
        for(int c = preCol-1; c > targetCol; c--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == c && piece.row == targetRow){
                    hittingP = piece;
                    return true;
                }
            }
        }

        // moving right
        for(int c = preCol+1; c < targetCol; c++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == c && piece.row == targetRow){
                    hittingP = piece;
                    return true;
                }
            }
        }

        // moving up
        for(int r = preRow-1; r > targetRow; r--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.row == r && piece.col == targetCol){
                    hittingP = piece;
                    return true;
                }
            }
        }

        //moving down
        for(int r = preRow+1; r < targetRow; r++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.row == r && piece.col == targetCol){
                    hittingP = piece;
                    return true;
                }
            }
        }

        return false;


    }

    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow){

        if(targetRow < preRow){
            //up left
            for(int c = preCol-1; c > targetCol; c--){
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow - diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }

            //up right
            for(int c = preCol+1; c < targetCol; c++){
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow - diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }

        }else if(targetRow > preRow){
            //down left
            for(int c = preCol-1; c > targetCol; c--){
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow + diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }

            //down right
            for(int c = preCol+1; c < targetCol; c++){
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow + diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }

        }

        return false;
    }

    public void draw(Graphics2D g2){
        g2.drawImage(image,x,y,Board.SQUARE_SIZE,Board.SQUARE_SIZE,null);
    }

}