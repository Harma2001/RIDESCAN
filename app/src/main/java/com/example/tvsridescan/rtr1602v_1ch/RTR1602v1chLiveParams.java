package com.example.tvsridescan.rtr1602v_1ch;

import static com.example.tvsridescan.KwpABS.KwpAbsDtcs.getBit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

public class RTR1602v1chLiveParams extends AppCompatActivity {


    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;
    String data = null;
    ArrayList collectdata = new ArrayList();
    ArrayList<DataModelCardView> dataModels;
    private static CustomAdapter_CardView adapter;
    ListView listView;
    static int count =0,group =1;
    Button btnprev,btnnxt;
    String finalval ="";

    int min =1,max =3;
    boolean loopstop = false;
    private  static  final String TAG= "RTR1602v1chLiveParams.this";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtr1602v1ch_live_params);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /*ImageView ivss = (ImageView) findViewById(R.id.ss);
        ivss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewss = getWindow().getDecorView().getRootView();
                AppVariables.takeScreenshot(viewss);
            }
        });*/


        bridge = SingleTone.getBluetoothBridge();
        if(bridge!=null)
        {
            Resposne();
            SendCmds thread = new SendCmds();
            thread.start();
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
                SystemClock.sleep(50);
            }

            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!") && mResponse_data.contains("50"))
            {
                mResponse_data = mResponse_data.replace(" ", "");
                if (mResponse_data.contains("50"))
                {
                    while (true)
                    {
                        if(loopstop)
                        {
                            break;
                        }
                        BufferedReader reader = null;
                        try
                        {
                            reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open("1602v1chliveparams.csv")));
                            String mLine;
                            while ((mLine = reader.readLine()) != null)
                            {
                                String dtcdata[] = mLine.split(",");
                                aBoolean = false;
                                send_Request_Command((dtcdata[2] + "\r\n").getBytes());
                                while (!aBoolean ) {
                                    SystemClock.sleep(50);
                                }
                                Log.e("Res", mResponse_data);
                                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                                {
                                    mResponse_data =mResponse_data.replace(" ","");

                                    if(!mResponse_data.contains("7F"))
                                    {
                                        String finalval = SwitchCase(mResponse_data, dtcdata[3],dtcdata[2]);
                                        collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + finalval);
                                    }
                                    else
                                    {
                                        if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                                        {
                                            String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                            String finalval  = data.replace(",", " ");
                                            collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + finalval);
                                        }
                                        else
                                        {
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
                            AppVariables.GenLogLine(TAG +e.getMessage());
                        }
                        finally {
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
        try {
            switch (lpCase)
            {
                case "1":
                    mainResponse ="";
                    //  String tempCmd = cmd.substring(2,cmd.length());
                    mainResponse = AppVariables.parseBosch19AAResposne(mResponse_data);  //Parsing Response
                    byte[] res = DataConversion.hexStringToByteArray(mainResponse);  //Converting it to HUMAN readable
                    mainResponse = new String(res);
                    break;
                case "2":
                    mainResponse ="";
                    mainResponse = AppVariables.parseBosch22cmd(mResponse_data,cmd.substring(2,cmd.length()));  //Parsing Response
                    mainResponse = ecuInput(mainResponse);
                    break;

                case "3":
                    mainResponse =  "";
                    mainResponse = AppVariables.parseBosch22cmd(mResponse_data,cmd.substring(2,cmd.length()));  //Parsing Response
                    mainResponse = muJumpCounter(mainResponse);
                    break;

                case "4":
                    mainResponse ="";
                    mainResponse = AppVariables.parseBosch22cmd(mResponse_data,cmd.substring(2,cmd.length()));  //Parsing Response
                    mainResponse = clusterConfiguration(mainResponse);
                    break;
                case "5":
                    mainResponse ="";
                    mainResponse = AppVariables.parseBosch22cmd(mResponse_data,cmd.substring(2,cmd.length()));  //Parsing Response
                    mainResponse = fswr(mainResponse);
                    break;
                case "6":
                    mainResponse ="";
                    mainResponse = AppVariables.parseBosch22cmd(mResponse_data,cmd.substring(2,cmd.length()));  //Parsing Response
                    mainResponse = processData(mainResponse);
                    break;
                case "7":
                    mainResponse ="";
                    mainResponse = AppVariables.parseBosch19AAResposne(mResponse_data);  //Parsing Response
                    mainResponse = flashHistoryData(mainResponse);
                    break;
            }

        }
        catch (Exception e){
            e.printStackTrace();
            AppVariables.GenLogLine(TAG +e.getMessage());

        }
        return mainResponse;
    }

    private String clusterConfiguration(String mainResponse) {
        String data1 = mainResponse;
        String val1  ;
        int a = Integer.parseInt(data1,16);
        val1 = String.valueOf(a);
        if(val1.equals("0"))
        {
            val1 ="CAN disabled cluster";
        }
        if(val1.equals("1"))
        {
            val1 ="CAN enabled cluster";
        }
        return val1;
    }

    private String fswr(String mainResponse)
    {
        String data1 = mainResponse;
        String val1 =mainResponse  ;

        if(val1.equals("00"))
        {
            val1 ="Invalid FSW is available";
        }
        if(val1.equals("01"))
        {
            val1 ="Valid FSW is available";
        }
        return val1;
    }
    private String processData(String mainResponse)
    {
        String val1 ="";

        int a = Integer.parseInt(mainResponse,16);
        val1 = String.valueOf(a);

        return val1;
    }

    private String flashHistoryData(String mainResponse)
    {
        String val1 ="";
        if(mainResponse.length()>10)
        {
            final String strYear  = mainResponse.substring(0,2);
            final String strMonth = mainResponse.substring(2,4);
            final String strDay  = mainResponse.substring(4,6);
         //   final String partNumber  = mainResponse.substring(6,mainResponse.length());

          //  Log.e("Flash hisotry ","Date : "+strYear+"/"+strMonth+"/"+strDay+","+partNumber );
          //  partNumber.getBytes()
           //val1 = " Time: "+valhour+":"+valmin+":"+valsec+"   Date: "+valdate1+"-"+valmon1+"-"+valyear1;
            val1 = "DD:MM:YYYY :"+strDay+"/"+strMonth+"/"+strYear;
        }

        return val1;
    }

    private String muJumpCounter(String mainResponse) {
       String val1 ="",val2="" ;

        if(mainResponse.length()==4)
        {
            String data1 = mainResponse.substring(0,4);
           // String data2 = mainResponse.substring(2,4);
            int a = Integer.parseInt(mainResponse,16);
           // int b = Integer.parseInt(data2,16);
            val1 = String.valueOf(a);
           // val2 = String.valueOf(b);
        }
        return val1;

    }

    private String ecuInput(String mainResponse) {

        int refInt = Integer.parseInt(mainResponse.substring(0,2), 16); //Front Wheel
        String frontWheel =  "Front wheel speed = "+String.valueOf(refInt)+"Km/h";

        int pumpInt = Integer.parseInt(mainResponse.substring(2,4), 16);
        String realWheel = "Rear Wheel Speed = "+String.valueOf(pumpInt)+"Km/h";

        int connectorInt = Integer.parseInt(mainResponse.substring(4,6), 16);
        float connectorFloat = (float) (connectorInt*0.078 );
        String powerSupply = " Power supply voltage  = "+String.valueOf(connectorFloat)+" V ";

        int statusValveInt = Integer.parseInt(mainResponse.substring(6,8), 16);
        String ValveStatus = "";
        if(getBit(statusValveInt,0)==1)
        {
            ValveStatus="Valve relay staus = ON\n";
        }
        else
        {
            ValveStatus="Valve relay staus = OFF\n";
        }
        if(getBit(statusValveInt,1)==1)
        {
            ValveStatus= ValveStatus+"Pump motor status = ON \n";
        }
        else
        {
            ValveStatus= ValveStatus+"Pump motor status = OFF\n";

        }
        if(getBit(statusValveInt,2)==1)
        {
            ValveStatus= ValveStatus+" Front Inlet Valve status = ON\n";
        }
        else
        {
            ValveStatus= ValveStatus+"Front Inlet Valve status = OFF\n";
        }

        if(getBit(statusValveInt,3)==1)
        {
            ValveStatus= ValveStatus+"  Front Outlet Valve status = ON\n";
        }
        else
        {
            ValveStatus= ValveStatus+" Front Outlet Valve status = ON\n";
        }
        String res = frontWheel+"\n"+realWheel+"\n"+powerSupply+"\n"+ValveStatus;
        return res;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.out,R.anim.in);

        bridge = SingleTone.getBluetoothBridge();
        if(loopstop && bridge!=null)
        {
            dataModels = new ArrayList<>();
            Resposne();
            Thread thread = new Thread(new SendCmds());
            thread.start();

        }

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
      //  SystemClock.sleep(1000);
        loopstop=true;
        finish();
    }
}
