package com.example.tvsridescan.ems;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.BluetoothConversation;
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

public class EMSLiveParameters extends AppCompatActivity
{
    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    volatile boolean aBoolean = false;
    //data collector
    ArrayList<String> collectdata;
    volatile boolean holdflag = false;
    //adapter
    ArrayList<DataModelCardView> dataModels;
    private CustomAdapter_CardView adapter;
    ListView listView;
    LinearLayout progressBar;
    public static String filename = null;

    volatile boolean stoploop = false;
    // String[] liststr = {"Engine History","OBD","ECU Identification","Engine Components","Current Status","Intake","Fueling and Ignition","Exhaust System","Sensors and Switches"};

    Button btnhistory,btnobd,btnecuid,btncomp,btnsts,btnint,btnfuel,btnexhaust,btnsensors;
    int count =0;
    int min =0;
    int max =5;
    Button btnnxt,btnprev;
    TextView txttit;
    int maxlimit=0;

    private static  final String TAG="EMSLiveParameters";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            context =EMSLiveParameters.this;

            setContentView(R.layout.activity_ems_live_parameters);
            bridge = SingleTone.getBluetoothBridge();

            setTitle("Live Parameters");
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            ImageView ivss =  findViewById(R.id.ss);
            txttit =  findViewById(R.id.tit);
            ivss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View viewss = getWindow().getDecorView().getRootView();
                    AppVariables.takeScreenshot(viewss);
                }
            });

            ImageView bikeview = findViewById(R.id.bikeview);

            if(AppVariables.BikeModel ==1)
            {
                bikeview.setImageResource(R.drawable.ic_biketopview);
            }
            else
            {
                bikeview.setImageResource(R.drawable.ic_biketopview);
            }

            progressBar = findViewById(R.id.pb);
            btnhistory = findViewById(R.id.btnhistory);
            btnobd = findViewById(R.id.btnobd);
            btnecuid = findViewById(R.id.btnecuid);
            btncomp =  findViewById(R.id.btncomp);
            btnsts =  findViewById(R.id.btnsts);
            btnint =  findViewById(R.id.btnint);
            btnfuel =  findViewById(R.id.btnfuel);
            btnexhaust =  findViewById(R.id.btnexhaust);
            btnsensors =  findViewById(R.id.btnsensors);

            btnnxt = findViewById(R.id.next);
            btnprev =  findViewById(R.id.prev);

            btnnxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(max<maxlimit)
                    {
                        // btnnxt.setVisibility(View.GONE);
                        min = min +5;
                        max = min+5;
                    }

                }
            });

            btnprev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(max > 5)
                    {
                        // btnnxt.setVisibility(View.VISIBLE);

                        min = min -5;
                        max = max-5;
                    }
                }
            });


            btnhistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnhistory.setBackground(getDrawable(R.drawable.bg_gradient_did31));

                        btnobd.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnecuid.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsts.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btncomp.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnint.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnfuel.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnexhaust.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsensors.setBackground(getDrawable(R.drawable.bg_gradient_did21));

                    }

                    if(AppVariables.BikeModel ==1)
                    {
                        filename = "engine history.csv";
                        dataModels = new ArrayList<>();
                        maxlimit = 10;
                        count = min = 0;max =5;
                    }
                    else
                    {
                        filename = "engine history1.csv";
                        dataModels = new ArrayList<>();
                        maxlimit = 4;
                        count = min = 0;max =4;
                    }
                }
            });

            btnobd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnobd.setBackground(getDrawable(R.drawable.bg_gradient_did31));

                        btnhistory.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnecuid.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsts.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btncomp.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnint.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnfuel.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnexhaust.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsensors.setBackground(getDrawable(R.drawable.bg_gradient_did21));

                    }
                    filename = "OBD.csv";
                    dataModels = new ArrayList<>();
                    maxlimit = 4;
                    count = min = 0;max =5;
                }
            });

            btnecuid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnecuid  .setBackground(getDrawable(R.drawable.bg_gradient_did31));

                        btnhistory.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnobd.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsts.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btncomp.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnint.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnfuel.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnexhaust.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsensors.setBackground(getDrawable(R.drawable.bg_gradient_did21));

                    }
                    filename = "ECU Identification.csv";
                    dataModels = new ArrayList<>();
                    maxlimit = 5;
                    count = min = 0;max =5;
                }
            });

            btnsts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnsts   .setBackground(getDrawable(R.drawable.bg_gradient_did31));

                        btnhistory.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnobd.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnecuid.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btncomp.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnint.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnfuel.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnexhaust.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsensors.setBackground(getDrawable(R.drawable.bg_gradient_did21));

                    }
                    filename = "Current Status.csv";
                    dataModels = new ArrayList<>();
                    maxlimit = 5;
                    count = min = 0;max =5;
                }
            });

            btncomp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btncomp.setBackground(getDrawable(R.drawable.bg_gradient_did31));

                        btnhistory.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnobd.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnecuid.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsts.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnint.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnfuel.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnexhaust.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsensors.setBackground(getDrawable(R.drawable.bg_gradient_did21));

                    }
                    if(AppVariables.BikeModel ==1)
                    {
                        filename = "Cranking.csv";
                        dataModels = new ArrayList<>();
                        maxlimit = 4;
                        count = min = 0;max =5;
                    }
                    else
                    {
                        filename = "Cranking1.csv";
                        dataModels = new ArrayList<>();
                        maxlimit = 3;
                        count = min = 0;max =5;
                    }
                }
            });

            btnint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnint.setBackground(getDrawable(R.drawable.bg_gradient_did31));

                        btnhistory.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnobd.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnecuid.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsts.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btncomp.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnfuel.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnexhaust.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsensors.setBackground(getDrawable(R.drawable.bg_gradient_did21));

                    }
                    filename = "Intake.csv";
                    dataModels = new ArrayList<>();
                    maxlimit = 10;
                    count = min = 0;max =5;
                }
            });

            btnfuel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnfuel   .setBackground(getDrawable(R.drawable.bg_gradient_did31));

                        btnhistory.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnobd.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnecuid.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsts.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btncomp.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnint.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnexhaust.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsensors.setBackground(getDrawable(R.drawable.bg_gradient_did21));

                    }
                    filename = "Fueling and Ignition.csv";
                    dataModels = new ArrayList<>();
                    maxlimit = 6;
                    count = min = 0;max =5;
                }
            });

            btnexhaust.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnexhaust.setBackground(getDrawable(R.drawable.bg_gradient_did31));

                        btnhistory.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnobd.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnecuid.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsts.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btncomp.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnint.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnfuel.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsensors.setBackground(getDrawable(R.drawable.bg_gradient_did21));

                    }
                    if(AppVariables.BikeModel ==1)
                    {
                        filename = "Exhuast System.csv";
                        dataModels = new ArrayList<>();
                        maxlimit = 4;
                        count = min = 0;max =5;
                    }
                    else
                    {
                        filename = "Exhuast System1.csv";
                        dataModels = new ArrayList<>();
                        maxlimit = 3;
                        count = min = 0;max =5;
                    }
                }
            });

            btnsensors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        btnsensors .setBackground(getDrawable(R.drawable.bg_gradient_did31));

                        btnhistory.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnobd.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnecuid.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnsts.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btncomp.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnint.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnfuel.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                        btnexhaust.setBackground(getDrawable(R.drawable.bg_gradient_did21));

                    }
                    if(AppVariables.BikeModel ==1)
                    {
                        filename = "Sensors and Switches.csv";
                        dataModels = new ArrayList<>();
                        maxlimit = 8;
                        count = min = 0;max =5;
                    }
                    else
                    {
                        filename = "Sensors and Switches1.csv";
                        dataModels = new ArrayList<>();
                        maxlimit = 3;
                        count = min = 0;max =5;
                    }


                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnhistory.setBackground(getDrawable(R.drawable.bg_gradient_did31));

                btnobd.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                btnecuid.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                btnsts.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                btncomp.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                btnint.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                btnfuel.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                btnexhaust.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                btnsensors.setBackground(getDrawable(R.drawable.bg_gradient_did21));

            }

            if(AppVariables.BikeModel ==1)
            {
                filename = "engine history.csv";
                maxlimit = 10;
                count = min = 0;max =5;
            }
            else
            {
                filename = "engine history1.csv";
                maxlimit = 4;
                count = min = 0;max =4;
            }

            dataModels = new ArrayList<>();
            txttit.setText(filename.replace(".csv","").toUpperCase());
          //  Emsrp  threadLivePara =new Emsrp();
            //threadLivePara.start();

            if(bridge!=null)
            {
                Resposne();

                Thread t = new Thread(new Emsrp());
                t.start();
            }
            else
            {

                Intent i = new Intent(context, ConnectionInterrupt.class);
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

                //BluetoothConversation.CheckConnection

            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public class Emsrp extends  Thread
    {
        @Override
        public void run()
        {

            while (true)
            {
                if(stoploop)
                {
                    Log.e(TAG,"While loop breaked");
                    break;
                }
                try
                {

                    int  j = 0;
                    String res = null;
                    collectdata = new ArrayList<>();
                    BufferedReader reader = null;
                    BluetoothConversation.delay = 0;
                    try
                    {
                        reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open(filename)));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txttit.setText(filename.replace(".csv","").replace("1","").toUpperCase());
                            }
                        });

                        count =0;
                        String mLine;

                        while ((mLine = reader.readLine()) != null)
                        {
                            if(min<=count && count<max)
                            {
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
                                while (!aBoolean && !holdflag) {
                                    Thread.sleep(50);
                                }

                                mResponse_data = mResponse_data.replace(" ", "");
                                if(!mResponse_data.contains("NODATA") && !mResponse_data.contains("ERROR")&& !mResponse_data.contains("@!"))
                                {
                                    if (mResponse_data.contains("7F") && mResponse_data.length() == 6)
                                    {
                                        String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                        //String[] val = data.split(",");
                                        res = data.replace(",", " ");
                                        sign = "";
                                        j = 1;
                                    }
                                    else
                                    {
                                        mResponse_data = AppVariables.parsecmd(mResponse_data, dtcdata[1]);
                                        switch (dtcdata[2])
                                        {
                                            case "0": {
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
                                                break;
                                            }
                                            case "1": {
                                                int lenpkt = Integer.parseInt(strlen, 16);
                                                mResponse_data = mResponse_data.substring(0, lenpkt * 2);
                                                String str = Integer.toBinaryString(Integer.parseInt(mResponse_data.substring(0, 2)));
                                                int i1 = Integer.parseInt(str, 16);
                                                String bin = Integer.toBinaryString(i1);
                                                String ressign ;
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
                                                break;
                                            }
                                            case "2": {
                                                //direct to ascii
                                                //byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);
                                                BigInteger val = new BigInteger(mResponse_data, 16);
                                                res = String.valueOf(val);
                                                j = 0;
                                                break;
                                            }
                                            case "3": {
                                                int val = Integer.parseInt(mResponse_data, 16);
                                                //
                                                if (0 == val) {
                                                    res = "FALSE";
                                                } else if (1 == val) {
                                                    res = "TRUE";
                                                }
                                                break;
                                            }
                                            case "4":
                                                if (mResponse_data.equals("01")) {
                                                    res = "Default session";
                                                } else if (mResponse_data.equals("03")) {
                                                    res = "Extended session";
                                                }
                                                break;
                                            case "5": {
                                                BigInteger val = new BigInteger(mResponse_data, 16);
                                                res = String.valueOf(val);
                                                j = 0;
                                                break;
                                            }
                                            case "7":
                                                res = EngineStatus(mResponse_data);
                                                break;
                                            case "8":
                                                res = CrankShaft_Signal(mResponse_data);
                                                break;
                                            case "9":
                                                res = Status_Cranking(mResponse_data);
                                                break;
                                            case "10":
                                                res = System_state(mResponse_data);
                                                break;
                                            case "11":
                                                res = Fuel_state(mResponse_data);
                                                break;
                                            case "12":
                                                byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);
                                                //removing all zeroes if present for displaying DealerID
                                                int k = 0;
                                                while (bytedata[k] == '0') {
                                                    k++;
                                                }
                                                if (k == 0) {
                                                    res = new String(bytedata);
                                                } else {
                                                    byte[] tempByteArr = new byte[bytedata.length - k];
                                                    for (int l = 0; k < bytedata.length; k++) {
                                                        tempByteArr[l++] = bytedata[k];
                                                    }
                                                    res = new String(tempByteArr);
                                                }

                                                break;
                                            case "13":
                                                res = mResponse_data;
                                                break;
                                            case "14":
                                                res = Date_Formate(mResponse_data);
                                                break;
                                            case "16":
                                                res = Relay_status(mResponse_data);
                                                break;
                                            case "17":
                                                res = displayZeroIfNeeded(mResponse_data);
                                                break;
                                            case "18":
                                                int lenpkt = Integer.parseInt(strlen, 16);
                                                mResponse_data = mResponse_data.substring(0, lenpkt);
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
                                                break;
                                            case "19":
                                                lenpkt = Integer.parseInt(strlen, 16);
                                                mResponse_data = mResponse_data.substring(0, lenpkt * 2);
                                                val = Integer.parseInt(mResponse_data, 16);
                                                //dtcdata[4].toString()
                                                e = new ExpressionBuilder(dtcdata[4])
                                                        .variables("A")
                                                        .build()
                                                        .setVariable("A", val);
                                                result = e.evaluate();

                                                if (dtcdata[6].equals("1")) //NO DATA
                                                {
                                                    res = "ON";
                                                } else {
                                                    res = "OFF";
                                                }
                                                j = 0;

                                                break;
                                            case "20":

                                                val = Integer.parseInt(mResponse_data, 16);
                                                //
                                                if (0 == val) {
                                                    res = "OFF";
                                                } else if (1 == val) {
                                                    res = "ON";
                                                }
                                                break;

                                            default:
                                                //byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);
                                                res = mResponse_data;
                                                j = 0;
                                                break;
                                        }
                                    }
                                    //i++;
                                    collectdata.add(dtcdata[0] + "," + res + " " + sign.replace("NA", "") + "," + j);

                                }
                                else
                                {
                                    collectdata.add(dtcdata[0] + "," +getString(R.string.ecunotresponding) + " "  + "," + j);
                                }

                            }
                            count++;

                        }

                    } catch (IOException e) {
                        Log.e("EMS LivePAra", e.toString());
                        AppVariables.GenLogLine("@EMS Live params "+e.getMessage());
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                                UI();

                            } catch (IOException e) {
                                //log the exception
                                e.getMessage();
                                Log.e("EMS LivePAra", e.toString());

                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.e("EMS LivePAra", e.toString());

                }
            }



        }
    }

    public  void UI()
    {
        dataModels = new ArrayList<>();

        for(int i=0;i<collectdata.size();i++)
        {
            String [] values = collectdata.get(i).split(",");
            dataModels.add(new DataModelCardView(String.valueOf(min+i+1),values[0],values[1],Integer.parseInt(values[2])));
        }
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
    public  void send_Request_Command(byte[] arr)
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
                Intent i = new Intent(context, ConnectionInterrupt.class);
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);

            }
            @Override
            public void Connected(String str)
            {

            }
        });
    }
    public  String displayZeroIfNeeded(String res)
    {
        int stringLeng = res.length();
        int counter = 0;
        String tempString = "";
        for(int i=0;i<stringLeng;i++)
        {
            if(res.charAt(i)=='0')
            {
                counter++;
            }
        }
        if(counter == stringLeng)
        {
            tempString= "0";
        }
        else
        {
            tempString=  res;
        }
        return tempString;
    }
    public String EngineStatus(String str)
    {
        //Stall, cranking, idling, running)

        String res = null;
        switch (str) {
            case "01":
                res = "Stall";
                break;
            case "02":
                res = "Cranking";
                break;
            case "03":
                res = "Idling";
                break;
            case "04":
                res = "Running";
                break;
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
        String res ;
        String dd = str.substring(0,2);
        String mm = str.substring(2,4);
        String yyyy = str.substring(4,8);
        res = yyyy+"/"+mm+"/"+dd;

        return res;
    }

    public String Relay_status(String str)
    {
        String res ;

        int val = Integer.parseInt(str);


        if(val == 1)
        {
            res = "ON";
        }
        else
        {
            res = "OFF";
        }

        return res;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(stoploop)
        {
            dataModels = new ArrayList<>();
            txttit.setText(filename.replace(".csv","").toUpperCase());
            Emsrp  threadLivePara =new Emsrp();
            threadLivePara.start();
        }

    }
/*
    @Override
    protected void onStart() {
        super.onStart();
        dataModels = new ArrayList<>();
        txttit.setText(filename.replace(".csv","").toUpperCase());
        Emsrp  threadLivePara =new Emsrp();
        threadLivePara.start();
    }*/


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        stoploop = true;
        holdflag = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stoploop = true;
        holdflag = true;
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}
