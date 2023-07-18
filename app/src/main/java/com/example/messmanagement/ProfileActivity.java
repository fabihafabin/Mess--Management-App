package com.example.messmanagement;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
public class ProfileActivity extends AppCompatActivity {
    Button profileBackBtn,profileLeaveMessBtn,editProfileBtn;
    TextView profileUserNameTV,messNameTextView,userProfileEmailTV,userProfileRoleTV;
    ImageView profileImageView;
    TextView userProfileContactTV;
    TextView messidTV;
    String imageString;
    SharedPreferences sharedPreferences;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth;
    Dialog profileDialog;
    EditText profileName, profilePhone;
    Button dialogUpdateMemberBtn;
    String updateName,updatePhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        sharedPreferences=this.getSharedPreferences(Constants.sharedPrefernces,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        initialize();
        setVales();
        setListeners();
        loadImage();
    }

    private void loadImage() {
        firebaseFirestore.collection("images")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String ss=document.getString("image");
                                byte[] decodedString = Base64.decode(ss, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                profileImageView.setImageBitmap(decodedByte);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Failed to load image");
                    }
                });
    }

    public void initialize(){
        profileDialog=new Dialog(this);
        profileDialog.setContentView(R.layout.profile_update_layout);
        profileDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        profileDialog.setCancelable(true);

        profileName=profileDialog.findViewById(R.id.dialogMemberNameET);
        profilePhone=profileDialog.findViewById(R.id.dialogMemberPhoneET);
        dialogUpdateMemberBtn=profileDialog.findViewById(R.id.dialogUpdateMemberBtn);

        editProfileBtn=findViewById(R.id.editProfileBtn);
        profileImageView=findViewById(R.id.profileImageView);
        messidTV=findViewById(R.id.messidTV);
        profileLeaveMessBtn=findViewById(R.id.profileLeaveMessBtn);
        sharedPreferences=getApplicationContext().getSharedPreferences(Constants.sharedPrefernces,MODE_PRIVATE);
        profileUserNameTV=findViewById(R.id.profileUserNameTV);
        profileBackBtn=findViewById(R.id.profileBackBtn);
        messNameTextView=findViewById(R.id.messNameTextView);
        userProfileEmailTV=findViewById(R.id.userProfileEmailTV);
        userProfileRoleTV=findViewById(R.id.userProfileRoleTV);
        userProfileContactTV=findViewById(R.id.userProfileContactTV);
    }
    public void setVales(){

//        messidTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                cm.setText(messidTV.getText());
//                Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
//            }
//        });
        messidTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(messidTV.getText());
                Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        profileUserNameTV.setText(sharedPreferences.getString(Constants.userName,null));
        userProfileEmailTV.setText(sharedPreferences.getString(Constants.userEmail,null));
        userProfileRoleTV.setText(sharedPreferences.getString(Constants.userRole,null));
        userProfileContactTV.setText(sharedPreferences.getString(Constants.userPhone,null));
        messidTV.setText(sharedPreferences.getString(Constants.userMessId,null));
        messNameTextView.setText(sharedPreferences.getString(Constants.userMessName,null));

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

    }
    public void openImage(){
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(512)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
//            if(requestCode==CAMERA_REQ_CODE){
//                Bitmap img=(Bitmap) data.getExtras().get("data");
//                imageView.setImageBitmap(img);
//            }
            Uri uri=data.getData();
            Bitmap bitmap= null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // initialize byte stream
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            // compress Bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            // Initialize byte array
            byte[] bytes=stream.toByteArray();
            // get base64 encoded string
            imageString= Base64.encodeToString(bytes,Base64.DEFAULT);
            Toast.makeText(getApplicationContext(),""+imageString.length(),Toast.LENGTH_SHORT).show();
            profileImageView.setImageURI(uri);
            uploadImagetoFirebase();
        }
        else{
            Toast.makeText(getApplicationContext(),"Failed to open camera",Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadImagetoFirebase() {
        HashMap<String,Object> obj=new HashMap<>();
        obj.put("image",imageString);
        firebaseFirestore.collection("images")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .set(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            showToast("Image Uploaded");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                    }
                });
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    public void setListeners(){
        dialogUpdateMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateName=profileName.getText().toString();
                updatePhone=profilePhone.getText().toString();
                if(updateName.equals("")){
                    profileName.setError("you must enter a value");
                }
                if(updatePhone.equals("")){
                    profilePhone.setError("you must enter a value");
                }
                if(updatePhone.length()<6){
                    profilePhone.setError("phone number is not valid");
                }
                if(updateName.length()<3){
                    profileName.setError("please enter a valid name");
                }
                if(!updateName.equals("") && !updatePhone.equals("") && updatePhone.length()>6 && updateName.length()>2){
                    updateProfileOnFirebase();
                    updateProfileOnMongodb();
                }

            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileName.setText(sharedPreferences.getString(Constants.userName,null));
                profilePhone.setText(sharedPreferences.getString(Constants.userPhone,null));

                profileDialog.show();
            }
        });
        profileBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        profileLeaveMessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("Are you sure you want to leave?","Leave Warning","Yes","No");
            }
        });
    }

    private void updateProfileOnMongodb() {
        new UpdateMongodb().execute();
    }

    private void updateProfileOnFirebase() {
        firebaseFirestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .update("name",updateName,"phone",updatePhone)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            showToast("Profile Updated");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                    }
                });
    }

    private void showDialog(String message, String title, String btn1,String btn2){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);

        builder.setCancelable(false)
                .setPositiveButton(btn1,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int id){
                        clearFirebaseMessData();
                        if(sharedPreferences.getString(Constants.userRole,null).equals("manager")){
                            startActivity(new Intent(getApplicationContext(),CreateMessActivity.class));
                        }
                        else{
                            startActivity(new Intent(getApplicationContext(),MemberJoinActivity.class));
                        }

                    }
                })
                .setNegativeButton(btn2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert=builder.create();
        alert.show();
    }

    private void clearFirebaseMessData() {
        DocumentReference docRef = firebaseFirestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getEmail());
        new LeaveMess().execute();
        Map<String,Object> updates = new HashMap<>();
        updates.put("messid", FieldValue.delete());
        updates.put("monthid", FieldValue.delete());

        docRef.update(updates);
    }


    class LeaveMess extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,Object> params=new HashMap<>();
            params.put("memberid",sharedPreferences.getString(Constants.userId,null));
            params.put("messid",sharedPreferences.getString(Constants.userMessId,null));
            editor.remove(Constants.userMessId);
            editor.apply();
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"leavemess",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            showToast("Image Uploaded");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast("That didn't work!");

                }
            });
            queue.add(jsObjRequest);
            return null;
        }
    }

    class UpdateMongodb extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,Object> params=new HashMap<>();
            params.put("userid",sharedPreferences.getString(Constants.userId,null));
            params.put("name",updateName);
            params.put("phone",updatePhone);

            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"updateprofile",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String name=response.getString("name");
                                String phone=response.getString("phone");
                                editor.putString(Constants.userName,name);
                                editor.putString(Constants.userPhone,phone);
                                editor.apply();
                                profileUserNameTV.setText(sharedPreferences.getString(Constants.userName,null));
                                userProfileContactTV.setText(sharedPreferences.getString(Constants.userPhone,null));
                                profileDialog.cancel();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast("That didn't work!");

                }
            });
            queue.add(jsObjRequest);
            return null;
        }
    }
}