package com.example.tvsridescan.rtr1602v_1ch;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;

public class RTR1602v1chWriteParam extends AppCompatActivity {


    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;
    String positiveResponse="";

    Button vinDataBtn,clusterConfig,JumpCounterBtn,processBtn;
    Dialog dialog;
    String writedata = null;
    String hinttxt = null;

    Context context;
    private static final String TAG = "RTR1602v1chW.this";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtr1602v1ch_write_param);
        init();
    }
    private void init()
    {
        context = RTR1602v1chWriteParam.this;
        bridge = SingleTone.getBluetoothBridge();
        Resposne();



        vinDataBtn =  findViewById(R.id.Button1);
       // clusterConfig =  findViewById(R.id.Button2);
        JumpCounterBtn =  findViewById(R.id.Button3);
       // processBtn =  findViewById(R.id.Button4);



        vinDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                writeVIN();
            }
        });
        /*clusterConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                writeClusterConfig();
            }
        });*/
        JumpCounterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeMuJumpCounter();
            }
        });
        /*processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeProcessCommand();
            }
        });*/


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

            }
            @Override
            public void Connected(String str)
            {

            }
        });
    }



    /*
    * VIN DATA
    * */

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
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setTextColor(Color.parseColor("#FFFFFF"));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(17)});
                if(!editText.getText().toString().isEmpty())
                {
                    if(editText.getText().length()==17)
                    {
                        editText.setTextColor(Color.GREEN);
                    }
                    else
                    {
                        editText.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                            Toast.makeText(getApplicationContext(), "ENTER 17 Characters ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "ENTER 17 Characters ", Toast.LENGTH_SHORT).show();
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

                byte[] cmd = "1003\r\n".getBytes(); //
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                {
                    byte[] cmdpandata = writedata.getBytes();
                    cmdpandata = DataConversion._ByteArrayToPanArray(cmdpandata);
                    byte[] cmdid = "2E90".getBytes();
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
                    if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("NO") && !mResponse_data.contains("@!"))
                    {
                        mResponse_data =   mResponse_data.replace(" ","");
                        if(mResponse_data.contains("6E90"))
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

    /*
    * Cluster Config
    * */

    public void writeClusterConfig()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.write_did_dialog);
        final EditText editText =  dialog.findViewById(R.id.et);

        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        editText.setHint(getString(R.string.enter0to1));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setTextColor(Color.GREEN);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
                if(!editText.getText().toString().isEmpty())
                {
                    int tempInteger = Integer.parseInt(editText.getText().toString());
                    if(tempInteger>=0 && tempInteger<=1)
                    {
                        editText.setTextColor(Color.GREEN);
                    }
                    else
                    {
                        editText.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                        int tempInteger = Integer.parseInt(writedata);

                        if(tempInteger==1 ||tempInteger==0)//VIN
                        {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            SystemClock.sleep(100);

                            WriteClusterThread writeCmd = new WriteClusterThread();
                            writeCmd.start();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.enter0to1), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.enter0to1), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    class WriteClusterThread extends  Thread
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

                byte[] cmd = "1003\r\n".getBytes(); //
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                {
                    byte[] cmdpandata ;
                    int temp = Integer.parseInt(writedata);
                    byte tempByte = (byte)(0x000000FF & temp);
                    Log.e("tempByte", String.valueOf(tempByte));
                    cmdpandata = DataConversion._ByteToPAN(tempByte);

                    //cmdpandata = DataConversion._ByteArrayToPanArray(cmdpandata);
                    byte[] cmdid = "2E03".getBytes();
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
                    if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("NO") && !mResponse_data.contains("@!"))
                    {
                        mResponse_data =   mResponse_data.replace(" ","");
                        if(mResponse_data.contains("6E03"))
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

    /*
     * RESET MU JUMP COUNTER
     * */
    public void writeMuJumpCounter()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.write_did_dialog);
        final EditText editText =  dialog.findViewById(R.id.et);

        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint(getString(R.string.enter0to65535));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setTextColor(Color.GREEN);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
                if(!editText.getText().toString().isEmpty())
                {
                    int tempInteger = Integer.parseInt(editText.getText().toString());
                    if(tempInteger>=0 && tempInteger<=65535)
                    {
                        editText.setTextColor(Color.GREEN);
                    }
                    else
                    {
                        editText.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

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

                        int tempInteger = Integer.parseInt(writedata);
                        if(tempInteger>=0 && tempInteger<=65535)//VIN
                        {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            SystemClock.sleep(100);

                            WriteMuJumpCounter writeCmd = new WriteMuJumpCounter();
                            writeCmd.start();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.enter0to65535), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.enter0to65535), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    class WriteMuJumpCounter extends  Thread
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

                byte[] cmd = "1003\r\n".getBytes(); //
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                {
                    String tempString = AppVariables.strHexString(writedata,4);
                    byte[] cmdpandata = tempString.getBytes();

                    //cmdpandata = DataConversion._ByteArrayToPanArray(cmdpandata);

                    byte[] cmdid = "2E40".getBytes();
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
                    if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("NO") && !mResponse_data.contains("@!"))
                    {
                        mResponse_data =   mResponse_data.replace(" ","");
                        if(mResponse_data.contains("6E40"))
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

    /*
    * Process Command
    * */



    public void writeProcessCommand()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.write_did_dialog);
        final EditText editText =  dialog.findViewById(R.id.et);

        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        editText.setHint(getString(R.string.enter0to255));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setTextColor(Color.GREEN);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
                if(!editText.getText().toString().isEmpty())
                {
                    int tempInteger = Integer.parseInt(editText.getText().toString());
                    if(tempInteger>=0 && tempInteger<=255)
                    {
                        editText.setTextColor(Color.GREEN);
                    }
                    else
                    {
                        editText.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                        int tempInteger = Integer.parseInt(writedata);
                        if(tempInteger>=0 && tempInteger<=255)//VIN
                        {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            SystemClock.sleep(100);

                            WriteProcessThread writeCmd = new WriteProcessThread();
                            writeCmd.start();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.enter0to255), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.enter0to255), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    class WriteProcessThread extends  Thread
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

                byte[] cmd = "1003\r\n".getBytes(); //
                aBoolean = false;
                send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                {
                    int temp = Integer.parseInt(writedata);
                    byte tempByte = (byte)(0x000000FF & temp);
                    byte[] cmdpandata ;
                    Log.e("tempByte", String.valueOf(tempByte));
                    cmdpandata = DataConversion._ByteToPAN(tempByte);
                    byte[] cmdid = "2EFA".getBytes();
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
                    if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("NO") && !mResponse_data.contains("@!"))
                    {
                        mResponse_data =   mResponse_data.replace(" ","");
                        if(mResponse_data.contains("6EFA"))
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
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.out,R.anim.in);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
