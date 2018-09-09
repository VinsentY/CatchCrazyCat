package com.example.vinsent_y.catchcrazycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class Playground extends SurfaceView implements OnTouchListener{

    private int WIDTH = 40;
    private int COL = 9;
    private int ROW = 9;
    private int BLOCK = 10;

    private int offsetY = 200;

    private Dot[][] matrix;

    private Dot cat;

    public Playground(Context context) {
        super(context);
        getHolder().addCallback(callback);
        matrix = new Dot[ROW][COL];
        setOnTouchListener(this);
        initGame();
    }

    void redraw() {
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(Color.LTGRAY);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < ROW; i++) {
            int offset = 0;
            if (i % 2 != 0) {
                offset = WIDTH / 2;
            }
            for (int j = 0; j < COL; j++) {
                Dot temp = matrix[i][j];
                switch (temp.getStatus()) {
                    case Dot.STATUS_EMPTY:
                        paint.setColor(0xFFEEEEEE);
                        break;
                    case Dot.STATUS_BLOCK:
                        paint.setColor(0xFFFFAA00);
                        break;
                    case Dot.STATUS_CAT:
                        paint.setColor(0xFFFF0000);
                        break;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawOval(temp.getCol() * WIDTH + offset, temp.getRow() * WIDTH + offsetY,
                            (temp.getCol() + 1) * WIDTH + offset, (temp.getRow() + 1) * WIDTH + offsetY, paint);
                }


            }
        }
        getHolder().unlockCanvasAndPost(canvas);
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                redraw();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            WIDTH = (int) (i1/(COL + 0.5));
            offsetY = i2 - ROW * WIDTH - WIDTH / 2;
            redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();

            Dot touchDot = getTouchedDot(x,y);

            if (touchDot == null) {
                initGame();
            } else if(touchDot.getStatus() == Dot.STATUS_EMPTY) {
                touchDot.setStatus(Dot.STATUS_BLOCK);
            }
            redraw();

        }
        return true;
    }

    private void initGame() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j] = new Dot(i,j);
                matrix[i][j].setStatus(Dot.STATUS_EMPTY);
            }
        }
        cat = new Dot(4, 4);
        cat.setStatus(Dot.STATUS_CAT);
        matrix[4][4] = cat;
        for (int i = 0; i <= BLOCK; ) {
            int row = (int) ((Math.random() * 1000) % ROW);
            int col = (int) ((Math.random() * 1000) % COL);
            if (matrix[row][col].getStatus() == Dot.STATUS_EMPTY) {
                matrix[row][col].setStatus(Dot.STATUS_BLOCK);
                i++;
            }
        }
    }

    private Dot getTouchedDot(int x, int y) {
        y = y - offsetY;
        int col;
        int row = y / WIDTH;
        if(row % 2 != 0) {
            col = (x - WIDTH/2)/WIDTH;
        } else {
            col = x/WIDTH;
        }

        if ( col + 1 > COL || row + 1 > ROW) {
            return null;
        } else {
            return matrix[row][col];
        }
    }


}