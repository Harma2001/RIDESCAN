package com.example.tvsridescan.KwpABS;

import static com.example.tvsridescan.Library.AppVariables.parseKwp1acmd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import java.math.BigInteger;
import java.util.ArrayList;

public class KwpAbsLp extends AppCompatActivity {

    BluetoothBridge bridge;
    static String mResponse_data = null;
    static byte[] response;
    static boolean aBoolean = false;
    String data = null;
    ArrayList collectdata = new ArrayList();
    ArrayList<DataModelCardView> dataModels;
    private static CustomAdapter_CardView adapter;
    ListView listView;
    static int count =0,group =1;
    boolean holdflag = false;
    boolean backholdflag = true;
    Button btnprev,btnnxt;
    String finalval ="";

    int min =1,max =5;
    boolean loopstop = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kwp_abs_lp);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        bridge = SingleTone.getBluetoothBridge();
        ImageView ivss =  findViewById(R.id.ss);
        ivss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewss = getWindow().getDecorView().getRootView();
                AppVariables.takeScreenshot(viewss);
            }
        });
        btnprev =  findViewById(R.id.prev);
        btnnxt = (Button) findViewById(R.id.next);
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(max>5)
                {
                    min = min-5;
                    max = max-5;
                }
            }
        });

        btnnxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(max<10)
                {
                    min = min+5;
                    max = max+5;
                }

            }
        });

        if(bridge!=null)
        {
            Resposne();
            Thread thread = new Thread(new SendCmds());
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
                overridePendingTransition(R.anim.out,R.anim.in);

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
        holdflag = true;
        loopstop = true;
        super.onDestroy();
    }

    public class SendCmds extends Thread
    {
        @Override
        public void run()
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
                    reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open("1604vabslp.csv")));
                    String mLine;

                    while ((mLine = reader.readLine()) != null)
                    {
                        String dtcdata[] = mLine.split(",");
                        int num = Integer.parseInt(dtcdata[0]);
                        if(min<= num && num<=max)
                        {
                            aBoolean = false;
                            holdflag = false;

                            send_Request_Command((dtcdata[2] + "\r\n").getBytes());

                            while (aBoolean != true && !holdflag) {
                                SystemClock.sleep(50);
                            }
                            Log.e("Res", mResponse_data);

                            if(!mResponse_data.contains("NO DATA") )
                            {
                                mResponse_data =mResponse_data.replace(" ","");
                                if( !mResponse_data.contains("BUSINIT") && !mResponse_data.contains("@!"))
                                {

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
                                            //String[] val = data.split(",");
                                            String finalval  = data.replace(",", " ");
                                            collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + finalval);
                                        }
                                        else
                                        {
                                            collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + getString(R.string.improperresponse));

                                        }

                                    }
                                }
                                else
                                {
                                    collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + getString(R.string.ecunotresponding));
                                }
                            }
                            else
                            {
                                collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + getString(R.string.ecunotresponding));
                            }

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
                        }
                    }
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
                mainResponse = AppVariables.parseKwp1acmd(mResponse_data,tempCmd);  //Parsing Response
                byte[] res = DataConversion.hexStringToByteArray(mainResponse);  //Converting it to HUMAN readable
                mainResponse = new String(res);
                break;
            case "2":
                mainResponse ="";
                mainResponse = AppVariables.parseKwp1acmd(mResponse_data,cmd.substring(2,cmd.length()));  //Parsing Response
                mainResponse = Date_Formate(mainResponse);
                break;

            case "3":
                mainResponse =  "";
                mainResponse = AppVariables.parseKwp1acmd(mResponse_data,cmd.substring(2,cmd.length()));  //Parsing Response
                mainResponse = fillandBleedResult(mainResponse);
                break;

            case "4":
                mainResponse ="";
                mainResponse = AppVariables.parseKwp21cmd(mResponse_data,cmd.substring(2,cmd.length()));  //Parsing Response
                mainResponse = readWheelSpeedSensorInputs(mainResponse);
                break;

            case "5":
                mainResponse ="";
                mainResponse = AppVariables.parseKwp21cmd(mResponse_data,cmd.substring(2,cmd.length()));  //Parsing Response
                mainResponse = statusInputandVoltages(mainResponse);
                break;
            case "6":
                mainResponse ="";
                mainResponse = AppVariables.parseKwp1acmd(mResponse_data,cmd.substring(2,cmd.length()));  //Parsing Response
                mainResponse = Mu_Jump_Counter(mainResponse);
                break;
        }


        return mainResponse;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bridge = SingleTone.getBluetoothBridge();
        if(loopstop && bridge!=null)
        {
            Resposne();
            dataModels = new ArrayList<>();
            Thread thread = new Thread(new SendCmds());
            thread.start();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loopstop=true;
        holdflag = true;
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);
    }
    public String Date_Formate(String str)
    {
        BigInteger centuryInteger = new BigInteger( str.substring(0,2), 16);
        BigInteger yearInteger = new BigInteger( str.substring(2,4), 16);
        BigInteger mmInteger = new BigInteger( str.substring(4,6), 16);
        BigInteger dayInteger = new BigInteger( str.substring(6,8), 16);

        String yyyy = String.valueOf(yearInteger);
        String mm =  String.valueOf(mmInteger);;
        String dd =  String.valueOf(dayInteger);;

       String tempres = yyyy+"/"+mm+"/"+dd;

        return tempres;
    }

    public String fillandBleedResult(String str)
    {
        String res = null;
        switch (str) {
            case "AA":
                res = "F&B Successfully executed";
                break;
            default:
                res = "F&B not executed successfully";
                break;
        }
        return res;
    }


    public  String readWheelSpeedSensorInputs(String str)
    {


        int readWheelInt = Integer.parseInt(str, 16);
        float readFloat = (float) (readWheelInt*0.01);
        return "Front Sensor : "+String.valueOf(readFloat);
    }
  /*  public String Status_ABS(String str)
    {
        String res;

        String val1 = str.substring(3,4);
        String val2 = str.substring(4,5);

        int a = Integer.parseInt(val1,16);
        int b = Integer.parseInt(val2,16);
        res = "Reference Voltage ="+String.valueOf(a)+" mVolt \n Pump Voltage ="+String.valueOf(b)+" mVolt \n KL30 Voltage ="+String.valueOf(c)+" mVolt \n External Vcc ="+String.valueOf(d);
        return res;
    }
*/
    public String statusInputandVoltages(String string)
    {

        int refInt = Integer.parseInt(string.substring(0,2), 16);
        refInt = refInt*39;
        String refString =  "Reference Voltage = "+String.valueOf(refInt)+"mV";

        int pumpInt = Integer.parseInt(string.substring(2,4), 16);
        float pumpFloat = (float) (pumpInt*57.7);
        String pumpString = "Pump = "+String.valueOf(pumpFloat)+"mV";

        int connectorInt = Integer.parseInt(string.substring(4,6), 16);
        float connectorFloat = (float) (connectorInt*57.7);
        String connectorString = " PIN 30 = "+String.valueOf(connectorFloat)+"mV";


        String res = refString+"\n"+pumpString+"\n"+connectorString;
        return res;

    }

    public String Mu_Jump_Counter(String str)
    {
        String data1 = str.substring(0,4);
        String data2 = str.substring(4,8);
        String val1 ,val2 ;
        int a = Integer.parseInt(data1,16);
        int b = Integer.parseInt(data2,16);
        val1 = "Front Wheel :"+String.valueOf(a);
        val2 = "Rear Wheel :"+String.valueOf(b);
        return val1+"\n"+val2;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
