package com.example.vinsent_y.catchcrazycat;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Playground extends SurfaceView implements OnTouchListener{

    private static final String TAG = "Playground";

    private int SCREEN_WIDTH;

    private int WIDTH = 40;
    private int COL = 9;
    private int ROW = 9;
    private int BLOCK = 10;

    private int offsetY = 200;

    private Dot[][] matrix;

    private Dot cat;

    private Timer timer = null;

    private TimerTask timerttask = null;

    // 做成神经猫动态图效果的单张图片
    private Drawable cat_drawable;
    // 背景图
    private Drawable background;
    // 神经猫动态图的索引
    private int index = 0;

    private final int IMAGE_NUM = 16;

    private int[] images = new int[IMAGE_NUM];

    // 行走步数
    private int steps;

    private boolean canMove = true;

    public Playground(Context context) {
        super(context);
        getHolder().addCallback(callback);
        matrix = new Dot[ROW][COL];
        setOnTouchListener(this);
        loadRec();
        initGame();
    }

    private void loadRec() {

        cat_drawable = getResources().getDrawable(R.drawable.cat1);


        images[0] = R.drawable.cat1;
        images[1] = R.drawable.cat2;
        images[2] = R.drawable.cat3;
        images[3] = R.drawable.cat4;
        images[4] = R.drawable.cat5;
        images[5] = R.drawable.cat6;
        images[6] = R.drawable.cat7;
        images[7] = R.drawable.cat8;
        images[8] = R.drawable.cat9;
        images[9] = R.drawable.cat10;
        images[10] = R.drawable.cat11;
        images[11] = R.drawable.cat12;
        images[12] = R.drawable.cat13;
        images[13] = R.drawable.cat14;
        images[14] = R.drawable.cat15;
        images[15] = R.drawable.cat16;

        background = getResources().getDrawable(R.drawable.bg);
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
                        paint.setColor(Color.WHITE);
                        break;
                    case Dot.STATUS_BLOCK:
                        paint.setColor(0xFFFFAA00);
                        break;
                    case Dot.STATUS_CAT:
                        paint.setColor(Color.RED);
                        break;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawOval(temp.getCol() * WIDTH + offset, temp.getRow() * WIDTH + offsetY,
                            (temp.getCol() + 1) * WIDTH + offset, (temp.getRow() + 1) * WIDTH + offsetY, paint);

                }


            }
        }

        int left = 0;
        int top = 0;
        if (cat.getRow() % 2 == 0) {
            left = cat.getCol() * WIDTH;
            top = cat.getRow() * WIDTH;
        } else {
            left = (int) (WIDTH / 2) + cat.getCol() * WIDTH;
            top = cat.getRow() * WIDTH;
        }
        // 此处神经猫图片的位置是根据效果图来调整的
        cat_drawable.setBounds(left - WIDTH / 6, top - WIDTH / 2
                + offsetY - WIDTH / 3, left + WIDTH, top + WIDTH + offsetY - WIDTH / 3);
        cat_drawable.draw(canvas);

        background.setBounds(0, 0, SCREEN_WIDTH, offsetY);
        background.draw(canvas);

        getHolder().unlockCanvasAndPost(canvas);
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startTimer();
                redraw();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            WIDTH = (int) (i1/(COL + 0.5));
            offsetY = i2 - ROW * WIDTH - WIDTH * 2;
            SCREEN_WIDTH = i1;
            redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            stopTimer();
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();

            Dot touchDot = getTouchedDot(x,y);

            if (touchDot == null) {
////                initGame();
//                for (int i = 0; i < 6; i++) {
//                    Log.d(TAG, "Position: " + ( i + 1)  + " @ " + getDistance(cat, i) + '\n');
//                    Log.d(TAG, "Neighbour: " + ( i + 1)  + " @ " + getNeighbour(cat, i).getRow() + " " + getNeighbour(cat, i).getCol() + '\n');
//                    //getDistance无问题.
//                    //getNeighbour无问题
//                }
            } else if(canMove == true && touchDot.getStatus() == Dot.STATUS_EMPTY) {
                touchDot.setStatus(Dot.STATUS_BLOCK);
                steps++;
                catMove(bestPosition());
            }
            redraw();

        }
        return true;
    }

    private void initGame() {
        steps = 0;

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

        return getDot(row, col);
    }

    private Dot getDot(int row, int col) {
        if ( col >= COL || row >= ROW || col < 0 || row < 0) {
            return null;
        } else {
            return matrix[row][col];
        }
    }

    private boolean isAtEdge(Dot dot) {
        if(dot.getCol() == 0 || dot.getRow() == 0 || dot.getCol() == COL - 1 || dot.getRow() == ROW - 1) {
            return true;
        } else {
            return  false;
        }
    }

    private Dot getNeighbour(Dot dot, int position) {
        Dot neighbour = null;
        int row = dot.getRow();
        int col = dot.getCol();
        switch (position) {
            case 0:
                neighbour = getDot(row,col - 1);
                break;
            case 1:
                if (row % 2 == 0) {
                    neighbour = getDot(row - 1, col - 1);
                } else {
                    neighbour = getDot(row - 1, col);
                }
                break;
            case 2:
                if (row % 2 == 0) {
                    neighbour = getDot(row - 1, col);
                } else {
                    neighbour = getDot(row - 1, col + 1);
                }
                break;
            case 3:
                neighbour = getDot(row, col + 1);
                break;
            case 4:
                if (row % 2 == 0) {
                    neighbour = getDot(row + 1, col);
                } else {
                    neighbour = getDot(row + 1, col + 1);
                }
                break;
            case 5:
                if (row % 2 == 0) {
                    neighbour = getDot(row + 1, col - 1);
                } else {
                    neighbour = getDot(row + 1, col);
                }
                break;
        }

        return neighbour;
    }

    private int getDistance(Dot dot, int position) {

        int distance = 0;
        Dot next = dot;
        while(true) {
             next = getNeighbour(next,position);
             if(next == null) {
                 return distance;
             } else if(next.getStatus() == Dot.STATUS_BLOCK) {
                return distance * -1;
            } else if (isAtEdge(next)){
                return ++distance;
            } else {
                distance++;
            }
        }
    }

    public int bestPosition() {

        class Info{

            private int position;

            private int distance;

            private Dot dot;

            public Info( Dot dot, int position, int distance) {
                this.position = position;
                this.distance = distance;
                this.dot = dot;
            }
        }

        List<Info> available = new ArrayList<>();


        //填入邻居
        for(int i = 0; i < 6; i++) {
            Dot temp = getNeighbour(cat, i);
            if (temp != null && temp.getStatus() == Dot.STATUS_EMPTY) {
                available.add(new Info(temp,i,getDistance(cat,i)));
            }
        }

        if (available.size() == 0) {
            return -1;
        } else if(available.size() == 1) {
            return available.get(0).position;
        } else {
            List<Info> positive = new ArrayList<>();
            List<Info> negative = new ArrayList<>();

            for(Info info : available) {
                if (info.distance == 0) {
                    return info.position;
                } else if(info.distance > 0) {
                    positive.add(info);
                } else {
                    negative.add(info);
                }
            }
            if (positive.size() != 0) {
                Info min = new Info(null,0,99);

                for(Info info : positive) {
                    if (info.distance < min.distance ){
                        min = info;
                    }
                }
                return min.position;
            } else {
                Info min = new Info(null,0,0);

                for(Info info : negative) {
                    if (info.distance < min.distance ){
                        min = info;
                    }
                }
                return min.position;
            }
        }


    }

    public void catMove(int position) {

        if (position == -1) {
            win();
            return;
        }

        Dot newCat = getNeighbour(cat, position);
        newCat.setStatus(Dot.STATUS_CAT);
        cat.setStatus(Dot.STATUS_EMPTY);
        cat = newCat;


        if (isAtEdge(newCat)) {
            lose();
            return;
        }

    }

    private void lose() {
        canMove = false;
        redraw();
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("通关失败");
        dialog.setMessage("你让神经猫逃出精神院啦(ˉ▽ˉ；)...");
        dialog.setCancelable(false);
        dialog.setNegativeButton("再玩一次", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                initGame();
                canMove = true;
            }
        });
        dialog.setPositiveButton("取消", null);
        dialog.show();
    }

    // 通关成功
    private void win() {
        canMove = false;
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("通关成功");
        dialog.setMessage("你用" + (steps + 1) + "步捕捉到了神经猫耶( •̀ ω •́ )y");
        dialog.setCancelable(false);
        dialog.setNegativeButton("再玩一次", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                initGame();
                canMove = true;
            }
        });
        dialog.setPositiveButton("取消", null);
        dialog.show();
    }

    // 开启定时任务
    private void startTimer() {
        timer = new Timer();
        timerttask = new TimerTask() {
            public void run() {
                gifImage();
            }
        };
        timer.schedule(timerttask, 50, 65);
    }

    // 停止定时任务
    public void stopTimer() {
        timer.cancel();
        timer.purge();
    }

    // 动态图
    private void gifImage() {
        index++;
        if (index >= IMAGE_NUM) {
            index = 0;
        }
        cat_drawable = getResources().getDrawable(images[index]);
        redraw();
    }
}