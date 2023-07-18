package com.example.messmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameChooseActivity extends AppCompatActivity {
Button tictactoeBtn,rockpaperBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_choose);
        tictactoeBtn=findViewById(R.id.tictactoeBtn);
        rockpaperBtn=findViewById(R.id.rockpaperBtn);


        tictactoeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TicTacActivity.class));
            }
        });

        rockpaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RockPaperScissorActivity.class));
            }
        });
    }
}