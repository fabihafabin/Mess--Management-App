package com.example.messmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TicTacActivity extends AppCompatActivity {
    int flag=0,result=0,count=0;
    String b[]=new String[10];
    Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac);
        tv=(TextView) findViewById(R.id.result);
        init();
    }
    public void init(){
        btn1=findViewById(R.id.btn1);
        btn2=findViewById(R.id.btn2);
        btn3=findViewById(R.id.btn3);
        btn4=findViewById(R.id.btn4);
        btn5=findViewById(R.id.btn5);
        btn6=findViewById(R.id.btn6);
        btn7=findViewById(R.id.btn7);
        btn8=findViewById(R.id.btn8);
        btn9=findViewById(R.id.btn9);
    }

    public void Check(View view){
        Button temp=(Button)view;

        if(temp.getText().toString().equals("") && result==0){
            count++;
            if(flag==0){
                ((Button) view).setText("X");
                flag=1;
            }
            else{
                ((Button) view).setText("O");
                flag=0;
            }
            if(count>4){
                getText();
                String ans= checkResult();
                if(!ans.equals("")){
                    result=1;

                    tv.setText(ans+" has won");
                    Toast.makeText(this, ans+" has won", Toast.LENGTH_LONG).show();
                }
            }
            if(count==9){
                Toast.makeText(this, "Match Draw", Toast.LENGTH_SHORT).show();
            }
        }

    }
    public void getText(){
        b[1]=btn1.getText().toString();
        b[2]=btn2.getText().toString();
        b[3]=btn3.getText().toString();
        b[4]=btn4.getText().toString();
        b[5]=btn5.getText().toString();
        b[6]=btn6.getText().toString();
        b[7]=btn7.getText().toString();
        b[8]=btn8.getText().toString();
        b[9]=btn9.getText().toString();
    }
    public String checkResult(){
        if(b[1].equals(b[2]) && b[2].equals(b[3]) && !b[1].equals("")){
            return b[1];
        }
        else if(b[4].equals(b[5]) && b[5].equals(b[6]) && !b[4].equals("")){
            return b[4];
        }
        else if(b[7].equals(b[8]) && b[8].equals(b[9]) && !b[7].equals("")){
            return b[7];
        }
        else if(b[1].equals(b[4]) && b[4].equals(b[7]) && !b[7].equals("")){
            return b[1];
        }
        else if(b[2].equals(b[5]) && b[5].equals(b[8]) && !b[2].equals("")){
            return b[2];
        }
        else if(b[3].equals(b[6]) && b[6].equals(b[9]) && !b[3].equals("")){
            return b[3];
        }
        else if(b[1].equals(b[5]) && b[5].equals(b[9]) && !b[1].equals("")){
            return b[1];
        }
        else if(b[3].equals(b[5]) && b[5].equals(b[7]) && !b[7].equals("")){
            return b[3];
        }
        return "";
    }
    public void Reset(View view){
        btn1.setText("");
        btn2.setText("");
        btn3.setText("");
        btn4.setText("");
        btn5.setText("");
        btn6.setText("");
        btn7.setText("");
        btn8.setText("");
        btn9.setText("");
        for (int i=0; i<b.length; i++){
            b[i]="";
        }
        result=0;
        count=0;
        tv.setText("");
    }
}