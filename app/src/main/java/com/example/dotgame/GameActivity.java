package com.example.dotgame;
import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends Activity {

    private PlayerView playerView;
    private float playerX, playerY;
    private List<Dot> dots = new ArrayList<>();
    private Random random = new Random();
    RelativeLayout gameLayout;
    int screenWidth;
    int screenHeight;
    private Map<Dot, DotView> dotViewMap = new HashMap<>();
    private Timer dotTimer;
    private static final int MAX_DOTS = 20;
    private TextView dotCountTextView;
    private int dotCount = 0;
    private double difficulty;
    private int dotsToWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameLayout = findViewById(R.id.gameLayout);
        dotCountTextView = findViewById(R.id.dotCountTextView);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        // Spawn player in middle of screen
        playerX = screenWidth / 2;
        playerY = screenHeight / 2;


        // Get difficulty selected from Main screen.
        difficulty = getIntent().getDoubleExtra("difficulty", 1);
        // 50 for easy, 75 for med, 100 for hard to win.
        dotsToWin = (int) (20 * difficulty);
        // Create red dot
        playerView = new PlayerView(this, playerX, playerY, 100);
        gameLayout.addView(playerView);
        // Create dot list
        initializeDots();
        // Draw dots on screen
        drawDots();

        /*
        Timer to call checkCollisions every .5 seconds to determine if dots have expired yet.
         */
        dotTimer = new Timer();
        dotTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkCollisions();
                        respawnDotsIfNeeded();
                    }
                });
            }
        }, 0, 500); // Check every .5 seconds
    }

    // Handle key events to move the player
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO logic to move the player (remember to check collisions)
        switch(keyCode){
            case KeyEvent.KEYCODE_W:
                playerView.updatePosition(playerView.getX(), playerView.getY() - 40);
                return true;
            case KeyEvent.KEYCODE_A:
                playerView.updatePosition(playerView.getX() - 40, playerView.getY());
                return true;
            case KeyEvent.KEYCODE_S:
                playerView.updatePosition(playerView.getX(), playerView.getY() + 40);
                return true;
            case KeyEvent.KEYCODE_D:
                playerView.updatePosition(playerView.getX() + 40, playerView.getY());
                return true;
        }
        return false;
    }

    private void initializeDots() {
        // Number of dots to create initially
        int numDots = MAX_DOTS;

        // Adjust the dot radius and other parameters as needed
        int dotRadius = 20;

        for (int i = 0; i < numDots; i++) {
            // Generate random positions for the dots
            float dotX = random.nextInt(screenWidth - 2 * dotRadius) + dotRadius;
            float dotY = random.nextInt(screenHeight - 2 * dotRadius) + dotRadius;

            // Create a new Dot object and add it to the list
            Dot dot = new Dot(dotX, dotY, dotRadius);
            dots.add(dot);

            // Create a new DotView for the dot and add it to the game layout
            DotView newDot = new DotView(this, dot);
            gameLayout.addView(newDot);

            // Update the dotViewMap with the new DotView
            dotViewMap.put(dot, newDot);
        }
    }

    /*
    Method to create dot objects. Maps a dot object to a specific dotView.
     */
    private void drawDots() {
        for (Dot dot : dots) {
            DotView newDot = new DotView(this, dot);
            gameLayout.addView(newDot);
            dotViewMap.put(dot, newDot);
        }
    }

    // Maintains 20 dots on screen
    private void respawnDotsIfNeeded() {
        // TODO: if dots drop below 20, respawn dots
        if (dots.size() < MAX_DOTS) {
            // Calculate how many more dots need to be respawned
            int dotsToRespawn = MAX_DOTS - dots.size();

            // Respawn the required number of dots
            for (int i = 0; i < dotsToRespawn; i++) {
                respawnDot();
            }
        }
    }

    // Recreates the dots. Respawn mechanic
    private void respawnDot() {
        int dotRadius = 20;

        // Generate random positions for the respawned dot
        float dotX = random.nextInt(screenWidth - 2 * dotRadius) + dotRadius;
        float dotY = random.nextInt(screenHeight - 2 * dotRadius) + dotRadius;

        // Create a new Dot object and add it to the list
        Dot dot = new Dot(dotX, dotY, dotRadius);
        dots.add(dot);

        // Create a new DotView for the dot and add it to the game layout
        DotView newDot = new DotView(this, dot);
        gameLayout.addView(newDot);

        // Update the dotViewMap with the new DotView
        dotViewMap.put(dot, newDot);
    }

    /*
    Method that checks to see if any collision has occurred.
     */
    private void checkCollisions() {
        for (int i = 0; i < dots.size(); i++) {
            Dot dot = dots.get(i);
            if (dot.isVisible() && isCollision(playerView, dot)) {
                dot.setInvisible();
                gameLayout.removeView(dotViewMap.get(dot));
                dots.remove(i);
                dotCount++;

                dotCountTextView.setText("Dots Collected: " + dotCount);
                if (dotCount >= dotsToWin) {
                    launchGameWinActivity();
                }
            } else if (dot.isExpired()) {
                dot.setInvisible();
                gameLayout.removeView(dotViewMap.get(dot));
                dots.remove(i);
            }
        }
    }

    /*
    Method that has logic to detect collisions.
    */
    private boolean isCollision(PlayerView playerView, Dot dot) {
        float playerX = playerView.getX();
        float playerY = playerView.getY();
        int playerRadius = playerView.getRadius();
        float dotX = dot.getX();
        float dotY = dot.getY();
        int dotRadius = dot.getRadius();

            /*
            Creates a rectangle around dot, and checks for an intersection between player rect and
            dot rect. Intersection = collision.
             */
        RectF playerRect = new RectF(playerX - playerRadius, playerY - playerRadius, playerX + playerRadius, playerY + playerRadius);
        RectF dotRect = new RectF(dotX - dotRadius, dotY - dotRadius, dotX + dotRadius, dotY + dotRadius);

        return playerRect.intersect(dotRect);
    }

    // Changes game screen to GameWinActivity
    private void launchGameWinActivity() {
        Intent intent = new Intent(this, GameWinActivity.class);
        startActivity(intent);
        finish();
    }
}
