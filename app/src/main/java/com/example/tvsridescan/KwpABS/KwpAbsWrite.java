package com.example.tvsridescan.KwpABS;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

import java.util.Calendar;
import java.util.Random;

public class KwpAbsWrite extends AppCompatActivity {


    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;
    String positiveResponse="";

    Button fillandbleedBtn, dateoffillBtn,vinDataBtn,resetJumpCounterBtn,productionDateBtn;
    //vehicleBtn
    Dialog dialog;

    Context context;
    private static final String TAG = "KwpAbsWrite";

    String cmddata = null;
    String writedata = null;
    String hinttxt = null;
    private static  String date = "empty";
    private final static int DATE_OF_Ser = 1; //date_of_pdi
    private int year;
    private int month;
    private int day;
    String strmonth = "";
    String strday = "";

    private  int casee = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kwp_abs_write);
        init();
    }

    private void init()
    {
        context = KwpAbsWrite.this;
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        dialog = new Dialog(this);
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




        fillandbleedBtn =  findViewById(R.id.Button1);
        dateoffillBtn =  findViewById(R.id.Button2);
        vinDataBtn =  findViewById(R.id.Button3);
        //vehicleBtn =  findViewById(R.id.Button4);
        resetJumpCounterBtn = findViewById(R.id.Button5);
        productionDateBtn =  findViewById(R.id.Button6);



        if(AppVariables.BikeModel ==7)
        {
            fillandbleedBtn.setVisibility(View.INVISIBLE);
            dateoffillBtn.setVisibility(View.INVISIBLE);
            productionDateBtn.setVisibility(View.INVISIBLE);

        }
        else if(AppVariables.BikeModel == 8)
        {
            fillandbleedBtn.setVisibility(View.INVISIBLE);
            dateoffillBtn.setVisibility(View.INVISIBLE);
            productionDateBtn.setVisibility(View.INVISIBLE);
        }
        else if(AppVariables.BikeModel ==4)
        {
            fillandbleedBtn.setVisibility(View.INVISIBLE);
            dateoffillBtn.setVisibility(View.INVISIBLE);
            productionDateBtn.setVisibility(View.INVISIBLE);
        }
        else if(AppVariables.BikeModel==5)
        {
            fillandbleedBtn.setVisibility(View.INVISIBLE);
            dateoffillBtn.setVisibility(View.INVISIBLE);
            productionDateBtn.setVisibility(View.INVISIBLE);
        }
        else
        {
            fillandbleedBtn.setVisibility(View.VISIBLE);
            dateoffillBtn.setVisibility(View.VISIBLE);
            productionDateBtn.setVisibility(View.VISIBLE);
        }
        fillandbleedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                writeFillandBleedResult();
            }
        });

        dateoffillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                writeDateofFil();
            }
        });


        vinDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeVIN();
            }
        });
      /*  vehicleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeVehicleCode();
            }
        });*/

        resetJumpCounterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muJumpCounter();
            }
        });

        productionDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeProductionDate();
            }
        });

    }


    /*
    * BT COMM
    * */
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



    /**
     *METHODS
     */

    /*
    * WRITE FILL AND BLEED RESULT
    * */

    private void writeFillandBleedResult()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                        }
                    });
                    SystemClock.sleep(1000);
                    byte[] cmd = "1087\r\n".getBytes(); //
                    aBoolean = false;
                    send_Request_Command(cmd);
                    while (!aBoolean )
                    {
                        SystemClock.sleep(10);
                    }
                    if(!mResponse_data.contains("NO DATA") )
                    {
                        if(!mResponse_data.contains("@!"))
                        {
                            cmd = "3B8AAA\r\n".getBytes(); //
                            aBoolean = false;
                            send_Request_Command(cmd);
                            while (!aBoolean )
                            {
                                SystemClock.sleep(10);
                            }
                            Log.e("cmd res",mResponse_data);
                            if(!mResponse_data.contains("NO DATA"))
                            {
                                if(!mResponse_data.contains("ERROR") && !mResponse_data.contains("@!"))
                                {
                                    mResponse_data =  mResponse_data.replace(" ","");
                                    if(mResponse_data.substring(0,2).equals("7B"))
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
                                        if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                                        {
                                            String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                            final String finalval  = data.replace(",", " ");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AppVariables.ShowDialog(dialog ,finalval,true,0);
                                                }
                                            });

                                        }
                                        else
                                        {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AppVariables.ShowDialog(dialog ,getString(R.string.unabletoprocess),true,0);
                                                }
                                            });
                                        }

                                    }
                                }
                                else
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
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
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
        thread.start();

    }



    /*
    * WRITE DATE OF FILL AND RESULT
    * */
    public void writeDateofFil()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.write_did_dialog);
        final EditText editText =  dialog.findViewById(R.id.et);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if(editText.length()==8)
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
        });
        editText.setHint(hinttxt);
        editText.setText(date);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_OF_Ser);
            }
        });


        Button b1 =  dialog.findViewById(R.id.submit);
        try
        {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                writedata = editText.getText().toString();

                if(!writedata.isEmpty())
                {
                  WriteDateOFFillandBleedThread writeCmd = new WriteDateOFFillandBleedThread();
                   dialog.dismiss();
                   writeCmd.start();
                }
                else
                {
                    Toast.makeText(context, ""+hinttxt, Toast.LENGTH_SHORT).show();
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
            writeDateofFil();

        }
    };
    class WriteDateOFFillandBleedThread extends  Thread
    {
        @Override
        public void run() {
            super.run();
            try
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                    }
                });
                SystemClock.sleep(1000);
                byte[] cmd = "1087\r\n".getBytes(); //
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                if(!mResponse_data.contains("NO DATA"))
                {
                    if(!mResponse_data.contains("ERROR") && !mResponse_data.contains("@!"))
                    {
                        cmd = PrepareProductionDate(writedata).getBytes(); //
                        aBoolean = false;
                        send_Request_Command(cmd);
                        while (!aBoolean )
                        {
                            SystemClock.sleep(10);
                        }
                        Log.e("cmd res",mResponse_data);
                        if(!mResponse_data.contains("NO DATA"))
                        {
                            if(!mResponse_data.contains("ERROR") && !mResponse_data.contains("@!"))
                            {
                                mResponse_data.replace(" ","");
                                if(mResponse_data.substring(0,2).equals("7B"))
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
                                    if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                                    {
                                        String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                        final String finalval  = data.replace(",", " ");
                                        runOnUiThread(new Runnable() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void run() {
                                                AppVariables.ShowDialog(dialog,finalval,true,0);
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
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public String PrepareProductionDate(String str)
    {
        byte[] op = new byte[0];


        int DD 	=  Integer.parseInt(str.substring(0,2));
        int MM 	=  Integer.parseInt(str.substring(2,4));
        int YYYY =  Integer.parseInt(str.substring(6,8));



        String tempDateString ;

        String CENString = String.format("%X", 21);
        String yearString = (String.format("%X", YYYY));
        String monthString = addZeroesIfneeded(String.format("%X", MM));
        String daytring = addZeroesIfneeded(String.format("%X", DD));

        tempDateString = "3B99"+CENString+yearString+monthString+daytring+"\r\n";
        return tempDateString;
    }



    /*
    * WRITE VIN
    * */

    public void writeVIN()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.write_did_dialog);
        final EditText editText =  dialog.findViewById(R.id.et);

        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        editText.setHint(getString(R.string.enter17digits));
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
            }
            @Override
            public void afterTextChanged(Editable et) {

            }
        });
        Button submitBtn =  dialog.findViewById(R.id.submit);
        dialog.show();
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

                        if(writedata.length() == 17)//VIN
                        {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            SystemClock.sleep(100);

                            WriteVINThread writeCmd = new WriteVINThread();
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
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    class WriteVINThread extends  Thread
    {
        @Override
        public void run()
        {
            super.run();
            try {
                SystemClock.sleep(100);
                // dialog = new Dialog(context);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog ,getString(R.string.processing),false,1);
                    }
                });

                SystemClock.sleep(1000);

                byte[] cmd = "1087\r\n".getBytes(); //
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("ERROR") && !mResponse_data.contains("@!"))
                {
                    byte[] cmdpandata = writedata.getBytes();
                    cmdpandata = DataConversion._ByteArrayToPanArray(cmdpandata);
                    byte[] cmdid = "3B90".getBytes();
                    byte[] writecmd = new byte[4+cmdpandata.length+2];
                    System.arraycopy(cmdid,0,writecmd,0,4);
                    System.arraycopy(cmdpandata,0,writecmd,4,cmdpandata.length);
                    System.arraycopy("\r\n".getBytes(),0,writecmd,4+cmdpandata.length,2);

                    aBoolean = false;
                    send_Request_Command(writecmd);
                    while (!aBoolean )
                    {
                        SystemClock.sleep(100);
                    }
                    Log.e(TAG,mResponse_data);
                    if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                    {
                        mResponse_data =   mResponse_data.replace(" ","");
                        if(mResponse_data.contains("7B90"))
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppVariables.ShowDialog(dialog ,getString(R.string.success),true,1);
                                }
                            });

                        }
                        else
                        {

                            if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                            {
                                String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                final String finalval  = data.replace(",", " ");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppVariables.ShowDialog(dialog ,finalval,true,0);
                                    }
                                });

                            }
                            else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppVariables.ShowDialog(dialog ,getString(R.string.unabletoprocess),true,0);
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
                                AppVariables.ShowDialog(dialog ,getString(R.string.ecunotresponding),true,0);
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
                            AppVariables.ShowDialog(dialog ,getString(R.string.ecunotresponding),true,0);
                        }
                    });
                }



            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    /*
    * RESET MU JUMP COUNTER
    * */
    private void muJumpCounter()
    {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                            }
                        });
                        SystemClock.sleep(1000);
                        byte[] cmd = "1087\r\n".getBytes(); //
                        aBoolean = false;
                        send_Request_Command(cmd);
                        while (!aBoolean )
                        {
                            SystemClock.sleep(10);
                        }
                        if(!mResponse_data.contains("NO DATA") )
                        {
                            if(!mResponse_data.contains("ERROR")  && !mResponse_data.contains("@!"))
                            {
                                Random rand = new Random();
                                int value = rand.nextInt(50);
                                String randomString = String.format("%X", value);
                                randomString ="3B97"+randomString+"\r\n";
                                cmd = randomString.getBytes(); //
                                aBoolean = false;
                                send_Request_Command(cmd);
                                while (!aBoolean )
                                {
                                    SystemClock.sleep(10);
                                }
                                Log.e("cmd res",mResponse_data);
                                if(!mResponse_data.contains("NO DATA"))
                                {
                                    if(!mResponse_data.contains("ERROR")  && !mResponse_data.contains("@!"))
                                    {
                                        mResponse_data =  mResponse_data.replace(" ","");
                                        if(mResponse_data.substring(0,2).equals("7B"))
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
                                            if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                                            {
                                                String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                                final String finalval  = data.replace(",", " ");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        AppVariables.ShowDialog(dialog ,finalval,true,0);
                                                    }
                                                });

                                            }
                                            else
                                            {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        AppVariables.ShowDialog(dialog ,getString(R.string.unabletoprocess),true,0);
                                                    }
                                                });
                                            }

                                        }
                                    }
                                    else
                                    {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
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
                            }
                            else
                            {
                                runOnUiThread(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void run() {
                                        AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
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
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /*
    * Production date
    * */


    public void writeProductionDate()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.write_did_dialog);
        final EditText editText =  dialog.findViewById(R.id.et);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint(getString(R.string.enter17digits));
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if(editText.length()==8)
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
        });
        editText.setHint(hinttxt);
        editText.setText(date);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_OF_Ser);
            }
        });


        Button b1 =  dialog.findViewById(R.id.submit);
        try
        {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    writedata = editText.getText().toString();

                    if(!writedata.isEmpty())
                    {
                        WriteDateOFProduction writeCmd = new WriteDateOFProduction();
                        dialog.dismiss();
                        writeCmd.start();
                    }
                    else
                    {
                        Toast.makeText(context, ""+hinttxt, Toast.LENGTH_SHORT).show();
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

    class WriteDateOFProduction extends  Thread
    {
        @Override
        public void run() {
            super.run();
            try
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog,getString(R.string.processing),false,2);
                    }
                });
                SystemClock.sleep(1000);
                byte[] cmd = "1087\r\n".getBytes(); //
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                if(!mResponse_data.contains("NO DATA"))
                {
                    if(!mResponse_data.contains("ERROR")  && !mResponse_data.contains("@!"))
                    {
                        cmd = PrepareDate(writedata).getBytes(); //
                        aBoolean = false;
                        send_Request_Command(cmd);
                        while (!aBoolean )
                        {
                            SystemClock.sleep(10);
                        }
                        Log.e("cmd res",mResponse_data);
                        if(!mResponse_data.contains("NO DATA"))
                        {
                            if(!mResponse_data.contains("ERROR")  && !mResponse_data.contains("@!"))
                            {
                                mResponse_data.replace(" ","");
                                if(mResponse_data.substring(0,2).equals("7B"))
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
                                    if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                                    {
                                        String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                        final String finalval  = data.replace(",", " ");
                                        runOnUiThread(new Runnable() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void run() {
                                                AppVariables.ShowDialog(dialog,finalval,true,0);
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
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public String PrepareDate(String str)
    {
        byte[] op = new byte[0];


        int DD 	=  Integer.parseInt(str.substring(0,2));
        int MM 	=  Integer.parseInt(str.substring(2,4));
        int YYYY =  Integer.parseInt(str.substring(6,8));



        String tempDateString ;

        String CENString = String.format("%X", 21);
        String yearString = (String.format("%X", YYYY));
        String monthString = addZeroesIfneeded(String.format("%X", MM));
        String daytring = addZeroesIfneeded(String.format("%X", DD));

        tempDateString = "3B8B"+CENString+yearString+monthString+daytring+"\r\n";
        return tempDateString;
    }

    public  String addZeroesIfneeded(String string)
    {
        if(string.length()==1)
        {
            string="0"+string;
        }
        return string;
    }



    /*
    * Write Vehicle CODE
    * */

    public void writeVehicleCode()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.write_did_dialog);
        final EditText editText =  dialog.findViewById(R.id.et);

        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        editText.setHint(getString(R.string.enter12digits));

        Button submitBtn =  dialog.findViewById(R.id.submit);
        dialog.show();
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

                        if(writedata.length() == 12)//VIN
                        {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            SystemClock.sleep(100);

                            WriteVehicleCodeThread writeCmd = new WriteVehicleCodeThread();
                            writeCmd.start();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.enter12digits), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.enter12digits), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    class WriteVehicleCodeThread extends  Thread
    {
        @Override
        public void run()
        {
            super.run();
            try {
                SystemClock.sleep(100);
                // dialog = new Dialog(context);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog ,getString(R.string.processing),false,1);
                    }
                });

                SystemClock.sleep(1000);
                byte[] cmd = "1087\r\n".getBytes(); //
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                if(!mResponse_data.contains("NO DATA") &&  !mResponse_data.contains("@!"))
                {
                    byte[] cmdpandata = writedata.getBytes();
                    cmdpandata = DataConversion._ByteArrayToPanArray(cmdpandata);
                    byte[] cmdid = "3B90".getBytes();
                    byte[] writecmd = new byte[4+cmdpandata.length+2];
                    System.arraycopy(cmdid,0,writecmd,0,4);
                    System.arraycopy(cmdpandata,0,writecmd,4,cmdpandata.length);
                    System.arraycopy("\r\n".getBytes(),0,writecmd,4+cmdpandata.length,2);

                    aBoolean = false;
                    send_Request_Command(writecmd);
                    while (!aBoolean )
                    {
                        SystemClock.sleep(100);
                    }
                    Log.e(TAG,mResponse_data);
                    if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!") && !mResponse_data.contains("ERROR"))
                    {
                        mResponse_data =   mResponse_data.replace(" ","");
                        if(mResponse_data.contains("7B96"))
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppVariables.ShowDialog(dialog ,getString(R.string.success),true,1);
                                }
                            });

                        }
                        else
                        {

                            if(mResponse_data.contains("7F") && mResponse_data.length()==6)
                            {
                                String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                final String finalval  = data.replace(",", " ");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppVariables.ShowDialog(dialog ,finalval,true,0);
                                    }
                                });

                            }
                            else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppVariables.ShowDialog(dialog ,getString(R.string.unabletoprocess),true,0);
                                    }
                                });
                            }


                        }

                    }
                    else
                    {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppVariables.ShowDialog(dialog ,getString(R.string.ecunotresponding),true,0);
                            }
                        });

                    }
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog ,getString(R.string.ecunotresponding),true,0);
                        }
                    });
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
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
