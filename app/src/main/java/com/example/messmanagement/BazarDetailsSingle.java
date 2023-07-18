package com.example.messmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BazarDetailsSingle extends AppCompatActivity{
    CardView bazarListCard;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String description;
    double amount;
    TextView monthTextView,totalAmountTV,bazarUserName;
    EditText bazarItemET,bazarAmount;
    Button addBazarBtn;
    ArrayList<Map<String,String>> bazarlist;
    RecyclerView bazarListRecycler;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bazar_details_single);
        Intent i=getIntent();
        userid=i.getStringExtra("userid");
        sharedPreferences = this.getSharedPreferences(Constants.sharedPrefernces, MODE_PRIVATE);
        bazarListRecycler = findViewById(R.id.bazarListRecycler);
        editor = sharedPreferences.edit();
        initialize();
        bazarUserName.setText(i.getStringExtra("name"));


        loadMonthDetails();
        new LoadBazarList().execute();
    }

    private void loadMonthDetails() {
        new LoadMonthDetails().execute();
    }

    public void initialize(){
        bazarUserName=findViewById(R.id.bazarUserName);
        totalAmountTV=findViewById(R.id.totalAmountTV);
        addBazarBtn=findViewById(R.id.addBazarBtn);
        bazarItemET=findViewById(R.id.bazarItemET);
        bazarAmount=findViewById(R.id.bazarAmount);
        bazarListCard=findViewById(R.id.bazarListCard);
        monthTextView=findViewById(R.id.monthTextView);

    }




    class LoadMonthDetails extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,Object> params=new HashMap<>();
            params.put("messid",sharedPreferences.getString(Constants.userMessId,null));
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"getcurrentMonth",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String name=response.getString("month");
                                String monthid=response.getString("monthid");
                                editor.putString(Constants.currentMonthId,monthid);
                                editor.apply();
                                monthTextView.setText("Bazar History of "+name);
//                                totalAmountTV.setTextColor(Constants.getColors());
//                                bazarUserName.setTextColor(Constants.getColors());
                            } catch (Exception e) {
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


    class LoadBazarList extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {

            Map<String,Object> params=new HashMap<>();
            params.put("messid",sharedPreferences.getString(Constants.userMessId,null));
            params.put("month",sharedPreferences.getString(Constants.currentMonthId,null));
            params.put("userid",userid);
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"getbazar",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                bazarlist=new ArrayList<>();
                                double total=0;
                                JSONArray bazarArray=response.getJSONArray("bazar");

                                for (int i=0; i<bazarArray.length(); i++){
                                    JSONObject obj=bazarArray.getJSONObject(i);


                                    Map<String,String> mp=new HashMap<>();
                                    mp.put("items",obj.getString("description"));
                                    total+=obj.getDouble("amount");
                                    mp.put("amount",String.valueOf(obj.getDouble("amount")));
                                    mp.put("date",obj.getString("createdAt").split("T")[0]);
                                    bazarlist.add(mp);

                                }
                                totalAmountTV.setText("Total amount:  "+total+" ৳");
                                callAdapter();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast("That didn't work!last");

                }
            });
            queue.add(jsObjRequest);
            return null;
        }
    }

    private void callAdapter() {
        BazarListAdapter adapter=new BazarListAdapter(getApplicationContext(),bazarlist);
        bazarListRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        bazarListRecycler.setAdapter(adapter);
    }


    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

}