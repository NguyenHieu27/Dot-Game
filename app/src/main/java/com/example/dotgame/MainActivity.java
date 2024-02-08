package com.example.dotgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startBtn = findViewById(R.id.startButton);

        // Set difficulty based on difficulty checked
        startBtn.setOnClickListener(v -> {
            RadioGroup difficultyRadioGroup = findViewById(R.id.difficultyRadioGroup);
            double difficulty = 1;

            int checkedRadioButtonId = difficultyRadioGroup.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.radioEasy) {
                difficulty = 0.5;
            } else if (checkedRadioButtonId == R.id.radioMedium) {
                difficulty = 0.75;
            } else if (checkedRadioButtonId == R.id.radioHard) {
                difficulty = 1;
            } else {
                difficulty = 0.5;
            }
            Intent game = new Intent(MainActivity.this, GameActivity.class);
            game.putExtra("difficulty", difficulty);
            startActivity(game);
            finish();
        });
    }
}