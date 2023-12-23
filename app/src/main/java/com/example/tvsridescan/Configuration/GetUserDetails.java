package com.example.tvsridescan.Configuration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.HttpCommunication;
import com.example.tvsridescan.R;
import com.example.tvsridescan.SplashScreen;
import com.example.tvsridescan.adapters.CustomAdapter_Config;
import com.example.tvsridescan.adapters.DataModel_Config;
import com.example.tvsridescan.connection.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GetUserDetails extends AppCompatActivity
{

    ListView listView;
    String[] arr = null;
    boolean aBoolean = true;
    ArrayList<DataModel_Config> dataModels;
    private static CustomAdapter_Config adapter;
    TextView txtname,txtemail,txtaddr;
    public static  int gatenumber = 0;
    LinearLayout linearLayout;
    TextView msg;
    HttpCommunication httpCommunication;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        setTitle("License Details");

        httpCommunication = new HttpCommunication();

        txtname = (TextView) findViewById(R.id.name);
        txtemail = (TextView) findViewById(R.id.email);
        txtaddr = (TextView) findViewById(R.id.addr);
        linearLayout = (LinearLayout) findViewById(R.id.ll1);
        listView = (ListView) findViewById(R.id.lv);

        msg = (TextView) findViewById(R.id.msg);
        msg.setText(getString(R.string.checkingdetails));
        Button b1 =  (Button) findViewById(R.id.ok);
        Button b2 =  (Button) findViewById(R.id.cncl);

        //verifying email id .
        PostData postData = new PostData();
        postData.start();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set mySet = new HashSet(Arrays.asList(arr));

                AppVariables.StoreSet(getApplicationContext(),mySet);

                if(gatenumber==0)
                {
                    HttpCommunication.PostUrl =HttpCommunication.Get_OTP;
                    startActivity(new Intent(getApplicationContext(),ChangePassword.class));
                    overridePendingTransition(R.anim.out,R.anim.in);

                }
                else
                if(gatenumber==1)
                {
                    startActivity(new Intent(getApplicationContext(),SplashScreen.class));
                    overridePendingTransition(R.anim.out,R.anim.in);

                }

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ValvcisnoActivity.class);
                finish();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });

    }

    public void UI()
    {
        dataModels = new ArrayList<>();
        String[] arr2;
        for(int i =0;i<arr.length;i++)
        {
            arr2 = arr[i].split(",");
            dataModels.add(new DataModel_Config(arr2[0], arr2[1]));
        }

        adapter = new CustomAdapter_Config(dataModels, getApplicationContext());
        listView.setAdapter(adapter);
    }



    public class PostData extends Thread
    {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msg.setText(getString(R.string.verifyingemail));
                }
            });

            httpCommunication.JSONDATA="{\"api_key\" : \""+httpCommunication.API_KEY+"\",\"email\" : \""+AppVariables.email_id+"\"}" ;
            httpCommunication.PostUrl =httpCommunication.Verify_Email;
            String Getop = httpCommunication.POSTDATA();


            try
            {
                JSONObject object = new JSONObject(Getop);
                httpCommunication.Status = object.getString("status");
                if(httpCommunication.Status.equals("Success"))
                {
                    JSONObject object1 = new JSONObject(String.valueOf(object.getJSONObject("data")));
                    Log.e("data", String.valueOf(object1));
                    HttpCommunication.Username = object1.getString("name");
                    txtname.setText(HttpCommunication.Username);

                    HttpCommunication.email_id = object1.getString("email");
                    txtemail.setText(HttpCommunication.email_id);

                    HttpCommunication.Address = object1.getString("address");
                    txtaddr.setText(HttpCommunication.Address);

                    HttpCommunication.is_Verfied = object1.getString("is_verified");
                    HttpCommunication.Device.clear();
                    JSONArray jsonArray = object1.getJSONArray("devices");
                    arr = new String[jsonArray.length()];
                    int count = jsonArray.length();
                    Log.e("count", String.valueOf(count));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String sno = jsonobject.getString("device_id");
                        String expdate = jsonobject.getString("expire_date");
                        HttpCommunication.Device.add(sno+";"+expdate);
                        arr[i]=sno+";"+expdate;
                    }


                    if(HttpCommunication.is_Verfied.equals("true")) // Verified User
                    {
                        Set mySet = new HashSet(Arrays.asList(arr));

                        AppVariables.StoreSet(getApplicationContext(),mySet);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.out,R.anim.in);
                            }
                        });


                    }
                    else // New User or Non Verified User
                    {
                        dataModels = new ArrayList<>();
                        for(int i =0;i<HttpCommunication.Device.size();i++)
                        {
                            String[] arr2 = HttpCommunication.Device.get(i).toString().split(";");
                            dataModels.add(new DataModel_Config(arr2[0], arr2[1]));
                        }

                        adapter = new CustomAdapter_Config(dataModels, getApplicationContext());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setAdapter(adapter);
                                linearLayout.setVisibility(View.VISIBLE);
                                msg.setVisibility(View.INVISIBLE);
                            }
                        });

                    }


                }
                else
                if(httpCommunication.Status.equals("false"))
                {
                    final String Err_MSG = object.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg.setText(Err_MSG);
                        }
                    });

                    Log.e("False",Err_MSG);
                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Toast.makeText(GetUserDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.out,R.anim.in);
                    }
                });

            }


            Log.e("output","Getop");

            super.run();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
