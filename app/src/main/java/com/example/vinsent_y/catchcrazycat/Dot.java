package com.example.vinsent_y.catchcrazycat;

public class Dot  {

    private int row;
    private int col;
    private int status;

    public static final int STATUS_EMPTY = 0;
    public static final int STATUS_BLOCK = 1;
    public static final int STATUS_CAT = 2;

    public Dot(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
