package com.example.tvsridescan.rtr1602v_1ch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.adapters.CustomAdapter_CardView;
import com.example.tvsridescan.adapters.DataModelCardView;
import com.example.tvsridescan.connection.ConnectionInterrupt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RTR1602v1chEcuParams extends AppCompatActivity {

    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;
    String data = null;
    ArrayList collectdata = new ArrayList();
    ArrayList<DataModelCardView> dataModels;
    private static CustomAdapter_CardView adapter;
    ListView listView;

    boolean loopstop = false;
    private static final String TAG = "RTR1602v1chEcuParams.this";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtr1602v1ch_ecu_params);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

      /*  ImageView ivss = (ImageView) findViewById(R.id.ss);
        ivss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewss = getWindow().getDecorView().getRootView();
                AppVariables.takeScreenshot(viewss);
            }
        });

*/
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        SendCmds sendCmds = new SendCmds();
        sendCmds.start();
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



    public  void UI()
    {
        dataModels = new ArrayList<>();

        for(int i = 0; i< collectdata.size(); i++)
        {
            String[] values = collectdata.get(i).toString().split(",");
            dataModels.add(new DataModelCardView(values[0],values[1],values[2],1));

        }
        collectdata = new ArrayList();
        adapter = new CustomAdapter_CardView(dataModels, getApplicationContext());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView =  findViewById(R.id.lv);
                listView.setAdapter(adapter);
                listView.setVisibility(View.VISIBLE);
            }
        });

    }
    @Override
    protected void onDestroy()
    {
        loopstop = true;
        super.onDestroy();
    }

    public class SendCmds extends Thread
    {
        @Override
        public void run()
        {

            byte[] cmd = "1003\r\n".getBytes();
            aBoolean = false;
            send_Request_Command(cmd);
            while (!aBoolean )
            {
                SystemClock.sleep(10);
            }

            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!")) {
                mResponse_data = mResponse_data.replace(" ", "");
                if (mResponse_data.contains("50"))
                {
                    BufferedReader reader = null;
                    try
                    {
                        reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open("1602v1checuparams.csv")));
                        String mLine;

                        while ((mLine = reader.readLine()) != null)
                        {
                            String dtcdata[] = mLine.split(",");
                            int num = Integer.parseInt(dtcdata[0]);

                            aBoolean = false;

                            send_Request_Command((dtcdata[2] + "\r\n").getBytes());

                            while (!aBoolean ) {
                                SystemClock.sleep(10);
                            }
                            Log.e("Res", mResponse_data);

                            if(!mResponse_data.contains("NO DATA")  &&  !mResponse_data.contains("@!") )
                            {
                                mResponse_data =mResponse_data.replace(" ","");
                                   if(mResponse_data.contains("50"))
                                   {
                                       SystemClock.sleep(200);
                                   }

                                    if(!mResponse_data.contains("7F"))
                                    {
                                        String finalval = SwitchCase(mResponse_data, dtcdata[3],dtcdata[2]);
                                        collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + finalval);
                                    }
                                    else {
                                        if (mResponse_data.contains("7F") && mResponse_data.length() == 6) {
                                            String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                            //String[] val = data.split(",");
                                            String finalval = data.replace(",", " ");
                                            collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + finalval);
                                        } else {
                                            collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + "Improper response");

                                        }

                                    }


                            }
                            else
                            {
                                collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + getString(R.string.ecunotresponding));
                            }



                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (reader != null)
                        {
                            try
                            {
                                reader.close();
                                UI();
                            }
                            catch (IOException e) {
                                //log the exception
                                e.printStackTrace();
                                AppVariables.GenLogLine(TAG +e.getMessage());
                            }
                        }
                    }

                }
                else {

                    if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                    {
                        String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                        //String[] val = data.split(",");
                        final String finalval  = data.replace(",", " ");
                        collectdata.add("1" + "," + " " + "," + finalval);

                    }
                    else
                    {
                        collectdata.add("1" + "," + " " + "," + getString(R.string.improperresponse));
                    }
                    try
                    {
                        UI();
                    }
                    catch (Exception e) {
                        //log the exception
                        e.printStackTrace();
                        AppVariables.GenLogLine(TAG +e.getMessage());
                    }
                }
            }
            else
            {
                collectdata.add("1" + "," + " " + "," + getString(R.string.ecunotresponding));
                try
                {
                    UI();
                }
                catch (Exception e) {
                    //log the exception
                    e.printStackTrace();
                    AppVariables.GenLogLine(TAG +e.getMessage());
                }
            }

            super.run();
        }
    }

    private String SwitchCase(String mResponse_data, String lpCase,String cmd) {
        String mainResponse ="";
        mResponse_data = mResponse_data.replace(" ","");
        switch (lpCase)
        {
            case "1":
                mainResponse ="";
                String tempCmd = cmd.substring(2,cmd.length());
                mainResponse = AppVariables.parseBosch1aAAResposne(mResponse_data,tempCmd);  //Parsing Response
                byte[] res = DataConversion.hexStringToByteArray(mainResponse);  //Converting it to HUMAN readable
                mainResponse = new String(res);
                break;
            case "2":
                //mainResponse ="";

                mainResponse = "Improper Response";
                break;


        }


        return mainResponse;
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.out,R.anim.in);

        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        //dataModels = new ArrayList<>();
      //  SendCmds sendCmds = new SendCmds();
        //sendCmds.start();



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SystemClock.sleep(500);
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
