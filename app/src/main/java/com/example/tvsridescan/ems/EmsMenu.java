package com.example.tvsridescan.ems;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.LiveParameterCat;
import com.example.tvsridescan.R;
import com.example.tvsridescan.SecureAccess;
import com.example.tvsridescan.StartDiagnosis;
import com.example.tvsridescan.connection.ConnectionInterrupt;

public class EmsMenu extends AppCompatActivity
{
    BluetoothBridge bridge;
    static String mResponse_data = null;
    static boolean aBoolean = true;
    TextView tv;
    static byte[] response;
    LinearLayout b1,b2,b3,b4,b6;
    LinearLayout linearLayout;
    boolean holdflag = false;
    Animation animFadein1,animFadein2,animFadein3,animFadein4,animFadein5,animFadein6;
    MediaPlayer mp;
    LinearLayout secureAccessLayout ;
    Context context;
    private static  final String TAG = "EmsMenu.this";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ems_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

       // AppVariables.loadNegativeResponses(EmsMenu.this,"negres.csv");
        StartDiagnosis.LoadCANNegtiveRespnseAcctoLanguage(getApplicationContext());
        if(AppVariables.BikeModel==1)
        {
            AppVariables.Readfile(getApplicationContext(),"dtcshrtdesc.csv");
        }
        else
        {
            AppVariables.Readfile(getApplicationContext(),"dtcs2.csv");
        }


        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        Log.e(TAG, "entered");
        tv =  findViewById(R.id.vin);
        b1 =  findViewById(R.id.live);
        b2 = findViewById(R.id.dtc);

        b3 =  findViewById(R.id.writedid);
        b4 = findViewById(R.id.iocontrol);
        secureAccessLayout = findViewById(R.id.secureaccseslayout);
        b6 =  findViewById(R.id.secure);


        if(AppVariables.BikeModel==1)
        {
            // b5.setVisibility(View.INVISIBLE);
            secureAccessLayout.setVisibility(View.GONE);
        }
        else
        if(AppVariables.BikeModel==2)
        {
            //b5.setVisibility(View.INVISIBLE);
            secureAccessLayout.setVisibility(View.VISIBLE);
        }



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

        linearLayout =  findViewById(R.id.ll);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LiveParameterCat.cat =1;
                Intent i = new Intent(getApplicationContext(),LiveParameterCat.class);
                b1.startAnimation(animFadein1);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

                // finish();

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),EmsDtcs.class);
                b2.startAnimation(animFadein2);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

                // finish();

            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),EMSWriteDIDs.class);
                b3.startAnimation(animFadein3);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

                // finish();

            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),EMSIOControl.class);
                b4.startAnimation(animFadein4);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

                // finish();

            }
        });


        /*b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(getApplicationContext(),PDIActivity.class);
                b5.startAnimation(animFadein5);
                mp.start();
                startActivity(i);
               // finish();
             //   finish();


            }
        });
*/
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    Intent i = new Intent(getApplicationContext(),SecureAccess.class);
                    b6.startAnimation(animFadein6);
                    mp.start();
                    startActivity(i);
                    overridePendingTransition(R.anim.out,R.anim.in);

                    // finish();
                }
                else
                {
                    Toast.makeText(EmsMenu.this, getString(R.string.interntUnavail), Toast.LENGTH_SHORT).show();
                }

            }
        });

       // SendCmd sendCmd = new SendCmd();
       // sendCmd.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bridge = SingleTone.getBluetoothBridge();
        Resposne();
        SendCmd sendCmd = new SendCmd();
        sendCmd.start();
    }

    public class SendCmd extends Thread {

        @Override
        public void run()
        {
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


                cmd = "22F190\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
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

                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run() {
                                    tv.setText(vin);
                                    linearLayout.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                    }
                    else
                    {
                        cmd = "22F190\r\n".getBytes();
                        aBoolean = false;
                        send_Request_Command(cmd);
                        while (!aBoolean)
                        {
                            SystemClock.sleep(10);
                        }

                        if (mResponse_data != null) {
                            if (!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!")) {
                                mResponse_data = mResponse_data.replace(" ","");
                                if(mResponse_data.contains("F190"))
                                {
                                    mResponse_data = AppVariables.parsecmd(mResponse_data,"F190");

                                    byte[] res = DataConversion.hexStringToByteArray(mResponse_data);

                                    final String vin = new String(res);
                                    AppVariables.VIN = vin;

                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            tv.setText(vin);
                                            linearLayout.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                                else
                                {
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            tv.setText(getString(R.string.improperresponse));
                                            linearLayout.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                            else
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        tv.setText(getString(R.string.ecunotresponding));
                                        linearLayout.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                        else
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run() {
                                    tv.setText(getString(R.string.ecunotresponding));
                                    linearLayout.setVisibility(View.VISIBLE);
                                }
                            });
                        }


                    }

                    aBoolean = true;
                } else {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            tv.setText(getString(R.string.ecunotresponding));
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                    });
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("error", e.getMessage());
            }

            super.run();
        }
    }

    @Override
    protected void onDestroy()
    {
        holdflag = true;
      // aBoolean = true;
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

