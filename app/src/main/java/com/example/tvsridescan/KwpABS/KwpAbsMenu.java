package com.example.tvsridescan.KwpABS;

import static com.example.tvsridescan.Library.AppVariables.VIN;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;

public class KwpAbsMenu extends AppCompatActivity {


    BluetoothBridge bridge;
    String mResponse_data = "";
    boolean aBoolean = true;
    TextView vinTextView;
    byte[] response;
    LinearLayout liveparaBtn,readDtcBtn,writeDidBtn,ioControlBtn,routineControlBtn;
    boolean holdflag = false;
    Animation animFadein1,animFadein2,animFadein3,animFadein4,animFadein5;
    MediaPlayer mp;
    LinearLayout secureAccessLayout ;

    Context context;
    private static final String TAG = "KwpAbsMenu";
    ArrayList al = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kwp_abs_menu);

        init();
    }

    private void init()
    {
        context = KwpAbsMenu.this;
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        AppVariables.Readfile(getApplicationContext(),"1604vdtcs.csv");

        loadKwpNegativeResposeAccLang(context);

        initBtns();

    }

    public  static  void loadKwpNegativeResposeAccLang(Context context)
    {
        String temp = LocaleHelper.getLanguage(context);
        if(temp!=null)
        {
            switch (temp)
            {

                case "en":
                    AppVariables.loadNegativeResponses(context,"negativeResKwp.csv");

                    break;
                case "hi":
                    AppVariables.loadNegativeResponses(context,"negativeResKwpHindi.csv");

                    break;
                default:
                    AppVariables.loadNegativeResponses(context,"negativeResKwp.csv");

                    break;
            }

        }
    }


    private void initBtns()
    {
        vinTextView =  findViewById(R.id.vin);

        liveparaBtn =  findViewById(R.id.live);
        readDtcBtn = findViewById(R.id.dtc);
        writeDidBtn =  findViewById(R.id.writedid);
        ioControlBtn = findViewById(R.id.iocontrol);
        routineControlBtn =  findViewById(R.id.routine);

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



        liveparaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),KwpAbsLp.class);
                liveparaBtn.startAnimation(animFadein1);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

                // finish();

            }
        });
        readDtcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(getApplicationContext(),KwpAbsDtcs.class);
                readDtcBtn.startAnimation(animFadein2);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

                // finish();

            }
        });


        ioControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),KwpAbsIOcontrol.class);
                ioControlBtn.startAnimation(animFadein3);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

                // finish();
            }
        });

        routineControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),KwpAbsRoutinecontrol.class);
                routineControlBtn.startAnimation(animFadein4);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });
        writeDidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),KwpAbsWrite.class);
                writeDidBtn.startAnimation(animFadein5);
                mp.start();
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

                // finish();

            }
        });

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
    protected void onResume() {
        super.onResume();
        bridge = SingleTone.getBluetoothBridge();
        if(bridge!=null)
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
                byte [] cmd = "XTSP5\r\n".getBytes();//header for ems
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }
                Log.e("res2",mResponse_data);

                cmd = "XTE0\r\n".getBytes();//header for ems
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }
                Log.e("res2",mResponse_data);

                cmd = "XTH0\r\n".getBytes();//header for ems
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }
                Log.e("res2",mResponse_data);

                cmd = "1081\r\n".getBytes(); //
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("ERROR") && !mResponse_data.contains("@!"))
                {
                    cmd = "1A90\r\n".getBytes();
                    aBoolean = false;
                    send_Request_Command(cmd);
                    Log.e("req5","1A90");
                    while (!aBoolean)
                    {
                        SystemClock.sleep(50);
                    }
                    Log.e("res5",mResponse_data);

                    if (mResponse_data != null)
                    {
                        if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!") )
                        {
                            mResponse_data = mResponse_data.replace(" ","");
                            if(mResponse_data.contains("5A90"))
                            {
                                mResponse_data = AppVariables.parseKwp1acmd(mResponse_data,"90");
                                byte[] res = DataConversion.hexStringToByteArray(mResponse_data);
                                AppVariables.VIN = new String(res);

                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        vinTextView.setText(VIN);
                                    }
                                });
                            }
                            else
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        vinTextView.setText(getString(R.string.improperresponse));
                                    }
                                });
                            }
                        }
                        else
                        {
                            cmd = "1A90\r\n".getBytes();
                            aBoolean = false;
                            send_Request_Command(cmd);
                            Log.e("req5","1A90");
                            while (!aBoolean)
                            {
                                SystemClock.sleep(50);
                            }
                            Log.e("res5",mResponse_data);
                            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                            {
                                mResponse_data = mResponse_data.replace(" ","");
                                if(mResponse_data.contains("5A90"))
                                {
                                    mResponse_data = AppVariables.parseKwp1acmd(mResponse_data,"90");
                                    byte[] res = DataConversion.hexStringToByteArray(mResponse_data);
                                    AppVariables.VIN = new String(res);

                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            vinTextView.setText(VIN);
                                        }
                                    });
                                }
                                else
                                {
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            vinTextView.setText(getString(R.string.improperresponse));
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
                                        vinTextView.setText(getString(R.string.ecunotresponding));
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
                                vinTextView.setText(getString(R.string.ecunotresponding));
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
                            vinTextView.setText(getString(R.string.ecunotresponding));
                        }
                    });
                }


            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                AppVariables.GenLogLine(e.getMessage());
            }

            super.run();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);
    }

    /*  boolean doubleBackToExitPressedOnce = false;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            BluetoothConversation.ConnectionCheck = true;
            finishAffinity();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }*/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}
