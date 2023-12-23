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

public class KwpAbsRoutinecontrol extends AppCompatActivity {


    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;
    String cmddata = "";
    String positiveResponse="";

    Button wheelSpeedSensorTest, StartEvacuationAndFill,startrepairfill,resultrepairfill,stoprepairandfill,stopevacandfill,resultevacandfill;
    Dialog dialog;

    Context context;
    private static final String TAG = "KwpAbsRoutinecontrol";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kwp_abs_routinecontrol);
        init();
    }



    private void init()
    {
        context = KwpAbsRoutinecontrol.this;
        Log.e(TAG,"Entered");
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        dialog = new Dialog(this);

        wheelSpeedSensorTest =  findViewById(R.id.Button1);
       /* StartEvacuationAndFill =  findViewById(R.id.Button2);
        startrepairfill =  findViewById(R.id.Button3);
        resultrepairfill =  findViewById(R.id.Button4);
        stopevacandfill = findViewById(R.id.Button5);
        stoprepairandfill =  findViewById(R.id.Button6);
        resultevacandfill =  findViewById(R.id.Button7);*/



       /* if(AppVariables.BikeModel ==7)
        {
            StartEvacuationAndFill.setVisibility(View.INVISIBLE);
            stopevacandfill.setVisibility(View.INVISIBLE);
            resultevacandfill.setVisibility(View.INVISIBLE);

            startrepairfill.setVisibility(View.INVISIBLE);
            stoprepairandfill.setVisibility(View.INVISIBLE);
            resultrepairfill.setVisibility(View.INVISIBLE);


        }
        else if(AppVariables.BikeModel ==8)
        {
            StartEvacuationAndFill.setVisibility(View.INVISIBLE);
            stopevacandfill.setVisibility(View.INVISIBLE);
            resultevacandfill.setVisibility(View.INVISIBLE);

            startrepairfill.setVisibility(View.INVISIBLE);
            stoprepairandfill.setVisibility(View.INVISIBLE);
            resultrepairfill.setVisibility(View.INVISIBLE);
        }
        else if(AppVariables.BikeModel==4)
        {
            StartEvacuationAndFill.setVisibility(View.INVISIBLE);
            stopevacandfill.setVisibility(View.INVISIBLE);
            resultevacandfill.setVisibility(View.INVISIBLE);

            startrepairfill.setVisibility(View.INVISIBLE);
            stoprepairandfill.setVisibility(View.INVISIBLE);
            resultrepairfill.setVisibility(View.INVISIBLE);

        }
        else if(AppVariables.BikeModel==5)
        {
            StartEvacuationAndFill.setVisibility(View.INVISIBLE);
            stopevacandfill.setVisibility(View.INVISIBLE);
            resultevacandfill.setVisibility(View.INVISIBLE);

            startrepairfill.setVisibility(View.INVISIBLE);
            stoprepairandfill.setVisibility(View.INVISIBLE);
            resultrepairfill.setVisibility(View.INVISIBLE);

        }
        else
        {
            StartEvacuationAndFill.setVisibility(View.VISIBLE);
            stopevacandfill.setVisibility(View.VISIBLE);
            resultevacandfill.setVisibility(View.VISIBLE);

            startrepairfill.setVisibility(View.VISIBLE);
            stoprepairandfill.setVisibility(View.VISIBLE);
            resultrepairfill.setVisibility(View.VISIBLE);
        }
*/

        wheelSpeedSensorTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "31FB0C07D0";
                positiveResponse = "71";

                SendRoutineCommand  sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });

      /*  StartEvacuationAndFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "31101E1E1E";
                positiveResponse = "71";

                SendRoutineCommand  sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });
        startrepairfill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "3111";
                positiveResponse = "71";

                SendRoutineCommand  sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });

        stopevacandfill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "3210";
                positiveResponse = "72";

                SendRoutineCommand  sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });
        stoprepairandfill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "3211";
                positiveResponse = "72";

                SendRoutineCommand  sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });


        resultevacandfill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "3310";
                positiveResponse = "73";

                SendRoutineCommand  sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
            }
        });
        resultrepairfill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmddata = "3311";
                positiveResponse = "73";

                SendRoutineCommand  sendRoutineCommand  = new SendRoutineCommand();
                sendRoutineCommand.start();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

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
                        if(!mResponse_data.contains("ERROR") && !mResponse_data.contains("@!"))
                        {
                            mResponse_data.replace(" ","");
                            if(mResponse_data.substring(0,2).equals(positiveResponse))
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
