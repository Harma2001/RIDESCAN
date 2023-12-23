package com.example.tvsridescan.KwpABS;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

public class KwpAbsIOcontrol extends AppCompatActivity {

    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;
    String cmddata = null;

    Button inletValveFront, outletValveFront,pumpBtn,lampOn,lampOff,lampBlink;
    Dialog dialog;

    Context context;
    private static final String TAG = "KwpAbsIOcontrol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kwp_abs_iocontrol);

        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

    }
    private void init()
    {
        context = KwpAbsIOcontrol.this;
        Log.e(TAG,"Entered");
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        dialog = new Dialog(this);

        inletValveFront =  findViewById(R.id.Button1);
        outletValveFront =  findViewById(R.id.Button2);
        pumpBtn =  findViewById(R.id.Button3);
        lampOn =  findViewById(R.id.Button4);
        lampOff =  findViewById(R.id.Button5);
       // lampBlink =  findViewById(R.id.Button6);




            inletValveFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "3010070100C800";
                SendIOCommand sendIOCommand = new SendIOCommand();
                sendIOCommand.start();
            }
        });

        outletValveFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "3010070200C800";
                SendIOCommand sendIOCommand = new SendIOCommand();
                sendIOCommand.start();
            }
        });
        pumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "3010070010C800";
                SendIOCommand sendIOCommand = new SendIOCommand();
                sendIOCommand.start();
            }
        });
        lampOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "30200701";
                SendIOCommand sendIOCommand = new SendIOCommand();
                sendIOCommand.start();
            }
        });
        lampOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "30200700";
                SendIOCommand sendIOCommand = new SendIOCommand();
                sendIOCommand.start();
            }
        });
    /*    lampBlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "30200702";
                SendIOCommand sendIOCommand = new SendIOCommand();
                sendIOCommand.start();
            }
        });*/

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


    public class SendIOCommand extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                    }
                });
                SystemClock.sleep(1000);
                byte[] cmd = "1087\r\n".getBytes(); //
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                if(!mResponse_data.contains("NO DATA") && cmddata.length()>0)
                {
                    cmd = (cmddata+"\r\n").getBytes(); //
                    aBoolean = false;
                    send_Request_Command(cmd);
                    while (!aBoolean )
                    {
                        SystemClock.sleep(10);
                    }
                    Log.e("cmd res",mResponse_data);
                    if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("ERROR") && !mResponse_data.contains("@!"))
                    {
                        mResponse_data.replace(" ","");
                        if(mResponse_data.substring(0,2).equals("70"))
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

                            if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                            {
                                String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                final String finalval  = data.replace(",", " ");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppVariables.ShowDialog(dialog ,finalval,true,0);
                                    }
                                });

                            }
                            else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppVariables.ShowDialog(dialog ,getString(R.string.unabletoprocess),true,0);
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

            }
            catch (Exception e)
            {
                e.printStackTrace();
                AppVariables.GenLogLine(e.getLocalizedMessage()) ;
            }

            super.run();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        aBoolean = true;

    }
    @Override
    protected void onResume() {
        super.onResume();
        bridge = SingleTone.getBluetoothBridge();
        Resposne();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
