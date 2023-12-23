package com.example.tvsridescan.icu;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClusterWriteDIDs extends Activity
{
    BluetoothBridge bridge;
    static String mResponse_data = null;
    static boolean aBoolean = true;
    static byte[] response;
    byte[] end = "\r\n".getBytes();
    String cmddata = null;
    String writedata = null;
    String hinttxt = null;
    Dialog dialog;
    String securitycmd = null;
    int byteconv = 0;
    LinearLayout b1,b2,b3,b4,b5,b6;
    boolean holdflag = false;
    int hexlen =0;
    EditText editText;
    int appendstr = 0;

    static String date = "empty";
    static final int DATE_OF_Ser = 1; //date_of_pdi
    private int year;
    private int month;
    private int day;
    String strmonth = "";
    String strday = "";

    Animation animFadein1,animFadein2,animFadein3;
    MediaPlayer mp;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_write_dids);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = ClusterWriteDIDs.this;
        setTitle("WRITE DID's");
        bridge = SingleTone.getBluetoothBridge();
        Resposne();


        mp = MediaPlayer.create(this, R.raw.buttonclick);

        animFadein1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein2 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);

        animFadein3 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);


        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH)+1;
        day   = c.get(Calendar.DAY_OF_MONTH);

        strmonth = String.valueOf(month);
        strday = String.valueOf(day);

        if(strmonth.length()==1)
        {
            strmonth = "0"+strmonth;
        }


        if(strday.length()==1)
        {
            strday = "0"+strday;
        }

        date =  (new StringBuilder().append(strday).append("").append(strmonth).append("")
                .append(year).append("")).toString();


        b3 =  findViewById(R.id.button3);
        b4 =  findViewById(R.id.button4);
        b5 =  findViewById(R.id.button5);

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                cmddata = "2EE12D";
                securitycmd = "1003";
                byteconv =2;
                hexlen = 4;
                appendstr = 4;
                hinttxt = getString(R.string.entervalfrom05000km);
                b3.startAnimation(animFadein1);
                mp.start();
                ShowDialog();
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                cmddata = "2EE12C";
                securitycmd = "1003";
                byteconv =4;
                appendstr = 0;
                hinttxt = getString(R.string.entervaldateddmmddd);
                b4.startAnimation(animFadein2);
                mp.start();
                ShowDialog();
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                cmddata = "2EE12B";
                securitycmd = "1003";
                byteconv =3;
                appendstr = 1;
                hinttxt = getString(R.string.entrvalutimeicudate);
                b5.startAnimation(animFadein3);
                mp.start();
                ShowDialog();
            }
        });
    }
    public void ShowDialog()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.write_did_dialog);
        editText =  dialog.findViewById(R.id.et);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if(appendstr ==4)
                {
                    editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
                    if(!editText.getText().toString().isEmpty())
                    {
                        long temp = Long.parseLong(editText.getText().toString());
                        if(temp<=50000)
                        {
                            editText.setTextColor(Color.GREEN);
                        }
                        else
                        {
                            editText.setTextColor(Color.parseColor("#FFFFFF"));

                        }
                    }

                }
               else if (appendstr==0)
                {
                    editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
                    editText.setTextColor(Color.GREEN);
                }
                else if(appendstr==1)
                {
                    editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(14)});

                }

            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                if (appendstr==0)
                {
                    editText.setTextColor(Color.GREEN);
                }
                else  if(appendstr==1)
                {
                    editText.setTextColor(Color.GREEN);

                }

            }
            @Override
            public void afterTextChanged(Editable et) {

            }
        });

            if(appendstr ==0)
            {
                editText.setHint(hinttxt);
                editText.setText(date);
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         showDialog(DATE_OF_Ser);
                    }
                });
            }
            else
            if(appendstr ==1)
            {
                editText.setHint(hinttxt);
                editText.setText(PresentDate_Time());
            }
            else
            {
                editText.setHint(hinttxt);
            }

        Button b1 =  dialog.findViewById(R.id.submit);
        try
        {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    writedata = editText.getText().toString();

                    if(!writedata.isEmpty())
                    {
                        if(appendstr ==4)
                        {
                            long val = Long.parseLong(writedata);
                            if(0<=val && val<=50000)
                            {
                                WriteCmd writeCmd = new WriteCmd();
                                dialog.dismiss();
                                writeCmd.start();
                            }
                            else
                            {
                                Toast.makeText(ClusterWriteDIDs.this, ""+hinttxt, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(appendstr ==1)
                        {
                            if(writedata.length()==14)
                            {
                                WriteCmd writeCmd = new WriteCmd();
                                dialog.dismiss();
                                writeCmd.start();
                            }
                            else
                            {
                                Toast.makeText(ClusterWriteDIDs.this, hinttxt, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            WriteCmd writeCmd = new WriteCmd();
                            dialog.dismiss();
                            writeCmd.start();
                        }

                    }
                    else
                    {
                        Toast.makeText(ClusterWriteDIDs.this, ""+hinttxt, Toast.LENGTH_SHORT).show();
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

    public class WriteCmd extends Thread
    {
        @Override
        public void run()
        {

                //SystemClock.sleep(1000);
                byte[] cmd = (securitycmd+"\r\n").getBytes();
                aBoolean = false;
                holdflag = false;
                send_Request_Command(cmd);
                while (!aBoolean && !holdflag)
                {
                    SystemClock.sleep(10);
                }
                Log.e("cmd res",mResponse_data);

                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                {
                    byte[] cmdid = cmddata.getBytes();
                    byte[] write_data_arr = new byte[0];


                    if(0 == byteconv)
                    {
                        write_data_arr = writedata.getBytes();
                    }
                    else
                    if(1 == byteconv)
                    {
                        write_data_arr = writedata.getBytes();
                        write_data_arr = DataConversion._ByteArrayToPanArray(write_data_arr);
                    }
                    else
                    if(2 == byteconv)
                    {
                        writedata = ConvertStrtoHexstr(writedata,hexlen);
                        byte[] revbytes = DataConversion.hexStringToByteArray(writedata); // get bytes

                        write_data_arr = DataConversion._ByteArrayToPanArray(revbytes); // pan format
                    }
                    else
                    if(3 == byteconv)
                    {
                        write_data_arr = PrepareDateTime(writedata); // pan format
                    }
                    else
                    if(4 == byteconv)
                    {
                        write_data_arr = PrepareDate(writedata); // pan format
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

                    if(!mResponse_data.contains("NO DATA") &&!mResponse_data.contains("ERROR"))
                    {
                        mResponse_data =  mResponse_data.replace(" ","");

                        if(mResponse_data.contains("6E"))
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
                                        AppVariables.ShowDialog(dialog,getString(R.string.somethingnotright),true,0);
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

    @Override
    protected void onPause() {
        super.onPause();
        if(dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case DATE_OF_Ser:
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth+1;
            day   = selectedDay;

            String strmonth = "";
            String strday = "";
            strmonth = String.valueOf(month);
            strday = String.valueOf(day);

            if(strmonth.length()==1)
            {
                strmonth = "0"+strmonth;
            }
            month = Integer.parseInt(strmonth);

            if(strday.length()==1)
            {
                strday = "0"+strday;
            }

            month = Integer.parseInt(strmonth);
            day = Integer.parseInt(strday);

            date =  (new StringBuilder().append(strday).append("").append(strmonth).append("")
                    .append(year).append("")).toString();

            dialog.dismiss();
            ShowDialog();

        }
    };



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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

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

    public byte[] PrepareDateTime(String str)
    {
        byte[] op = new byte[0];

        String timedate = str;//"08153009112017";

        byte hh 	= (byte) Integer.parseInt(timedate.substring(0,2));
        byte mm 	= (byte) Integer.parseInt(timedate.substring(2,4));
        byte ss 	= (byte) Integer.parseInt(timedate.substring(4,6));
        byte DD 	= (byte) Integer.parseInt(timedate.substring(6,8));
        byte MM 	= (byte) Integer.parseInt(timedate.substring(8,10));
        short YYYY = (short) Integer.parseInt(timedate.substring(10,14));

        byte[] timedatebytes = new byte[14];
        byte[] hhbytes = DataConversion._ByteToPAN(hh);
        byte[] mmbytes = DataConversion._ByteToPAN(mm);
        byte[] ssbytes = DataConversion._ByteToPAN(ss);
        byte[] DDbytes = DataConversion._ByteToPAN(DD);
        byte[] MMbytes = DataConversion._ByteToPAN(MM);
        byte[] YYYYbytes = DataConversion._HWordToPAN(YYYY);

        System.arraycopy(hhbytes, 0, timedatebytes, 0, 2);
        System.arraycopy(mmbytes, 0, timedatebytes, 2, 2);
        System.arraycopy(ssbytes, 0, timedatebytes, 4, 2);
        System.arraycopy(DDbytes, 0, timedatebytes, 6, 2);
        System.arraycopy(MMbytes, 0, timedatebytes, 8, 2);
        System.arraycopy(YYYYbytes, 0, timedatebytes, 10, 4);
        op = timedatebytes;
        return op;
    }

    public String ConvertStrtoHexstr(String str,int lenhex)
    {
        String strdata = str;
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

        String finalhexdata = stbr.toString();

        return finalhexdata;
    }
    public String PresentDate()
    {
        String str;
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date date = new Date();
        str = sdf.format(date);
        return str;
    }
    public String PresentDate_Time()
    {
        String str;
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmssddMMyyyy");
        Date date = new Date();
        str = sdf.format(date);
        return str;
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}
