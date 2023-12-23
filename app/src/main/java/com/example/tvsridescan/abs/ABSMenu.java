package com.example.tvsridescan.abs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.LiveParameterCat;
import com.example.tvsridescan.R;
import com.example.tvsridescan.StartDiagnosis;
import com.example.tvsridescan.connection.ConnectionInterrupt;

public class ABSMenu extends Activity
{
    BluetoothBridge bridge;
    static String mResponse_data = null;
    static byte[] response;
    static boolean aBoolean = false;
    LinearLayout b1,b2,b4,b5,b6;
    LinearLayout linearLayout,vinlayout;
    byte[] cmd = new byte[0];
    boolean holdflag = false;
    Animation animFadein1,animFadein2,animFadein3,animFadein4,animFadein5,animFadein6;
    MediaPlayer mp;
    TextView tv;
    private static  final  String TAG ="ABSMenu";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absmenu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle("Anti-Lock Braking System");
        bridge = SingleTone.getBluetoothBridge();
        Resposne();
        StartDiagnosis.LoadCANNegtiveRespnseAcctoLanguage(getApplicationContext());
       // AppVariables.loadNegativeResponses(ABSMenu.this,"negres.csv");

        AppVariables.Readfile(getApplicationContext(),"dtcshrtdesc.csv");

        linearLayout =  findViewById(R.id.ll);
        vinlayout = findViewById(R.id.vinlayout);
        vinlayout.setVisibility(View.GONE);

        tv=  findViewById(R.id.vin);



        b1 =  findViewById(R.id.live);
        b2 = findViewById(R.id.dtc);
        b4 = findViewById(R.id.writedid);
        b5 = findViewById(R.id.iocontrol);
        b6 = findViewById(R.id.routine);


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

        animFadein6 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LiveParameterCat.cat =2;
                Intent i = new Intent(ABSMenu.this, LiveParameterCat.class);
                b1.startAnimation(animFadein1);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ABSMenu.this, ABSDtcs.class);
                b2.startAnimation(animFadein2);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ABSMenu.this, ABSWriteDID.class);
                b4.startAnimation(animFadein4);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ABSMenu.this, ABSIOControl.class);
                b5.startAnimation(animFadein5);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ABSMenu.this, ABSRoutinecontrol.class);
                b6.startAnimation(animFadein6);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        bridge = SingleTone.getBluetoothBridge();
        if(bridge!=null)
        {
            Resposne();
            SetHeader setHeader = new SetHeader();
            setHeader.start();
        }
        else
        {
            Intent i = new Intent(getApplicationContext(), ConnectionInterrupt.class);
            startActivity(i);
            overridePendingTransition(R.anim.out,R.anim.in);

        }

    }

    public class SetHeader extends Thread
    {

        @Override
        public void run()
        {
            try
            {

                cmd = "XTSP6\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);

                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }

                cmd = "XTEA1\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);

                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }



                cmd = "XTSH6F0\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);

                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }

                cmd = "XTRH629\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }

                aBoolean = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vinlayout.setVisibility(View.GONE);
                    }
                });
                ///Firing VIN command

                cmd = "22F190\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(50);
                }

                if (mResponse_data != null)
                {
                    if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                    {
                        mResponse_data = mResponse_data.replace(" ","");
                        if(mResponse_data.contains("F190"))
                        {
                            mResponse_data = AppVariables.parsecmd(mResponse_data,"F190");

                            byte[] res = DataConversion.hexStringToByteArray(mResponse_data);

                            final String vin = new String(res);
                            AppVariables.VIN = vin;



                            if(AppVariables.BikeModel!=1)
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        vinlayout.setVisibility(View.VISIBLE);
                                        tv.setText(vin);
                                    }
                                });
                            }
                            else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        vinlayout.setVisibility(View.GONE);
                                    }
                                });
                            }


                        }

                    }
                    else
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                vinlayout.setVisibility(View.VISIBLE);
                                tv.setText(getString(R.string.ecunotresponding));
                            }
                        });
                    }
                    aBoolean = true;

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            vinlayout.setVisibility(View.GONE);

                        }
                    });
                }


            } catch (Exception e) {
                e.printStackTrace();
                AppVariables.GenLogLine(TAG+e.getMessage());
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        if(vinlayout!=null && tv!=null)
                        {
                            vinlayout.setVisibility(View.VISIBLE);
                            tv.setText(getString(R.string.ecunotresponding));
                        }

                    }
                });
                Log.e(TAG, e.getMessage());
            }
            super.run();
        }
    }

    @Override
    protected void onDestroy()
    {
        holdflag = true;
        super.onDestroy();
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
