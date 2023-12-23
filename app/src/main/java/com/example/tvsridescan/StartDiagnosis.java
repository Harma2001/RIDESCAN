package com.example.tvsridescan;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.BluetoothConversation;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.abs.ABSMenu;
import com.example.tvsridescan.connection.ConnectionInterrupt;
import com.example.tvsridescan.ems.EmsMenu;
import com.example.tvsridescan.icu.ClusterMenu;

public class StartDiagnosis extends AppCompatActivity
{

    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;

    LinearLayout ll1,ll2,ll3,ll4;
    Animation animFadein1,animFadein2,animFadein3,animFadein4,animFadein5;
    Animation animFadein11,animFadein22,animFadein33;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_diagnosis);
        BluetoothConversation.reshandle =4;
        if(AppVariables.BikeModel==1)
        {
            AppVariables.Readfile(getApplicationContext(),"dtcshrtdesc.csv");
        }
        else
        {
            AppVariables.Readfile(getApplicationContext(),"dtcs2.csv");
        }

        LoadCANNegtiveRespnseAcctoLanguage (getApplicationContext());

        //  AppVariables.takeScreenshot();
        BluetoothConversation.StartTimeDuration();
        bridge = SingleTone.getBluetoothBridge();



        Resposne();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle("Vehicle Diagnostics");

        mp = MediaPlayer.create(this, R.raw.buttonclick);

        animFadein1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein2 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);

        animFadein3 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein4 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein5 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);

        animFadein11 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animFadein22 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        animFadein33 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        try
        {
            // ll = (LinearLayout) findViewById(R.id.ll);
            ll1 =  findViewById(R.id.ems);
            ll2 =  findViewById(R.id.abs);
            ll3 =  findViewById(R.id.cluster);
            ll4 =  findViewById(R.id.secure);
            ll1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ll1.startAnimation(animFadein1);
                    mp.start();
                    startActivity(new Intent(getApplicationContext(),EmsMenu.class));
                    overridePendingTransition(R.anim.out,R.anim.in);

                }
            });
            ll2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ll2.startAnimation(animFadein2);
                    mp.start();
                    startActivity(new Intent(getApplicationContext(),ABSMenu.class));
                    overridePendingTransition(R.anim.out,R.anim.in);

                }
            });
            ll3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ll3.startAnimation(animFadein3);
                    mp.start();
                    startActivity(new Intent(getApplicationContext(),ClusterMenu.class));
                    overridePendingTransition(R.anim.out,R.anim.in);

                }
            });

            ll4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(AppVariables.CheckInternet(getApplicationContext()))
                    {
                        ll4.startAnimation(animFadein4);
                        mp.start();
                        startActivity(new Intent(getApplicationContext(),SecureAccess.class));
                        overridePendingTransition(R.anim.out,R.anim.in);

                    }
                    else
                    {
                        Toast.makeText(StartDiagnosis.this, getString(R.string.interntUnavail), Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void LoadCANNegtiveRespnseAcctoLanguage(Context context)
    {
        String temp = LocaleHelper.getLanguage(context);
        if(temp!=null)
        {
            switch (temp)
            {
                case "en":
                    AppVariables.loadNegativeResponses(context,"negres.csv");
                    break;
                case "hi":
                    AppVariables.loadNegativeResponses(context,"negresHindi.csv");
                    break;

                default:
                    AppVariables.loadNegativeResponses(context,"negres.csv");
                    break;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bridge = SingleTone.getBluetoothBridge();
        if(bridge!=null)
        {
            Resposne();
            SendCommands sendCommands = new SendCommands();
            sendCommands.start();
        }
        else
        {
            Intent i = new Intent(getApplicationContext(), ConnectionInterrupt.class);
            startActivity(i);
            overridePendingTransition(R.anim.out,R.anim.in);

        }
    }

    class SendCommands extends  Thread{
        @Override
        public void run() {
            super.run();
            try
            {

                byte[] cmd = "xtsp6\r\n".getBytes();//header for ems
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }


                cmd = "xth0\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }


                cmd = "xte0\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }

                cmd = "XTEA0\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }


                cmd = "xtsh7e0\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }

                cmd = "xtrh7e8\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }


               cmd = "XTTM500\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }



                aBoolean = false;

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("error", e.getMessage());
            }

        }
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    public void send_Request_Command(byte[] arr)
    {
        bridge.SendCmd(arr);
    }
    public void Resposne()
    {
        bridge.MethResponseInt(new BluetoothBridge.ResponseInterface()
        {
            @Override
            public void ResponseMeth(byte[] arr, String str)
            {
                response = arr;
                mResponse_data = str;
                aBoolean = true;
            }
            @Override
            public void ConnectionLost()
            {
                Intent i = new Intent(getApplicationContext(), ConnectionInterrupt.class);
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

            }
            @Override
            public void Connected(String str)
            {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
/*

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
*/
    boolean doubleBackToExitPressedOnce = false;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            finish();
            overridePendingTransition(R.anim.out,R.anim.in);

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.pleaseclickbackagain), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
