package com.example.tvsridescan.rtr1602v_1ch;

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

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

public class RTR1602v1chMenu extends AppCompatActivity {



    BluetoothBridge bridge;
    static String mResponse_data = null;
    static byte[] response;
    static boolean aBoolean = false;
    LinearLayout b1,b2,b4,b5,b6;
    LinearLayout linearLayout,vinlayout;
    byte[] cmd = new byte[0];
    Animation animFadein1,animFadein2,animFadein3,animFadein4,animFadein5,animFadein6;
    MediaPlayer mp;
    TextView tv;
    private static  final  String TAG ="RTR1602v1ch";
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtr1602v1ch_menu);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle("Anti-Lock Braking System");
        context = RTR1602v1chMenu.this;
        bridge = SingleTone.getBluetoothBridge();
        Resposne();
        AppVariables.Readfile(getApplicationContext(),"1602v1chdtcs.csv");
        loadKwpNegativeResposeAccLang(context);

       // AppVariables.loadNegativeResponses(context,"negativeResAbsBosch.csv");

        //AppVariables.Readfile(getApplicationContext(),"dtcshrtdesc.csv");

        linearLayout =  findViewById(R.id.ll);
        vinlayout = findViewById(R.id.vinlayout);
        tv=  findViewById(R.id.vin);



        b1 =  findViewById(R.id.live);
        b2 = findViewById(R.id.dtc);
        b4 = findViewById(R.id.writedid);
        b5 = findViewById(R.id.ecuid);
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
                Intent i = new Intent(context, RTR1602v1chLiveParams.class);
                b1.startAnimation(animFadein1);
                mp.start();
                startActivity(i);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, RTR1602v1chDtc.class);
                b2.startAnimation(animFadein2);
                mp.start();
                startActivity(i);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, RTR1602v1chWriteParam.class);
                b4.startAnimation(animFadein4);
                mp.start();
                startActivity(i);
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(context, RTR1602v1chEcuParams.class);
                b5.startAnimation(animFadein5);
                mp.start();
                startActivity(i);
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, RTR1602v1chRoutine.class);
                b6.startAnimation(animFadein6);
                mp.start();
                startActivity(i);
            }
        });

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
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }


                cmd = "XTSH7E1\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }

                cmd = "XTRH7E9\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
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

                cmd = "1003\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                {
                    mResponse_data = mResponse_data.replace(" ","");
                    if(mResponse_data.contains("50"))
                    {
                        //Firing VIN command
                        cmd = "2290\r\n".getBytes();
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
                                if(mResponse_data.contains("6290"))
                                {
                                    mResponse_data = AppVariables.parseBosch19AAResposne(mResponse_data);

                                    byte[] res = DataConversion.hexStringToByteArray(mResponse_data);

                                    final String vin = new String(res);
                                    AppVariables.VIN = vin;

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
                                    if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                                    {
                                        String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                        //String[] val = data.split(",");
                                        final String finalval  = data.replace(",", " ");
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run() {
                                                vinlayout.setVisibility(View.VISIBLE);
                                                tv.setText(finalval);
                                            }
                                        });
                                    }
                                    else
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run() {
                                                vinlayout.setVisibility(View.VISIBLE);
                                                tv.setText(getString(R.string.improperresponse));
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
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run() {
                                    vinlayout.setVisibility(View.VISIBLE);
                                    tv.setText(getString(R.string.ecunotresponding));
                                }
                            });
                        }
                    }
                    else
                    {
                        if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                        {
                            String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                            //String[] val = data.split(",");
                            final String finalval  = data.replace(",", " ");
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run() {
                                   vinlayout.setVisibility(View.VISIBLE);
                                   tv.setText(finalval);
                                }
                            });
                        }
                        else
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run() {
                                   vinlayout.setVisibility(View.VISIBLE);
                                   tv.setText(getString(R.string.improperresponse));
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

            }
            @Override
            public void Connected(String str)
            {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.out,R.anim.in);

        bridge = SingleTone.getBluetoothBridge();
        Resposne();
        SetHeader setHeader = new SetHeader();
        setHeader.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


    public  static  void loadKwpNegativeResposeAccLang(Context context)
    {
        String temp = LocaleHelper.getLanguage(context);
        if(temp!=null)
        {
            switch (temp)
            {

                case "en":
                    AppVariables.loadNegativeResponses(context,"negativeResAbsBosch.csv");

                    break;
                case "hi":
                    AppVariables.loadNegativeResponses(context,"negativeResAbsBoschHindi.csv");

                    break;
                default:
                    AppVariables.loadNegativeResponses(context,"negativeResAbsBosch.csv");

                    break;
            }

        }
    }

}
