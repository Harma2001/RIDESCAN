package com.example.tvsridescan.abs;

import static com.example.tvsridescan.Library.AppVariables.parsecmd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.adapters.CustomAdapter_CardView;
import com.example.tvsridescan.adapters.DataModelCardView;
import com.example.tvsridescan.connection.ConnectionInterrupt;

import java.util.ArrayList;

public class ABSReadDIDs extends Activity
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
    static int count =0;
    LinearLayout progressBar;
    RelativeLayout relativeLayout;
    boolean holdflag = false;
    boolean backholdflag = true;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absread_dids);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        progressBar = (LinearLayout) findViewById(R.id.pb) ;
        bridge = SingleTone.getBluetoothBridge();
        Resposne();
        relativeLayout = (RelativeLayout) findViewById(R.id.rl);

        SendCommands sendCommands = new SendCommands();
        sendCommands.start();
    }
    public  void UI()
    {
        dataModels = new ArrayList<>();

        for(int i = 0; i< collectdata.size(); i++)
        {
        String[] values = collectdata.get(i).toString().split(",");
        dataModels.add(new DataModelCardView(values[0],values[1],values[2],0));

        }
        adapter = new CustomAdapter_CardView(dataModels, getApplicationContext());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView = (ListView) findViewById(R.id.lv);
                listView.setAdapter(adapter);
                listView.setVisibility(View.VISIBLE);
            }
        });

    }
    @Override
    protected void onDestroy()
    {
        holdflag = true;
        SystemClock.sleep(1000);
        super.onDestroy();
    }
    public class SendCommands extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                collectdata  = new ArrayList();

                byte[] cmd = "22F186\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                count = 0;
                String val = parsecmd(mResponse_data,"F186");
                data = ActiveDiagSess(val);
                collectdata.add(" "+1+",Active Diagnostic Session,"+data);


                cmd = "221000\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"1000");
                data = Teststamp(val);
                collectdata.add(" "+2+",Test Stamp,"+data);

                cmd = "221700\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"1700");
                data = KilometerStand(val);
                collectdata.add(" "+3+",Kilometer Stand,"+data);


                cmd = "221701\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"1701");
                data = SystemTime(val);
                collectdata.add(" "+4+",SystemTime,"+data);


                aBoolean = false;
                holdflag = false;
                cmd = "22E0E2\r\n".getBytes();
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"E0E2");
                data = AppVariables.TrimDouble(BatteryVoltage(val));
                collectdata.add(" "+5+",Battery Voltage,"+data+" V");


                cmd = "22E0DF\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"E0DF");
                data = ABSRefVel(val);
                collectdata.add(" "+6+",ABS Reference Velocity,"+data);


                cmd = "22E0E1\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"E0E1");
                data = ABSControlmode(val);
                collectdata.add(" "+7+",ABS Control mode,"+data);


                cmd = "222502\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"2502");
                data = Programming_counter(val);
                collectdata.add(" "+8+",Programming Counter,"+data);


                cmd = "22F101\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"F101");
                data = Firmware_Ver(val);
                collectdata.add(" "+9+",Firmware Version,"+data);


                cmd = "22F150\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"F150");
                data = Sgbd_id(val);
                collectdata.add(" "+10+",SGBD ID,"+data);


                cmd = "22F18B\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"F18B");
                data = ECU_manufacturing_data(val);
                collectdata.add(" "+11+",ECU manufacturing data,"+data);


                cmd = "22F18C\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"F18C");
                data = ECU_Serial_number(val);
                collectdata.add(" "+12+",ECU Serial number,"+data);


                cmd = "224001\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"4001");
                data = StatusECU(val);
                collectdata.add(" "+13+",Status ECU,"+data);



                cmd = "224003\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"4003");
                data = Read_control_function_status(val);
                collectdata.add(" "+14+",Read control function status,"+data);



                cmd = "22E0D8\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"E0D8");
                data = Mu_Jump_Counter(val);
                collectdata.add(" "+15+",Mu Jump Counter,"+data);



                cmd = "22E0DC\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"E0DC");
                data = Status_ABS(val);
                collectdata.add(" "+16+",Status ABS,"+data);


                cmd = "22E0DA\r\n".getBytes(); aBoolean = false;
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"E0DA");
                data = Status_pump(val);
                collectdata.add(" "+17+",Status pump,"+data);



                cmd = "22E0DB\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);SystemClock.sleep(250);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"E0DB");
                data = Status_Valve(val);
                collectdata.add(" "+18+",Status Valve,"+data);



                cmd = "22E0DDr\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);SystemClock.sleep(250);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"E0DD");
                data = WheelSpeed(val);
                collectdata.add(" "+20+",Wheel Speed,"+data);



                cmd = "22E198\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                val = parsecmd(mResponse_data,"E198");
                data = Status_ABS_switch(val);
                //  data = (val);
                collectdata.add(" "+21+",Status ABS switch,"+data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        backholdflag = false;
                        progressBar.setVisibility(View.INVISIBLE);
                        relativeLayout.setVisibility(View.VISIBLE);
                    }
                });
                UI();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            super.run();
        }
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
    public String Teststamp(String str)
    {
        String data = null;
        int a = Integer.parseInt(str,16);
        data = String.valueOf(a);
        return  data;
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
        String val1 = null;String val2 = null;
        String data1 = str.substring(0,2);
        String data2 = str.substring(2,4);
        switch(data1)
        {
            case "01": val1 = "ABS Regelung VA aktiv  (ABS control Front active)"; break;
            case "02": val1 = "ABS Regelung HA aktiv (ABS Control Rear Active)"; break;
            case "04": val1 = "Integralbremse HA aktiv (Integral brake Rear Active)"; break;
            case "10": val1 = "RLP Regelung aktiv (rlp control active)"; break;
            case "20": val1 = "MHG Regelung aktiv (mhg control active)"; break;
            default:  val1 ="No Data"; break;
        }

        switch(data2)
        {
            case"01": val2 = "System im FirstRun -> Initialisierungsphase der Anlage"; break;
            case"02": val2 = "Warnlampe _ddres (Warning lamp active)"; break;
            case"04": val2 = "ABS System durch Fahrer abgeschaltet (ABS system deactivated by driver)"; break;
            case"08": val2 = "ABS System ohne Funktion";break;
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
        String data = null;
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
    public String Firmware_Ver(String str)
    {
        byte[] data = DataConversion.hexStringToByteArray(str);
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<data.length;i++)
        {
            builder.append(data[i]);
        }
        String res = builder.toString();

        return  res;
    }

    public String Sgbd_id(String str)
    {
        String data = null;
       // int a = (int) (Integer.parseInt(str,16));
        //data = String.valueOf(a);
        return  str;
    }
    public String ECU_manufacturing_data(String str)
    {
        String data = null;
        //int a = (int) (Integer.parseInt(str,16));
        //data = String.valueOf(a);
        return  str;
    }
    public String ECU_Serial_number(String str)
    {
        String data = new String(DataConversion.hexStringToByteArray(str));
      //  int a = (int) (Integer.parseInt(str,16));
       // data = str;
        return  data;
    }
    //Vin
    public String Vin(String str)
    {
        String data = str;
//        int a = (int) (Integer.parseInt(str,16));
//        data = String.valueOf(a);
        return  data;
    }

    public String StatusECU(String str)
    {
        String val1 = null;

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
        String val1 = null,val2 = null;
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
            stringBuilder.append("Low Pressure Feed Valve Rear: Activated \n");
        }


        if(f.equals("00"))
        {
            stringBuilder.append("Master Cyl. Isolation Valve Rear: Deactivated \n");
        }
        else
        if(f.equals("64"))
        {
            stringBuilder.append("Master Cyl. Isolation Valve Rear: Activated \n");
        }

        res = stringBuilder.toString();

        return res;
    }

    public String WheelSpeed(String str)
    {
        String res = null;
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

            }
            @Override
            public void Connected(String str)
            {

            }
        });
    }
    //on backpressed
    boolean doubleBackToExitPressedOnce = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed()
    {
        if(backholdflag)
        {
            if (doubleBackToExitPressedOnce)
            {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please wait.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    doubleBackToExitPressedOnce=false;
                }
            }, 100);
        }
        else
        {
            super.onBackPressed();
            return;
        }

    }

}
