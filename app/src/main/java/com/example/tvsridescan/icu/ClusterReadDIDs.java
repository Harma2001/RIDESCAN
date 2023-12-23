package com.example.tvsridescan.icu;

import static com.example.tvsridescan.Library.AppVariables.parsecmd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

public class ClusterReadDIDs extends Activity
{
    BluetoothBridge bridge;
    static String mResponse_data = null;
    static byte[] response;
    static boolean aBoolean = false;

    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10,tv11,tv12;
    boolean holdflag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_clstrread_dids);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            bridge = SingleTone.getBluetoothBridge();
            Resposne();

            AbsReadDid absReadDid = new AbsReadDid();
            absReadDid.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy()
    {
        SystemClock.sleep(1000);
        holdflag = true;
        super.onDestroy();
    }


    public class AbsReadDid extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                SystemClock.sleep(1000);
                //start battery voltate-------------
                byte[] cmd = "22E142\r\n".getBytes();
                send_Request_Command(cmd);
                aBoolean = false;
                holdflag = false;
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                if(!mResponse_data.contains("NO DATA"))
                {

                }
                else
                {

                }

                String valstr = parsecmd(mResponse_data,"E142");
                int val = (int) (Integer.parseInt(valstr,16)*0.1);
                final int finalVal1 = val;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv1.setText(finalVal1 +" V");
                    }
                });


                //indicator status
                cmd = "22E1B3\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }

                Log.e("Res", mResponse_data);
                mResponse_data = parsecmd(mResponse_data,"E1B3");
                String v1 = mResponse_data.substring(0,4);
                String v2 = mResponse_data.substring(4,8);
                String v3 = mResponse_data.substring(8,12);
                String v4 = mResponse_data.substring(12,16);

                final int val1 = Integer.parseInt(v1,16);
                final int val2 = Integer.parseInt(v2,16);
                final int val3 = Integer.parseInt(v3,16);
                final int val4 = Integer.parseInt(v4,16);
                final int finalVal = val;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv2.setText("Left Front Indicator Current ="+val1+" mA \nRight Front Indicator Current ="+val2+" mA \nLeft Rear Indicator Current ="+val3+" mA \nRight Rear Indicator Current ="+val4+" mA");
                    }
                });


                //Photosensor Value
                cmd = "22E11B\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                mResponse_data = parsecmd(mResponse_data,"E11B");
                valstr = mResponse_data;
                val = (int) (Integer.parseInt(valstr,16)*1);
                final int finalVal2 = val;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv3.setText(finalVal2 +" %");
                    }
                });



                //Wake Line Status

                cmd = "22E11C\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                mResponse_data = parsecmd(mResponse_data,"E11C");
                valstr = mResponse_data;
                val = (int) (Integer.parseInt(valstr,16)*1);
                final int finalVal3 = val;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if(finalVal3==0)
                        {
                            tv4.setText("Inactive");
                        }
                        else
                        if(finalVal3==1)
                        {
                            tv4.setText("Active");
                        }

                    }
                });


                //Displayed Odometer value
                cmd = "22E119\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }

                Log.e("Res", mResponse_data);
                mResponse_data = parsecmd(mResponse_data,"E119");
                valstr = mResponse_data;
                val = (int) (Integer.parseInt(valstr,16)*1);
                final int finalVal5 = val;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv5.setText(finalVal5 +" Km");
                    }
                });

                cmd = "22D10D\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                mResponse_data = parsecmd(mResponse_data,"D10D");
                String valstr1 = mResponse_data.substring(0,8);
                String valstr2 = mResponse_data.substring(8,16);
                final int vala = (int) (Integer.parseInt(valstr1,16)*1);
                final int valb = (int) (Integer.parseInt(valstr2,16)*1);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv6.setText("RAM ="+vala +" Km \nEEPROM ="+valb+"Km");
                    }
                });

                cmd = "22D114\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }

                Log.e("Res", mResponse_data);
                mResponse_data = parsecmd(mResponse_data,"D114");
                valstr = mResponse_data;
                val = (int) (Integer.parseInt(valstr,16)*1);
                final int finalVal7 = val;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv7.setText(finalVal7 +" Km");
                    }
                });


                cmd = "22E12C\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                mResponse_data = parsecmd(mResponse_data,"E12C");

                String strvaldate = mResponse_data.substring(0,2);
                String strvalmon = mResponse_data.substring(2,4);
                String strvalyear = mResponse_data.substring(4,8);

                final int valdate = (int) (Integer.parseInt(strvaldate,16));
                final int valmon = (int) (Integer.parseInt(strvalmon,16));
                final int valyear = (int) (Integer.parseInt(strvalyear,16));
                final int finalVal9 = val;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv8.setText(valdate +"-"+valmon+"-"+valyear+"");
                    }
                });


                cmd = "22E12D\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                mResponse_data = parsecmd(mResponse_data,"E12D");
                val = (int) (Integer.parseInt(mResponse_data,16)*1);
                final int finalVal10 = val;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv9.setText(finalVal10 +" Km");
                    }
                });



                cmd = "22E064\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }
                Log.e("Res", mResponse_data);
                mResponse_data = parsecmd(mResponse_data,"E064");
                valstr = mResponse_data.substring(0,4);
                final int vala1 = (int) (Integer.parseInt(valstr,16)*1);
                valstr = mResponse_data.substring(4,8);
                final int vala2 = (int) (Integer.parseInt(valstr,16)*1);

                final int finalVal8 = val;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv10.setText("Fuel Tank Capacity ="+vala1 +" ml \nFuel Tank Sensor ="+vala2+" ohm");
                    }
                });



                cmd = "22E12B\r\n".getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(50);
                }

                Log.e("Res", mResponse_data);
                mResponse_data = parsecmd(mResponse_data,"E12B");
                final String strhour  = mResponse_data.substring(0,2);
                final String strmin  = mResponse_data.substring(2,4);
                final String strsec  = mResponse_data.substring(4,6);
                final String strdate  = mResponse_data.substring(6,8);
                final String strmon  = mResponse_data.substring(8,10);
                final String stryear  = mResponse_data.substring(10,14);

                final int valhour = (int) (Integer.parseInt(strhour,16)*1);
                final int valmin = (int) (Integer.parseInt(strmin,16)*1);
                final int valsec = (int) (Integer.parseInt(strsec,16)*1);
                final int valdate1 = (int) (Integer.parseInt(strdate,16)*1);
                final int valmon1 = (int) (Integer.parseInt(strmon,16)*1);
                final int valyear1 = (int) (Integer.parseInt(stryear,16)*1);

                final int finalVal11 = val;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv11.setText(" Time "+valhour+":"+valmin+":"+valsec+"   Date: "+valdate1+"-"+valmon1+"-"+valyear1);
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            super.run();

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
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
