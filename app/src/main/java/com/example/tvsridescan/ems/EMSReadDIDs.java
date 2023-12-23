package com.example.tvsridescan.ems;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.adapters.CustomAdapter_CardView;
import com.example.tvsridescan.adapters.DataModelCardView;
import com.example.tvsridescan.connection.ConnectionInterrupt;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;

public class EMSReadDIDs extends Activity
{
    //bt variables
    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false,posflag = false;
    //data collector
    ArrayList collectdata = new ArrayList();
    boolean holdflag = false;
    //adapter
    ArrayList<DataModelCardView> dataModels;
    private static CustomAdapter_CardView adapter;
    ListView listView;
    LinearLayout progressBar;
    public static String filename = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emsreaddids);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        bridge = SingleTone.getBluetoothBridge();
        Resposne();
        progressBar = (LinearLayout) findViewById(R.id.pb);

        try
        {
            Emsrp emsrp = new Emsrp();
            emsrp.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy()
    {
        holdflag = true;
        super.onDestroy();
    }

    public class Emsrp extends Thread
    {
        @Override
        public void run()
        {
            try {


                int i = 0, j = 0;
                String res = null;
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open(filename)));

                    // do reading, usually loop until end of file reading
                    String mLine;

                    while ((mLine = reader.readLine()) != null) {
                        String dtcdata[] = mLine.split(",");

                        String strlen = dtcdata[3]; // res length to copy

                        String sign = dtcdata[5]; // sign of output value

                        Log.e("Req", dtcdata[1]);
                        aBoolean = false;
                        holdflag = false;
                        send_Request_Command(("22" + dtcdata[1] + "\r\n").getBytes());
                        if (holdflag) {
                            break;
                        }
                        while (aBoolean != true && !holdflag) {
                            SystemClock.sleep(50);
                        }

                        Log.e("Res", mResponse_data);

                        mResponse_data = mResponse_data.replace(" ", "");

                        if (mResponse_data.contains("7F") && mResponse_data.length() == 6) {
                            String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                            String[] val = data.split(",");
                            res = data.replace(",", " ");
                            sign = "";
                            j = 1;
                        } else {
                            mResponse_data = AppVariables.parsecmd(mResponse_data, dtcdata[1]);
                            if (dtcdata[2].equals("0")) {
                                int lenpkt = Integer.parseInt(strlen, 16);
                                mResponse_data = mResponse_data.substring(0, lenpkt * 2);
                                int val = Integer.parseInt(mResponse_data, 16);
                                //dtcdata[4].toString()
                                Expression e = new ExpressionBuilder(dtcdata[4])
                                        .variables("A")
                                        .build()
                                        .setVariable("A", val);
                                double result = e.evaluate();

                                if (dtcdata[6].equals("1")) //NO DATA
                                {
                                    res = String.valueOf((int) result);
                                } else {
                                    res = AppVariables.TrimDouble(String.valueOf(result));
                                }

                                j = 0;
                            } else if (dtcdata[2].equals("1")) {
                                int lenpkt = Integer.parseInt(strlen, 16);
                                mResponse_data = mResponse_data.substring(0, lenpkt * 2);
                                String str = Integer.toBinaryString(Integer.parseInt(mResponse_data.substring(0, 2)));
                                int i1 = Integer.parseInt(str, 16);
                                String bin = Integer.toBinaryString(i1);
                                String ressign = "";
                                if (bin.length() > 7 && bin.substring(0, 1).equals("1")) {
                                    ressign = "-";
                                } else {
                                    ressign = "";
                                }
                                int val = Integer.parseInt(mResponse_data, 16);
                                //dtcdata[4].toString()
                                Expression e = new ExpressionBuilder(dtcdata[4])
                                        .variables("A")
                                        .build()
                                        .setVariable("A", val);
                                double result = e.evaluate();


                                if (dtcdata[6].equals("1")) //NO DATA
                                {
                                    res = ressign + String.valueOf((int) result);
                                } else {
                                    res = ressign + String.valueOf(result);
                                }
                                j = 0;
                            } else if (dtcdata[2].equals("2")) {
                                //direct to ascii
                                //byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);
                                BigInteger val = new BigInteger(mResponse_data, 16);
                                res = String.valueOf(val);
                                j = 0;
                            } else if (dtcdata[2].equals("3"))
                            {
                                int val = Integer.parseInt(mResponse_data, 16);
                                //
                                if (0 == val) {
                                    res = "FALSE";
                                } else if (1 == val) {
                                    res = "TRUE";
                                }
                            } else if (dtcdata[2].equals("4")) {
                                if (mResponse_data.equals("01")) {
                                    res = "Default session";
                                } else if (mResponse_data.equals("03")) {
                                    res = "Extended session";
                                }
                            } else if (dtcdata[2].equals("5")) {
                                BigInteger val = new BigInteger(mResponse_data, 16);
                                res = String.valueOf(val);
                                j = 0;
                            } else if (dtcdata[2].equals("7")) {
                                res = EngineStatus(mResponse_data);
                            } else if (dtcdata[2].equals("8")) {
                                res = CrankShaft_Signal(mResponse_data);
                            } else if (dtcdata[2].equals("9")) {
                                res = Status_Cranking(mResponse_data);
                            } else if (dtcdata[2].equals("10")) {
                                res = System_state(mResponse_data);
                            } else if (dtcdata[2].equals("11")) {
                                res = Fuel_state(mResponse_data);
                            } else if (dtcdata[2].equals("12")) {
                                byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);

                                res = new String(bytedata);
                            } else if (dtcdata[2].equals("13")) {
                                res = mResponse_data;
                            } else if (dtcdata[2].equals("14")) {
                                res = Date_Formate(mResponse_data);
                            } else {
                                //byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);
                                res = mResponse_data;
                                j = 0;
                            }
                        }
                        i++;
                  /* if(dtcdata[6].equals("1")) //NO DATA
                    {
                       //BigDecimal d = new BigDecimal(res).setScale(res.length(), RoundingMode.HALF_UP).stripTrailingZeros();
                        //res = d.toPlainString();
                        int val = Integer.parseInt(res);
                        res = String.valueOf(val);
                    }*/
                        collectdata.add(dtcdata[0] + "," + res + " " + sign.replace("NA", "") + "," + j);

                    }

                } catch (IOException e) {
                    Log.e("exception", e.toString());
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                            UI();

                        } catch (IOException e) {
                            //log the exception
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            super.run();
        }


    }
    public  void UI()
    {
        dataModels = new ArrayList<>();

        for(int i=0;i<collectdata.size();i++)
        {
            String [] values = collectdata.get(i).toString().split(",");

            dataModels.add(new DataModelCardView(String.valueOf(i+1),values[0],values[1],Integer.parseInt(values[2])));
        }

        adapter = new CustomAdapter_CardView(dataModels, getApplicationContext());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView = (ListView) findViewById(R.id.lv);
                listView.setAdapter(adapter);
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
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

            }
            @Override
            public void Connected(String str)
            {

            }
        });
    }

    public String EngineStatus(String str)
    {
       //Stall, cranking, idling, running)

        String res = null;
        if(str.equals("00"))
        {
         res = "Stall";
        }
        else
        if(str.equals("01"))
        {
            res = "Cranking";
        }
        else
        if(str.equals("02"))
        {
            res = "Idling";
        }
        else
        if(str.equals("03"))
        {
            res = "Running";
        }
        return res;
    }


    //"(HALT, 0), (WAITSIG, 1), (IGNTIME, 2), (IGNEDGE, 3), (WAITGAP, 4), (KEEPDGISYNC,
//5), (GAPFOUND, 6), (TIMERMODE, 7), (DISBLNEWSYNC, 8)"

    public String CrankShaft_Signal(String str)
    {
        String res = null;
        switch (str)
        {
            case "00": res = "HALT";break;
            case "01": res = "WAITSIG";break;
            case "02": res = "IGNTIME";break;
            case "03": res = "IGNEDGE";break;
            case "04": res = "WAITGAP";break;
            case "05": res = "KEEPDGISYNC";break;
            case "06": res = "GAPFOUND";break;
            case "07": res = "TIMERMODE";break;
            case "08": res = "DISBLNEWSYNC";break;
        }
        return  res;
    }
    public String Status_Cranking(String str)
    {
        String res = null;
        //Normal, clear flood
        if(str.equals("00"))
        {
            res = "Normal";
        }
        else
        if(str.equals("01"))
        {
            res = "Clear flood";
        }
        return res;
    }

    public String System_state(String str)
    {
        String res = null;
        //(Closed loop  / Open loop)
        if(str.equals("00"))
        {
            res = "Closed loop";
        }
        else
        if(str.equals("01"))
        {
            res = "Open loop";
        }
        return res;
    }
    public String Fuel_state(String str)
    {
        String res = null;
        //(Closed loop  / Open loop)
        if(str.equals("00"))
        {
            res = "Rich";
        }
        else
        if(str.equals("01"))
        {
            res = "lean";
        }
        return res;
    }

    public String Date_Formate(String str)
    {
        byte[] bytedata = DataConversion.hexStringToByteArray(str);

        str = new String(bytedata);
        String res = null;
        String dd = str.substring(0,2);
        String mm = str.substring(2,4);
        String yyyy = str.substring(4,8);
        res = dd+"-"+mm+"-"+yyyy;

        return res;
    }

}
