/* Matthew Kennedy
 * mkennedymsm at gmail dot com
 * mkennedm at bu dot edu
 * A clone of the popular Windows game written in Java.
 */

package betterminesweeper;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;


public class BetterMinesweeper{
    
    /*mouse functions are needed for right clicking to work*/
    private static class rightClick implements MouseListener{
        int rightClicks = 0;
        
        public void mousePressed(MouseEvent e){
            Component c = e.getComponent();
            if (e.getButton() == MouseEvent.BUTTON3){
                if (c.getBackground() != Color.lightGray){
                    rightClicks++;
                    if (rightClicks % 3 == 1){
                        c.setBackground(Color.red);
                        beginnerRemaining.setText(Integer.toString(minesRemaining(getButtonArray("beginner"))));
                        intermediateRemaining.setText(Integer.toString(minesRemaining(getButtonArray("intermediate"))));
                        advancedRemaining.setText(Integer.toString(minesRemaining(getButtonArray("advanced"))));
                    }
                    else if (rightClicks % 3 == 2){
                        c.setBackground(Color.white);
                        beginnerRemaining.setText(Integer.toString(minesRemaining(getButtonArray("beginner"))));
                        intermediateRemaining.setText(Integer.toString(minesRemaining(getButtonArray("intermediate"))));
                        advancedRemaining.setText(Integer.toString(minesRemaining(getButtonArray("advanced"))));
                    }
                    else if (rightClicks % 3 == 0){
                        c.setBackground(Color.blue);
                    }
                }
            }
        }
        
        public boolean doubleClicked(MouseEvent e){
            return e.getClickCount() == 2;
        }
        
        public void mouseReleased(MouseEvent e)
        {}
        public void mouseEntered(MouseEvent e) 
        {}
        public void mouseExited(MouseEvent e) 
        {}
        public void mouseClicked(MouseEvent e) 
        {}
    }
    
    public static void mouseListen(JButton [][] buttons){
        for (int row = 0; row < buttons.length; row ++){
            for (int  col = 0; col < buttons[0].length; col++){
                MouseListener rightClick = new rightClick();
                buttons[row][col].addMouseListener(rightClick);
            }
        }
    }
    
    private static class Mine{
        //stores the location of each mine
        int row,col;
        Random r = new Random();
        
        //constructor
        public Mine(int rows, int cols){
            this.row = r.nextInt(rows);
            this.col = r.nextInt(cols);
        }
        
        //tells you if this mine has the same row and col of another mine m
        public boolean equals(Mine m){
            return ((this.row == m.row) && (this.col == m.col));
        }
        
        public String toString(){
            return "" + this.row + " " + this.col;
        }
    }
    
    private static class Tile{
        int row = -1;
        int col = -1;
        int near = 0;
        int timeClicked = -1;
        
        boolean isRevealed = false;
        boolean isMine = false;
        boolean tracked = false;
        
        /*
        postion of the tile: 
        top, bottom, left, right, middle,
        one of the four courners
            top left
            top right
            bottom left
            bottom right*/
        String pos = ""; 
        
        public Tile (int givenRow, int givenCol){
            this.row = givenRow;
            this.col = givenCol;
        }
        
        public String toString(){
            return "" + this.row + " " + this.col;
        }
        
        int [] nearMines = initializeMines();//an array containing mines adjacent to this tile
        
        //initializes the tiles in the array
        public int [] initializeMines(){
            int [] t = new int [16];
            for (int i = 0; i < t.length; i = i + 2){
                t[i] = -1;
                t[i+1] = -1;
            }
            return t;
        }
        
        //stores a new mine in the nearMines array 
        public void addMine (int row, int col){
            for (int i = 0; i < nearMines.length; i = i + 2){
                if (nearMines[i] == -1){
                    nearMines[i] = row;
                    nearMines[i + 1] = col;
                    i = 17;
                }
            }
        }
        
        public boolean equals(Tile t){
            return t.row == this.row && t.col == this.col;
        }
        
        //prints the locations of adjacent mines
        public void printMines(){
            for (int i = 0; i < near*2; i = i + 2){
                System.out.println(nearMines[i] + " " + nearMines[i+1]);
            }
            System.out.println();
        }
    }
   
    // an object that keeps track of tiles that have been revealed and have numbers in them
    public static class TileTracker{
        Tile [] numLocs;
        int size;
        
        public TileTracker(String diff){
            if (diff == "beginner"){
                this.size = 71;
            }
            else if (diff == "intermediate"){
                this.size = 216;
            }
            else {
                this.size = 381;
            }
        }
        
        public void update(Tile [][] board){
            int index = 0;
            this.numLocs = new Tile [this.size];
            
            for (Tile [] row : board){
                for (Tile tile : row){
                    if (tile.isRevealed && !tile.tracked && numAdjacentUnrevealed(tile, board) > tile.near){
                        this.numLocs[index] = tile;
                        index++;
                    }
                }
            }
        }
        
        public int numAdjacentUnrevealed(Tile t, Tile [][] board){
            int num = 0;
            int minRow = 0; 
            int maxRow = board.length - 1;
            int minCol = 0;
            int maxCol = board[0].length - 1;
            
            // already correct if t.row = 0 or 1
            if (t.row > minRow + 1){
                minRow = t.row - 1;
            }
            // already correct if t.row = board[0].length or board[0].length - 1
            if (t.row < maxRow - 1){
                maxRow = t.row + 1;
            }
            if (t.col > minCol + 1){
                minCol = t.col - 1;
            }
            if (t.col < maxCol - 1){
                maxCol = t.col + 1;
            }
            
            for (int row =  minRow; row < maxRow + 1; row++){
                for(int col =  minCol; col < maxCol + 1; col++){
                    if (!t.equals(board[row][col]) && !board[row][col].isRevealed){
                        num++;
                    }
                }
            }
            
            return num;
        }
        
        public void printTiles(){
            System.out.println("Tracked Tiles");
            
            for (int i = 0; i < this.numLocs.length && numLocs[i] != null; i++){
                System.out.println(numLocs[i]);
            }
            
            System.out.println("Done");
        }
    }
    
    /*all the buttons needed and the functions that set their sizes, color and text
     * the tile buttons will be created by the button array function
     */    
    static JLabel menuTitle = new JLabel("Minesweeper");
    static JLabel beginnerTitle = new JLabel("Minesweeper");
    static JLabel intermediateTitle = new JLabel("Minesweeper");
    static JLabel advancedTitle = new JLabel("Minesweeper");
    
    static ImageIcon mineImg = new ImageIcon("src/betterminesweeper/mine.jpg");
    
    static JButton beginnerMineImg  = new JButton(mineImg);
    static JButton intermediateMineImg  = new JButton(mineImg);
    static JButton advancedMineImg  = new JButton(mineImg);
    
    static JTextArea beginnerRemaining = new JTextArea();
    static JTextArea intermediateRemaining = new JTextArea();
    static JTextArea advancedRemaining = new JTextArea();
    
    static JButton diff1 = new JButton("Beginner");
    static JButton diff2 = new JButton("Intermediate");
    static JButton diff3 = new JButton("Advanced");
    
    static JLabel diff1Mines = new JLabel("10 mines");
    static JLabel diff2Mines = new JLabel("40 mines");
    static JLabel diff3Mines = new JLabel("99 mines");
    
    static JLabel diff1Size = new JLabel("9 x 9 tile grid");
    static JLabel diff2Size = new JLabel("16 x 16 tile grid");
    static JLabel diff3Size = new JLabel("16 x 30 tile grid");
    
    static JButton diff1New = new JButton("New Game");
    static JButton diff2New = new JButton("New Game");
    static JButton diff3New = new JButton("New Game");
    
    static ImageIcon clockImg = new ImageIcon ("src/betterminesweeper/clock.jpg");
    
    static JButton diff1ClockImg = new JButton (clockImg);
    static JButton diff2ClockImg = new JButton (clockImg);
    static JButton diff3ClockImg = new JButton (clockImg);
    
    static JTextArea diff1Clock = new JTextArea ();
    static JTextArea diff2Clock = new JTextArea ();
    static JTextArea diff3Clock = new JTextArea ();
    
    static boolean clockRunning = false; //tells me whether or not the game clock is running
    
    /*in an effort to shorten this file, i will not be explicitly creating each individual button
     * this function will do it instead
     declaring the array without a function for some reason gives me nll pointer exceptions whenever I try to 
     access any of the elements in the array*/
    public static JButton [][] buttonArray(int rows, int cols) {
        JButton [][] array = new JButton [rows][cols];
        
        for (int row = 0; row < rows; row++){
            for (int col = 0; col < cols; col++){
                array[row][col] = new JButton();
            }
        }
        return array;
    }
    
    static JButton [][] beginner = buttonArray(9,9);
    static JButton [][] intermediate = buttonArray(16,16);
    static JButton [][] advanced = buttonArray(16,30);
    
    //bodies for the GUI
    static JBox menu = JBox.vbox(
                                 JBox.hbox(JBox.hglue(), menuTitle, JBox.hglue()),
                                 JBox.vglue(),
                                 JBox.hbox(JBox.hglue(), diff1,JBox.hglue()),
                                 JBox.hbox(JBox.hglue(), diff1Mines,JBox.hglue()),
                                 JBox.hbox(JBox.hglue(), diff1Size,JBox.hglue()),
                                 JBox.vglue(),
                                 JBox.hbox(JBox.hglue(), diff2,JBox.hglue()),
                                 JBox.hbox(JBox.hglue(), diff2Mines,JBox.hglue()),
                                 JBox.hbox(JBox.hglue(), diff2Size,JBox.hglue()),
                                 JBox.vglue(),
                                 JBox.hbox(JBox.hglue(), diff3,JBox.hglue()),
                                 JBox.hbox(JBox.hglue(), diff3Mines,JBox.hglue()),
                                 JBox.hbox(JBox.hglue(), diff3Size,JBox.hglue()),
                                 JBox.vglue()
                                );
    
    
    static JBox beginnerBody = JBox.vbox( 
                                         JBox.hbox(JBox.hglue(), beginnerTitle, JBox.hglue()), 
                                         JBox.vspace(20), 
                                         JBox.hbox(JBox.hglue(), beginner[0][0], beginner[0][1], beginner[0][2], beginner[0][3], beginner[0][4], beginner[0][5], beginner[0][6], beginner[0][7], beginner[0][8], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), beginner[1][0], beginner[1][1], beginner[1][2], beginner[1][3], beginner[1][4], beginner[1][5], beginner[1][6], beginner[1][7], beginner[1][8], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), beginner[2][0], beginner[2][1], beginner[2][2], beginner[2][3], beginner[2][4], beginner[2][5], beginner[2][6], beginner[2][7], beginner[2][8], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), beginner[3][0], beginner[3][1], beginner[3][2], beginner[3][3], beginner[3][4], beginner[3][5], beginner[3][6], beginner[3][7], beginner[3][8], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), beginner[4][0], beginner[4][1], beginner[4][2], beginner[4][3], beginner[4][4], beginner[4][5], beginner[4][6], beginner[4][7], beginner[4][8], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), beginner[5][0], beginner[5][1], beginner[5][2], beginner[5][3], beginner[5][4], beginner[5][5], beginner[5][6], beginner[5][7], beginner[5][8], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), beginner[6][0], beginner[6][1], beginner[6][2], beginner[6][3], beginner[6][4], beginner[6][5], beginner[6][6], beginner[6][7], beginner[6][8], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), beginner[7][0], beginner[7][1], beginner[7][2], beginner[7][3], beginner[7][4], beginner[7][5], beginner[7][6], beginner[7][7], beginner[7][8], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), beginner[8][0], beginner[8][1], beginner[8][2], beginner[8][3], beginner[8][4], beginner[8][5], beginner[8][6], beginner[8][7], beginner[8][8], JBox.hglue()), 
                                         JBox.vspace(10), 
                                         JBox.vspace(10), 
                                         JBox.hbox(JBox.hspace(75), diff1ClockImg, diff1Clock, beginnerMineImg, beginnerRemaining, diff1New, JBox.hspace(150)), 
                                         JBox.vspace(100) 
                                        ); 
    
    static JBox intermediateBody = JBox.vbox( 
                                             JBox.hbox(JBox.hglue(), intermediateTitle, JBox.hglue()), 
                                             JBox.vspace(20), 
                                             JBox.hbox(JBox.hglue(), intermediate[0][0], intermediate[0][1], intermediate[0][2], intermediate[0][3], intermediate[0][4], intermediate[0][5], intermediate[0][6], intermediate[0][7], intermediate[0][8], intermediate[0][9], intermediate[0][10], intermediate[0][11], intermediate[0][12], intermediate[0][13], intermediate[0][14], intermediate[0][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[1][0], intermediate[1][1], intermediate[1][2], intermediate[1][3], intermediate[1][4], intermediate[1][5], intermediate[1][6], intermediate[1][7], intermediate[1][8], intermediate[1][9], intermediate[1][10], intermediate[1][11], intermediate[1][12], intermediate[1][13], intermediate[1][14], intermediate[1][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[2][0], intermediate[2][1], intermediate[2][2], intermediate[2][3], intermediate[2][4], intermediate[2][5], intermediate[2][6], intermediate[2][7], intermediate[2][8], intermediate[2][9], intermediate[2][10], intermediate[2][11], intermediate[2][12], intermediate[2][13], intermediate[2][14], intermediate[2][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[3][0], intermediate[3][1], intermediate[3][2], intermediate[3][3], intermediate[3][4], intermediate[3][5], intermediate[3][6], intermediate[3][7], intermediate[3][8], intermediate[3][9], intermediate[3][10], intermediate[3][11], intermediate[3][12], intermediate[3][13], intermediate[3][14], intermediate[3][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[4][0], intermediate[4][1], intermediate[4][2], intermediate[4][3], intermediate[4][4], intermediate[4][5], intermediate[4][6], intermediate[4][7], intermediate[4][8], intermediate[4][9], intermediate[4][10], intermediate[4][11], intermediate[4][12], intermediate[4][13], intermediate[4][14], intermediate[4][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[5][0], intermediate[5][1], intermediate[5][2], intermediate[5][3], intermediate[5][4], intermediate[5][5], intermediate[5][6], intermediate[5][7], intermediate[5][8], intermediate[5][9], intermediate[5][10], intermediate[5][11], intermediate[5][12], intermediate[5][13], intermediate[5][14], intermediate[5][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[6][0], intermediate[6][1], intermediate[6][2], intermediate[6][3], intermediate[6][4], intermediate[6][5], intermediate[6][6], intermediate[6][7], intermediate[6][8], intermediate[6][9], intermediate[6][10], intermediate[6][11], intermediate[6][12], intermediate[6][13], intermediate[6][14], intermediate[6][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[7][0], intermediate[7][1], intermediate[7][2], intermediate[7][3], intermediate[7][4], intermediate[7][5], intermediate[7][6], intermediate[7][7], intermediate[7][8], intermediate[7][9], intermediate[7][10], intermediate[7][11], intermediate[7][12], intermediate[7][13], intermediate[7][14], intermediate[7][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[8][0], intermediate[8][1], intermediate[8][2], intermediate[8][3], intermediate[8][4], intermediate[8][5], intermediate[8][6], intermediate[8][7], intermediate[8][8], intermediate[8][9], intermediate[8][10], intermediate[8][11], intermediate[8][12], intermediate[8][13], intermediate[8][14], intermediate[8][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[9][0], intermediate[9][1], intermediate[9][2], intermediate[9][3], intermediate[9][4], intermediate[9][5], intermediate[9][6], intermediate[9][7], intermediate[9][8], intermediate[9][9], intermediate[9][10], intermediate[9][11], intermediate[9][12], intermediate[9][13], intermediate[9][14], intermediate[9][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[10][0], intermediate[10][1], intermediate[10][2], intermediate[10][3], intermediate[10][4], intermediate[10][5], intermediate[10][6], intermediate[10][7], intermediate[10][8], intermediate[10][9], intermediate[10][10], intermediate[10][11], intermediate[10][12], intermediate[10][13], intermediate[10][14], intermediate[10][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[11][0], intermediate[11][1], intermediate[11][2], intermediate[11][3], intermediate[11][4], intermediate[11][5], intermediate[11][6], intermediate[11][7], intermediate[11][8], intermediate[11][9], intermediate[11][10], intermediate[11][11], intermediate[11][12], intermediate[11][13], intermediate[11][14], intermediate[11][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[12][0], intermediate[12][1], intermediate[12][2], intermediate[12][3], intermediate[12][4], intermediate[12][5], intermediate[12][6], intermediate[12][7], intermediate[12][8], intermediate[12][9], intermediate[12][10], intermediate[12][11], intermediate[12][12], intermediate[12][13], intermediate[12][14], intermediate[12][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[13][0], intermediate[13][1], intermediate[13][2], intermediate[13][3], intermediate[13][4], intermediate[13][5], intermediate[13][6], intermediate[13][7], intermediate[13][8], intermediate[13][9], intermediate[13][10], intermediate[13][11], intermediate[13][12], intermediate[13][13], intermediate[13][14], intermediate[13][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[14][0], intermediate[14][1], intermediate[14][2], intermediate[14][3], intermediate[14][4], intermediate[14][5], intermediate[14][6], intermediate[14][7], intermediate[14][8], intermediate[14][9], intermediate[14][10], intermediate[14][11], intermediate[14][12], intermediate[14][13], intermediate[14][14], intermediate[14][15], JBox.hglue()), 
                                             JBox.hbox(JBox.hglue(), intermediate[15][0], intermediate[15][1], intermediate[15][2], intermediate[15][3], intermediate[15][4], intermediate[15][5], intermediate[15][6], intermediate[15][7], intermediate[15][8], intermediate[15][9], intermediate[15][10], intermediate[15][11], intermediate[15][12], intermediate[15][13], intermediate[15][14], intermediate[15][15], JBox.hglue()), 
                                             JBox.vspace(10), 
                                             JBox.vspace(10), 
                                             JBox.hbox(JBox.hspace(175), diff2ClockImg, diff2Clock, intermediateMineImg, intermediateRemaining, diff2New, JBox.hspace(250)), 
                                             JBox.vspace(100) 
                                            ); 
    
    static JBox advancedBody = JBox.vbox( 
                                         JBox.hbox(JBox.hglue(), advancedTitle, JBox.hglue()), 
                                         JBox.vspace(20), 
                                         JBox.hbox(JBox.hglue(), advanced[0][0], advanced[0][1], advanced[0][2], advanced[0][3], advanced[0][4], advanced[0][5], advanced[0][6], advanced[0][7], advanced[0][8], advanced[0][9], advanced[0][10], advanced[0][11], advanced[0][12], advanced[0][13], advanced[0][14], advanced[0][15], advanced[0][16], advanced[0][17], advanced[0][18], advanced[0][19], advanced[0][20], advanced[0][21], advanced[0][22], advanced[0][23], advanced[0][24], advanced[0][25], advanced[0][26], advanced[0][27], advanced[0][28], advanced[0][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[1][0], advanced[1][1], advanced[1][2], advanced[1][3], advanced[1][4], advanced[1][5], advanced[1][6], advanced[1][7], advanced[1][8], advanced[1][9], advanced[1][10], advanced[1][11], advanced[1][12], advanced[1][13], advanced[1][14], advanced[1][15], advanced[1][16], advanced[1][17], advanced[1][18], advanced[1][19], advanced[1][20], advanced[1][21], advanced[1][22], advanced[1][23], advanced[1][24], advanced[1][25], advanced[1][26], advanced[1][27], advanced[1][28], advanced[1][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[2][0], advanced[2][1], advanced[2][2], advanced[2][3], advanced[2][4], advanced[2][5], advanced[2][6], advanced[2][7], advanced[2][8], advanced[2][9], advanced[2][10], advanced[2][11], advanced[2][12], advanced[2][13], advanced[2][14], advanced[2][15], advanced[2][16], advanced[2][17], advanced[2][18], advanced[2][19], advanced[2][20], advanced[2][21], advanced[2][22], advanced[2][23], advanced[2][24], advanced[2][25], advanced[2][26], advanced[2][27], advanced[2][28], advanced[2][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[3][0], advanced[3][1], advanced[3][2], advanced[3][3], advanced[3][4], advanced[3][5], advanced[3][6], advanced[3][7], advanced[3][8], advanced[3][9], advanced[3][10], advanced[3][11], advanced[3][12], advanced[3][13], advanced[3][14], advanced[3][15], advanced[3][16], advanced[3][17], advanced[3][18], advanced[3][19], advanced[3][20], advanced[3][21], advanced[3][22], advanced[3][23], advanced[3][24], advanced[3][25], advanced[3][26], advanced[3][27], advanced[3][28], advanced[3][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[4][0], advanced[4][1], advanced[4][2], advanced[4][3], advanced[4][4], advanced[4][5], advanced[4][6], advanced[4][7], advanced[4][8], advanced[4][9], advanced[4][10], advanced[4][11], advanced[4][12], advanced[4][13], advanced[4][14], advanced[4][15], advanced[4][16], advanced[4][17], advanced[4][18], advanced[4][19], advanced[4][20], advanced[4][21], advanced[4][22], advanced[4][23], advanced[4][24], advanced[4][25], advanced[4][26], advanced[4][27], advanced[4][28], advanced[4][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[5][0], advanced[5][1], advanced[5][2], advanced[5][3], advanced[5][4], advanced[5][5], advanced[5][6], advanced[5][7], advanced[5][8], advanced[5][9], advanced[5][10], advanced[5][11], advanced[5][12], advanced[5][13], advanced[5][14], advanced[5][15], advanced[5][16], advanced[5][17], advanced[5][18], advanced[5][19], advanced[5][20], advanced[5][21], advanced[5][22], advanced[5][23], advanced[5][24], advanced[5][25], advanced[5][26], advanced[5][27], advanced[5][28], advanced[5][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[6][0], advanced[6][1], advanced[6][2], advanced[6][3], advanced[6][4], advanced[6][5], advanced[6][6], advanced[6][7], advanced[6][8], advanced[6][9], advanced[6][10], advanced[6][11], advanced[6][12], advanced[6][13], advanced[6][14], advanced[6][15], advanced[6][16], advanced[6][17], advanced[6][18], advanced[6][19], advanced[6][20], advanced[6][21], advanced[6][22], advanced[6][23], advanced[6][24], advanced[6][25], advanced[6][26], advanced[6][27], advanced[6][28], advanced[6][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[7][0], advanced[7][1], advanced[7][2], advanced[7][3], advanced[7][4], advanced[7][5], advanced[7][6], advanced[7][7], advanced[7][8], advanced[7][9], advanced[7][10], advanced[7][11], advanced[7][12], advanced[7][13], advanced[7][14], advanced[7][15], advanced[7][16], advanced[7][17], advanced[7][18], advanced[7][19], advanced[7][20], advanced[7][21], advanced[7][22], advanced[7][23], advanced[7][24], advanced[7][25], advanced[7][26], advanced[7][27], advanced[7][28], advanced[7][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[8][0], advanced[8][1], advanced[8][2], advanced[8][3], advanced[8][4], advanced[8][5], advanced[8][6], advanced[8][7], advanced[8][8], advanced[8][9], advanced[8][10], advanced[8][11], advanced[8][12], advanced[8][13], advanced[8][14], advanced[8][15], advanced[8][16], advanced[8][17], advanced[8][18], advanced[8][19], advanced[8][20], advanced[8][21], advanced[8][22], advanced[8][23], advanced[8][24], advanced[8][25], advanced[8][26], advanced[8][27], advanced[8][28], advanced[8][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[9][0], advanced[9][1], advanced[9][2], advanced[9][3], advanced[9][4], advanced[9][5], advanced[9][6], advanced[9][7], advanced[9][8], advanced[9][9], advanced[9][10], advanced[9][11], advanced[9][12], advanced[9][13], advanced[9][14], advanced[9][15], advanced[9][16], advanced[9][17], advanced[9][18], advanced[9][19], advanced[9][20], advanced[9][21], advanced[9][22], advanced[9][23], advanced[9][24], advanced[9][25], advanced[9][26], advanced[9][27], advanced[9][28], advanced[9][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[10][0], advanced[10][1], advanced[10][2], advanced[10][3], advanced[10][4], advanced[10][5], advanced[10][6], advanced[10][7], advanced[10][8], advanced[10][9], advanced[10][10], advanced[10][11], advanced[10][12], advanced[10][13], advanced[10][14], advanced[10][15], advanced[10][16], advanced[10][17], advanced[10][18], advanced[10][19], advanced[10][20], advanced[10][21], advanced[10][22], advanced[10][23], advanced[10][24], advanced[10][25], advanced[10][26], advanced[10][27], advanced[10][28], advanced[10][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[11][0], advanced[11][1], advanced[11][2], advanced[11][3], advanced[11][4], advanced[11][5], advanced[11][6], advanced[11][7], advanced[11][8], advanced[11][9], advanced[11][10], advanced[11][11], advanced[11][12], advanced[11][13], advanced[11][14], advanced[11][15], advanced[11][16], advanced[11][17], advanced[11][18], advanced[11][19], advanced[11][20], advanced[11][21], advanced[11][22], advanced[11][23], advanced[11][24], advanced[11][25], advanced[11][26], advanced[11][27], advanced[11][28], advanced[11][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[12][0], advanced[12][1], advanced[12][2], advanced[12][3], advanced[12][4], advanced[12][5], advanced[12][6], advanced[12][7], advanced[12][8], advanced[12][9], advanced[12][10], advanced[12][11], advanced[12][12], advanced[12][13], advanced[12][14], advanced[12][15], advanced[12][16], advanced[12][17], advanced[12][18], advanced[12][19], advanced[12][20], advanced[12][21], advanced[12][22], advanced[12][23], advanced[12][24], advanced[12][25], advanced[12][26], advanced[12][27], advanced[12][28], advanced[12][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[13][0], advanced[13][1], advanced[13][2], advanced[13][3], advanced[13][4], advanced[13][5], advanced[13][6], advanced[13][7], advanced[13][8], advanced[13][9], advanced[13][10], advanced[13][11], advanced[13][12], advanced[13][13], advanced[13][14], advanced[13][15], advanced[13][16], advanced[13][17], advanced[13][18], advanced[13][19], advanced[13][20], advanced[13][21], advanced[13][22], advanced[13][23], advanced[13][24], advanced[13][25], advanced[13][26], advanced[13][27], advanced[13][28], advanced[13][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[14][0], advanced[14][1], advanced[14][2], advanced[14][3], advanced[14][4], advanced[14][5], advanced[14][6], advanced[14][7], advanced[14][8], advanced[14][9], advanced[14][10], advanced[14][11], advanced[14][12], advanced[14][13], advanced[14][14], advanced[14][15], advanced[14][16], advanced[14][17], advanced[14][18], advanced[14][19], advanced[14][20], advanced[14][21], advanced[14][22], advanced[14][23], advanced[14][24], advanced[14][25], advanced[14][26], advanced[14][27], advanced[14][28], advanced[14][29], JBox.hglue()), 
                                         JBox.hbox(JBox.hglue(), advanced[15][0], advanced[15][1], advanced[15][2], advanced[15][3], advanced[15][4], advanced[15][5], advanced[15][6], advanced[15][7], advanced[15][8], advanced[15][9], advanced[15][10], advanced[15][11], advanced[15][12], advanced[15][13], advanced[15][14], advanced[15][15], advanced[15][16], advanced[15][17], advanced[15][18], advanced[15][19], advanced[15][20], advanced[15][21], advanced[15][22], advanced[15][23], advanced[15][24], advanced[15][25], advanced[15][26], advanced[15][27], advanced[15][28], advanced[15][29], JBox.hglue()),
                                         JBox.vspace(10),
                                         JBox.vspace(10),
                                         JBox.hbox(JBox.hspace(475), diff3ClockImg, diff3Clock, advancedMineImg, advancedRemaining, diff3New, JBox.hspace(600)),
                                         JBox.vspace(100)
                                        ); 
    
    //sets the size of every button in the given 2d array
    public static void setSize(JButton [][] array){
        for (int row = 0; row < array.length; row ++){
            for (int col = 0; col < array[0].length; col++){
                JBox.setSize(array[row][col],40,40);
                array[row][col].setFont(new Font("Arial", Font.BOLD, 10));
            }
        }
    }
    
    //sets the color of every button to blue
    public static void setColor(JButton [][] array){
        for (int row = 0; row < array.length; row ++){
            for (int col = 0; col < array[0].length; col++){
                array[row][col].setBackground(Color.blue);
                array[row][col].setOpaque(true);
            }
        }
    }
    
    public static void printBody(int rows, int cols){
        System.out.println("JBox beginnerBody = JBox.vbox(");
        System.out.println("JBox.hbox(JBox.hglue(), title, JBox.hglue()),");
        System.out.println("JBox.vspace(20),");
        
        for (int row = 0; row < rows; row++){
            System.out.print("JBox.hbox(JBox.hglue(), ");
            for(int col = 0; col < cols; col++){
                if (col == cols - 1){
                    System.out.print("beginner[" + row + "][" + col + "]");
                }
                else {
                    System.out.print("beginner[" + row + "][" + col + "], ");
                }
                
            }
            
            System.out.println(", JBox.hglue()),");      
        }
        System.out.println("JBox.vspace(10),");
        System.out.println("JBox.vspace(10),");
        System.out.println("JBox.hbox(JBox.hspace(600), mineImg, remaining,JBox.hspace(600)),");
        System.out.println("JBox.vspace(100)");
        System.out.println(");");
    }
    
    /*sets the color of the text for each tile depending on how many mines it is touching if the given 
     * tile is not itself a mine*/
    public static void setTextColor(Tile [][] board, JButton [][] buttons){
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[0].length; col++){
                if (board[row][col].near == 1 || board[row][col].near == 4 || board[row][col].near == 6){
                    buttons[row][col].setForeground(Color.blue);
                }
                if (board[row][col].near == 2){
                    buttons[row][col].setForeground(Color.green);
                }
                if (board[row][col].near == 3 || board[row][col].near == 5){
                    buttons[row][col].setForeground(Color.red);
                }
                if (board[row][col].near == 7){
                    buttons[row][col].setForeground(Color.magenta);
                }
                if (board[row][col].near == 8){
                    buttons[row][col].setForeground(Color.gray);
                }
            }
        }
    }
    
    /*removes the text from every button in the given array*/
    public static void removeText(JButton [][] buttons){
        for (int row = 0; row < buttons.length; row++){
            for (int col = 0; col < buttons[0].length; col++){
                buttons[row][col].setText("");
            }
        }
    }
    
    //copy of the function necessary for new arrays
    public static void ready (Tile [][] board, JButton [][] buttons){
        setSize(buttons);
        setColor(buttons);
        setTextColor(board, buttons);
        removeText(buttons);
        mouseListen(buttons);
        
        beginnerRemaining.setText(Integer.toString(minesRemaining(buttons)));
        intermediateRemaining.setText(Integer.toString(minesRemaining(buttons)));
        advancedRemaining.setText(Integer.toString(minesRemaining(buttons)));
        
        beginnerTitle.setFont(new Font("Arial", Font.BOLD, 25));
        intermediateTitle.setFont(new Font("Arial", Font.BOLD, 25));
        advancedTitle.setFont(new Font("Arial", Font.BOLD, 25));
        
        JBox.setSize(diff1New, 100, 43);
        JBox.setSize(diff2New, 100, 43);
        JBox.setSize(diff3New, 100, 43);
        
        JBox.setSize(diff1ClockImg, 75, 43);
        JBox.setSize(diff2ClockImg, 75, 43);
        JBox.setSize(diff3ClockImg, 75, 43);
        
        beginnerRemaining.setFont(new Font("Arial", Font.PLAIN, 30));
        intermediateRemaining.setFont(new Font("Arial", Font.PLAIN, 30));
        advancedRemaining.setFont(new Font("Arial", Font.PLAIN, 30));
        
        diff1Clock.setFont(new Font("Arial", Font.PLAIN, 30));
        diff2Clock.setFont(new Font("Arial", Font.PLAIN, 30));
        diff3Clock.setFont(new Font("Arial", Font.PLAIN, 30));
        
        diff1Clock.setText("000");
        diff2Clock.setText("000");
        diff3Clock.setText("000");
    }
    
    
    /*changes color to black for  all the buttons that are mines*/
    /*opy of function ncessary for new arrays*/
    public static void loseMines(Tile [][] board, JButton [][] buttons){
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[0].length; col++){
                if (board[row][col].isMine){
                    buttons[row][col].setBackground(Color.black);
                }
            }
        }
    }
    
    
    /*changes color to green for  all the buttons that are mines*/
    /*copy of function needed for new arrays*/
    public static void winMines(Tile [][] board, JButton [][] buttons){
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[0].length; col++){
                if (board[row][col].isMine){
                    buttons[row][col].setBackground(Color.green);
                }
            }
        }
    }
    
    /*controls the start menu in the GUI, returns a string of the difficulty the plyer chooses*/
    public static String startMenu(JFrame frame){
        frame.setSize(500,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuTitle.setFont(new Font("Arial", Font.BOLD, 25));
        frame.add(menu);
        frame.setVisible(true);
        
        JEventQueue events = new JEventQueue();
        events.listenTo(diff1, "beginner");
        events.listenTo(diff2, "intermediate");
        events.listenTo(diff3, "advanced");
        
        EventObject event = events.waitEvent();
        String name = events.getName(event);
        frame.remove(menu);
        
        return name;
    }
    
    /*control the GUI once the game is in progress*/
    public static void runGame(String diff, JFrame frame){
        BetterMinesweeper m = new BetterMinesweeper();
        Tile [][] board = m.setBoard(diff);
        JEventQueue events = new JEventQueue();
        TileTracker numbered = new TileTracker(diff);
        
        events.listenTo(getNewGame(diff), "New Game");
        ready(board, getButtonArray(diff));
        frame.add(getBody(diff));
        int [] mineLocations = getMineLocations(board);
        
        int time = 0;
        events.startTimer(1000, "clock");
        clockRunning = true;
        
        if (diff.equals("beginner")){
            frame.setSize(500, 600);
            frame.setVisible(true);
            m.listenTo(events, beginner);        
        }
        else if (diff.equals("intermediate")){
            frame.setSize(700, 900);
            frame.setVisible(true);
            m.listenTo(events, intermediate);        
        }
        else if (diff.equals("advanced")){
            frame.setSize(1300, 900);
            frame.setVisible(true);
            m.listenTo(events, advanced);        
        }
        
        boolean endLoop = false;
        while (!endLoop){
            while(!(win(board, getButtonArray(diff), diff, events)) && !endLoop){
                EventObject event = events.waitEvent();
                String name = events.getName(event);
                int row = -1;
                int col = -1;
                
                
                if (name.equals("New Game")){
                    endLoop = true;
                    if (clockRunning){
                        events.stopTimer("clock");
                        clockRunning = false;
                    }
                }
                else if (name.equals("clock") && time > 0 && time < 999){
                    time++;
                    if (time < 10){
                        getClock(diff).setText("00" + Integer.toString(time));
                    }
                    else if (time > 9 && time < 100){
                        getClock(diff).setText("0" + Integer.toString(time));
                    }
                    else{
                        getClock(diff).setText(Integer.toString(time));
                    }
                }
                
                else if (Character.isDigit(name.charAt(0))){//only does this block if a tile has been selected
                    row = getIndex(name)[0]; 
                    col = getIndex(name)[1];
                    
                    if (noneRevealed(board)){
                        time = 1;
                    }
                    
                    /*if the first click is not on a tile near zero mines, the mines will be swapped to the first 
                     * empty tile starting from the top left corner*/
                    while (noneRevealed(board) && !zero(row, col, board)){
                        safeArea(row, col, board);
                        board = fillSums(board);
                        setTextColor(board, getButtonArray(diff));
                    }
                    if (!lose(row, col, board, getButtonArray(diff), mineLocations, events)){
                        if (!board[row][col].isRevealed && !board[row][col].isMine){
                            revealOne(row, col, getButtonArray(diff), board);
                        }
                        else if (board[row][col].isRevealed && (board[row][col].timeClicked == time || board[row][col].timeClicked == time - 1)){
                            doubleClick(board[row][col], getButtonArray(diff), board);
                        }
                        board[row][col].timeClicked = time;
                        numbered.update(board);
                        //numbered.printTiles();
                    }
                }
                
            }
            if (win(board, getButtonArray(diff), diff, events)){
                if (events.getName(events.waitEvent()).equals("New Game")){
                    endLoop = true;
                }
                
            }
        }
        frame.remove(getBody(diff));
    }
    
    /*returns a remaining button based on difficulty level*/
    public static JTextArea getRemaining(String diff){
        if (diff.equals("beginner")){
            return beginnerRemaining;
        }
        else if (diff.equals("intermediate")){
            return intermediateRemaining;
        }
        return advancedRemaining;
    }
    
    /*returns the appropriate array of buttons from this class based on difficulty level*/
    public static JButton [][] getButtonArray(String diff){
        if (diff.equals("beginner")){
            return beginner;
        }
        else if (diff.equals("intermediate")){
            return intermediate;
        }
        return advanced;
    }
    
    /*returns appropriate New Game button depending on difficulty*/
    public static JButton getNewGame(String diff){
        if (diff.equals("beginner")){
            return diff1New;
        }
        else if (diff.equals("intermediate")){
            return diff2New;
        }
        return diff3New;
    }
    
    /*returns appropriate body depending on difficulty*/
    public static JBox getBody(String diff){
        if (diff.equals("beginner")){
            return beginnerBody;
        }
        else if (diff.equals("intermediate")){
            return intermediateBody;
        }
        return advancedBody;
    }
    
    /*returns appropriate clock text area depending on difficulty*/
    public static JTextArea getClock(String diff){
        if (diff.equals("beginner")){
            return diff1Clock;
        }
        else if (diff.equals("intermediate")){
            return diff2Clock;
        }
        return diff3Clock;
    }
    
    /*returns true if the player has won the game (the player has revealed all non-mines) calls winMines if the player won*/
    public static boolean win (Tile [][] board, JButton [][] buttonArray, String diff, JEventQueue timer){
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[0].length; col++){
                if (!board[row][col].isMine){
                    if (!board[row][col].isRevealed){
                        return false;
                    }
                }
            }
        }
        getRemaining(diff).setText("0");
        winMines(board, buttonArray);
        if (clockRunning){
            timer.stopTimer("clock");
            clockRunning = false;
        }
        return true;
    }
    
    /*returns true if the player has lost calls loseMines if that is the case*/
    public static boolean lose(int row, int col, Tile [][] board, JButton [][] buttonArray, int [] mineLocations, JEventQueue timer){
        
        if (board[row][col].isMine && buttonArray[row][col].getBackground() != Color.red){
            loseMines(board, buttonArray);
            if (clockRunning){
                timer.stopTimer("clock");
                clockRunning = false;
            }
            return true;
        }
        if (board[row][col].isRevealed && !goodDoubleClick(row, col, board, buttonArray) && numReds(board[row][col], buttonArray) == board[row][col].near){
            //losses that happen when the play double clicks a tile that has already been revealed
            loseMines(board, buttonArray);
            if (clockRunning){
                timer.stopTimer("clock");
                clockRunning = false;
            }
            return true;
        }
        if (buttonArray[mineLocations[0]][mineLocations[1]].getBackground() == Color.black){
            //this block keeps the player from being able to click blue tiles after they have already lost
            return true;
        }
        return false;
    }
    
    
    
    /*retuns a 2d array of tiles with mines and near fields of adjacent tiles filled in*/
    public static Tile [][] setBoard(String diff){
        int rows, cols, numMines;
        
        if (diff.equalsIgnoreCase("beginner")){
            rows = 9;
            cols = 9;
            numMines = 10;
        }
        else if(diff.equalsIgnoreCase("intermediate")){
            rows = 16;
            cols = 16;
            numMines = 40;
        }
        else{
            rows = 16;
            cols = 30;
            numMines = 99;
        }
        
        Mine [] mineLocations = setUniqueLocations(numMines, rows, cols);
        Tile [][] board = createTileBoard(rows, cols);
        board = placeMines(board, mineLocations);
        board = fillSums(board);
        
        return board;
    }
    
    //returns an array of mines. mines may or may not have unique locations
    public static Mine [] setMineLocations(int mines, int rows, int cols){
        Mine [] locations = new Mine [mines];
        
        for (int i = 0; i < locations.length; i++){
            locations[i] = new Mine(rows, cols);
        }
        
        return locations;
    }
    
    /*returns true only if the mines in the array each have a unique location*/
    public static boolean checkUnique(Mine [] m){
        for (int i = 0; i < m.length; i++){
            for (int j = 0; j < m.length; j++){
                if (i != j){
                    if (m[i].equals(m[j])){
                        return false;
                    } 
                }
            }
        }
        return true;
    }
    
    //creates an array of mines and makes sure they are unique before returning
    public static Mine [] setUniqueLocations(int mines, int rows, int cols){
        Mine [] m = setMineLocations(mines , rows, cols);
        while (!checkUnique(m)){
            m = setMineLocations(mines , rows, cols);
        }
        return m;
    }
    
    
    //places mines in an array of Tiles
    public static Tile [][] placeMines(Tile [][] board, Mine [] mines){
        for(Mine m : mines){
            board[m.row][m.col].isMine = true;
        }
        return board;
    }
    
    /*the functions in the following series return true if the tile in the corresponding direction of the tile given 
     * contains a mine
     top left, for example returns true if the tile to the top left of the one given contains a mine
     all of these functions prevent index out of bounds errors in their first conditional*/
    public static  boolean topLeft(int row, int col, Tile [][]board){
        if (row == 0 || col == 0){
            return false;
        }
        return board[row-1][col-1].isMine;
    }
    
    public static boolean top(int row, int col, Tile [][]board){
        if (row == 0){
            return false;
        }
        return board[row-1][col].isMine;
    }
    
    public static boolean topRight(int row, int col, Tile [][]board){
        if (row == 0 || col == board[0].length - 1){
            return false;
        }
        return board[row-1][col+1].isMine;
    }
    
    public static boolean left(int row, int col, Tile [][]board){
        if (col == 0){
            return false;
        }
        return board[row][col-1].isMine;
    }
    
    public static boolean right(int row, int col, Tile [][]board){
        if (col == board[0].length - 1){
            return false;
        }
        return board[row][col+1].isMine;
    }
    
    public static boolean bottomLeft(int row, int col, Tile [][]board){
        if (row == board.length - 1 || col == 0){
            return false;
        }
        return board[row+1][col-1].isMine;
    }
    
    public static boolean bottom(int row, int col, Tile [][]board){
        if (row == board.length - 1){
            return false;
        }
        return board[row+1][col].isMine;
    }
    
    public static boolean bottomRight(int row, int col, Tile [][]board){
        if (row == board.length - 1 || col == board[0].length - 1){
            return false;
        }
        return board[row+1][col+1].isMine;
    }
    
    /*returns the total number of mines the tile at the given row and col is touching and stores the locations of 
     * adjacent mines in nearMines*/
    public static int tileSum(int row, int col, Tile [][] board){
        int sum = 0;
        
        if (topLeft(row, col, board)){
            sum++;
            board[row][col].addMine(row - 1, col - 1);
        }
        if (top(row, col, board)){
            sum++;
            board[row][col].addMine(row - 1, col);
        }
        if (topRight(row, col, board)){
            sum++;
            board[row][col].addMine(row - 1, col + 1);
        }
        if (left(row, col, board)){
            sum++;
            board[row][col].addMine(row, col - 1);
        }
        if (right(row, col, board)){
            sum++;
            board[row][col].addMine(row, col + 1);
        }
        if (bottomLeft(row, col, board)){
            sum++;
            board[row][col].addMine(row + 1, col - 1);
        }
        if (bottom(row, col, board)){
            sum++;
            board[row][col].addMine(row + 1, col);
        }
        if (bottomRight(row, col, board)){
            sum++;
            board[row][col].addMine(row + 1, col + 1);
        }
        
        return sum;
    }
    
    /*populates board with numbers indication how many mines each tile is touching places a 9 if the tile is a mine*/
    public static Tile [][] fillSums(Tile [][] board){
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[0].length; col++){
                board[row][col].near = tileSum(row, col, board);
            }
        }
        return board;
    }
    
    //return the index of the clicked button as an array of two ints
    public static int [] getIndex(String click){
        int row, col;
        
        if (click.charAt(1) == '_'){
            row = Integer.parseInt(click.substring(0,1));  //single digit row
            col = Integer.parseInt(click.substring(2)); 
        }
        else{
            row = Integer.parseInt(click.substring(0,2)); //double digit row
            col = Integer.parseInt(click.substring(3));
        }
        
        int [] index = {row, col};
        return index;
    }
    
    /*reveals number behind and changes color of only the button that ahas been clicked. modified for type Tile
     * there might be a way to do this without passing in the entire array of Tiles since each tile already has its 
     * index stored inside*/
    public static void revealOne(int row, int col, JButton [][] buttons, Tile [][] board){
        //System.out.println("revealOne(" + row + ", " + col + ")");
        //conditional prevents index out of bounds errors
        if (row > - 1 && row < board.length && col > - 1 && col < board[0].length){
            if (board[row][col].isRevealed == false && buttons[row][col].getBackground() != Color.red){
                if (board[row][col].near > 0){
                    buttons[row][col].setText(Integer.toString(board[row][col].near));
                }
                buttons[row][col].setBackground(Color.lightGray);
                buttons[row][col].setOpaque(true);
                board[row][col].isRevealed = true;
                if (board[row][col].near == 0){
                    revealNear(row, col, buttons, board);
                }
            }
        }
    }
    
    //reveals every tile the given tile is touching only if it has not already been revealed
    public static void revealNear(int row, int col, JButton [][] buttons, Tile [][] board){
        revealOne(row - 1, col - 1, buttons, board); //top left
        revealOne(row - 1, col, buttons, board); //top
        revealOne(row - 1, col + 1, buttons, board); //top right
        
        revealOne(row, col - 1, buttons, board);//left
        revealOne(row, col + 1, buttons, board);//right
        
        revealOne(row + 1, col - 1, buttons, board);//bottom left
        revealOne(row + 1, col, buttons, board);//bottom
        revealOne(row + 1, col + 1, buttons, board);//bottom right
    }
    
    /*reveals every tile that is not a mine. only used for testing*/
    public static void revealAllNonMines(JButton [][] buttons, Tile [][] board){
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[0].length; col++){
                revealOne(row, col, buttons, board);
            }
        }
    }
    
    /*changes to color of a flagged tile to red*/
    public static void colorFlag(int row, int col, JButton [][] buttons){
        if (buttons[row][col].getBackground() != Color.red){
            buttons[row][col].setBackground(Color.red);
            buttons[row][col].setOpaque(true);
        }
        else {
            buttons[row][col].setBackground(Color.blue);
            buttons[row][col].setOpaque(true);
        }
    }
    
    /*changes to color of a question marked tile to white*/
    public static void colorQuestion(int row, int col, JButton [][] buttons){
        if (buttons[row][col].getBackground() != Color.white){
            buttons[row][col].setBackground(Color.white);
            buttons[row][col].setOpaque(true);
        }
        else{
            buttons[row][col].setBackground(Color.blue);
            buttons[row][col].setOpaque(true);
        }
    }
    
    /*prints the indexes of all the mines in the array*/
    public static void printMines(Mine [] m){
        for (int i = 0; i < m.length; i++){
            System.out.println(m[i].toString());
        }
    }
    
    
    public static void printBoard(Tile [][] board){
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[0].length; col++){
                System.out.print(board[row][col].near + " ");
            }
            System.out.println();
        }
    }
    
    public static Tile [][] createTileBoard (int rows, int cols){
        Tile [][] board = new Tile [rows][cols];
        for (int row = 0; row < rows; row++){
            for (int col = 0; col < cols; col++){
                board[row][col] = new Tile(row, col);
                board[row][col].pos = "middle";
            }
        }
        
        /*setting the position field of each non-middle tile*/
        board[0][0].pos = "top left";
        board[0][cols-1].pos = "top right";
        board[rows-1][0].pos = "bottom left";
        board[rows-1][cols-1].pos = "bottom right";
        
        for (int row = 1; row < rows-1; row++){
            board[row][0].pos = "left";
            board[row][cols-1].pos = "right";
        }
        
        for (int col = 1; col < cols-1; col++){
            board[0][col].pos = "top";
            board[rows-1][col].pos = "bottom";
        }
        
        return board;
    }
    
    public static int minesRemaining(JButton [][] buttons){
        int flags = 0;
        int mines = 10;
        
        if (buttons[0].length == 16){
            mines = 40;
        }
        else if (buttons[0].length == 30){
            mines = 99;
        }
        
        for (int row = 0; row < buttons.length; row++){
            for (int col = 0; col < buttons[0].length; col ++){
                if (buttons[row][col].getBackground() == Color.red){
                    flags++;
                }
            }
        }
        
        return mines - flags;
    }
    
    /*a function that returns the total number of mines on the baord*/
    public static int totalMines(Tile [][] board){
        int total = 0;
        
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[0].length; col++){
                if (board[row][col].isMine){
                    total++;
                }
            }
        }
        
        return total;
    }
    
    //makes the EventQueue listen to every button in the array
    public static void listenTo(JEventQueue events, JButton [][] tiles){
        for (int row = 0; row < tiles.length; row++){
            for (int col = 0; col < tiles[0].length; col++){
                events.listenTo(tiles[row][col], Integer.toString(row) + "_" + Integer.toString(col));
            }
        }
    }
    
    /*returns true if none of the tile have been revealed yet*/
    public static boolean noneRevealed(Tile [][] board){
        
        for (int row = 0; row < board.length; row ++){
            for (int col = 0; col < board[0].length; col++){
                if (board[row][col].isRevealed){
                    return false;
                }
            }
        }
        return true;
    }
    
    /*returns to number of red tiles the given tile is touching*/
    public static int numReds(Tile click, JButton [][] board){
        int sum = 0;
        
        if (click.pos.equals("middle")){
            if(board[click.row-1][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row-1][click.col].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row-1][click.col+1].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row][click.col+1].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row+1][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row+1][click.col].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row+1][click.col+1].getBackground() == Color.red){
                sum++;
            }
        }
        else if (click.pos.equals("top")){
            if(board[click.row][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row][click.col+1].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row+1][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row+1][click.col].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row+1][click.col+1].getBackground() == Color.red){
                sum++;
            }
        }
        else if (click.pos.equals("left")){
            if(board[click.row-1][click.col].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row-1][click.col+1].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row][click.col+1].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row+1][click.col].getBackground() == Color.red){
                sum++;
            }
            if(board[click.row+1][click.col+1].getBackground() == Color.red){
                sum++;
            }
        }
        else if (click.pos.equals("right")){
            if (board[click.row-1][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row-1][click.col].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row+1][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row+1][click.col].getBackground() == Color.red){
                sum++;
            }
        }
        else if (click.pos.equals("bottom")){
            if (board[click.row-1][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row-1][click.col].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row-1][click.col+1].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row][click.col+1].getBackground() == Color.red){
                sum++;
            }
        }
        else if (click.pos.equals("top left")){
            if (board[click.row][click.col+1].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row+1][click.col].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row+1][click.col+1].getBackground() == Color.red){
                sum++;
            }
        }
        else if (click.pos.equals("top right")){
            if (board[click.row][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row+1][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row+1][click.col].getBackground() == Color.red){
                sum++;
            }
        }
        else if (click.pos.equals("bottom left")){
            if (board[click.row-1][click.col].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row-1][click.col+1].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row][click.col+1].getBackground() == Color.red){
                sum++;
            }
        }
        /*bottom right*/
        else{
            if (board[click.row-1][click.col-1].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row-1][click.col].getBackground() == Color.red){
                sum++;
            }
            if (board[click.row][click.col-1].getBackground() == Color.red){
                sum++;
            }
        }
        
        return sum;
    }
    
    public static void printPos(Tile [][] board){
        String s = "";
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[0].length; col++){
                s = board[row][col].pos;
                if (s.equals("middle")){
                    System.out.print("m " );
                }
                else if(s.equals("top")){
                    System.out.print("t " );
                }
                else if(s.equals("left")){
                    System.out.print("l  " );
                }
                else if(s.equals("right")){
                    System.out.print(" r" );
                }
                else if(s.equals("bottom")){
                    System.out.print("b " );
                }
                else if(s.equals("top left")){
                    System.out.print("tl " );
                }
                else if(s.equals("top right")){
                    System.out.print("tr " );
                }
                else if(s.equals("bottom left")){
                    System.out.print("bl " );
                }
                else if(s.equals("bottom right")){
                    System.out.print("br " );
                }
            }
            System.out.println();
        }
    }
    
    /*reveals non-red adjacent tiles if the number of non-reds is equals to the number of the tile clicked*/
    public static void doubleClick(Tile click, JButton [][] buttons, Tile [][] board){
        if (board[click.row][click.col].near == numReds(click, buttons)){
            revealNear(click.row, click.col, buttons, board);
        }
    }
    
    public static boolean goodDoubleClick(int row, int col, Tile [][] board, JButton [][] buttons){
        Tile t = board[row][col];
        
        if (t.pos.equals("middle")){
            for (int r = row - 1; r < row + 2; r++){
                for (int c = col - 1; c < col + 2; c++){
                    if ((r != row && c != col) || (r != row ^ c != col)){
                        if (buttons[r][c].getBackground() == Color.red && !board[r][c].isMine){
                            return false;
                        }
                        else if (buttons[r][c].getBackground() != Color.red && board[r][c].isMine){
                            return false;
                        }
                    }
                }
            }
        }
        else if (t.pos.equals("top")){
            for (int r = row; r < row + 2; r++){
                for (int c = col - 1; c < col + 2; c++){
                    if ((r != row && c != col) || (r != row ^ c != col)){
                        if (buttons[r][c].getBackground() == Color.red && !board[r][c].isMine){
                            return false;
                        }
                        else if (buttons[r][c].getBackground() != Color.red && board[r][c].isMine){
                            return false;
                        }
                    }
                }
            }
        }
        else if (t.pos.equals("left")){
            for (int r = row - 1; r < row + 2; r++){
                for (int c = col; c < col + 2; c++){
                    if ((r != row && c != col) || (r != row ^ c != col)){
                        if (buttons[r][c].getBackground() == Color.red && !board[r][c].isMine){
                            return false;
                        }
                        else if (buttons[r][c].getBackground() != Color.red && board[r][c].isMine){
                            return false;
                        }
                    }
                }
            }
        }
        else if (t.pos.equals("right")){
            for (int r = row - 1; r < row + 2; r++){
                for (int c = col - 1; c < col + 1; c++){
                    if ((r != row && c != col) || (r != row ^ c != col)){
                        if (buttons[r][c].getBackground() == Color.red && !board[r][c].isMine){
                            return false;
                        }
                        else if (buttons[r][c].getBackground() != Color.red && board[r][c].isMine){
                            return false;
                        }
                    }
                }
            }
        }
        else if (t.pos.equals("bottom")){
            for (int r = row - 1; r < row + 1; r++){
                for (int c = col - 1; c < col + 2; c++){
                    if ((r != row && c != col) || (r != row ^ c != col)){
                        if (buttons[r][c].getBackground() == Color.red && !board[r][c].isMine){
                            return false;
                        }
                        else if (buttons[r][c].getBackground() != Color.red && board[r][c].isMine){
                            return false;
                        }
                    }
                }
            }
        }
        else if (t.pos.equals("top left")){
            for (int r = row; r < row + 2; r++){
                for (int c = col; c < col + 2; c++){
                    if ((r != row && c != col) || (r != row ^ c != col)){
                        if (buttons[r][c].getBackground() == Color.red && !board[r][c].isMine){
                            return false;
                        }
                        else if (buttons[r][c].getBackground() != Color.red && board[r][c].isMine){
                            return false;
                        }
                    }
                }
            }
        }
        else if (t.pos.equals("top right")){
            for (int r = row; r < row + 2; r++){
                for (int c = col - 1; c < col + 1; c++){
                    if ((r != row && c != col) || (r != row ^ c != col)){
                        if (buttons[r][c].getBackground() == Color.red && !board[r][c].isMine){
                            return false;
                        }
                        else if (buttons[r][c].getBackground() != Color.red && board[r][c].isMine){
                            return false;
                        }
                    }
                }
            }
        }
        else if (t.pos.equals("bottom left")){
            for (int r = row - 1; r < row + 1; r++){
                for (int c = col; c < col + 2; c++){
                    if ((r != row && c != col) || (r != row ^ c != col)){
                        if (buttons[r][c].getBackground() == Color.red && !board[r][c].isMine){
                            return false;
                        }
                        else if (buttons[r][c].getBackground() != Color.red && board[r][c].isMine){
                            return false;
                        }
                    }
                }
            }
        }
        //bottom right
        else {
            for (int r = row - 1; r < row + 1; r++){
                for (int c = col - 1; c < col + 1; c++){
                    if ((r != row && c != col) || (r != row ^ c != col)){
                        if (buttons[r][c].getBackground() == Color.red && !board[r][c].isMine){
                            return false;
                        }
                        else if (buttons[r][c].getBackground() != Color.red && board[r][c].isMine){
                            return false;
                        }
                    }
                }
            }
        }
        //System.out.println("true");
        return true;
    }
    
    /*returns an array of type Mine containing the locations of all the mines in the given board*/
    public static int [] getMineLocations(Tile [][] board){
        int numMines;
        
        if (board[0].length == 9){
            numMines = 10;
        }
        else if (board[0].length == 16){
            numMines = 40;
        }
        else {
            numMines = 99;
        }
        
        int [] loc = new int [numMines*2];
        
        int index = 0;
        
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[0].length; col++){
                if (board[row][col].isMine){
                    loc[index] = row;
                    loc[index + 1] = col;
                    index = index + 2;
                }
            }
        }
        return loc;
    }
    
    /*scans the board from top left down to the bottom right and returns the first non-mine tile*/
    public static Tile firstEmpty(Tile [][] board){
        Tile t = new Tile (0,0);
        
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[0].length; col++){
                if (!board[row][col].isMine){
                    t = board[row][col];
                    row = 100;
                    col = 100;
                }
            }
        }
        return t;
    }
    
    /*reverses the state of the isMine field for the two tiles*/
    public static void swapMines(Tile mine, Tile nonMine){
        mine.isMine = false;
        nonMine.isMine = true;
    }
    
    /*makes sure the first click is a not a mine and is not adjacent to any mines*/
    public static void safeArea(int row, int col, Tile [][] board){
        if(board[row][col].isMine){
        swapMines(board[row][col], firstEmpty(board));
        }
        for (int i = 0; board[row][col].nearMines[i] != -1; i = i + 2){
            swapMines(board[board[row][col].nearMines[i]][board[row][col].nearMines[i + 1]], firstEmpty(board));
        }
    }
    
    //returns true if the given index is not a mine and is not touching any mines
    public static boolean zero(int row, int col, Tile [][] board){
        if (board[row][col].isMine || board[row][col].near > 0){
            return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        
        JFrame frame = new JFrame("Minesweeper");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        BetterMinesweeper m = new BetterMinesweeper();
        
        while (true){
            String diff = startMenu(frame);
            runGame(diff, frame);
        }
        
    }
    
}
