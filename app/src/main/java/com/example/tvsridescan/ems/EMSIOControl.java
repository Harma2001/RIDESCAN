package com.example.tvsridescan.ems;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

import bwmorg.bouncycastle.crypto.digests.RIPEMD160Digest;

public class EMSIOControl extends Activity
{


    BluetoothBridge bridge;
    Dialog dialog;
    static String mResponse_data = null;
    static byte[] response;
    static boolean aBoolean = false;
    byte[] cmd = new byte[0];
    String cmddata = null;

    String securitycmd = "1003";
    byte[] seed = new byte[8];
    byte[] key = new byte[16];
    byte[] end = "\r\n".getBytes();
    boolean holdflag = false;
    LinearLayout mainll;
    long count =0;
    long startTime =0;
    LinearLayout ll2;


    Button Button1,Button2,Button3,Button4,Button5,Button6,Button7,Button8;
    Context context;
    private  static  final String TAG = "EMSIOcontrol";

    String[] cmdon ={"2FA8000301","2FA8010301","2FA8020301","2FA8030301","2FA8040301","2FA808030001","2FA809030001","2FA80A0301","2FA8060301","2FA8100301","2FA8130301","2FA8150301","2FA8090301"};
    //                       0        1         2            3             4            5                  6           7          8              9             10            11           12
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emsiocontrol);
        context = EMSIOControl.this;




        AppVariables.DialogDelay = 4000;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ll2 =  findViewById(R.id.ll1);
        ImageView bikeview =  findViewById(R.id.bikeview);
        if(AppVariables.BikeModel ==1)
        {
            bikeview.setImageResource(R.drawable.ic_biketopview);
        }
        else
        {
            ll2.setVisibility(View.INVISIBLE);
            bikeview.setImageResource(R.drawable.ic_biketopview);
        }
        dialog = new Dialog(this);
        Button1 =  findViewById(R.id.Button1);
        Button2 =  findViewById(R.id.Button2);
        Button3 =  findViewById(R.id.Button3);

        Button4 =  findViewById(R.id.Button5);
        Button5 =  findViewById(R.id.Button6);
        Button6 =  findViewById(R.id.Button7);
        Button7 =  findViewById(R.id.Button8);
        Button8 =  findViewById(R.id.Button4);

        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmddata = cmdon[1-1];
                WriteCmd writeCmd = new WriteCmd();
                writeCmd.start();
            }
        });
        Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmddata = cmdon[2-1];
                WriteCmd writeCmd = new WriteCmd();
                writeCmd.start();
            }
        });
        Button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cmddata = cmdon[5-1];
                WriteCmd writeCmd = new WriteCmd();
                writeCmd.start();
            }
        });

        Button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //06
                cmddata = cmdon[9-1];
                WriteCmd writeCmd = new WriteCmd();
                writeCmd.start();
            }
        });

        Button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //10
                cmddata = cmdon[10-1];
                WriteCmd writeCmd = new WriteCmd();
                writeCmd.start();
            }
        });

        Button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//13
                cmddata = cmdon[11-1];
                WriteCmd writeCmd = new WriteCmd();
                writeCmd.start();
            }
        });

        Button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//15
                cmddata = cmdon[12-1];
                WriteCmd writeCmd = new WriteCmd();
                writeCmd.start();
            }
        });

        Button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//0A
                cmddata = cmdon[8-1];
                WriteCmd writeCmd = new WriteCmd();
                writeCmd.start();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        bridge = SingleTone.getBluetoothBridge();
        if(bridge!=null)
        {
            Resposne();
        }
        else
        {
            Intent i = new Intent(getApplicationContext(), ConnectionInterrupt.class);
            startActivity(i);
            overridePendingTransition(R.anim.out,R.anim.in);

        }
    }

    public class WriteCmd extends Thread
    {
        @Override
        public void run()
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                }
            });
            SystemClock.sleep(1000);

            if(SecurityProcess())
            {
                if(cmddata.length()%2==0)
                {
                    cmd = (cmddata+"\r\n").getBytes(); //

                    aBoolean = false;
                    holdflag = false;

                    send_Request_Command(cmd);
                    startTime = System.currentTimeMillis();
                    while (!aBoolean )
                    {
                        SystemClock.sleep(10);
                    }

                 //   SystemClock.sleep(100);
                    Log.e("cmd res",mResponse_data);
                    mResponse_data.replace(" ","");
                    if(mResponse_data.contains("6F"))
                    {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                AppVariables.ShowDialog(dialog,getString(R.string.success),true,1);
                            }
                        });
                    }
                    else
                    {
                        mResponse_data = mResponse_data.replace(" ","");

                        if (mResponse_data.contains("7F") && mResponse_data.length() == 6)
                        {
                            final String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                            final String[] arr = data.split(",");
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void run() {
                                    AppVariables.ShowDialog(dialog,arr[1],true,0);
                                }
                            });
                        }
                        else if(mResponse_data.contains("NODATA") && mResponse_data.contains("@!"))
                        {
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void run() {
                                    AppVariables.ShowDialog(dialog,getString(R.string.ecunotresponding),true,0);
                                }
                            });
                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void run() {
                                    AppVariables.ShowDialog(dialog,getString(R.string.improperresponse),true,0);
                                }
                            });
                        }
                    }
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog,getString(R.string.cmdincorrect),true,0);
                        }
                    });
                }
            }
            else
            {

                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog,getString(R.string.ecunotresponding),true,0);
                    }
                });
            }

            super.run();
        }
    }

    public boolean SecurityProcess()
    {
        boolean flag = false;
        cmd = (securitycmd+"\r\n").getBytes();
        aBoolean = false;
        holdflag = false;
        send_Request_Command(cmd);
        while (!aBoolean )
        {
            SystemClock.sleep(10);
        }
        if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!") && mResponse_data.contains("50"))
        {
            Log.e("1003 res",mResponse_data);

            //reguesting seed again security
            cmd = "2701\r\n".getBytes();
            aBoolean = false;
            holdflag = false;
            send_Request_Command(cmd);
            while (!aBoolean && !holdflag)
            {
                SystemClock.sleep(10);
            }
            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!")&& mResponse_data.contains("67"))
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
                holdflag = false;
                send_Request_Command(sendkey);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }

                Log.e("res",mResponse_data);
                if(!mResponse_data.contains("NO DATA")&& !mResponse_data.contains("ERROR"))
                {
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
    protected void onDestroy()
    {
        holdflag = true;
        AppVariables.DialogDelay = 2000;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.out,R.anim.in);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}
