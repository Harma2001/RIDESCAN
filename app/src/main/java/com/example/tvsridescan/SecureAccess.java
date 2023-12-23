package com.example.tvsridescan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.HttpCommunication;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.connection.ConnectionInterrupt;
import com.example.tvsridescan.ems.ECUFlashing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bwmorg.bouncycastle.crypto.digests.RIPEMD160Digest;

public class SecureAccess extends AppCompatActivity
{

    private static final String TAG ="SecureAccess";
    private static  final String EMSTAG= "SetEMS";
    private static final String ICUSTAG = "SetICU";
    private static final String ABSSTAG = "SetABS";
    BluetoothBridge bridge;
    static String mResponse_data = null;
    static boolean aBoolean = true;
    static byte[] response;

    Button btnflash,btnvin,btneng_rhcount,btnkmrcount;
    Button btnsb;
    Button btnodo,btnodoval,btnVinICu;

    Button btnchng;
    LinearLayout  btnems,btnabs,btnicu;
    LinearLayout ll1,ll2,ll3;
    int flownumber =0;
    byte[] seed = new byte[8];
    byte[] key = new byte[16];
    byte[] end = "\r\n".getBytes();
    byte[] cmd;
    String securitycmd = null;
    String cmddata = null;
    String writedata = null;
    String hinttxt = null;
    int inputlen =0;
    int cnvsrn_type =0;
    int validate_type = 0;
    boolean holdflag = false;
    int hexopbytes = 0;
    int deftxt = 0;
    int etnum = 0;

    int byteconv = 0;
    Dialog dialog,dialogsec;

    public static ArrayList Email_list = null;

    TextView txtsecemail;
    int serverbleeding = 0;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_access);
        context = SecureAccess.this;
        dialog = new Dialog(this);
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        ll1 = findViewById(R.id.emsll);
        ll2 =  findViewById(R.id.absll);
        ll3 =  findViewById(R.id.icull);

        btnems = findViewById(R.id.ems);
        btnabs = findViewById(R.id.abs);
        btnicu =  findViewById(R.id.icu);

        txtsecemail =  findViewById(R.id.sec_email);
        AppVariables.RetSecEmail(getApplicationContext());        // retrieving saved secured email address
        txtsecemail.setText(AppVariables.Secured_Email_Id);

        btnflash =  findViewById(R.id.flashing);
        btnvin =  findViewById(R.id.vin);
        btneng_rhcount =findViewById(R.id.engine_run);
        btnkmrcount = findViewById(R.id.kilrun);

        btnsb = findViewById(R.id.servbleed);
        btnodo =  findViewById(R.id.odo);
        btnodoval = findViewById(R.id.odoval);
        btnVinICu= findViewById(R.id.vinIcu);

        btnchng =  findViewById(R.id.change);

        if(AppVariables.BikeModel != 1)
        {
            btnems.setVisibility(View.INVISIBLE);
            btnabs.setVisibility(View.INVISIBLE);
            btnicu.setVisibility(View.INVISIBLE);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnems.setBackground(getDrawable(R.drawable.bg_gradient_did31));
            btnabs.setBackground(getDrawable(R.drawable.bg_gradient_did21));
            btnicu.setBackground(getDrawable(R.drawable.bg_gradient_did21));

        }
        btnems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnems.setBackground(getDrawable(R.drawable.bg_gradient_did31));
                    btnabs.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                    btnicu.setBackground(getDrawable(R.drawable.bg_gradient_did21));

                }

                SetEms setEms = new SetEms();
                setEms.start();
                ll1.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.INVISIBLE);
                ll3.setVisibility(View.INVISIBLE);
            }
        });

        btnabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnems.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                    btnabs.setBackground(getDrawable(R.drawable.bg_gradient_did31));
                    btnicu.setBackground(getDrawable(R.drawable.bg_gradient_did21));

                }
                SetABS setABS = new SetABS();
                setABS.start();
                ll1.setVisibility(View.INVISIBLE);
                ll2.setVisibility(View.VISIBLE);
                ll3.setVisibility(View.INVISIBLE);
            }
        });
        btnicu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnems.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                    btnabs.setBackground(getDrawable(R.drawable.bg_gradient_did21));
                    btnicu.setBackground(getDrawable(R.drawable.bg_gradient_did31));
                }
                SetICU setICU = new SetICU();
                setICU.start();

                ll1.setVisibility(View.INVISIBLE);
                ll2.setVisibility(View.INVISIBLE);
                ll3.setVisibility(View.VISIBLE);
            }
        });

        btnflash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                flownumber =1;
                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    PushNotification();
                }
                else
                {
                    Toast.makeText(SecureAccess.this, "No internet!", Toast.LENGTH_SHORT).show();
                }
               // startActivity(new Intent(getApplicationContext(),ECUFlashing.class));

            }
        });
        btnvin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                flownumber =2;

                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    PushNotification();
                }
                else
                {
                    Toast.makeText(SecureAccess.this, "No internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btneng_rhcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                flownumber =3;
                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    PushNotification();
                }
                else
                {
                    Toast.makeText(SecureAccess.this, "No internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnkmrcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                flownumber =4;
                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    PushNotification();
                }
                else
                {
                    Toast.makeText(SecureAccess.this, "No internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnsb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                flownumber =5;
                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    PushNotification();
                }
                else
                {
                    Toast.makeText(SecureAccess.this, "No internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flownumber = 6;
                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    PushNotification();
                }
                else
                {
                    Toast.makeText(SecureAccess.this, "No internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnodoval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flownumber = 7;
                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    PushNotification();
                }
                else
                {
                    Toast.makeText(SecureAccess.this, "No internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnVinICu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flownumber = 8;
                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    PushNotification();
                }
                else
                {
                    Toast.makeText(SecureAccess.this, "No internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnchng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    PostData postData = new PostData();
                    postData.start();
                }
                else
                {
                    Toast.makeText(SecureAccess.this, "No Internet!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //Setting EMS by default
        SetEms setEms = new SetEms();
        setEms.start();

    }
    public void PushNotification()
    {
        if(!AppVariables.Secured_Email_Id.equals("Not Selected"))
        {
            //Email Id already selected ,Sending OTP to Selected (or) Stored Secure Email Id.
            GetSecuredOTP getSecuredOTP = new GetSecuredOTP();
            getSecuredOTP.start();
        }
        else
        {
            //Retrieving Secure Email IDs from the MAHLE Database and popping them

            PostData postData = new PostData();
            postData.start();
        }

    }


    public void VIN()
    {
        cmddata = "2EF190";
        securitycmd = "1003";
        hinttxt = "Enter 17 Characters";
        inputlen = 17;
        etnum = 0;
        cnvsrn_type = 6;
        validate_type = 1;
        deftxt = 0;
        ShowDialog();
    }

    public void ServiceBleeding()
    {
        cmddata = "2E3101B00D";//05 06 07 08
        securitycmd = "1003";
        hinttxt = "Enter A Digit between 05 to 08 ";
        inputlen = 2;
        etnum = 1;
        cnvsrn_type = 1;
        validate_type = 8;
        deftxt = 0;
        ShowDialog();
    }

    //odometer offeset
    public void OdometerOffset()
    {
        cmddata = "2ED114";
        securitycmd = "1003";
        validate_type = 7;
        cnvsrn_type = 9;
        byteconv = 2;
        etnum = 1;
        hinttxt = "Enter The Value from 0 - 255 km";
        ShowDialog();
    }
    public void OdoValue()
    {
        cmddata = "2EE119";
        securitycmd = "1003";
        byteconv =3;
        validate_type = 10;
        etnum =1;

        hinttxt = "Enter Number in Range 0 - 4294967295";
        ShowDialog();
    }
    public  void VinICU()
    {
        cmddata = "2EF190";
        securitycmd = "1003";
        hinttxt = "Enter 17 Characters";
        inputlen = 17;
        etnum = 0;
        cnvsrn_type = 6;
        validate_type = 11;
        deftxt = 0;
        ShowDialog();
    }




    public void KilRun()
    {
        cmddata = "2E0100";
        securitycmd = "1003";
        hinttxt = "Enter Number in Range 0 - 4294967295"; //4294967294
        inputlen = 10;
        validate_type = 9;//9
        cnvsrn_type =1;
        hexopbytes =8;
        etnum =1;
        ShowDialog();
    }
    public void EngRunHour()
    {
        cmddata = "2E0120";
        securitycmd = "1003";
        hinttxt = "Enter Number in Range 0 - 4294967295"; //4294967294
        inputlen = 10;
        validate_type = 9;
        cnvsrn_type =1;
        hexopbytes =8;
        etnum =1;
        ShowDialog();
    }


    public class SetEms extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                byte[] cmd = "XTEA0\r\n".getBytes();// extended off
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }
                Log.e(EMSTAG,"Response for XTEAO :"+mResponse_data);

                cmd = "xtsh7e0\r\n".getBytes();//header for ems
                aBoolean = false;
                send_Request_Command(cmd);
                Log.e(EMSTAG,"Sending for xtsh7e0 ");
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }
                Log.e(EMSTAG,"Response for xtsh7e0 :"+mResponse_data);


                cmd = "XTRH7E8\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);

                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }
                super.run();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e(EMSTAG,e.getMessage());
            }

        }
    }
    public class SetABS extends Thread
    {

        @Override
        public void run()
        {
            try
            {
                cmd = "XTEA1\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);

                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }
                Log.e(ABSSTAG,"Response for XTEA1"+mResponse_data);

                cmd = "XTRH629\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }
                Log.e(ABSSTAG,"Response for XTRH629"+mResponse_data);

                cmd = "XTSH6F0\r\n".getBytes();
                aBoolean = false;
                send_Request_Command(cmd);

                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(ABSSTAG,e.getMessage());

            }

            super.run();
        }
    }

    public class SetICU extends Thread
    {
        @Override
        public void run()
        {
            byte[] cmd = "XTEA1\r\n".getBytes();

            aBoolean = false;
            send_Request_Command(cmd);

            while (!aBoolean && !holdflag)
            {
                SystemClock.sleep(10);
            }

            cmd = "XTRH660\r\n".getBytes();
            aBoolean = false;
            send_Request_Command(cmd);

            while (!aBoolean && !holdflag)
            {
                SystemClock.sleep(10);
            }

            cmd = "XTSH6F0\r\n".getBytes();
            aBoolean = false;
            send_Request_Command(cmd);

            while (!aBoolean && !holdflag)
            {
                SystemClock.sleep(10);
            }


            super.run();
        }
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog,getString(R.string.processing),false,1);
                        }
                    });
                }
            });
            SystemClock.sleep(500);
            if(SecurityProcess())
            {
                byte[] cmdid = cmddata.getBytes();
                byte[] cmdpandata ;

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
                    SystemClock.sleep(200);
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
                            AppVariables.ShowDialog(dialog,"Success",true,1);
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
                                AppVariables.ShowDialog(dialog,getString(R.string.unabletoprocess),true,0);
                            }
                        });
                    }
                }

                cmd = "1001\r\n".getBytes();
                send_Request_Command(cmd);
                Log.e("res",mResponse_data);
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog,"Security Failed, Try Again",true,0);
                    }
                });

            }
            super.run();
        }
    }

    public class WriteCmd1 extends Thread
    {
        @Override
        public void run()
        {
            byte[] cmd = (securitycmd+"\r\n").getBytes();
            aBoolean = false;
            holdflag = false;
            send_Request_Command(cmd);
            while (!aBoolean && !holdflag)
            {
                SystemClock.sleep(10);
            }
            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F") && !mResponse_data.contains("@!"))
            {
                Log.e("cmd res",mResponse_data);
                byte[] cmdid = cmddata.getBytes();
                byte[] write_data_arr = new byte[0];

                if(2 == byteconv)
                {
                    writedata = ConvertStrtoHexstr(writedata,2);
                    byte[] revbytes = DataConversion.hexStringToByteArray(writedata); // get bytes

                    write_data_arr = DataConversion._ByteArrayToPanArray(revbytes); // pan format
                }
                else
                if(3 == byteconv)
                {
                    writedata = ConvertStrtoHexstr(writedata,8);
                    byte[] revbytes = DataConversion.hexStringToByteArray(writedata); // get bytes

                    write_data_arr = DataConversion._ByteArrayToPanArray(revbytes); // pan format
                }
                byte[] writevin = new byte[6+2+write_data_arr.length];
                System.arraycopy(cmdid,0,writevin,0,6);
                System.arraycopy(write_data_arr,0,writevin,6,write_data_arr.length);
                System.arraycopy(end,0,writevin,6+write_data_arr.length,2);

                aBoolean = false;
                holdflag = false;
                send_Request_Command(writevin);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                    }
                });

                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }
                Log.e("cmd res",mResponse_data);
                if( !mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!") &&mResponse_data.contains("6E"))
                {
                    mResponse_data.replace(" ","");

                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog,"Success",true,1);
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
                                AppVariables.ShowDialog(dialog,arr[1].toString(),true,0);
                            }
                        });

                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                AppVariables.ShowDialog(dialog,getString(R.string.unabletoprocess),true,0);
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
                        AppVariables.ShowDialog(dialog,getString(R.string.ecunotresponding),true,0);
                    }
                });
            }

            super.run();
        }
    }

    public class WriteCmd2 extends Thread
    {
        @Override
        public void run()
        {
            byte[] cmd = (securitycmd+"\r\n").getBytes();
            aBoolean = false;
            holdflag = false;
            send_Request_Command(cmd);
            while (!aBoolean && !holdflag)
            {
                SystemClock.sleep(10);
            }

            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7F") && !mResponse_data.contains("@!"))
            {
                Log.e("cmd res",mResponse_data);
                byte[] write_data_arr = String.valueOf(cmddata+"0"+serverbleeding+"\r\n").getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(write_data_arr);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                    }
                });

                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }
                Log.e("cmd res",mResponse_data);
                mResponse_data.replace(" ","");
                if(mResponse_data.contains("6E") && !mResponse_data.contains("7F") && !mResponse_data.contains("@!"))
                {
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog,"Success",true,1);
                        }
                    });
                }
                else
                {
                    mResponse_data = mResponse_data.replace(" ","");
                    if (mResponse_data.length() == 6 && mResponse_data.contains("7F")  ) {
                        final String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                        final String[] arr = data.split(",");
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                AppVariables.ShowDialog(dialog,arr[1].toString(),true,0);
                            }
                        });

                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                AppVariables.ShowDialog(dialog,getString(R.string.unabletoprocess),true,0);
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
                        AppVariables.ShowDialog(dialog,getString(R.string.ecunotresponding),true,0);
                    }
                });
            }

            super.run();
        }
    }

    public class WriteCmd3 extends Thread
    {
        @Override
        public void run()
        {
            byte[] cmd = (securitycmd+"\r\n").getBytes();
            byte[] cmdpandata ;

            aBoolean = false;
            holdflag = false;
            send_Request_Command(cmd);
            while (!aBoolean && !holdflag)
            {
                SystemClock.sleep(10);
            }
            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("7E") && !mResponse_data.contains("@!"))
            {
                byte[] cmdid = cmddata.getBytes();

                cmdpandata = writedata.getBytes();
                cmdpandata = DataConversion._ByteArrayToPanArray(cmdpandata);


                byte[] writecmd = new byte[6+2+cmdpandata.length];
                System.arraycopy(cmdid,0,writecmd,0,6);
                System.arraycopy(cmdpandata,0,writecmd,6,cmdpandata.length);
                System.arraycopy(end,0,writecmd,6+cmdpandata.length,2);

                Log.e("VIN request",new String (writecmd));
                byte[] write_data_arr = String.valueOf(writecmd).getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(writecmd);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                    }
                });

                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }
                Log.e("cmd res",mResponse_data);
                mResponse_data.replace(" ","");
                if(mResponse_data.contains("6E"))
                {
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog,"Success",true,1);
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
                                AppVariables.ShowDialog(dialog,arr[1].toString(),true,0);
                            }
                        });

                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                AppVariables.ShowDialog(dialog,getString(R.string.unabletoprocess),true,0);
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
                        AppVariables.ShowDialog(dialog,getString(R.string.ecunotresponding),true,0);
                    }
                });
            }

            super.run();
        }
    }
    public class GetSecuredOTP  extends Thread
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
            HttpCommunication.JSONDATA="{\"api_key\" : \""+HttpCommunication.API_KEY+"\",\"email\" :\""+AppVariables.email_id+"\",\"secure_email\" : \""+AppVariables.Secured_Email_Id+"\",\"vci_serial\" :\""+AppVariables.Serialnumber+"\"}" ;
            HttpCommunication.PostUrl =HttpCommunication.Get_Secure_OTP;
            String Getop = HttpCommunication.POSTDATA();
            try
            {
                JSONObject object = new JSONObject(Getop);
                HttpCommunication.Status = object.getString("status");
                if(HttpCommunication.Status.equals("Success"))
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog,getString(R.string.success),true,10);
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ValidateOTP();
                        }
                    });
                }
                else
                if(HttpCommunication.Status.equals("false"))
                {
                    final String Err_MSG = object.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            AppVariables.ShowDialog(dialog,"Failed to request",true,0);

                            Toast.makeText(context, Err_MSG, Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("False",Err_MSG);
                }

            }
            catch (final JSONException e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog,e.getMessage(),true,0);

                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            Log.e("output","Getop");

            super.run();
        }
    }

    public class VerifySecuredOTP  extends Thread
    {
        @Override
        public void run()
        {
            HttpCommunication.JSONDATA="{\"api_key\" : \""+HttpCommunication.API_KEY+"\",\"email\" : \""+AppVariables.email_id+"\",\"otp\":\""+HttpCommunication.Secured_Password+"\"}" ;
            HttpCommunication.PostUrl =HttpCommunication.Verify_secure_OTP;
            String Getop = HttpCommunication.POSTDATA();
            try
            {
                JSONObject object = new JSONObject(Getop);
                HttpCommunication.Status = object.getString("status");
                if(HttpCommunication.Status.equals("Success"))
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.hide();
                            switch (flownumber)
                            {
                                case 1:      startActivity(new Intent(getApplicationContext(),ECUFlashing.class));overridePendingTransition(R.anim.out,R.anim.in);break;
                                case 2:      VIN(); break;
                                case 3:      EngRunHour(); break;
                                case 4:      KilRun(); break;
                                case 5:      ServiceBleeding(); break;
                                case 6:      OdometerOffset();  break;
                                case 7:      OdoValue();  break;
                                case 8:      VinICU(); break;

                            }
                        }
                    });

                }
                else
                if(HttpCommunication.Status.equals("false"))
                {
                    final String Err_MSG = object.getString("message");
                    Log.e("False",Err_MSG);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SecureAccess.this, ""+Err_MSG, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
            catch (final JSONException e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            Log.e("output","Getop");

            super.run();
        }
    }

    public class PostData extends Thread
    {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                }
            });


            HttpCommunication.JSONDATA="{\"api_key\" : \""+HttpCommunication.API_KEY+"\",\"email\" : \""+AppVariables.email_id+"\"}" ;
            HttpCommunication.PostUrl =HttpCommunication.Get_Secured_Email;
            String Getop = HttpCommunication.POSTDATA();
            Email_list = new ArrayList();

            try
            {
                JSONObject object = new JSONObject(Getop);
                HttpCommunication.Status = object.getString("status");

                if(HttpCommunication.Status.equals("Success"))
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog,getString(R.string.success),true,10);
                        }
                    });

                    JSONArray jsonArray = object.getJSONArray("data");
                    int count = jsonArray.length();
                    Log.e("count", String.valueOf(count));
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String email = jsonobject.getString("email");
                        Email_list.add(email);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ShowDialog_Secure_Email(Email_list);
                        }
                    });

                }
                else
                if(HttpCommunication.Status.equals("false"))
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog,"Failed to request",true,0);

                        }
                    });

                    final String Err_MSG = object.getString("message");
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(SecureAccess.this, ""+Err_MSG, Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("False",Err_MSG);
                }
            }
            catch (final JSONException e)
            {
                e.printStackTrace();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        AppVariables.ShowDialog(dialog,e.getMessage(),true,0);

                        Toast.makeText(SecureAccess.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            Log.e("output","Getop");

            super.run();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bridge =SingleTone.getBluetoothBridge();

        if(!AppVariables.CheckInternet(getApplicationContext()))
        {
            Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT).show();
        }
        if(bridge!=null)
        {
            Resposne();

        }
        else if(bridge==null)
        {
            Intent i = new Intent(getApplicationContext(), ConnectionInterrupt.class);
            startActivity(i);
            overridePendingTransition(R.anim.out,R.anim.in);
        }

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



    public void ShowDialog()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.write_did_dialog);
        final EditText editText;
        editText =  dialog.findViewById(R.id.et);
        if(etnum == 0)
        {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        else
        {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        if(deftxt ==1)
        {
            editText.setText(R.string.TESTSTAM_DEFAULT); //768
        }
        editText.setHint(hinttxt);
        if(validate_type==9)
        {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        if(1 == validate_type)
        {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setFilters(new InputFilter[]{new InputFilter.AllCaps(),new InputFilter.LengthFilter(17)});
            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    if (editText.length() == 17) {
                        editText.setTextColor(Color.GREEN);
                    } else {
                        editText.setTextColor(Color.parseColor("#FFFFFF"));

                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable et1) {
                    String s=et1.toString();
                    if(!s.equals(s.toUpperCase()))
                    {
                        s=s.toUpperCase();
                        editText.setText(s);
                    }
                }
            });
        }
        else if(11 == validate_type)
        {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setFilters(new InputFilter[]{new InputFilter.AllCaps(),new InputFilter.LengthFilter(17)});
            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    if (editText.length() == 17) {
                        editText.setTextColor(Color.GREEN);
                    } else {
                        editText.setTextColor(Color.parseColor("#FFFFFF"));

                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }

                @Override
                public void afterTextChanged(Editable et2) {
                    String s=et2.toString();
                    if(!s.equals(s.toUpperCase()))
                    {
                        s=s.toUpperCase();
                        editText.setText(s);
                    }
                }
            });
        }
        Button submitBtn =  dialog.findViewById(R.id.submit);
        try
        {
            submitBtn.setOnClickListener(new View.OnClickListener()
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
                                Toast.makeText(SecureAccess.this, hinttxt, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), hinttxt, Toast.LENGTH_SHORT).show();
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
                                WriteCmd writeCmd = new WriteCmd();
                                dialog.hide();
                                writeCmd.start();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), hinttxt, Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        if(9 == validate_type)
                        {
                            if(writedata.length()<=11)
                            {
                                long val = Long.parseLong(writedata);
                                if(val >= 0 && val <= 4294967295l)
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
                                {
                                    Toast.makeText(getApplicationContext(), hinttxt, Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), hinttxt, Toast.LENGTH_SHORT).show();
                            }


                        }
                        else
                        if(1 == validate_type)
                        {
                           /* editText.setInputType(InputType.TYPE_CLASS_TEXT);
                            editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(17)});
                            editText.addTextChangedListener(new TextWatcher() {

                                @Override
                                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                                    if(editText.length()==17)
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
                                    editText.setTextColor(Color.GREEN);

                                }
                                @Override
                                public void afterTextChanged(Editable et) {

                                }
                            });*/
                            if(writedata.length() == inputlen)//VIN
                            {
                                WriteCmd writeCmd = new WriteCmd();
                                dialog.hide();
                                writeCmd.start();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), hinttxt, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(validate_type==11)
                        {
                            /*editText.setInputType(InputType.TYPE_CLASS_TEXT);
                            editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(17)});
                            editText.addTextChangedListener(new TextWatcher() {

                                @Override
                                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                                    if(editText.length()==17)
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
                                    editText.setTextColor(Color.GREEN);

                                }
                                @Override
                                public void afterTextChanged(Editable et) {

                                }
                            });*/
                            if(writedata.length() == inputlen)//VIN
                            {
                                WriteCmd3 writeCmd = new WriteCmd3();
                                dialog.hide();
                                writeCmd.start();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), hinttxt, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        if(7 == validate_type)
                        {
                            int val = Integer.parseInt(editText.getText().toString());
                            if(0 <= val && val <= 255)
                            {
                                WriteCmd1 writeCmd = new WriteCmd1();
                                dialog.hide();
                                writeCmd.start();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), hinttxt, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        if(8 == validate_type)
                        {
                            int val = Integer.parseInt(editText.getText().toString());
                            if(5 <= val && val <= 8)
                            {
                                serverbleeding = val;
                                WriteCmd2 writeCmd = new WriteCmd2();
                                dialog.hide();
                                writeCmd.start();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), hinttxt, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        if(10 == validate_type)
                        {
                            int val = Integer.parseInt(editText.getText().toString());

                            if(val >= 0 && val <= 4294967295l)
                            {
                                WriteCmd1 writeCmd = new WriteCmd1();
                                dialog.hide();
                                writeCmd.start();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), hinttxt, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), hinttxt, Toast.LENGTH_SHORT).show();
                    }



                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        dialog.show();

    }



    public boolean SecurityProcess()
    {
        boolean flag ;
        cmd = (securitycmd+"\r\n").getBytes();
        aBoolean = false;
        holdflag = false;
        send_Request_Command(cmd);
        while (!aBoolean && !holdflag)
        {
            SystemClock.sleep(10);
        }

        Log.e("1002 res",mResponse_data);
        if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!")  && !mResponse_data.contains("7F"))
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
            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!") && !mResponse_data.contains("7F"))
            {
                mResponse_data = AppVariables.parsecmd2(mResponse_data,"6701");
                //   seed = mResponse_data.getBytes();
                Log.e("res",mResponse_data);

                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!") && !mResponse_data.contains("NODATA"))
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
            else {
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

       // byte[] randombytes = seed;
        byte[] sendtobyte = new byte[24];

        System.arraycopy(symm_byte,0,sendtobyte,0,symm_byte.length);
        System.arraycopy(seed,0,sendtobyte,symm_byte.length,seed.length);

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
    public String ConvertStrtoHexstr(String strdata)
    {
        //String  = str;
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

       // returning finalhexdata

        return stbr.toString();
    }

    public byte[] PrepareDate(String timedate)
    {
        byte[] op ;

        //timedate format "09112017"

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
    public String ConvertStrtoHexstr(String strdata,int lenhex)
    {
       // String  = str;
        long val = Long.parseLong(strdata);
        String hexdata = String.format("%X", val);
        int len = hexdata.length();
        int rem = lenhex-len;
        StringBuilder stbr = new StringBuilder();
        for(int i=0;i<rem;i++)
        {
            stbr.append("0");
        }
        stbr.append(hexdata);

        // returning finalhexdata

        return stbr.toString();
    }

    public void ValidateOTP()
    {
        final int[] i = {0};
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_validate_otp);
     //   dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        Button btnsubmit,btnresend;
        final TextView tvotpmail;
        final EditText etotp;
        tvotpmail = dialog.findViewById(R.id.otpmail);
        etotp =  dialog.findViewById(R.id.et);
        tvotpmail.setText("OTP has been sent to :"+AppVariables.Secured_Email_Id);
        btnsubmit =  dialog.findViewById(R.id.submit);
        btnresend = dialog.findViewById(R.id.resend);

        final String[] strotp = {""};
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(AppVariables.CheckInternet(context))
                {
                    if(etotp.getText().length()==6)
                    {
                        strotp[0] = etotp.getText().toString();
                        HttpCommunication.Secured_Password = strotp[0];
                        VerifySecuredOTP  verifySecuredOTP = new VerifySecuredOTP();
                        verifySecuredOTP.start();
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, getString(R.string.otperr), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, getString(R.string.interntUnavail), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        btnresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppVariables.CheckInternet(context))
                {
                    dialog.hide();
                    PushNotification();
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, getString(R.string.interntUnavail), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        dialog.show();
    }

    public void ShowDialog_Secure_Email(ArrayList arrayList)
    {
        dialogsec= new Dialog(this);
        final ListView listView;
        dialogsec.setContentView(R.layout.dialog_change_email);
        dialogsec.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogsec.setCanceledOnTouchOutside(false);

        listView = dialogsec.findViewById(R.id.lv);
        final String [] arr = new String[arrayList.size()];
        for(int i=0;i<arr.length;i++)
        {
            arr[i] = arrayList.get(i).toString();
        }
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.custom_listview_sec, arr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                AppVariables.Secured_Email_Id = arr[position];
                txtsecemail.setText(AppVariables.Secured_Email_Id);
                AppVariables.StoreSecEmail(getApplicationContext(),AppVariables.Secured_Email_Id);         //Storing Selected EMAILid
                dialogsec.hide();
            }
        });

        dialogsec.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }
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
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
