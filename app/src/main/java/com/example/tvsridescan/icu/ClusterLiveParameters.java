package com.example.tvsridescan.icu;

import static com.example.tvsridescan.Library.AppVariables.parsecmd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.adapters.CustomAdapter_CardView;
import com.example.tvsridescan.adapters.DataModelCardView;
import com.example.tvsridescan.connection.ConnectionInterrupt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ClusterLiveParameters extends Activity
{
    BluetoothBridge bridge;
    static String mResponse_data = null;
    static byte[] response;
    static boolean aBoolean = false;
    boolean holdflag = false;
    Button btnprev,btnnxt;

    ArrayList<String> collectdata;
    String finalval ="";
    ListView listView;
    ArrayList<DataModelCardView> dataModels;
    private  CustomAdapter_CardView adapter;
    int min =1,max =4;
    boolean loopstop = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_clstrread_dids);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            ImageView ivss =  findViewById(R.id.ss);
            ivss.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    View viewss = getWindow().getDecorView().getRootView();
                    AppVariables.takeScreenshot(viewss);
                }
            });

            btnprev =  findViewById(R.id.prev);
            btnnxt =  findViewById(R.id.next);
            collectdata = new ArrayList<>();

            bridge = SingleTone.getBluetoothBridge();
            btnprev =  findViewById(R.id.prev);
            btnnxt =  findViewById(R.id.next);

            btnprev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(max>4)
                    {
                        min = min-4;
                        max = max-4;
                    }
                }
            });

            btnnxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(max<11)
                    {
                        min = min+4;
                        max = max+4;
                    }

                }
            });

            if(bridge!=null)
            {
                Resposne();
                Thread thread = new Thread(new AbsReadDid());
                thread.start();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bridge =SingleTone.getBluetoothBridge();
        if(loopstop && bridge!=null)
        {
            Resposne();
            dataModels = new ArrayList<>();
            Thread thread = new Thread(new AbsReadDid());
            thread.start();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        loopstop = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        loopstop = true;
        SystemClock.sleep(1000);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loopstop = true;
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

    }

    public class AbsReadDid extends Thread
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
                    reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open("clusterliveparameters.csv")));
                    String mLine;

                    while ((mLine = reader.readLine()) != null)
                    {
                        String dtcdata[] = mLine.split(",");
                        int num = Integer.parseInt(dtcdata[0]);

                        if(min<= num && num<=max)
                        {
                            aBoolean = false;
                            holdflag = false;
                            send_Request_Command(("22" + dtcdata[2] + "\r\n").getBytes());

                            while (!aBoolean && !holdflag)
                            {
                                SystemClock.sleep(50);
                            }

                            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                            {
                                Log.e("Res", mResponse_data);
                                String valstr = parsecmd(mResponse_data,dtcdata[2]);
                                finalval = Output(valstr,dtcdata[0]);
                                collectdata.add(dtcdata[0]+","+dtcdata[1]+","+finalval);
                            }
                            else
                            {
                                collectdata.add(dtcdata[0]+","+dtcdata[1]+","+getString(R.string.ecunotresponding));

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

        }
    }

    public void UI()
    {
        dataModels = new ArrayList<>();

        for(int i=0;i<collectdata.size();i++)
        {
            String [] values = collectdata.get(i).split(",");
            dataModels.add(new DataModelCardView(values[0],values[1],values[2],0));
        }
        collectdata = new ArrayList<>();

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
public String Output(String str,String num)
{
    String op ="";

    switch (num)
    {
        case "1": //battery
                    int val = (int) (Integer.parseInt(str,16)*0.1);
                    op = val+"";
                    break;
        case "2"://Indicator Control
                    String mResponse_data = str;
                    String v1 = mResponse_data.substring(0,4);
                    String v2 = mResponse_data.substring(4,8);
                    String v3 = mResponse_data.substring(8,12);
                    String v4 = mResponse_data.substring(12,16);

                    final int val1 = Integer.parseInt(v1,16);
                    final int val2 = Integer.parseInt(v2,16);
                    final int val3 = Integer.parseInt(v3,16);
                    final int val4 = Integer.parseInt(v4,16);
                    op ="Left Front Indicator Current ="+val1+" mA \nRight Front Indicator Current ="+val2+" mA \nLeft Rear Indicator Current ="+val3+" mA \nRight Rear Indicator Current ="+val4+" mA";
                    break;

        case "3": //Photosensor Value
                    val =  (Integer.parseInt(str,16));
                    op = val+"%";
                    break;

        case "4": //Wake Line Status
                    val =  (Integer.parseInt(str,16));
                    if(val==0)
                    {
                        op = "Inactive";
                    }
                    else
                    if(val==1)
                    {
                        op = "Active";
                    }
                    break;

        case "5": //Displayed Odometer value
                    val =  (Integer.parseInt(str,16));
                    op = val+"Km";break;
        case "6": //Absolute Odometer value
                    String valstr1 = str.substring(0,8);
                    String valstr2 = str.substring(8,16);
                    final int vala =  (Integer.parseInt(valstr1,16));
                    final int valb =  (Integer.parseInt(valstr2,16));
                    op = "RAM ="+vala +" Km \nEEPROM ="+valb+"Km";
                    break;

        case "7"://Odometer offset
                    val = (Integer.parseInt(str,16));
                    op = val+"Km";
                    break;

        case "8"://Service Date

                    String strvaldate = str.substring(0,2);
                    String strvalmon = str.substring(2,4);
                    String strvalyear = str.substring(4,8);

                    final int valdate =  (Integer.parseInt(strvaldate,16));
                    final int valmon =  (Integer.parseInt(strvalmon,16));
                    final int valyear = (Integer.parseInt(strvalyear,16));
                    op = valdate +"-"+valmon+"-"+valyear;
                     break;

        case "9" ://Service Distance
                    val =(Integer.parseInt(str,16));
                    op = val+"Km";
                    break;

        case "10" ://Fuel Tank
                    mResponse_data = str;
                    String valstr = mResponse_data.substring(0,4);
                    final int vala1 = (Integer.parseInt(valstr,16));
                    valstr = mResponse_data.substring(4,8);
                    final int vala2 = (Integer.parseInt(valstr,16));

                    op =  "Fuel Tank Capacity ="+vala1 +" ml \nFuel Tank Sensor ="+vala2+" ohm";
                    break;

        case "11"://

                    mResponse_data = str;
                    final String strhour  = mResponse_data.substring(0,2);
                    final String strmin  = mResponse_data.substring(2,4);
                    final String strsec  = mResponse_data.substring(4,6);
                    final String strdate  = mResponse_data.substring(6,8);
                    final String strmon  = mResponse_data.substring(8,10);
                    final String stryear  = mResponse_data.substring(10,14);

                    final int valhour =  (Integer.parseInt(strhour,16));
                    final int valmin = (Integer.parseInt(strmin,16));
                    final int valsec = (Integer.parseInt(strsec,16));
                    final int valdate1 =  (Integer.parseInt(strdate,16));
                    final int valmon1 =  (Integer.parseInt(strmon,16));
                    final int valyear1 =  (Integer.parseInt(stryear,16));

                    op = " Time: "+valhour+":"+valmin+":"+valsec+"   Date: "+valdate1+"-"+valmon1+"-"+valyear1;
                    break;
    }
    return op;
}
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
