package com.example.tvsridescan.abs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

public class ABSWriteDID extends Activity {
    BluetoothBridge bridge;
    static String mResponse_data = null;
    static boolean aBoolean = true;
    static byte[] response;
    byte[] cmd;
    byte[] seed = new byte[8];
    byte[] key = new byte[16];
    byte[] end = "\r\n".getBytes();
    String cmddata = null;
    String writedata = null;
    String hinttxt = null;
    Dialog dialog;
    String securitycmd = null;
    boolean holdflag = false;
    Button b1,b2;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abswrite_did);
        context = ABSWriteDID.this;
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
        setTitle("WRITE DID's");
        bridge = SingleTone.getBluetoothBridge();
        Resposne();
        dialog = new Dialog(this);
        b1 =  findViewById(R.id.button1);

        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                cmddata = "2E3101B00D07"; //checked //2E E0B5   2E 310107  //2E3101B00D07
                securitycmd = "1003";
                writedata = "01";
                WriteCmd writeCmd = new WriteCmd();
                writeCmd.start();
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
                byte[] cmdid = cmddata.getBytes();
                String writedata_str = writedata;
                byte[] write_data_arr = writedata_str.getBytes();

                byte[] writevin = new byte[6+2+write_data_arr.length];
                System.arraycopy(cmdid,0,writevin,0,6);
                System.arraycopy(write_data_arr,0,writevin,6,write_data_arr.length);
                System.arraycopy(end,0,writevin,6+write_data_arr.length,2);

                //change mode
                aBoolean = false;
                send_Request_Command((securitycmd+"\r\n").getBytes());
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }

                Log.e("cmd res",mResponse_data);
                mResponse_data.replace(" ","");
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                {

                    aBoolean = false;
                    send_Request_Command(writevin);
                    while (!aBoolean && !holdflag)
                    {
                        SystemClock.sleep(10);
                    }
                    if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                    {
                        Log.e("cmd res",mResponse_data);
                        mResponse_data.replace(" ","");

                        if(mResponse_data.contains("6E"))
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
        aBoolean=true;
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
