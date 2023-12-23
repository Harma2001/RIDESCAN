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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

public class ClusterRoutineControl extends Activity
{

    BluetoothBridge bridge;
    static String mResponse_data = null;
    static byte[] response;
    static boolean aBoolean = false;
    byte[] cmd = new byte[0];
    String cmddata = null;
    boolean holdflag = false;
    ToggleButton toggleButton1;
    Dialog dialog;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_routine_control);
        context = ClusterRoutineControl.this;
        ImageView bikeview = (ImageView) findViewById(R.id.bikeview);
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
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        toggleButton1 = findViewById(R.id.toggleButton1);
        toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(!toggleButton1.isChecked())
                {
                    cmddata = "3102B004";
                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                }
                else
                {
                    cmddata = "3101B004";
                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                }
            }
        });

    }

    public class WriteCmd extends Thread
    {
        @Override
        public void run()
        {
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
            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
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
                if(mResponse_data.substring(0,2).equals("71"))
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
                                AppVariables.ShowDialog(dialog,arr[1],true,0);
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                AppVariables.ShowDialog(dialog,getString(R.string.negativeresponse),true,0);
                            }
                        });
                    }
                }
            }
            else
            {
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
