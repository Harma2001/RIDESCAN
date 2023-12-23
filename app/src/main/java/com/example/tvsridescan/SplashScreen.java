package com.example.tvsridescan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.tvsridescan.Configuration.ChooseLangActivity;
import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothConversation;
import com.example.tvsridescan.Library.HttpCommunication;
import com.example.tvsridescan.connection.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.supercharge.shimmerlayout.ShimmerLayout;


public class SplashScreen extends AppCompatActivity
{
    Context context;
    private static final String TAG ="SplashScreen";
    Intent i;
    BluetoothAdapter bluetoothAdapter;
    TextView textView;
    LinearLayout ll1;
    ShimmerLayout shimmerLayout;
    static  boolean isFirstRun;
    public static SharedPreferences firstRunPrefs;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

            context = SplashScreen.this;
            setContentView(R.layout.activity_splash_screen);

            ll1 = findViewById(R.id.llint);
            TextView tv = findViewById(R.id.tv);
            shimmerLayout = findViewById(R.id.splashshim);
            BluetoothConversation.ConnectionCheck = false;
            BluetoothConversation.Sessioncount = 0;
            Typeface typeface = Typeface.createFromAsset(getAssets(), "ridescan.ttf");
            tv.setTypeface(typeface);

            BluetoothConversation.FlagLog = true;


            Animation animMove =  AnimationUtils.loadAnimation(context, R.anim.fade_in);
            final Animation animMovel =  AnimationUtils.loadAnimation(getApplicationContext(), R.anim.speedl);
            tv.setAnimation(animMove);
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(!bluetoothAdapter.isEnabled())
            {
                bluetoothAdapter.enable();
            }
             shimmerLayout.startShimmerAnimation();


            AppVariables.RetSSCount(getApplicationContext());
            AppVariables.RetEmail(getApplicationContext());
            AppVariables.pwd = AppVariables.Retpwd(getApplicationContext());


            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                if(!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    /// Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show();
                    // finishAffinity();
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},3);
                }
            }
            firstRunPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            isFirstRun = firstRunPrefs.getBoolean("FIRSTRUN", true);
            if(isFirstRun)
            {
               Intent intent = new Intent(SplashScreen.this,ChooseLangActivity.class);
               startActivity(intent);
               overridePendingTransition(R.anim.out,R.anim.in);
            }
            else
            {
                if(!AppVariables.email_id.equals(""))
                {
                    if(CheckSessionDate())
                    {
                        if(AppVariables.CheckInternet(getApplicationContext()))
                        {
                            GetDeviceDetails  getDeviceDetails = new GetDeviceDetails();
                            getDeviceDetails.start();
                        }
                        else
                        {
                            ll1.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.out,R.anim.in);
                            }
                        },4500);

                    }
                }
                else
                {
                    if(AppVariables.CheckInternet(getApplicationContext()))
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.out,R.anim.in);

                            }
                        },4500);
                    }
                    else
                    {
                        ll1.setVisibility(View.VISIBLE);
                    }
                }
            }




    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finishAffinity();
                }
            }
            break;
        }
    }

    public class GetDeviceDetails  extends Thread
    {
        @Override
        public void run()
        {
            super.run();

            try
            {
                HttpCommunication.JSONDATA="{\"api_key\" : \""+HttpCommunication.API_KEY+"\",\"email\" : \""+AppVariables.email_id+"\"}" ;
                HttpCommunication.PostUrl =HttpCommunication.Verify_Email;
                String Getop = HttpCommunication.POSTDATA();

                if(Getop!=null)
                {
                    JSONObject object = new JSONObject(Getop);
                    HttpCommunication.Status = object.getString("status");
                    if(HttpCommunication.Status.equals("Success"))
                    {
                        JSONObject object1 = new JSONObject(String.valueOf(object.getJSONObject("data")));
                        Log.e("data", String.valueOf(object1));

                        HttpCommunication.is_Verfied = object1.getString("is_verified");
                        JSONArray jsonArray = object1.getJSONArray("devices");
                        int count = jsonArray.length();
                        HttpCommunication.Device.clear();
                        Log.e("count", String.valueOf(count));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                            String sno = jsonobject.getString("device_id");
                            String expdate = jsonobject.getString("expire_date");
                            HttpCommunication.Device.add(sno+";"+expdate);
                        }
                        i = new Intent(getApplicationContext(), LoginActivity.class);                       //Pucshing to Login Activiy
                        SystemClock.sleep(3000);
                        startActivity(i);
                        overridePendingTransition(R.anim.out,R.anim.in);

                    }
                    else
                    if(HttpCommunication.Status.equals("false"))
                    {
                        final String Err_MSG = object.getString("message");
                        Log.e("False",Err_MSG);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SplashScreen.this, Err_MSG, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,getString(R.string.interntUnavail), Toast.LENGTH_SHORT).show();
                            ll1.setVisibility(View.VISIBLE);

                        }
                    });

                }


            }
            catch (JSONException e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,getString(R.string.interntUnavail), Toast.LENGTH_SHORT).show();
                        ll1.setVisibility(View.VISIBLE);

                    }
                });
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            Log.e("output","Getop");


        }
    }
    public static boolean CheckSessionDate()
    {
        boolean flag ;
        if(AppVariables.Sessioncount<=10)
        {
            flag = false;
        }
        else
        {
            flag = true;
        }
        return flag;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}
