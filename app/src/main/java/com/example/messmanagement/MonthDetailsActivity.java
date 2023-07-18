package com.example.messmanagement;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

public class MonthDetailsActivity extends AppCompatActivity implements RecyclerInterface{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView totalBazarTV,currentMonthTV;
    RecyclerView bazarUsersRecycleView;
    Button notificationTriggerBtn;
    ArrayList<Map<String,String>> userBazarList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_details);
        sharedPreferences=this.getSharedPreferences(Constants.sharedPrefernces,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        initialize();
        new GetMonthDetails().execute();

        notificationTriggerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("hi");
                NotificationManager notif=(NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
                Notification notify=new Notification.Builder
                        (getApplicationContext()).setContentTitle("Mon valo nei").setContentText("body").
                        setContentTitle("subject").setSmallIcon(R.drawable.booking).build();

                notify.flags = Notification.FLAG_AUTO_CANCEL;
                notif.notify(0, notify);
            }
        });
    }
    public void initialize(){
        notificationTriggerBtn=findViewById(R.id.notificationTriggerBtn);
        totalBazarTV=findViewById(R.id.totalBazarTV);
        currentMonthTV=findViewById(R.id.currentMonthTV);
        bazarUsersRecycleView=findViewById(R.id.bazarUsersRecycleView);
    }
    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }



    class GetMonthDetails extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,Object> params=new HashMap<>();
            params.put("messid",sharedPreferences.getString(Constants.userMessId,null));
            params.put("month",sharedPreferences.getString(Constants.currentMonthId,null));


            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"getmonthdetails",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                totalBazarTV.setText("Total Bazar: "+response.getString("totalBazar")+" ৳");
                                currentMonthTV.setText("Current Month: "+response.getString("current_month"));

                                JSONArray array=response.getJSONArray("userBazarArray");

                                userBazarList=new ArrayList<>();
                                for (int i=0; i<array.length(); i++){
                                    Map<String,String> mp=new HashMap<>();
                                    JSONObject obj=array.getJSONObject(i);
                                    mp.put("name",obj.getJSONObject("_id").getString("name"));
                                    mp.put("userid",obj.getJSONObject("_id").getString("_id"));
                                    mp.put("amount",obj.getString("userTotalBazar"));
                                    userBazarList.add(mp);

                                }
                                showAdapter(userBazarList);
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

    private void showAdapter(ArrayList<Map<String,String>> userBazarList) {

        BazarUserRecyclerAdapter adapter=new BazarUserRecyclerAdapter(getApplicationContext(),userBazarList, this);
        bazarUsersRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        bazarUsersRecycleView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(int position) {

        Intent intent=new Intent(this,BazarDetailsSingle.class);
        intent.putExtra("userid",userBazarList.get(position).get("userid"));
        intent.putExtra("name",userBazarList.get(position).get("name"));
        startActivity(intent);
    }
}