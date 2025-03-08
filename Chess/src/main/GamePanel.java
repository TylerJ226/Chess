package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable{

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();


    //pieces
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activeP, checkingP;
    public static Piece castlingP;

    //colour
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColour = WHITE;

    //boleans
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    

    public GamePanel(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.BLACK);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces(){
        //white pieces
        pieces.add(new Pawn(WHITE,0,6));
        pieces.add(new Pawn(WHITE,1,6));
        pieces.add(new Pawn(WHITE,2,6));
        pieces.add(new Pawn(WHITE,3,6));
        pieces.add(new Pawn(WHITE,4,6));
        pieces.add(new Pawn(WHITE,5,6));
        pieces.add(new Pawn(WHITE,6,6));
        pieces.add(new Pawn(WHITE,7,6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE,2,7));
        pieces.add(new Bishop(WHITE,5,7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        //Black pieces
        pieces.add(new Pawn(BLACK,0,1));
        pieces.add(new Pawn(BLACK,1,1));
        pieces.add(new Pawn(BLACK,2,1));
        pieces.add(new Pawn(BLACK,3,1));
        pieces.add(new Pawn(BLACK,4,1));
        pieces.add(new Pawn(BLACK,5,1));
        pieces.add(new Pawn(BLACK,6,1));
        pieces.add(new Pawn(BLACK,7,1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK,2,0));
        pieces.add(new Bishop(BLACK,5,0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));

    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for(int i = 0; i < source.size(); i++){
            target.add(source.get(i));
        }
    }

    @Override
    public void run(){
        
        //game loop
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update(){

        if(promotion){
            promoting();
        }else if(!gameover){
            //moused button pressed
            if(mouse.pressed){
                if(activeP == null){
                    for(Piece piece : simPieces){
                        
                        if(piece.colour == currentColour && piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE){
                            activeP = piece;
                        } 
                    }
                }else{
                    //if player is holding a piece simulate the move
                    simulate();
                }
            }
            //mouse btn released
            if(mouse.pressed == false){

                if(activeP != null){
                    if(validSquare){

                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        if(castlingP != null){
                            castlingP.updatePosition();
                        }

                        if(isKingInCheck() && isCheckmate()){
                            gameover = true;

                        }else{
                            if(canPromote()){
                                promotion = true;
                            }else{
                                changePlayer();
                            }
                                
                        }
                        
                    }else{
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    }
                    
                    
                }
            }
        }

         
    }

    private void simulate(){

        canMove = false;
        validSquare = false;

        copyPieces(pieces, simPieces);

        //reset castling piece
        if(castlingP != null){
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        if(activeP.canMove(activeP.col, activeP.row)){
            canMove = true;

            //if hitting oppent piece remove it
            if(activeP.hittingP != null){
                simPieces.remove(activeP.hittingP.getIndex());
            }

            checkCastiling();

            if(isIllegal(activeP) == false && opponentCanCaptureKing() == false){
                validSquare = true;
            }

            //validSquare = true;
        }
    }

    private boolean isIllegal(Piece king){ 
        // make sure king doesn't move into check
        if(king.type == Type.KING){
            for(Piece piece : simPieces){
                if(piece != king && piece.colour != king.colour && piece.canMove(king.col, king.row)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean opponentCanCaptureKing(){
        Piece king = getKing(false);

        for(Piece piece : simPieces){
            if(piece.colour != king.colour && piece.canMove(king.col, king.row)){
                return true;
            }
        }
        return false;
        
    }

    private boolean isKingInCheck(){

        Piece king = getKing(true);

        if(activeP.canMove(king.col, king.row)){
            checkingP = activeP;
            return true;
        }else{
            checkingP = null;
        }
        return false;
    }

    private Piece getKing(boolean opponent){
        Piece king = null;

        for(Piece piece : simPieces){
            if(opponent){
                if(piece.type == Type.KING && piece.colour != currentColour){
                    king = piece;
                }
            }else{
                if(piece.type == Type.KING && piece.colour == currentColour){
                    king = piece;
                }
            }
        }
        return king;    
        
    }

    private boolean isCheckmate(){

        Piece king = getKing(true);

        if(kingCanMove(king)){
            return false;
        }else{
            //check is other piece can block check
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);

            if(colDiff == 0){
                //vertical attack
                if(checkingP.row < king.row){
                    for(int row = checkingP.row; row < king.row; row++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.colour != currentColour && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.row > king.row){
                    for(int row = checkingP.row; row > king.row; row--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.colour != currentColour && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }

            }else if(rowDiff == 0){
                if(checkingP.col < king.col){
                    for(int col = checkingP.col; col < king.row; col++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.colour != currentColour && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.col > king.col){
                    for(int col = checkingP.col; col > king.row; col--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.colour != currentColour && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }

            }else if(colDiff == rowDiff){
                if(checkingP.row < king.row){
                    if(checkingP.col < king.col){
                        for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.colour != currentColour && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){
                        for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.colour != currentColour && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }
                if(checkingP.row > king.row){
                    if(checkingP.col < king.col){
                        for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.colour != currentColour && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){
                        for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.colour != currentColour && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }



        return true;
    }

    private boolean kingCanMove(Piece king){
        //check if king can move to any square
        if(isValidMove(king, -1, -1)){return true;}
        if(isValidMove(king, 0, -1)){return true;}
        if(isValidMove(king, 1, -1)){return true;}
        if(isValidMove(king, -1, 0)){return true;}
        if(isValidMove(king, 1, 0)){return true;}
        if(isValidMove(king, -1, 1)){return true;}
        if(isValidMove(king, 0, 1)){return true;}
        if(isValidMove(king, 1, 1)){return true;}

        return false;
    }

    private boolean isValidMove(Piece king, int colPlus, int rowPlus){

        boolean isValidMove = false;

        //update king pos temp
        king.col += colPlus;
        king.row += rowPlus;

        if(king.canMove(king.col, king.row)){
            if(king.hittingP != null){
                simPieces.remove(king.hittingP.getIndex());
            }
            if(isIllegal(king) == false){
                isValidMove = true;
            }
        }

        //reset king pos
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }

    private void checkCastiling(){
        if(castlingP != null){
            if(castlingP.col == 0){
                castlingP.col += 3;
            }else if(castlingP.col == 7){
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    private void changePlayer(){
        if(currentColour == WHITE){
            currentColour = BLACK;
            //reset foe enpassent
            for(Piece piece : pieces){
                if(piece.colour == BLACK){
                    piece.twoStepped = false;
                }
            }
        }else{
            currentColour = WHITE;
            //reset foe enpassent
            for(Piece piece : pieces){
                if(piece.colour == WHITE){
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }

    private boolean canPromote(){
        if(activeP.type == Type.PAWN){
            if(activeP.row == 0 || activeP.row == 7){
                promoPieces.clear();
                promoPieces.add(new Rook(currentColour,9,2));
                promoPieces.add(new Knight(currentColour,9,3));
                promoPieces.add(new Bishop(currentColour,9,4));
                promoPieces.add(new Queen(currentColour,9,5));
                return true;
            }
        }
        return false;
    }

    private void promoting(){

        if(mouse.pressed){
            for(Piece piece : promoPieces){
                if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE){
                    switch(piece.type){
                        case ROOK: simPieces.add(new Rook(currentColour, activeP.col, activeP.row)); break;
                        case KNIGHT: simPieces.add(new Knight(currentColour, activeP.col, activeP.row)); break;
                        case BISHOP: simPieces.add(new Bishop(currentColour, activeP.col, activeP.row)); break;
                        case QUEEN: simPieces.add(new Queen(currentColour, activeP.col, activeP.row)); break;
                        default: break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        //board
        board.draw(g2);

        //pieces
        for(Piece p : simPieces){
            p.draw(g2);
        }

        if(activeP != null){
            if(canMove){
                if(isIllegal(activeP) || opponentCanCaptureKing()){
                    g2.setColor(Color.red);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
                }else{
                    g2.setColor(Color.GREEN);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
                }
                
            }

            activeP.draw(g2);
        }

        //turn msg
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.WHITE);

        if(promotion){
            g2.drawString("Promote to:", 840, 150);
            for(Piece piece : promoPieces){
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        }else{
            if(currentColour == WHITE){
                g2.drawString("White's turn", 840, 550);
                //king in check ...
                if(checkingP != null && checkingP.colour == BLACK){
                    g2.setColor(Color.red);
                    g2.drawString("White king", 840, 110);
                    g2.drawString("in check!", 840, 160);
                }

            }else{
                g2.drawString("Black's turn", 840, 250);
                //king in check ...
                if(checkingP != null && checkingP.colour == WHITE){
                    g2.setColor(Color.red);
                    g2.drawString("Black king", 840, 640);
                    g2.drawString("in check!", 840, 690);
                }
            }
        }

        if(gameover){
            String s = "";
            if(currentColour == WHITE){
                s = "White Wins";
            }else{
                s = "Black Wins";
            }
            g2.setFont(new Font("Book Antiqua", Font.PLAIN, 90));
            g2.setColor(Color.CYAN);
            g2.drawString(s, 200, 420);
        }
        

    }


}
