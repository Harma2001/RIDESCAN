package com.example.tvsridescan.abs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.BluetoothConversation;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

public class ABSIOControl extends Activity
{

    BluetoothBridge bridge;
    Dialog dialog;
    static String mResponse_data = null;
    static boolean aBoolean = true;
    static byte[] response;

    byte[] end = "\r\n".getBytes();
    byte[] cmd = new byte[0];
    String cmddata = null;
    String writedata = null;
    boolean holdflag = false;

    ToggleButton toggleButton1,toggleButton2,toggleButton3,toggleButton4,toggleButton5;

    String[] cmdon ={"2FE0DA0301","2FE0DB03010000000000FC","2FE0DB03000100000000FC","2FE0DB03000001000000FC","2FE0DB03000000000100FC"}; //2F E0 DB 03 00 00 00 01 00 FC
    //,
    String[] cmdoff = {"2FE0DA0300","2FE0DB03000000000000FC","2FE0DB03000000000000FC","2FE0DB03000000000000FC","2FE0DB03000000000000FC"}; //2F E0 DB 03 00 00 00 00 00 00 FC
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absiocontrol);
        context =ABSIOControl.this;
        ImageView bikeview =  findViewById(R.id.bikeview);
        if(AppVariables.BikeModel ==1)
        {
            bikeview.setImageResource(R.drawable.ic_skeletontop);
        }
        else
        {
            bikeview.setImageResource(R.drawable.ic_skeletontop);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BluetoothConversation.delay =2000;
        bridge = SingleTone.getBluetoothBridge();
        Resposne();
        dialog = new Dialog(this);
        toggleButton1 =  findViewById(R.id.toggleButton1);
        toggleButton2 =  findViewById(R.id.toggleButton2);
        toggleButton3 =  findViewById(R.id.toggleButton3);
        toggleButton4 =  findViewById(R.id.toggleButton4);
        toggleButton5 =  findViewById(R.id.toggleButton5);
        toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleButton1.isChecked())
                {
                    cmddata = cmdon[1-1];
                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                }
                else
                {
                    cmddata = cmdoff[1-1];
                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                }
            }
        });

        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleButton2.isChecked())
                {
                    cmddata = cmdon[2-1];
                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                }
                else
                {
                    cmddata = cmdoff[2-1];
                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                }
            }
        });

        toggleButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleButton3.isChecked())
                {
                    cmddata = cmdon[3-1];
                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                }
                else
                {
                    cmddata = cmdoff[3-1];
                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                }
            }
        });

        toggleButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleButton4.isChecked())
                {
                    cmddata = cmdon[4-1];
                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                }
                else
                {
                    cmddata = cmdoff[4-1];
                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                }
            }
        });

        toggleButton5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggleButton5.isChecked())
                {
                    cmddata = cmdon[5-1];
                    WriteCmd writeCmd = new WriteCmd();
                    writeCmd.start();
                }
                else
                {
                    cmddata = cmdoff[5-1];
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

                if(!mResponse_data.contains("NO DATA")&& !mResponse_data.contains("@!"))
                {
                    mResponse_data.replace(" ","");
                    if(mResponse_data.contains("6F"))
                    {
                        runOnUiThread(new Runnable() {
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
                                    AppVariables.ShowDialog(dialog,getString(R.string.ecunotresponding),true,0);
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

    @Override
    protected void onDestroy()
    {
        holdflag = true;
        BluetoothConversation.delay =1000;
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
