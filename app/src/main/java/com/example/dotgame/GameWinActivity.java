package com.example.dotgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameWinActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_win);
        Button exitBtn = findViewById(R.id.exit);
        Button restartBtn = findViewById(R.id.restart);

        // Exits app on click
        exitBtn.setOnClickListener(v -> {
            Intent exit = new Intent(Intent.ACTION_MAIN);
            exit.addCategory(Intent.CATEGORY_HOME);
            exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(exit);
        });

        restartBtn.setOnClickListener(v -> {
            Intent config = new Intent(GameWinActivity.this, MainActivity.class);
            startActivity(config);
            finish();
        });
    }
}