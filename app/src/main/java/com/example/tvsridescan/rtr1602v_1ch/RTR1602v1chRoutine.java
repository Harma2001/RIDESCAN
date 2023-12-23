package com.example.tvsridescan.rtr1602v_1ch;

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

public class RTR1602v1chRoutine extends AppCompatActivity {

    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;
    String cmddata = "";
    String positiveResponse="";

    Button wheelSpeedSensorTest, startEvacuationAndFill,startrepairbleed,stoprepairandbleed,stopevacandfillBleed,absLampBlink,actuatorTest;
    Dialog dialog;
    Context context;
    private static final String TAG = "RTR1602v1chRoutine.this";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtr1602v1ch_routine);
        init();
    }

    private void init()
    {
        context = RTR1602v1chRoutine.this;
        Log.e(TAG,"Entered");
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        dialog = new Dialog(this);

        //startEvacuationAndFill =  findViewById(R.id.Button1);
        //stopevacandfillBleed =  findViewById(R.id.Button2);
        wheelSpeedSensorTest =  findViewById(R.id.Button3);
       // startrepairbleed =  findViewById(R.id.Button4);
        //stoprepairandbleed = findViewById(R.id.Button5);
        absLampBlink =  findViewById(R.id.Button6);
       /* actuatorTest = findViewById(R.id.Button7);

        startEvacuationAndFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "31FB01FF02FF02";
                positiveResponse = "71";

                SendRoutineCommand sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });

        stopevacandfillBleed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "31FB0100020002";
                positiveResponse = "71";

                SendRoutineCommand sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });*/
        wheelSpeedSensorTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "31FB0C00C8";
                positiveResponse = "71";

                SendRoutineCommand sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });

      /*  startrepairbleed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "31FB0202FF0002";
                positiveResponse = "71";

                SendRoutineCommand sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });
        stoprepairandbleed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "31FB0202000002";
                positiveResponse = "71";

                SendRoutineCommand sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });
*/

        absLampBlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "31FB0F";
                positiveResponse = "71";

                SendRoutineCommand sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });

    /*    actuatorTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "31FB0600FFFF0022FFFF0030C80AFFFF003200A50064";
                positiveResponse = "71";

                SendRoutineCommand sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });*/



    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.out,R.anim.in);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
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


    public class SendRoutineCommand extends Thread
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

                aBoolean = false;
                send_Request_Command("1003\r\n".getBytes());
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                Log.e("cmd res",mResponse_data);
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("ERROR"))
                {
                    byte[] cmd = (cmddata+"\r\n").getBytes(); //
                    aBoolean = false;
                    send_Request_Command(cmd);
                    while (!aBoolean )
                    {
                        SystemClock.sleep(10);
                    }
                    Log.e("cmd res",mResponse_data);
                    if(!mResponse_data.contains("NO DATA"))
                    {
                        if(!mResponse_data.contains("ERROR"))
                        {
                            mResponse_data.replace(" ","");
                            if(mResponse_data.substring(0,2).equals(positiveResponse))
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
                                    AppVariables.ShowDialog(dialog,"No response from VCI, Try Again",true,0);
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
                                AppVariables.ShowDialog(dialog,"No response from ECU, Try Again",true,0);
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
                AppVariables.GenLogLine(e.getLocalizedMessage());

            }


            super.run();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aBoolean = true;

    }
}
