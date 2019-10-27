package com.example.adam.checkersprojectfinal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Display display;
    private TableLayout mainBoardLayout;
    private ConstraintLayout con;

    private int screenWidth;
    private int screenHeight;

    private int currentPieceId = 0;
    private int currentPieceX = 0;
    private int currentPieceY = 0;

    Drawable whiteOnSquareImg;
    Drawable blackOnSquareImg;
    Drawable whiteDameOnSquareImg;
    Drawable blackDameOnSquareImg;

    List<ImageButton> imgList = new ArrayList<ImageButton>();

    Checkers checkers = new Checkers();

    public Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage, int xShift, int yShift) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, xShift, yShift, null);
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBoardLayout = (TableLayout) findViewById(R.id.boardLayout);
        con = (ConstraintLayout) findViewById(R.id.con);
        con.setBackgroundResource(R.drawable.background);

        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x - (size.x % 8);
        screenHeight = size.y - (size.y % 8);

        Bitmap darkSquareImg = BitmapFactory.decodeResource(getResources(), R.drawable.dark_square);
        Bitmap whitePieceImg = BitmapFactory.decodeResource(getResources(), R.drawable.white_piece);
        Bitmap blackPieceImg = BitmapFactory.decodeResource(getResources(), R.drawable.black_piece);
        Bitmap whiteDameImg = BitmapFactory.decodeResource(getResources(), R.drawable.white_dame);
        Bitmap blackDameImg = BitmapFactory.decodeResource(getResources(), R.drawable.black_dame);
        whiteOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, whitePieceImg, 25, 25));
        blackOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, blackPieceImg, 25, 25));
        whiteDameOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, whiteDameImg, 25, 25));
        blackDameOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, blackDameImg, 25, 25));

        startGame();
    }

    public void startGame() {
        for (int i = 0; i < 8; i++) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(lp);
            for (int j = 0; j < 8; j++) {
                ImageButton imgButton = new ImageButton(this);
                imgButton.setLayoutParams(new TableRow.LayoutParams(screenWidth / 8, screenWidth / 8));
                imgButton.setId(i * 8 + j);
                imgButton.setOnClickListener(this);

                if (i <= 2 && (i + j) % 2 == 1) {
                    imgButton.setBackground(blackOnSquareImg);
                } else if (i >= 5 && (i + j) % 2 == 1) {
                    imgButton.setBackground(whiteOnSquareImg);
                } else if ((i + j) % 2 == 1) {
                    imgButton.setBackgroundResource(R.drawable.dark_square);
                } else {
                    imgButton.setBackgroundResource(R.drawable.white_square);
                }

//                if (checkers.board[i][j] == -1) {
//                    imgButton.setBackgroundResource(R.drawable.white_square);
//                }
//                else if (checkers.board[i][j] == 0) {
//                    imgButton.setBackgroundResource(R.drawable.dark_square);
//                }
//                else if (checkers.board[i][j] == 1) {
//                    imgButton.setBackground(whiteOnSquareImg);
//                }
//                else
//                {
//                    imgButton.setBackground(blackOnSquareImg);
//                }

                tableRow.addView(imgButton);
                imgList.add(imgButton);
            }

            mainBoardLayout.addView(tableRow, i);
        }
    }

    @Override
    public void onClick(View v) {
        System.out.println("Click!");
        if (currentPieceId == 0 || checkers.board[((int) v.getId()) / 8][((int) v.getId()) % 8] != 0) {
            try {
                for (int i = 0; i < checkers.bestCaptures.size(); ++i) {
                    if ((((int) v.getId()) / 8 == Character.getNumericValue(checkers.bestCaptures.get(i).charAt(checkers.bestCaptures.get(i).length() - 2))) && (((int) v.getId()) % 8 == Character.getNumericValue(checkers.bestCaptures.get(i).charAt(checkers.bestCaptures.get(i).length() - 1)))) {
                        checkers.checkMove(currentPieceX, currentPieceY, ((int) v.getId()) / 8, ((int) v.getId()) % 8);
                    }
                }
            } catch (NullPointerException ex) {}
            currentPieceId = v.getId();
            currentPieceX = currentPieceId / 8;
            currentPieceY = currentPieceId % 8;
        }
        else {
            checkers.checkMove(currentPieceX, currentPieceY, ((int) v.getId()) / 8, ((int) v.getId()) % 8);
            currentPieceId = 0;
            currentPieceX = 0;
            currentPieceY = 0;
        }

//        if (checkers.isMaximizingTurnNow()) {
//            checkers.run();
//        }

        drawBoard();
    }

//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                if (checkers.board[i][j] != -1) {
//                    imgList.get(i * 8 + j).setOnClickListener(this);
//                }
//            }
//        }

    void drawBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkers.board[i][j] == -1) {
                    imgList.get(i*8+j).setBackgroundResource(R.drawable.white_square);
                }
                else if (checkers.board[i][j] == 0) {
                    imgList.get(i*8+j).setBackgroundResource(R.drawable.dark_square);
                }
                else if (checkers.board[i][j] == 1) {
                    imgList.get(i*8+j).setBackground(whiteOnSquareImg);
                }
                else if (checkers.board[i][j] == 2)
                {
                    imgList.get(i*8+j).setBackground(blackOnSquareImg);
                }
                else if (checkers.board[i][j] == 3)
                {
                    imgList.get(i*8+j).setBackground(whiteDameOnSquareImg);
                }
                else if (checkers.board[i][j] == 4)
                {
                    imgList.get(i*8+j).setBackground(blackDameOnSquareImg);
                }
            }
        }
    }
}
