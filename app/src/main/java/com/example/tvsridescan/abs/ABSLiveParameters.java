package com.example.tvsridescan.abs;

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


public class ABSLiveParameters extends Activity
{
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

    int min =1,max =4;
    boolean loopstop = false;

    String filename ="absliveparameters.csv";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absread_dids);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        bridge = SingleTone.getBluetoothBridge();
        ImageView ivss = (ImageView) findViewById(R.id.ss);
        ivss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewss = getWindow().getDecorView().getRootView();
                AppVariables.takeScreenshot(viewss);
            }
        });


        if(AppVariables.BikeModel==2)
        {
            filename = "absliveparameters2.csv";
        }
        else if(AppVariables.BikeModel==3)
        {
            filename = "absliveparameters2.csv";
        }
        else
        {
            filename = "absliveparameters.csv";
        }


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
                if(max<15)
                {
                    min = min+4;
                    max = max+4;
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
       // SystemClock.sleep(1000);
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
                    reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open(filename)));
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
                            while (aBoolean != true && !holdflag) {
                                SystemClock.sleep(5);
                            }
                            Log.e("Res", mResponse_data);
                            if(!mResponse_data.contains("NO DATA")  && !mResponse_data.contains("@!") )
                            {
                                mResponse_data = mResponse_data.replace(" ","");
                                if(!mResponse_data.contains("7F"))
                                {
                                    String valstr = parsecmd(mResponse_data, dtcdata[2]);
                                    String finalval = OutputMeth(valstr, dtcdata[0]);
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
                                        collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + "Improper response");

                                    }
                                }

                            }
                            else
                            {
                                collectdata.add(dtcdata[0] + "," + dtcdata[1] + "," + "No response");
                            }

                        }


                    }
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
                            Log.e("ABSLP","exception");
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
              /*  finally
                {

                }*/
            }
            super.run();
        }
    }

    public String OutputMeth(String str,String num)
    {
        String output ="";

        switch (num)
        {
            case "1":
                    output = ActiveDiagSess(str);
                    break;

            case "2":
                    output = KilometerStand(str);
                    break;

            case "3":
                    output = SystemTime(str);
                    break;

            case "4":
                    output = BatteryVoltage(str);
                    break;

            case "5":
                    output = ABSRefVel(str);
                    break;

            case "6":
                    output = ABSControlmode(str);
                    break;

            case "7":
                    output = Programming_counter(str);
                    break;

            case "8":
                    output = StatusECU(str);
                    break;

            case "9":
                    output = Read_control_function_status(str);
                    break;

            case "10":
                    output = Mu_Jump_Counter(str);
                    break;

            case "11":
                    output = Status_ABS(str);
                    break;

            case "12":
                    output = Status_pump(str);
                    break;

            case "13":
                    output = Status_Valve(str);
                    break;

            case "14":
                    output = WheelSpeed(str);
                    break;

            case "15":
                    output = Status_ABS_switch(str);
                    break;


        }
        return output;
    }

    public String ActiveDiagSess(String str)
    {
        String firstbyte = str.substring(0,2);
        String secbyte = str.substring(2,4);
        String session = null;
        String sessionstatus = null;
        String resval = null;
        switch(firstbyte)
        {
            case "01": session = "Default";
                switch(secbyte)
                {
                    case "81": sessionstatus = "Application  FlashMode deactivated"; break;
                    case "82": sessionstatus = "Application  FlashMode activated"; break;
                }

                break;
            case "02": session = "Programming"; break;
            case "03": session = "Extended ";
                switch(secbyte)
                {
                    case "81": sessionstatus = "ExtendedSessionStarted"; break;
                    case "82": sessionstatus = "ExtendedSessionDTCOff_1"; break;
                    case "83": sessionstatus = "ExtendedSessionNDCDisabled "; break;
                    case "84": sessionstatus = "ExtendedSession-FlashModeActivated"; break;
                    case "85": sessionstatus = "ExtendedSessionDTCOff_2 In Coding Session"; break;
                }
                break;
            case "04": session = "Coding";
                switch(secbyte)
                {
                    case "01": sessionstatus = "ECU is in ’CodingSession’with state ’locked’"; break;
                    case "02": sessionstatus = "ECU is in ’CodingSession’with state ’unlocked’"; break;
                }
                break;

        }
        resval = "Active Session : "+session+"  \nSession Status :"+sessionstatus;
        return resval;
    }

    public String KilometerStand(String str)
    {
        String data = null;
        int a = Integer.parseInt(str,16);
        data = String.valueOf(a)+" Km";
        return  data;
    }

    public String SystemTime(String str)
    {
        String data = null;
        int a = Integer.parseInt(str,16);
        data = String.valueOf(a);
        return  data;
    }
    public String BatteryVoltage(String str)
    {
        String data = null;
        double a = (Integer.parseInt(str,16)*0.001);
        data = String.valueOf(a);
        data = AppVariables.TrimDouble(data);
        return  data;
    }

    public String ABSRefVel(String str)
    {
        String data = null;
        int a = (int) (Integer.parseInt(str,16));
        data = String.valueOf(a)+" km/h";
        return  data;
    }

    public String ABSControlmode(String str)
    {
        String val1 = "";String val2 = "";
        String data1 = str.substring(0,2);
        String data2 = str.substring(2,4);
        switch(data1)
        {
            case "01": val1 = "ABS control Front active"; break;
            case "02": val1 = "ABS Control Rear Active"; break;
            case "04": val1 = "Integral brake Rear Active"; break;
            case "10": val1 = "rlp control active"; break;
            case "20": val1 = "mhg control active"; break;
            default:  val1 ="No Data"; break;
        }

        switch(data2)
        {
            case"01": val2 = "System in FirstRun -> initialization phase of the system"; break;
            case"02": val2 = "Warning lamp active"; break;
            case"04": val2 = "ABS system deactivated by driver"; break;
            case"08": val2 = "ABS system without function";break;
            case"10": val2 = "ABS Normal operation";break;
            case"20": val2 = "ABS fallback level";break;
            case"40": val2 = "RLP Normal operation";break;
            case"80": val2 = "IB Normal operation";break;
            default:  val2 = "No Data"; break;
        }

        return val1+"\n"+val2;

    }

    public String Programming_counter(String str)
    {
        String data = "";
        String val1 = str.substring(0,2);
        String val2 = str.substring(2,4);
        String val3 = str.substring(4,6);
        String val4 = str.substring(6,8);

        int a = Integer.parseInt(val1,16);
        int b = Integer.parseInt(val2,16);
        int c = Integer.parseInt(val3,16);
        int d = Integer.parseInt(val4,16);

        data =  " reserved :" +a+
                " \nProgrammingCounterStatus :" +b+
                " \nProgrammingCounter (MSB) :" +c+
                " \nProgrammingCounter :"+d;
        return  data;
    }

    public String StatusECU(String str)
    {
        String val1 = "";

        String data1 = str.substring(0, 2);

        switch (data1)
        {
            case "00":
                val1 = "Initialisierung (Initialization)";
                break;
            case "01":
                val1 = "Normal Operation";
                break;
            case "02":
                val1 = "Normal Operation Overvoltage";
                break;
            case "03":
                val1 = "Normal Operation / Undervoltage";
                break;
            case "04":
                val1 = "Diagnose (Diagnostic)";
                break;
            case "05":
                val1 = "Diagnostic / Overvoltage";
                break;
            case "06":
                val1 = "Diagnostic / Overvoltage";
                break;
            case "07":
                val1 = "PowerDown (CAN ignition has to be considered)";
                break;
            case "08":
                val1 = "PowerSave";
                break;
            case "09":
                val1 = "Nicht verfügbar (Not available)";
                break;
            case "0A":
                val1 = "Reset";
                break;
            case "0B":
                val1 = "Reserved 11";
                break;
            case "0C":
                val1 = "Reserved 12";
                break;
            case "0D":
                val1 = "Reserved 13";
                break;
            case "0E":
                val1 = "Reserved 14";
                break;
            case "0F":
                val1 = "Not valid";
                break;
        }
        return val1;
    }
    public String Read_control_function_status(String str)
    {
        String data1 = str.substring(0,2);
        String data2 = str.substring(2,4);
        String val1 = null,val2 = null;
        switch (data1)
        {
            case "00": val1 = "ABS active";break;
            case "01": val1 = "ABS deactived";break;

        }
        switch (data2)
        {
            case "00": val2 = "Integral brake active";break;
            case "01": val2 = "Integral brake deactived";break;

        }
        return val1+"\n"+val2;
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

    public void EnergyMode(String str)
    {
        String Enerymode = null;
        switch(str)
        {
            case "01": Enerymode = "production mode"; break;
            case "02": Enerymode = "transport mode"; break;
            case "03": Enerymode = "Flash mode"; break;

        }
    }

    public String Status_ABS(String str)
    {
        String res;

        String val1 = str.substring(0,2);
        String val2 = str.substring(2,4);
        String val3 = str.substring(4,6);
        String val4 = str.substring(6,8);

        int a = Integer.parseInt(val1,16);
        int b = Integer.parseInt(val2,16);
        int c = Integer.parseInt(val3,16);
        int d = Integer.parseInt(val4,16);
        res = "Reference Voltage ="+String.valueOf(a)+" mVolt \n Pump Voltage ="+String.valueOf(b)+" mVolt \n KL30 Voltage ="+String.valueOf(c)+" mVolt \n External Vcc ="+String.valueOf(d);
        return res;
    }
    public String Status_pump(String str)
    {
        String res = null;
        int a = Integer.parseInt(str,16);
        switch (a)
        {
            case 01: res = "Pump On"; break;
            case 00: res = "Pump Off"; break;
        }
        return res;
    }
    public String Status_Valve(String str)
    {
        String res = null;
        String a = str.substring(0,2);
        String b = str.substring(2,4);
        String c = str.substring(4,6);
        String d = str.substring(6,8);
        String e = str.substring(8,10);
        String f = str.substring(10,12);

        StringBuilder stringBuilder = new StringBuilder();

        if(a.equals("00"))
        {
            stringBuilder.append("Inlet Valve Front: Deactivated \n");
        }
        else
        if(a.equals("64"))
        {
            stringBuilder.append("Inlet Valve Front: Activated \n");
        }


        if(b.equals("00"))
        {
            stringBuilder.append("Inlet Valve Rear: Deactivated \n");
        }
        else
        if(b.equals("64"))
        {
            stringBuilder.append("Inlet Valve Rear: Activated \n" );
        }

        if(c.equals("00"))
        {
            stringBuilder.append("Outlet Valve Front: Deactivated \n");
        }
        else
        if(c.equals("64"))
        {
            stringBuilder.append("Outlet Valve Front: Activated \n");
        }


        if(d.equals("00"))
        {
            stringBuilder.append("Outlet Valve Rear: Deactivated \n");
        }
        else
        if(d.equals("64"))
        {
            stringBuilder.append("Outlet Valve Rear: Activated \n");
        }


        if(e.equals("00"))
        {
            stringBuilder.append("Low Pressure Feed Valve Rear: Deactivated \n");
        }
        else
        if(e.equals("64"))
        {
            stringBuilder.append("Low Pressure Feed Valve Rear: Activated\n");
        }


        if(f.equals("00"))
        {
            stringBuilder.append("Master Cyl. Isolation Valve Rear: Deactivated");
        }
        else
        if(f.equals("64"))
        {
            stringBuilder.append("Master Cyl. Isolation Valve Rear: Activated");
        }

        res = stringBuilder.toString();

        return res;
    }

    public String WheelSpeed(String str)
    {
        String res ;
        String direc = null;

        double a = Integer.parseInt(str.substring(0,4),16)*0.01;
        double b = Integer.parseInt(str.substring(4,8),16)*0.01;
        double c = Integer.parseInt(str.substring(8,12),16)*0.01;
        int d = Integer.parseInt(str.substring(12,16),16);
        int e = Integer.parseInt(str.substring(16,18),16);
        int f = Integer.parseInt(str.substring(18,22),16);

        if(0==e)
        {
            direc = "STANDSTILL";
        }
        else
        if(1 ==e)
        {
            direc = "FORWARD";
        }
        else
        if(2 ==e)
        {
            direc = "BACKWARD";
        }
        else
        if(3 ==e)
        {
            direc = "NOT DEFINED";
        }


        /*
        *   0x00 = STANDSTILL
            0x01 = FORWARD
            0x02 = BACKWARD
            0x03 = NOT DEFINED
            */

        res = "FW: Front Velocity ="+a+" kmh\n RW: Rear Velocity ="+b+" kmh\n Vehicle Velocity="+c+" kmh\n FW: Direction of Wheel Rotation = "+d+"\n REF: Direction of Vehicle="+direc+"\n FW: Signal Quality="+f+" %";

        return res;
    }

    public String Status_ABS_switch(String str)
    {
        String res = null;
        int a = Integer.parseInt(str.substring(0,2),16);
        if(00 == a)
        {
            //Status ABS Tester
            res = "Status ABS Tester: not pressed";
        }
        else
        if(01 == a)
        {
            res = "Status ABS Tester: pressed";
        }
        return res;
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
    protected void onResume()
    {
        super.onResume();
        bridge= SingleTone.getBluetoothBridge();
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
      //  SystemClock.sleep(500);
        loopstop=true;
        holdflag = true;
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
