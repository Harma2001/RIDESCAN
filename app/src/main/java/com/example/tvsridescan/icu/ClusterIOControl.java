package com.example.tvsridescan.icu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

public class ClusterIOControl extends Activity
{

    BluetoothBridge bridge;
    static String mResponse_data = null;
    static byte[] response;
    static boolean aBoolean = false;
    byte[] cmd = new byte[0];
    String cmddata = null;
    Button retbtn1,retbtn2;
    ToggleButton toggleButton1,toggleButton2;

    String[] cmdstart = {"2FE1B303010080","2FE1B303000140"};
    String[] cmdstop = {"2FE1B303000000","2FE1B303000000"};
    String[] cmdreturn = {"2FE1B30000","2FE1B30000"};
    boolean holdflag = false;
    Dialog dialog;
    Animation blinkanimation1,blinkanimation2;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_iocontrol);
        context = ClusterIOControl.this;
        ImageView bikeview =  findViewById(R.id.bikeview);
        if(AppVariables.BikeModel ==1)
        {
            bikeview.setImageResource(R.drawable.ic_biketopview);
        }
        else
        {
            bikeview.setImageResource(R.drawable.ic_biketopview);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dialog = new Dialog(this);
        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);
        bridge = SingleTone.getBluetoothBridge();
        Resposne();
        blinkanimation1 =  AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        blinkanimation2 =  AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleButton1.isChecked())
                {
                    cmddata = cmdstart[1-1];
                    dialog = new Dialog(ClusterIOControl.this);

                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                    toggleButton1.setAlpha(1f);
                    toggleButton1.startAnimation(blinkanimation1);

                }
                else
                {
                    cmddata = cmdstop[1-1];
                    dialog = new Dialog(ClusterIOControl.this);

                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                    toggleButton1.setAlpha(0.2f);
                    toggleButton1.clearAnimation();
                }
            }
        });
        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleButton2.isChecked())
                {
                    cmddata = cmdstart[2-1];
                    dialog = new Dialog(ClusterIOControl.this);

                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();

                    toggleButton2.setAlpha(1f);
                    toggleButton2.startAnimation(blinkanimation2);

                }
                else
                {
                    cmddata = cmdstop[2-1];
                    dialog = new Dialog(ClusterIOControl.this);

                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                    toggleButton2.setAlpha(0.2f);
                    toggleButton2.clearAnimation();

                }
            }
        });

    }
    public class WriteCmd extends Thread
    {
        @Override
        public void run()
        {
            if(dialog.isShowing())
            {
                dialog.dismiss();
            }
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                }
            });
            SystemClock.sleep(1000);
            cmd = "1003\r\n".getBytes(); //
            aBoolean = false;
            send_Request_Command(cmd);
            while (!aBoolean && !holdflag)
            {
                SystemClock.sleep(10);
            }
            if(!mResponse_data.contains("NO DATA") &&!mResponse_data.contains("@!"))
            {
                cmd = (cmddata+"\r\n").getBytes(); //
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }

                Log.e("cmd res",mResponse_data);
                mResponse_data.replace(" ","");
                if(mResponse_data.contains("6F"))
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void run() {
                                    AppVariables.ShowDialog(dialog,getString(R.string.success),true,1);
                                }
                            });
                        }
                    });
                }
                else
                if(mResponse_data.contains("62"))
                {
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog,getString(R.string.success),true,1);
                        }
                    });
                }
                else
                {
                    mResponse_data = mResponse_data.replace(" ","");

                    if (mResponse_data.contains("7F") && mResponse_data.length() == 6) {
                        final String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                        final String[] arr = data.split(",");
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                AppVariables.ShowDialog(dialog,arr[1].toString(),true,0);
                            }
                        });

                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                AppVariables.ShowDialog(dialog,getString(R.string.improperresponse),true,0);
                            }
                        });
                    }
                }
            }
            else
            {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog,getString(R.string.ecunotresponding),true,0);
                    }
                });
            }



            super.run();
        }
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
    protected void onDestroy()
    {
        holdflag = true;
        super.onDestroy();
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

