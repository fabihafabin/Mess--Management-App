package com.example.messmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
ImageView googleRegisterIcon;
GoogleSignInOptions gso;
GoogleSignInClient gsc;
EditText nameET,emailET,passwordET,phoneET;
FirebaseAuth mAuth;
FirebaseFirestore firebaseFirestore;
String name,email,phone,password,role;
Button registerBtn;
ProgressBar progressBar;
Context context;
    HashMap<String, Object> params;
CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context=this;
        firebaseFirestore=FirebaseFirestore.getInstance();
        changeStatusBarColor();
        initialize();
        setListeners();
        googleRegisterIcon=findViewById(R.id.googleRegisterIcon);
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc= GoogleSignIn.getClient(this,gso);

        mAuth=FirebaseAuth.getInstance();

        googleRegisterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=gsc.getSignInIntent();
                startActivityForResult(i,100);

            }
        });
    }
    public void initialize(){
        checkBox=findViewById(R.id.managerCheckbox);
        progressBar=findViewById(R.id.registerProgressBar);
        nameET=findViewById(R.id.editTextName);
        emailET=findViewById(R.id.editTextEmail);
        passwordET=findViewById(R.id.editTextPassword);
        phoneET=findViewById(R.id.editTextMobile);
        registerBtn=findViewById(R.id.cirRegisterButton);
    }
    public void showProgressBar(boolean bool){
        if(bool){
            progressBar.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.INVISIBLE);
        }
        else{
            registerBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    public void setListeners(){
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showProgressBar(true);
                name=nameET.getText().toString();
                email=emailET.getText().toString();
                password=passwordET.getText().toString();
                phone=phoneET.getText().toString();
                if(checkBox.isChecked()){
                    role="manager";
                }
                else{
                    role="member";
                }
                params = new HashMap<String,Object>();

                params.put("name",name);
                params.put("email",email);
                params.put("phone",phone);
                params.put("role",role);
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    showToast("Account Created Successfully");
                                    firebaseFirestore.collection("users")
                                            .document(email).set(params)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        addToOwnServer();
//                                                        EmailVerification();
                                                        showProgressBar(false);
                                                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                                    }

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    showToast(e.getMessage());
                                                    showProgressBar(false);
                                                }
                                            });

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showProgressBar(false);
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });



            }
        });
    }
public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
}
    public void EmailVerification(){
        FirebaseUser user=mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            showProgressBar(false);
                            Toast.makeText(getApplicationContext(),"Please check your mail to verify your account",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }



    public void changeStatusBarColor()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }
    public void onLoginClick(View view)
    {
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

public void addToOwnServer(){
    final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
    final String[] out = {""};
    String server_url=Constants.LOCAL_SERVER_URL+"adduser";
    showToast(params.get("name").toString());

    JsonObjectRequest jsObjRequest = new
            JsonObjectRequest(Request.Method.POST,
            server_url,
            new JSONObject(params),
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        showToast(response.getString("msg"));
                        showProgressBar(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            showToast("That didn't work!");
            showProgressBar(false);
        }
    });
    queue.add(jsObjRequest);

}


}