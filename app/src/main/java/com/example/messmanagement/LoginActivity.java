package com.example.messmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
TextView fortgotPasswordTV;
EditText emailEt,passwordET;
Button loginBtn;
    String mess;
FirebaseAuth firebaseAuth;
ProgressBar progressBar;
FirebaseFirestore firebaseFirestore;
SharedPreferences sharedPreferences;
SharedPreferences.Editor editor;
    String sharedMess,role;
String email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        sharedPreferences=this.getSharedPreferences(Constants.sharedPrefernces,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        sharedMess=sharedPreferences.getString(Constants.userMessId,null);
        firebaseFirestore=FirebaseFirestore.getInstance();

        initialize();
        setListeners();
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
           // if(firebaseUser.isEmailVerified()){

                if(sharedMess!=null){
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();
                }
                else{

                    DocumentReference documentReference=firebaseFirestore.collection("users")
                            .document(firebaseAuth.getCurrentUser().getEmail());
                    documentReference.get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot value = task.getResult();
                                    Toast.makeText(getApplicationContext(),value.getString("role"),Toast.LENGTH_SHORT).show();
                                    mess = value.getString("messid");
                                    email=value.getString("email");
                                    role=value.getString("role");
                                    editor.putString(Constants.userMessId,mess);
                                    editor.putString(Constants.userEmail,email);
                                    editor.putString(Constants.userRole,role);
                                    editor.apply();
                                    if (mess == null && role.equals("manager")) {
                                    startActivity(new Intent(getApplicationContext(), CreateMessActivity.class));
                                        finish();

                                    }
                                    else if(mess==null && role.equals("member")){
                                        startActivity(new Intent(getApplicationContext(), MemberJoinActivity.class));
                                        finish();
                                    }
                                    else{

                                        Toast.makeText(getApplicationContext(),"tata bye bye",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                        finish();
                                    }

                                }
                            });
                }



//            }email verified part
//            else{
//                firebaseAuth.signOut();
//            }
        }
    }

    public void initialize(){
        progressBar=findViewById(R.id.loginProgressBar);
        fortgotPasswordTV=findViewById(R.id.forgotPasswordTv);
        emailEt=findViewById(R.id.editTextEmailLogin);
        passwordET=findViewById(R.id.editTextPasswordLogin);
        loginBtn=findViewById(R.id.cirLoginButton);
    }

    public void showProgress(boolean bool){
        if(bool){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.INVISIBLE);
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    public void setListeners(){
        fortgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ForgotPasswordActivity.class));
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                email=emailEt.getText().toString();
                password=passwordET.getText().toString();
                firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                                   // if(firebaseUser.isEmailVerified()){


                                        DocumentReference documentReference=firebaseFirestore.collection("users")
                                                .document(firebaseAuth.getCurrentUser().getEmail());
                                        documentReference.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        showProgress(false);
                                                        DocumentSnapshot value = task.getResult();
                                                        Toast.makeText(getApplicationContext(),value.getString("role"),Toast.LENGTH_SHORT).show();
                                                        mess = value.getString("messid");

                                                        role=value.getString("role");
                                                        editor.putString(Constants.userMessId,mess);
                                                        editor.putString(Constants.userRole,role);
                                                        editor.apply();
                                                        if (mess == null && role.equals("manager")) {
                                                            startActivity(new Intent(getApplicationContext(), CreateMessActivity.class));
                                                        }
                                                        else if(mess==null && role.equals("member")){
                                                            startActivity(new Intent(getApplicationContext(), MemberJoinActivity.class));
                                                        }
                                                        else{

                                                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                                        }
                                                        finish();
                                                    }
                                                });
                                    }
//                                    else{ email verfication with login btn part
//                                        showProgress(false);
//                                        Toast.makeText(getApplicationContext(),"Please Verify Your Email",Toast.LENGTH_SHORT).show();
//                                        firebaseAuth.signOut();
//                                    }
//                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showProgress(false);
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    public void onLoginClick(View view)
    {
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
}