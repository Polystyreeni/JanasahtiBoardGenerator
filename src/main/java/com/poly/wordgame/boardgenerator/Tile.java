package com.poly.wordgame.boardgenerator;

public class Tile {
    private int x;
    private int y;
    private String character;
    private boolean occupied = false;

    public Tile(int x, int y, String character) {
        this.x = x;
        this.y = y;
        this.character = character;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getCharacter() {
        return character;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean value) {
        occupied = value;
    }
}
