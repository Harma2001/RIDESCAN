package com.example.tvsridescan.ems;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

import bwmorg.bouncycastle.crypto.digests.RIPEMD160Digest;

public class EMSWriteDIDs extends Activity
{

    BluetoothBridge bridge;
    static String mResponse_data = null;
    static boolean aBoolean = true;
    static byte[] response;

    byte[] seed = new byte[8];
    byte[] key = new byte[16];
    byte[] end = "\r\n".getBytes();
    byte[] cmd;
    String securitycmd = null;
    String cmddata = null;
    String writedata = null;
    String hinttxt = null;
    Dialog dialog;
    int inputlen =0;
    int cnvsrn_type =0;
    int validate_type = 0;
    boolean holdflag = false;
    int hexopbytes = 0;
    int deftxt = 0;
    int etnum = 0;
    LinearLayout b1,b2,b3,b4,b5,b6,b7,b8;

    Animation animFadein1,animFadein2,animFadein3;
    MediaPlayer mp;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emswrite_params);
        context = EMSWriteDIDs.this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle("WRITE DID's");
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

            b1 =  findViewById(R.id.dealerId);
            b7 =  findViewById(R.id.no_of_veh_serv);
            b8 =  findViewById(R.id.timestamp);

            mp = MediaPlayer.create(this, R.raw.buttonclick);

            animFadein1 = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.clickani);
            animFadein2 = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.clickani);

            animFadein3 = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.clickani);

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    cmddata = "2EF198";
                    securitycmd = "1002";
                    hinttxt = getString(R.string.entr5to10ofdealer); //
                    validate_type = 5;
                    etnum = 0;
                    inputlen = 10 ;
                    cnvsrn_type = 6;
                    deftxt = 0;
                    b1.startAnimation(animFadein1);
                    mp.start();
                    ShowDialog();
                }
            });
            if(AppVariables.BikeModel !=1)
            {
                b7.setVisibility(View.INVISIBLE);
                b8.setVisibility(View.INVISIBLE);
            }

            b7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    cmddata = "2E1016";
                    securitycmd = "1003";
                    hinttxt = getString(R.string.entr1digit);
                    inputlen = 1;
                    etnum = 1;
                    hexopbytes = 2;
                    cnvsrn_type = 6;
                    validate_type = 1;
                    deftxt = 0;
                    b7.startAnimation(animFadein2);
                    mp.start();
                    ShowDialog();
                }
            });
            b8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    cmddata = "2E10FF";
                    securitycmd = "1003";
                    hinttxt = getString(R.string.entrtestmpvalue);
                    inputlen = 4;
                    deftxt = 1;
                    etnum = 1;
                    hexopbytes = 4;
                    cnvsrn_type = 1;
                    validate_type = 3;
                    b8.startAnimation(animFadein3);
                    mp.start();
                    ShowDialog();
                }
            });
    }
    EditText editText;
    public void ShowDialog()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.write_did_dialog);
        editText =  dialog.findViewById(R.id.et);
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(inputlen)});
        if(etnum == 0)
        {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        else
        {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if(editText.length()==inputlen)
                {
                    editText.setTextColor(Color.GREEN);
                }
                else
                {
                    editText.setTextColor(Color.parseColor("#FFFFFF"));

                }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable et) {

            }
        });


        if(deftxt ==1)
        {
            editText.setText("768");
        }
        editText.setHint(hinttxt);
        Button b1 = (Button) dialog.findViewById(R.id.submit);
        try
        {
            b1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    writedata = editText.getText().toString();

                    if(!writedata.isEmpty())
                    {
                        if(2 == validate_type)
                        {
                            long ldata = Long.parseLong(writedata);
                            if(ldata<4294967296l)
                            {
                                WriteCmd writeCmd = new WriteCmd();
                                writeCmd.start();
                                dialog.hide();
                            }
                            else
                            {
                                Toast.makeText(EMSWriteDIDs.this, hinttxt, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        if(3 == validate_type)
                        {
                            long ldata = Long.parseLong(writedata);
                            if(ldata<65535)
                            {
                                WriteCmd writeCmd = new WriteCmd();
                                writeCmd.start();
                                dialog.hide();
                            }
                            else
                            {
                                Toast.makeText(EMSWriteDIDs.this, hinttxt, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        if(4 == validate_type)
                        {

                            int rem = 10 - writedata.length();
                            StringBuilder builder = new StringBuilder();
                            for(int i=0;i<rem;i++)
                            {
                                builder.append("0");
                            }

                            String finaldata = builder.toString()+writedata;
                            writedata = finaldata;
                            WriteCmd writeCmd = new WriteCmd();
                            dialog.hide();
                            writeCmd.start();
                        }
                        else
                        if(5 == validate_type)
                        {
                            if(writedata.length()>=5 && writedata.length()<=10)
                            {
                                int rem = 10 - writedata.length();
                                StringBuilder builder = new StringBuilder();
                                for(int i=0;i<rem;i++)
                                {
                                    builder.append("0");
                                }

                                String finaldata = builder.toString()+writedata;
                                writedata = finaldata;
                                if(dialog.isShowing())
                                {
                                    dialog.hide();
                                }
                                WriteCmd writeCmd = new WriteCmd();
                                writeCmd.start();
                            }
                            else
                            {
                                Toast.makeText(EMSWriteDIDs.this, hinttxt, Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        if(1 == validate_type)
                        {
                            if(writedata.length() <= inputlen)
                            {
                                WriteCmd writeCmd = new WriteCmd();
                                dialog.hide();
                                writeCmd.start();
                            }
                            else
                            {
                                Toast.makeText(EMSWriteDIDs.this, hinttxt, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    else
                    {
                        Toast.makeText(EMSWriteDIDs.this, hinttxt, Toast.LENGTH_SHORT).show();
                    }



                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(dialog!=null)
                {
                    dialog.show();
                }
            }
        });

    }

    public class WriteCmd extends Thread
    {
        @Override
        public void run()
        {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    dialog = new Dialog(EMSWriteDIDs.this);
                    AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                }
            });
            SystemClock.sleep(1000);
            if(SecurityProcess())
            {
                byte[] cmdid = cmddata.getBytes();
                byte[] cmdpandata = new byte[0];

                String cmdstr = writedata;
                if(1 == cnvsrn_type)
                {

                    writedata = ConvertStrtoHexstr(writedata);
                    cmdpandata = DataConversion.hexStringToByteArray(writedata);
                    cmdpandata = DataConversion._ByteArrayToPanArray(cmdpandata);
                }
                else
                if(6 == cnvsrn_type)
                {
                    cmdpandata = writedata.getBytes();
                    cmdpandata = DataConversion._ByteArrayToPanArray(cmdpandata);
                }
                else
                if(2 == cnvsrn_type)
                {
                    cmdpandata = PrepareDate(writedata);
                }

                else
                {
                    cmdpandata =cmdstr.getBytes();
                }

                byte[] writecmd = new byte[6+2+cmdpandata.length];
                System.arraycopy(cmdid,0,writecmd,0,6);
                System.arraycopy(cmdpandata,0,writecmd,6,cmdpandata.length);
                System.arraycopy(end,0,writecmd,6+cmdpandata.length,2);

                aBoolean = false;
                holdflag = false;
                send_Request_Command(writecmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }
                Log.e("cmd res",mResponse_data);
                mResponse_data.replace(" ","");

                if(mResponse_data.contains("6E"))
                {
                    if(5 ==validate_type)
                    {
                        aBoolean = false;
                        holdflag = false;
                        cmd = "1101\r\n".getBytes();
                        send_Request_Command(cmd);
                        while (!aBoolean && !holdflag)
                        {
                            SystemClock.sleep(10);
                        }
                        Log.e("cmd res",mResponse_data);
                        SystemClock.sleep(6000);
                    }
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

                    if (mResponse_data.contains("7F") && mResponse_data.length() == 6) {
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
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                AppVariables.ShowDialog(dialog,getString(R.string.negativeresponse),true,0);
                            }
                        });
                    }
                }

                cmd = "1001\r\n".getBytes();
                send_Request_Command(cmd);
                while (!aBoolean)
                {
                    SystemClock.sleep(10);
                }
                Log.e("res",mResponse_data);
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
        while (!aBoolean && !holdflag)
        {
            SystemClock.sleep(10);
        }

        Log.e("1002 res",mResponse_data);
        if(!mResponse_data.contains("NO DATA") && mResponse_data.contains("50"))
        {
            //reguesting seed again security
            cmd = "2701\r\n".getBytes();
            aBoolean = false;
            holdflag = false;
            send_Request_Command(cmd);
            while (!aBoolean && !holdflag)
            {
                SystemClock.sleep(10);
            }
            if(!mResponse_data.contains("NO DATA") && mResponse_data.contains("67"))
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
                while (!aBoolean && !holdflag)
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

    @Override
    protected void onDestroy()
    {
        holdflag = true;
        super.onDestroy();
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


    public String ConvertStrtoHexstr(String str)
    {
        String strdata = str;
        long val = Long.parseLong(strdata);
        String hexdata = String.format("%X", val);
        int len = hexdata.length();
        int rem = hexopbytes-len;
        StringBuilder stbr = new StringBuilder();
        for(int i=0;i<rem;i++)
        {
            stbr.append("0");
        }
        stbr.append(hexdata);

        String finalhexdata = stbr.toString();

        return finalhexdata;
    }

    public byte[] PrepareDate(String str)
    {
        byte[] op = new byte[0];

        String timedate = str;//"09112017";

        byte DD 	= (byte) Integer.parseInt(timedate.substring(0,2));
        byte MM 	= (byte) Integer.parseInt(timedate.substring(2,4));
        short YYYY = (short) Integer.parseInt(timedate.substring(4,8));

        byte[] timedatebytes = new byte[8];

        byte[] DDbytes = DataConversion._ByteToPAN(DD);
        byte[] MMbytes = DataConversion._ByteToPAN(MM);
        byte[] YYYYbytes = DataConversion._HWordToPAN(YYYY);

        System.arraycopy(DDbytes, 0, timedatebytes, 0, 2);
        System.arraycopy(MMbytes, 0, timedatebytes, 2, 2);
        System.arraycopy(YYYYbytes, 0, timedatebytes, 4, 4);
        op = timedatebytes;
        return op;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
