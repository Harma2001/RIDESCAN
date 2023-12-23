package com.example.tvsridescan.ems;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.BluetoothConversation;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.HttpCommunication;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.Progressbar.CircleProgressView;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import bwmorg.bouncycastle.crypto.digests.RIPEMD160Digest;


public class ECUFlashing extends AppCompatActivity
{
    BluetoothBridge bridge;
    static String mResponse_data = null;
    static byte[] response;
    static boolean aBoolean = false;
    byte[] cmd = new byte[0];
    byte[] seed = new byte[8];
    byte[] key = new byte[16];
    SendCmds sendCmds;
    byte[] chunkbytearr = new byte[0];
    Date d1;
    String time;
    TextView dur = null;
    boolean stopflag = false;
    CircleProgressView circleProgressView;
    ArrayList<Byte> PayloadPool = new ArrayList<>();
    int progresscount = 0;
    Button b1;
    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    TextView vin,bat;
    String securitycmd = "1003";
    long startTime =0;
    static long count =0;
    String strems = "SUCCESS",strteststamp="FAILED";
    String size = null;
    long filesize = 0;
    TextView tvper,tvfilename;
    Context context;
    boolean batteryLow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecuflashing);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BluetoothConversation.reshandle=4;
        context = ECUFlashing.this;
        relativeLayout =  findViewById(R.id.rr);
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        vin =  findViewById(R.id.vin);
        bat =  findViewById(R.id.bat);
        tvper =  findViewById(R.id.tvper);
        tvfilename =  findViewById(R.id.fn);

        circleProgressView =  findViewById(R.id.cp);
        linearLayout =  findViewById(R.id.pb);
        if(AppVariables.CheckInternet(context))
        {
            PostData postData = new PostData();
            postData.start();
        }
        else
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(ECUFlashing.this, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        b1 = (Button) findViewById(R.id.reflash);
        dur = (TextView) findViewById(R.id.dur);

        //check conditions
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                b1.setEnabled(false);
                b1.setAlpha((float) 0.5);
                d1 = new Date();
                stopflag = false;
                Duration duration = new Duration();
                duration.start();
                BluetoothConversation.delay =2000;
                sendCmds= new SendCmds();
                sendCmds.start();
            }
        });
    }

    public class Condition_Check extends Thread
    {
        @Override
        public void run()
        {
            //battery voltage from cluster
            cmd = "XTEA1\r\n".getBytes();
            aBoolean = false;
            send_Request_Command(cmd);

            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }

            cmd = "XTSH6F0\r\n".getBytes();
            aBoolean = false;
            send_Request_Command(cmd);

            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }



            Log.e("res clster",mResponse_data);
            cmd = "XTRH660\r\n".getBytes();
            aBoolean = false;
            send_Request_Command(cmd);

            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }
            Log.e("res clster",mResponse_data);

            batteryLow = false;
            /*
            * Requesting Battery command
            * */
            cmd = "22E142\r\n".getBytes();
            aBoolean = false;
            send_Request_Command(cmd);
            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }
            Log.e("cmd res",mResponse_data);

            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("ERROR"))
            {
                if( !mResponse_data.substring(0,2).equals("7F"))
                {
                    String data = mResponse_data.replace(" ", "");
                    data = data.substring(6,8);
                    final double val = Integer.parseInt(data, 16) * 0.1;
                    if(val<12)
                    {
                        batteryLow = true;
                    }

                    /**
                     * Requesting VIN
                     */
                    cmd = "22F190\r\n".getBytes(); // 220106
                    aBoolean = false;
                    send_Request_Command(cmd);
                    while (!aBoolean)
                    {
                        SystemClock.sleep(10);
                    }
                    Log.e("cmd res vin ems ",mResponse_data);

                    if(mResponse_data != null &&!mResponse_data.contains("NO DATA") &&!mResponse_data.contains("ERROR"))
                    {
                        mResponse_data = mResponse_data.replace(" ","");
                        if(mResponse_data.contains("F190"))
                        {
                            mResponse_data = AppVariables.parsecmd(mResponse_data,"F190");
                            byte[] res = DataConversion.hexStringToByteArray(mResponse_data);

                            final String vinstr = new String(res);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    vin.setText(vinstr+"");
                                }
                            });




                            //end cluster commands
                            cmd = "XTEA0\r\n".getBytes(); // 220106
                            aBoolean = false;
                            send_Request_Command(cmd);
                            while (!aBoolean)
                            {
                                SystemClock.sleep(10);
                            }
                            Log.e("cmd res vin ems ",mResponse_data);

                            cmd = "XTSH7E0\r\n".getBytes(); // 220106
                            aBoolean = false;
                            send_Request_Command(cmd);
                            while (!aBoolean)
                            {
                                SystemClock.sleep(10);
                            }
                            Log.e("cmd res vin ems ",mResponse_data);


                            cmd = "XTRH7E8\r\n".getBytes(); // 220106
                            aBoolean = false;
                            send_Request_Command(cmd);
                            while (!aBoolean)
                            {
                                SystemClock.sleep(10);
                            }
                            Log.e("cmd res vin ems ",mResponse_data);

                            cmd = "XTTM5000\r\n".getBytes(); // 220106
                            aBoolean = false;
                            send_Request_Command(cmd);
                            while (!aBoolean)
                            {
                                SystemClock.sleep(10);
                            }

                            if(batteryLow)
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run() {

                                        b1.setEnabled(false);
                                        ShowioDialog(context,"Battery Voltage is Below 12 V",1);
                                        bat.setText(String.valueOf(val) + " V");
                                    }
                                });
                            }
                            else
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        ShowioDialog(context,"All conditions satisfied,ready to flash",0);
                                        bat.setText(String.valueOf(val) + " V");
                                    }
                                });
                            }

                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    vin.setText("Something went wrong");
                                }
                            });


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ShowioDialog(context,"ECU not responding \nPlease try again",1);
                                }
                            });
                        }

                        Log.e("in handler not null", "not null");
                        aBoolean = true;
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                vin.setText("ECU not responding!");
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ShowioDialog(context,"ECU not responding \nPlease try again",1);
                            }
                        });
                    }

                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ShowioDialog(context,"Something went wrong \nPlease check  ECU",1);
                        }
                    });
                }
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowioDialog(context,"ECU not responding \nPlease try again",1);
                    }
                });
            }

            super.run();
        }
    }

    //first step
    public class SendCmds extends Thread
    {
        @Override
        public void run()
        {

            BluetoothConversation.delay = 3000;
            cmd = "1002\r\n".getBytes();
            aBoolean = false;
            send_Request_Command(cmd);
            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }
            Log.e("1002 res",mResponse_data);

            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F"))
            {
                //reguesting seed again security
                cmd = "2701\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(50);
                }
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("ERROR")&& !mResponse_data.contains("7F"))
                {
                    mResponse_data = AppVariables.parsecmd2(mResponse_data,"6701");
                    //   seed = mResponse_data.getBytes();
                    Log.e("res",mResponse_data);

                    seed = DataConversion.hexStringToByteArray(mResponse_data);

                    //unlock
                    key = UnlockSecurity(seed);


                    byte[] siddid =  DataConversion._ByteArrayToPanArray(DataConversion.hexStringToByteArray("2702"));
                    byte[] sendkey = new byte[22];

                    byte[] end = "\r\n".getBytes();
                    System.arraycopy(siddid,0,sendkey,0,4);
                    System.arraycopy(key,0,sendkey,4,16);
                    System.arraycopy(end,0,sendkey,20,2);

                    aBoolean = false;
                    send_Request_Command(sendkey);
                    while (!aBoolean)
                    {
                        SystemClock.sleep(50);
                    }
                    Log.e("res",mResponse_data);
                    if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F"))
                    {
                        mResponse_data = mResponse_data.replace(" ","");

                        //Tester Serial number
                        cmd = "2EF1980102030405060708090A\r\n".getBytes();
                        aBoolean = false;
                        send_Request_Command(cmd);
                        while (!aBoolean)
                        {
                            SystemClock.sleep(50);
                        }
                        Log.e("sno res",mResponse_data);
                        AppVariables.GenLogLine("Tester Serial RES"+mResponse_data);
                        if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F"))
                        {
                            //write date DDMMYYYY
                            d1 = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy"); //yyyy-MM-dd HH:mm:ss.SSS
                            String strdate = sdf.format(d1);
                            byte[] pandatebytes = DataConversion._ByteArrayToPanArray(strdate.getBytes());
                            byte[] datepktarr = new byte[24];
                            // cmd = ("2EF199"+strdate+"\r\n").getBytes();
                            byte[] cmdid = "2EF199".getBytes();

                            System.arraycopy(cmdid,0,datepktarr,0,6);
                            System.arraycopy(pandatebytes,0,datepktarr,6,pandatebytes.length);
                            System.arraycopy(end,0,datepktarr,6+pandatebytes.length,2);


                            aBoolean = false;
                            send_Request_Command(datepktarr);
                            while (!aBoolean)
                            {
                                SystemClock.sleep(50);
                            }
                            Log.e("date res",mResponse_data);

                            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F"))
                            {
                                progresscount++;
                                UpdateProgress(progresscount);


                                FlashingcmdReg1 flashingcmdReg1 = new FlashingcmdReg1();
                                flashingcmdReg1.start();
                            }
                            else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ShowioDialog(context,"Flashing failed !",1);
                                    }
                                });
                            }
                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ShowioDialog(context,"Flashing failed !",1);
                                }
                            });
                        }

                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ShowioDialog(context,"Flashing failed!",1);
                            }
                        });
                    }

                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ShowioDialog(context,"Flashing failed!",1);
                        }
                    });
                }

            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            ShowioDialog(context,"Flashing failed!",1);
                    }
                });
            }

            super.run();
        }
    }



    //region one
    public class FlashingcmdReg1 extends Thread
    {
        @Override
        public void run()
        {

            byte[] cmdid = "36".getBytes();
            byte[] seqbyts = new byte[2];
            byte[] chunkarr = new byte[0];
            byte[] end = "\r\n".getBytes();

            // 3101FF000101 erase flashing
            cmd = "3101FF000101\r\n".getBytes();

            aBoolean = false;
            send_Request_Command(cmd);
            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }

            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F"))
            {
                Log.e("erase flashing Res",mResponse_data);
                AppVariables.GenLogLine("erase flashing  Res"+mResponse_data);

                // 340044090C0000000C0000  Request download
                cmd = "340044090C0000000C0000\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }
                Log.e("Request download",mResponse_data);
                AppVariables.GenLogLine("Request download Res"+mResponse_data);

                //090C0000
                //09080000

                //ignoring 09 0A 0B offset locations

                int startoffset = Integer.parseInt("090C0000",16)-Integer.parseInt("09080000",16);
                mResponse_data = mResponse_data.replace(" ","");
                if(!mResponse_data.contains("NODATA") && !mResponse_data.contains("7F"))
                {
                    mResponse_data = mResponse_data.replace(" ","").substring(4,8);
                    int chunksize = Integer.parseInt(mResponse_data,16);

                    int totalbytestosend = Integer.parseInt("C0000",16);

                    int totalchunks = totalbytestosend/(chunksize-2);//4093 = 64
                    Log.e("1Totalchunk", String.valueOf(totalchunks));
                    int rempktsize = totalbytestosend%(chunksize-2);//ex 2196
                    Log.e("1remchunksize", String.valueOf(rempktsize));
                    //send chunk by taking length above mentioned.


                    chunkbytearr = new byte[(chunksize-2)*2]; // 4093
                    int i = 0;
                    byte seqno = 0;
                    //start sending chunks
                    for(i =0;i< totalchunks;)//0 1 2 . . . 64
                    {
                        seqno++;
                        seqbyts = DataConversion._ByteToPAN(seqno);
                        chunkarr = new byte[2+2+((chunksize-2)*2)+2];//sid + seqno + chunsize + end;
                        //send
                        //take 4093 from arraylist
                        for(int bytscount =0;bytscount<(chunksize-2)*2;bytscount++) // 4093 bytescount 123  . . .4093
                        {
                            chunkbytearr[bytscount] = PayloadPool.get((startoffset*2) + bytscount + (i*(chunksize-2)*2));
                            //0 = bytecount 0 1 2 3 4 8186   0 8186
                            //0
                            //4093
                            //8186
                            //..

                        }
                        System.arraycopy(cmdid,0,chunkarr,0,2); //copy cmdid
                        System.arraycopy(seqbyts,0,chunkarr,2,2);//copy seqno
                        System.arraycopy(chunkbytearr,0,chunkarr,4,chunkbytearr.length); //copy chunk pkt
                        System.arraycopy(end,0,chunkarr,4+chunkbytearr.length,2); // copy end
                        if(sendChunk(chunkarr))
                        {
                            i++;
                            progresscount++;
                            UpdateProgress(progresscount);
                            Log.e("chunk res","Positive");
                        }
                        else
                        {
                            Log.e("chunk res","neg");
                        }

                        //copy everything to chunkarray

                        // 36 && pan of seqno && chunkpakt && \r\n;

                        //send
                        //positive
                        //for incres

                    }
                    if(rempktsize>0)
                    {
                        seqno++;

                        seqbyts = DataConversion._ByteToPAN(seqno);
                        chunkbytearr = new byte[rempktsize*2]; // 4
                        chunkarr = new byte[2+2+(rempktsize*2)+2];//sid + seqno + chunsize + end;
                        for(int bytscount =0;bytscount<(rempktsize*2);bytscount++) // 4093 bytescount 123  . . .4093
                        {
                            chunkbytearr[bytscount] = PayloadPool.get(startoffset+bytscount+(i*(chunksize-2)*2));
                            //0 = bytecount 0 1 2 3 4 8186   0 8186
                            //0
                            //4093
                            //8186
                            //..

                        }
                        System.arraycopy(cmdid,0,chunkarr,0,2); //copy cmdid
                        System.arraycopy(seqbyts,0,chunkarr,2,2);//copy seqno
                        System.arraycopy(chunkbytearr,0,chunkarr,4,chunkbytearr.length); //copy chunk pkt
                        System.arraycopy(end,0,chunkarr,4+chunkbytearr.length,2); // copy end

                        if(sendChunk(chunkarr))
                        {
                            //increment
                            Log.e("chunk res","Positive");
                            finalcmdRegion1 finalcmdRegion1 = new finalcmdRegion1();
                            finalcmdRegion1.start();
                        }
                        else
                        {
                            Log.e("chunk res","neg");
                            AppVariables.GenLogLine("Sending chunk failed");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ShowioDialog(context,"Flashing failed!",1);
                                }
                            });
                        }
                        //send rem bytes
                        //send

                    }
                    else
                    {
                        finalcmdRegion1 finalcmdRegion1 = new finalcmdRegion1();
                        finalcmdRegion1.start();
                    }
                }
                else
                {
                    if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                    {
                        String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                        //String[] val = data.split(",");
                        final String finalval  = data.replace(",", " ");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ShowioDialog(context,"Flashing Failed!\n"+finalval,1);
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ShowioDialog(context,"Flashing Failed!",1);
                            }
                        });
                    }

                }

            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowioDialog(context,"Flashing Failed!",1);
                    }
                });
            }

            //34 00 44 09 08 00 00 00 04 00 00
            super.run();
        }
    }

    public class finalcmdRegion1 extends Thread
    {

        @Override
        public void run()
        {
            cmd = "37\r\n".getBytes();
            aBoolean = false;
            send_Request_Command(cmd);
            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }
            Log.e("chunk resp",mResponse_data);
            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F"))
            {
                //check memory status
                //310102020101
                cmd = "310102020101\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }
                Log.e("chunk resp",mResponse_data);

                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F"))
                {
                    mResponse_data = mResponse_data.replace(" ","");

                    progresscount++;
                    UpdateProgress(progresscount);
                    //start second region
                    FlashingcmdReg2 flashingcmdReg2 = new FlashingcmdReg2();
                    flashingcmdReg2.start();
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ShowioDialog(context,"Flashing Failed!"+"",1);
                        }
                    });

                }
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowioDialog(context,"Flashing Failed!"+"",1);
                    }
                });
            }





            super.run();
        }
    }

    //end of region one





    //start of second region


    public class FlashingcmdReg2 extends Thread
    {
        @Override
        public void run()
        {

            // 31 01 FF 00 01 02 erase flashing
            cmd = "3101FF000102\r\n".getBytes();

            byte[] cmdid = "36".getBytes();
            byte[] seqbyts = new byte[2];
            byte[] chunkarr = new byte[0];
            byte[] end = "\r\n".getBytes();

            aBoolean = false;
            send_Request_Command(cmd);
            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }
            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F"))
            {
                Log.e("erase flashing",mResponse_data);
                progresscount++;
                UpdateProgress(progresscount);

                //  Request download
                cmd = "3400440908000000040000\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }

                Log.e("Request download",mResponse_data);

                if(!mResponse_data.contains("NO DATA")&&!mResponse_data.contains("7F") )
                {
                    mResponse_data = mResponse_data.replace(" ","").substring(4,8);
                    int chunksize = Integer.parseInt(mResponse_data,16);

                    int totalbytestosend = Integer.parseInt("40000",16);

                    int totalchunks = totalbytestosend/(chunksize-2);//4093 = 64
                    int rempktsize = totalbytestosend%(chunksize-2);//ex 2196

                    //send chunk by taking length above mentioned.


                    chunkbytearr = new byte[(chunksize-2)*2]; // 4093
                    int i = 0;
                    byte seqno = 0;
                    //start sending chunks
                    for(i =0;i< totalchunks;)//0 1 2 . . . 64
                    {
                        seqno++;
                        seqbyts = DataConversion._ByteToPAN(seqno);
                        chunkarr = new byte[2+2+((chunksize-2)*2)+2];//sid + seqno + chunsize + end;
                        //send
                        //take 4093 from arraylist
                        for(int bytscount =0;bytscount<(chunksize-2)*2;bytscount++) // 4093 bytescount 123  . . .4093
                        {
                            chunkbytearr[bytscount] = PayloadPool.get(bytscount+(i*(chunksize-2)*2));
                            //0 = bytecount 0 1 2 3 4 8186   0 8186
                            //0
                            //4093
                            //8186
                            //..

                        }
                        System.arraycopy(cmdid,0,chunkarr,0,2); //copy cmdid
                        System.arraycopy(seqbyts,0,chunkarr,2,2);//copy seqno
                        System.arraycopy(chunkbytearr,0,chunkarr,4,chunkbytearr.length); //copy chunk pkt
                        System.arraycopy(end,0,chunkarr,4+chunkbytearr.length,2); // copy end
                        if(sendChunk(chunkarr))
                        {
                            i++;
                            progresscount++;
                            UpdateProgress(progresscount);
                            Log.e("chunk res","Positive");
                        }
                        else
                        {
                            Log.e("chunk res","neg");
                        }

                        //copy everything to chunkarray

                        // 36 && pan of seqno && chunkpakt && \r\n;

                        //send
                        //positive
                        //for incres

                    }
                    if(rempktsize>0)
                    {
                        seqno++;
                        seqbyts = DataConversion._ByteToPAN(seqno);
                        chunkbytearr = new byte[rempktsize*2]; // 4
                        chunkarr = new byte[2+2+(rempktsize*2)+2];//sid + seqno + chunsize + end;
                        for(int bytscount =0;bytscount<(rempktsize*2);bytscount++) // 4093 bytescount 123  . . .4093
                        {
                            chunkbytearr[bytscount] = PayloadPool.get(bytscount+(i*(chunksize-2)*2));
                            //0 = bytecount 0 1 2 3 4 8186   0 8186
                            //0
                            //4093
                            //8186
                            //..

                        }
                        System.arraycopy(cmdid,0,chunkarr,0,2); //copy cmdid
                        System.arraycopy(seqbyts,0,chunkarr,2,2);//copy seqno
                        System.arraycopy(chunkbytearr,0,chunkarr,4,chunkbytearr.length); //copy chunk pkt
                        System.arraycopy(end,0,chunkarr,4+chunkbytearr.length,2); // copy end

                        if(sendChunk(chunkarr))
                        {
                            //increment
                            Log.e("chunk res","Positive");
                            finalcmdRegion2  finalcmdRegion2 = new finalcmdRegion2();
                            finalcmdRegion2.start();
                        }
                        else
                        {
                            Log.e("chunk res","neg");
                        }
                        //send rem bytes
                        //send

                    }
                    else
                    {
                        finalcmdRegion2  finalcmdRegion2 = new finalcmdRegion2();
                        finalcmdRegion2.start();
                    }
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ShowioDialog(context,"ECU Flashing failed!",1);
                        }
                    });
                }
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowioDialog(context,"ECU Flashing failed!",1);
                    }
                });
            }




            //34 00 44 09 08 00 00 00 04 00 00
            super.run();
        }
    }

    public class finalcmdRegion2 extends Thread
    {

        @Override
        public void run()
        {
            cmd = "37\r\n".getBytes();
            aBoolean = false;
            send_Request_Command(cmd);
            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }
            Log.e("chunk resp",mResponse_data);
            AppVariables.GenLogLine("37 Res "+mResponse_data);

            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F"))
            {
                //check memory status
                //31 01 02 02 01 02
                cmd = "310102020102\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }
                Log.e("chunk resp",mResponse_data);
                AppVariables.GenLogLine("Check Mem status Res "+mResponse_data);
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F"))
                {
                    SystemClock.sleep(4000);

                    cmd = "1101\r\n".getBytes();
                    aBoolean = false;
                    send_Request_Command(cmd);
                    while (!aBoolean)
                    {
                        SystemClock.sleep(10);
                    }
                    Log.e("reset resp",mResponse_data);

                    if(mResponse_data.contains("51"))
                    {
                        //emsstatus
                        strems = "SUCCESS";
                    }
                    else
                    {
                        strems = "FAILED";
                    }

                    if(AppVariables.BikeModel==1)
                    {
                        SystemClock.sleep(10000);
                        SendTestStamp  sendTestStamp = new SendTestStamp();
                        sendTestStamp.start();
                    }
                    else
                    {

                        progresscount++;
                        UpdateProgress(progresscount);
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {

                                stopflag = true;
                                //increment
                                DialogStatus(1);

                            }
                        });
                    }
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ShowioDialog(context,"Flashing failed !",1);
                        }
                    });
                }


            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowioDialog(context,"Flashing failed !",1);
                    }
                });
            }




            super.run();
        }
    }

    //end of second region

    public boolean sendChunk(byte[] chunk)
    {
        aBoolean = false;
        send_Request_Command(chunk);
        while (!aBoolean)
        {
            SystemClock.sleep(10);
        }
        Log.e("chunk resp",mResponse_data);

        return true;

    }

    public byte[] UnlockSecurity(byte[] seed)
    {

        String symmetrickey_hexstr ="3F 9B 3E 54 43 24 22 DE ED DB 8D 9D CC 2C E7 70";
        symmetrickey_hexstr = symmetrickey_hexstr.replace(" ","");
        byte[] symm_byte = DataConversion.hexStringToByteArray(symmetrickey_hexstr); //16

        byte[] randombytes = seed;
        byte[] sendtobyte = new byte[24];

        System.arraycopy(symm_byte,0,sendtobyte,0,symm_byte.length);
        System.arraycopy(randombytes,0,sendtobyte,symm_byte.length,randombytes.length);

        RIPEMD160Digest d = new RIPEMD160Digest();
        d.reset();
        d.update (sendtobyte, 0, sendtobyte.length);
        byte[] output = new byte[d.getDigestSize()];
        d.doFinal (output, 0);
       /* String hexdata = new String(output);
        key = hexdata.substring(0,16);*/

       byte[] newone = DataConversion._ByteArrayToPanArray(output);
       byte[] keydata = new byte[16];
       System.arraycopy(newone,0,keydata,0,16);

        return keydata;

    }

    @Override
    protected void onDestroy()
    {
        BluetoothConversation.delay = 1000;
        stopflag = false;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopflag = false;

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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
    public void TimeOut()
    {
        startTime = System.currentTimeMillis();
        while (count<10000)
        {
            count = System.currentTimeMillis()-startTime;
            SystemClock.sleep(10);
        }
        if(count >= 10000)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogStatus(2);
                }
            });

        }

    }

    //reading file
    public void UpdateProgress(int i)
    {
        final float val = (float) (i*100/260);
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                circleProgressView.setValue(val);
            }
        });
    }


    public class Duration extends Thread
    {
        @Override
        public void run()
        {
            while(true)
            {
                Date d2 = new Date();

                long difference = d2.getTime() - d1.getTime();

                long millis = difference;
                time = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dur.setText(time);
                    }
                });
                super.run();
                if(stopflag)
                {

                    break;

                }
            }

        }
    }
    public void DialogStatus(int i)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_success);
        TextView emsstatus = (TextView) dialog.findViewById(R.id.emsstatus);
        TextView teststampstatus = (TextView) dialog.findViewById(R.id.teststampstatus);
        LinearLayout hintlayout = (LinearLayout) dialog.findViewById(R.id.llhint);

        Button button = (Button) dialog.findViewById(R.id.dialogb1);
        emsstatus.setText(strems);
        teststampstatus.setText(strteststamp);
        if(strems.equals("SUCCESS"))
        {
            emsstatus.setTextColor(Color.parseColor("#186300"));
        }
        else
        if(strems.equals("FAILED"))
        {
            emsstatus.setTextColor(Color.parseColor("#FFEF0700"));
        }

        if(AppVariables.BikeModel==1)
        {
            if(strteststamp.equals("SUCCESS"))
            {
                teststampstatus.setTextColor(Color.parseColor("#186300"));
                hintlayout.setVisibility(View.GONE);
            }
            else
            if(strteststamp.equals("FAILED"))
            {
                teststampstatus.setTextColor(Color.parseColor("#FFEF0700"));
                hintlayout.setVisibility(View.VISIBLE);
            }

        }
        else
        {
            LinearLayout teststamplayout = dialog.findViewById(R.id.teststamplayout);
            teststamplayout.setVisibility(View.GONE);
            hintlayout.setVisibility(View.GONE);
        }

        try
        {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    dialog.hide();
                    finish();
                    overridePendingTransition(R.anim.out,R.anim.in);

                  //  Intent i = new Intent(getApplicationContext(),EmsMenu.class);
                 //   startActivity(i);

                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("@ems flash",e.getMessage());
        }
        dialog.show();
    }

    public  void ShowioDialog(Context context, final String str, int colorcode)
    {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.io_dialog);
        try
        {
            LinearLayout linearLayout =  dialog.findViewById(R.id.ll);
            final TextView textView =  dialog.findViewById(R.id.msg);
            final Button okay = dialog.findViewById(R.id.okdialog);

            boolean errorDialog = false;
            if(colorcode == 0)
            {
                linearLayout.setBackgroundColor(Color.parseColor("#094e09"));
                errorDialog = false;
            }
            else
            if(colorcode == 1)
            {
                linearLayout.setBackgroundColor(Color.parseColor("#d9534f"));
                errorDialog = true;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(str);
                    dialog.show();
                }
            });
            final boolean finalErrorDialog = errorDialog;
            okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finalErrorDialog)
                    {
                        if(dialog.isShowing())
                        {
                            dialog.dismiss();
                        }
                        finish();

                    }
                    else
                    {
                        if(dialog.isShowing())
                        {
                            dialog.dismiss();
                        }
                    }

                }
            });


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public class SendTestStamp extends Thread
    {
        @Override
        public void run()
        {
            if(SecurityProcess())
            {
                boolean flag = false;
                cmd = (securitycmd + "\r\n").getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean) {
                    SystemClock.sleep(10);
                }

                Log.e("1002 res", mResponse_data);

                //reguesting seed again security
                cmd = "2701\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean) {
                    SystemClock.sleep(10);
                }
                mResponse_data = AppVariables.parsecmd2(mResponse_data, "6701");
                //   seed = mResponse_data.getBytes();
                Log.e("res", mResponse_data);

                if (!mResponse_data.contains("NO DATA")) {
                    seed = DataConversion.hexStringToByteArray(mResponse_data);

                    //unlock
                    key = UnlockSecurity(seed);


                    byte[] siddid = DataConversion._ByteArrayToPanArray(DataConversion.hexStringToByteArray("2702"));
                    byte[] sendkey = new byte[22];

                    byte[] end = "\r\n".getBytes();
                    System.arraycopy(siddid, 0, sendkey, 0, 4);
                    System.arraycopy(key, 0, sendkey, 4, 16);
                    System.arraycopy(end, 0, sendkey, 20, 2);

                    aBoolean = false;
                    send_Request_Command(sendkey);  // 22 bytes
                    while (!aBoolean) {
                        SystemClock.sleep(10);
                    }
                    Log.e("res", mResponse_data);
                    mResponse_data = mResponse_data.replace(" ", "");

                    cmd = "2E10FF0300\r\n".getBytes();
                    aBoolean = false;
                    send_Request_Command(cmd);
                    while (!aBoolean) {
                        SystemClock.sleep(10);
                    }
                    Log.e("res test stamp", mResponse_data);

                    if(mResponse_data.contains("6E"))
                    {
                        strteststamp = "SUCCESS";
                    }
                    else
                    {
                        strteststamp = "FAILED";
                    }
                    progresscount++;
                    UpdateProgress(progresscount);
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {

                                    stopflag = true;
                                    //increment
                                    DialogStatus(1);

                        }
                    });


                }
                else
                {
                    Toast.makeText(ECUFlashing.this, "Security Failed", Toast.LENGTH_SHORT).show();
                }

                super.run();
            }
        }
    }
    public boolean SecurityProcess()
    {
        boolean flag = false;
        cmd = (securitycmd+"\r\n").getBytes();
        aBoolean = false;
        send_Request_Command(cmd);
        while (!aBoolean)
        {
            SystemClock.sleep(10);
        }

        Log.e("1002 res",mResponse_data);

        //reguesting seed again security
        cmd = "2701\r\n".getBytes();
        aBoolean = false;
        send_Request_Command(cmd);
        while (!aBoolean)
        {
            SystemClock.sleep(10);
        }
        mResponse_data = AppVariables.parsecmd2(mResponse_data,"6701");
        //   seed = mResponse_data.getBytes();
        Log.e("res",mResponse_data);

        if(!mResponse_data.contains("NO DATA"))
        {
            seed = DataConversion.hexStringToByteArray(mResponse_data);

            //unlock
            key = UnlockSecurity(seed);


            byte[] siddid =  DataConversion._ByteArrayToPanArray(DataConversion.hexStringToByteArray("2702"));
            byte[] sendkey = new byte[22];

            byte[] end = "\r\n".getBytes();
            System.arraycopy(siddid,0,sendkey,0,4);
            System.arraycopy(key,0,sendkey,4,16);
            System.arraycopy(end,0,sendkey,20,2);

            aBoolean = false;
            send_Request_Command(sendkey);
            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }
            Log.e("res",mResponse_data);
            mResponse_data = mResponse_data.replace(" ","");
            if(mResponse_data.equals("6702"))
            {
                flag = true;
            }
            else
            {
                flag = false;
            }
        }
        else
        {
            flag = false;
        }
        return flag;
    }

    boolean doubleBackToExitPressedOnce = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            finish();
            overridePendingTransition(R.anim.out,R.anim.in);
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click Back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public class PostData extends Thread
    {
        @Override
        public void run() {

            HttpCommunication.JSONDATA="{\"api_key\" : \""+HttpCommunication.API_KEY+"\",\"email\" : \""+AppVariables.email_id+"\",\"model\":\""+AppVariables.BikeModel_str+"\"}" ;
            HttpCommunication.PostUrl ="https://amsmssi.com/DMS/api/get_falsh_files";
            String Getop = HttpCommunication.POSTDATA();

            try
            {
                JSONObject object = new JSONObject(Getop);
                HttpCommunication.Status = object.getString("status");

                if(HttpCommunication.Status.equals("Success"))
                {
                    JSONObject object1 = new JSONObject(String.valueOf(object.getJSONObject("data")));
                    HttpCommunication.Download_Path = object1.getString("file");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            File f = new File(HttpCommunication.Download_Path);
                            tvfilename.setText(f.getName());
                        }
                    });
                    size = object1.getString("size");
                    if(size.contains("MB"))
                    {
                        filesize = (long)Double.parseDouble(size.replace("MB",""))*1024*1024;
                    }

                    if(AppVariables.CheckInternet(context))
                    {
                        new DownloadFile().start();
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                Toast.makeText(context, "No internet available!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else
                if(HttpCommunication.Status.equals("false"))
                {
                    final String Err_MSG = object.getString("message");
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(ECUFlashing.this, ""+Err_MSG, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            catch (final JSONException e)
            {
                e.printStackTrace();
                Log.e("ECUFlashing",e.getMessage());
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        finish();
                        Toast.makeText(ECUFlashing.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }


            Log.e("output","Getop");

            super.run();
        }
    }

    //GET type
    static InputStream is = null;
    public class DownloadFile extends Thread
    {
        @Override
        public void run()
        {


            if(HttpCommunication.Download_Path!=null && HttpCommunication.Download_Path.length()>0)
            {
                String reclen = null;
                String rectype = null;
                float val = 0;
                byte[] Temp_Line;
                try
                {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("https://amsmssi.com/DMS/"+HttpCommunication.Download_Path);
                    Log.d("LINK","https://amsmssi.com/DMS/"+HttpCommunication.Download_Path);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    String line = null;
                    long counttot = 0,linecount =0;

                    while ((line = reader.readLine()) != null)
                    {
                        linecount = line.length();
                        counttot = counttot+linecount;
                        line = line.replace(":","");
                        rectype = line.substring(6,8);
                        reclen = line.substring(0,2);
                        byte[] record1 = line.getBytes();

                        if (rectype.equals("00"))
                        {
                            int len = Integer.parseInt(reclen,16);
                            Temp_Line = new byte[len*2];
                            val = (float) (counttot*100/filesize);
                            final float finalVal = val;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    if(finalVal <=100)
                                    {
                                        tvper.setText(finalVal +" % Downloaded");
                                    }
                                    else
                                    {
                                        tvper.setText("Please Wait");
                                    }
                                }
                            });
                            System.arraycopy(record1,8,Temp_Line,0,len*2);
                            for(int j=0;j<Temp_Line.length;j++)
                            {
                                PayloadPool.add(Temp_Line[j]);//
                            }

                        }
                    }
                    is.close();

                }
                catch (Exception e)
                {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                    finish();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ECUFlashing.this, "Download Failed Try Again.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                finally
                {
                    if(val>=100)
                    {
                        b1.setVisibility(View.VISIBLE);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvper.setText("File Downloaded");
                                linearLayout.setVisibility(View.INVISIBLE);
                                relativeLayout.setVisibility(View.VISIBLE);
                            }
                        });
                        if(AppVariables.BikeModel==1) {
                            Condition_Check condition_check = new Condition_Check();
                            condition_check.start();
                        }
                        else
                        {
                            //Cause RTR200 and 160 doesnt have Cluster or ABS  and  battery voltage response comes from cluster
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    vin.setText(AppVariables.VIN);
                                    LinearLayout batHeading= findViewById(R.id.batterylayout);
                                    batHeading.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                    else
                    {
                        finish();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ECUFlashing.this, "Download Failed Try Again.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            }
            else
            {
                finish();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ECUFlashing.this, "Download Failed Try Again, path not found", Toast.LENGTH_LONG).show();
                    }
                });
            }

            super.run();
        }
    }

}
