package com.example.messmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RockPaperScissorActivity extends AppCompatActivity {
Button rockBtn,paperBtn,scissorBtn,computerBtn;
TextView finalResultTv,userOutput,computerOutput;
String userChoose="";
String computerChoose="";
int i=3;
CountDownTimer countDownTimer;
String[] options={"Rock","Paper","Scissors"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        rockBtn=findViewById(R.id.rockBtn);
        paperBtn=findViewById(R.id.paperBtn);
        scissorBtn=findViewById(R.id.scissorBtn);
        userOutput=findViewById(R.id.userOutput);
        finalResultTv=findViewById(R.id.finalResultTv);

        computerOutput=findViewById(R.id.computerOutput);


        rockBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                userChoose="Rock";

                timer();
               // computerTurn();
            }
        });
        paperBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                userChoose="Paper";
                timer();
                //computerTurn();
            }
        });

        scissorBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                userChoose="Scissors";
                timer();

            }
        });
    }
    public void timer(){
        countDownTimer= new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                finalResultTv.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                computerTurn();
            }
        }.start();
    }
    public void computerTurn(){
        userOutput.setText(userChoose);
        int rand=(int)(Math.random() * 3);
        computerChoose=options[rand];
        computerOutput.setText(computerChoose);
        if(userChoose.equals(computerChoose)){
            finalResultTv.setText("Match Draw");
        }
        else if(userChoose.equals("Rock") && computerChoose.equals("Paper")){
            finalResultTv.setText("Computer wins");
        }
        else if(userChoose.equals("Paper") && computerChoose.equals("Rock")){
            finalResultTv.setText("You win");
        }
        else if(userChoose.equals("Scissors") && computerChoose.equals("Paper")){
            finalResultTv.setText("You win");
        }
        else if(userChoose.equals("Paper") && computerChoose.equals("Scissors")){
            finalResultTv.setText("Computer wins");
        }
        else if(userChoose.equals("Scissors") && computerChoose.equals("Rock")){
            finalResultTv.setText("Computer wins");
        }
        else if(userChoose.equals("Rock") && computerChoose.equals("Scissors")){
            finalResultTv.setText("You win");
        }

    }
}