package com.example.tvsridescan.connection;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.BluetoothConversation;
import com.example.tvsridescan.Library.BluetoothSemaphoreService;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.Library.TeAES_Lib;
import com.example.tvsridescan.R;
import com.example.tvsridescan.SelectVehicle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class PairedDevices extends AppCompatActivity {
    EditText et;
    String userid = null;
    String pass = "";
    public static String TAG = LoginActivity.class.getSimpleName();
    //bt variables
    BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;

    boolean flag = true;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;
    TextView load;
    LinearLayout linearLayoutpass,layoutpb;
    String[] serilnum;
    HashMap<String, String> hmap = new HashMap<String, String>();
    String itemvci;
    boolean DiscFlag = true;
    TextView txtpass,txtupdate;
    BluetoothBridge bridge;
    Dialog dialog;

    BluetoothSemaphoreService bluetoothSemaphoreService;

    Context context;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired_devices);
        context = PairedDevices.this;
        bridge = new BluetoothBridge(getApplicationContext());
        dialog = new Dialog(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BluetoothConversation.reshandle =1;

        bridge.EstaConn(getApplicationContext(),"00:04:3E:9C:7B:C5");
        AppVariables.BluetoothDeviceAdress ="00:04:3E:9C:7B:C5";
        SystemClock.sleep(500);
        Resposne();
    }

    public class UnLockSecurity extends Thread
    {
        @Override
        public void run()
        {

            SingleTone.setBluetoothBridge(bridge);
            bluetoothSemaphoreService = new BluetoothSemaphoreService();

            try {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // load.setText("Authenticating VCI.");
                        Toast.makeText(PairedDevices.this, "Authenticating", Toast.LENGTH_SHORT).show();
                    }
                });

                SystemClock.sleep(500);
                BluetoothConversation.reshandle = 1;
                boolean flagresponse = true;

                //write ref data
                String cmdstr = AppVariables.FormCmd(AppVariables.PING, "");
                byte[] cmd = cmdstr.getBytes();
                aBoolean = false;
                send_Request_Command(cmd);
                while (aBoolean != true)
                {
                    SystemClock.sleep(50);
                };
                aBoolean = false;
                Log.e("Res", mResponse_data);

                final byte[] getklearntstatus = AppVariables.FormCmd(AppVariables.READ_KEY_LEARN_STATUS, "").getBytes();
                final byte[] serialarr = AppVariables.FormCmd(AppVariables.READ_SERIAL_NUMBER, "").getBytes();
                //byte[] cmd = "atz\r\n".getBytes();
                send_Request_Command(getklearntstatus);
                while (aBoolean != true)
                {
                    SystemClock.sleep(50);
                };
                aBoolean = false;
                Log.e("Res", mResponse_data);

                if (mResponse_data.contains("5555")) {

                }
                send_Request_Command(serialarr);
                while (aBoolean != true)
                {
                    SystemClock.sleep(50);
                };
                aBoolean = false;
                Log.e("Res", mResponse_data);

                if (mResponse_data.length() > 18) {

                    AppVariables.Serialnumber = mResponse_data.substring(9, 21);
                    flagresponse = true;
                } else {
                    Log.e("serial number resp", mResponse_data);

                    flagresponse = false;
                }
                final byte[] readref = AppVariables.FormCmd(AppVariables.READ_REFERENCE_DATA, "").getBytes();
                final byte[] readProp = AppVariables.FormCmd(AppVariables.READ_PROPRIETARY_DATA, "").getBytes();

                aBoolean = false;
                send_Request_Command(readref);
                while (aBoolean != true)
                {
                    SystemClock.sleep(50);
                };
                aBoolean = false;
                Log.e("Res", mResponse_data);

                AppVariables.ref_data = mResponse_data.substring(9, 25);
                if(mResponse_data.contains(AppVariables.ref_data))
                {
                    Log.e("matched","ref");

                }
                aBoolean = false;
                send_Request_Command(readProp);

                while (aBoolean != true)
                {
                    SystemClock.sleep(50);
                };
                aBoolean = false;
                Log.e("Res", mResponse_data);
                AppVariables.prop_data = mResponse_data.substring(9, 33);

                if (flagresponse)
                {
                    String rndmenc = null;
                    TeAES_Lib aes_lib = new TeAES_Lib();

                    try
                    {
                        rndmenc = aes_lib.Generatedencdata();
                        Log.i("val", "AESstarted");
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    cmd = AppVariables.FormCmd(AppVariables.WRITE_ENCRYPTED_DATA,rndmenc).getBytes();
                    Log.e("key",DataConversion.bytesToHexstr(TeAES_Lib.genkey));
                    aBoolean = false;
                    send_Request_Command(cmd);//send encdata

                    while (aBoolean != true)
                    {
                        SystemClock.sleep(50);
                    };

                    aBoolean = false;

                    Log.e("Res", mResponse_data);

                    byte[] encdata = new byte[32];
                    if (mResponse_data.length() > 33)
                    {
                        System.arraycopy(response, 9, encdata, 0,32);
                        byte[] cnvpayload = DataConversion._PanToByteArray(encdata,16);
                        boolean flag = responsehandler(cnvpayload);
                        if (flag)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //AppVariables.writeDateofFil(dialog,"Connected",true,1);
                                    Toast.makeText(PairedDevices.this, "Connected", Toast.LENGTH_SHORT).show();

                                }
                            });

                            //SystemClock.sleep(1000);;

                            SendCmd sendCmd = new SendCmd();
                            sendCmd.start();
                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                 //   AppVariables.writeDateofFil(dialog,"Unable to connect!",true,0);
                                    Toast.makeText(PairedDevices.this, "Not  Connected", Toast.LENGTH_SHORT).show();
                                }
                            });
                         /*   SystemClock.sleep(1000);;
                            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                            finish();
                            startActivity(i);*/

                        }
                    } else {

                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public class SendCmd extends Thread
    {

        @Override
        public void run()
        {
            try
            {
                BluetoothConversation.reshandle = 4;
                bluetoothSemaphoreService = new BluetoothSemaphoreService();


                byte[] cmd = "xtsp6\r\n".getBytes();//header for ems
                aBoolean = false;
               /* send_Request_Command(cmd);
                while (!aBoolean )
                {
                    SystemClock.sleep(10);
                }*/
                mResponse_data = "";
                byte[] semaphoreRequest = bluetoothSemaphoreService.SendCommandwithTimeout(cmd,false,false,2000);
                if(semaphoreRequest!=null  && mResponse_data.contains("OK") )
                {
                    cmd = "xth0\r\n".getBytes();
                    aBoolean = false;
                   /* send_Request_Command(cmd);
                    while (!aBoolean)
                    {
                        SystemClock.sleep(50);
                    }*/
                    mResponse_data = "";
                    semaphoreRequest = new byte[0];
                    semaphoreRequest = bluetoothSemaphoreService.SendCommandwithTimeout(cmd, false, false, 2000);
                    if(semaphoreRequest!=null  && mResponse_data.contains("OK"))
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               // AppVariables.ShowDialog(dialog,getString(R.string.connected),true,1);
                            }
                        });

                        cmd = "xte0\r\n".getBytes();
                        aBoolean = false;
                        /*send_Request_Command(cmd);
                        while (!aBoolean)
                        {
                            SystemClock.sleep(50);
                        }*/
                        mResponse_data = "";
                        semaphoreRequest = new byte[0];
                        semaphoreRequest = bluetoothSemaphoreService.SendCommandwithTimeout(cmd, false, false, 2000);
                        if(semaphoreRequest!=null  && mResponse_data.contains("OK"))
                        {
                            cmd = "XTEA0\r\n".getBytes();
                            aBoolean = false;
                            send_Request_Command(cmd);
                            while (!aBoolean)
                            {
                                SystemClock.sleep(10);
                            }


                            cmd = "xtsh7e0\r\n".getBytes();
                            aBoolean = false;
                            send_Request_Command(cmd);
                            while (!aBoolean)
                            {
                                SystemClock.sleep(10);
                            }

                            cmd = "xtrh7e8\r\n".getBytes();
                            aBoolean = false;
                            send_Request_Command(cmd);
                            while (!aBoolean)
                            {
                                SystemClock.sleep(10);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getApplicationContext(), SelectVehicle.class));
                                    overridePendingTransition(R.anim.out,R.anim.in);

                                }
                            });
                        }
                        else
                        {
                            try {
                                BluetoothConversation.ConnectionCheck=true;
                            }
                            catch (Exception e2)
                            {
                                e2.printStackTrace();
                                BluetoothConversation.CollectLogData("@ state switch : "+e2.getMessage());
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
                                    //linearLayoutpass.setVisibility(View.VISIBLE);
                                  /*  shimmerLayout =  findViewById(R.id.shimmer);
                                    shimmerLayout.startShimmerAnimation();*/
                                    et.setText("");
                                }
                            });
                        }


                    }
                    else
                    {
                        try {
                            BluetoothConversation.ConnectionCheck=true;
                        }
                        catch (Exception e2)
                        {
                            e2.printStackTrace();
                            BluetoothConversation.CollectLogData("@ state switch : "+e2.getMessage());
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               // AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
                             //   linearLayoutpass.setVisibility(View.VISIBLE);
                               /* shimmerLayout =  findViewById(R.id.shimmer);
                                shimmerLayout.startShimmerAnimation();*/
                               // et.setText("");
                            }
                        });
                    }

                }
                else
                {
                    try {
                        BluetoothConversation.ConnectionCheck=true;
                    }
                    catch (Exception e2)
                    {
                        e2.printStackTrace();
                        BluetoothConversation.CollectLogData("@ state switch : "+e2.getMessage());
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
                           // linearLayoutpass.setVisibility(View.VISIBLE);
                           /* shimmerLayout =  findViewById(R.id.shimmer);
                            shimmerLayout.startShimmerAnimation();*/
                           // et.setText("");
                        }
                    });
                }





            }
            catch (final Exception e)
            {
                e.printStackTrace();
                try {
                    BluetoothConversation.ConnectionCheck=true;
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                    BluetoothConversation.CollectLogData("@ state switch : "+e2.getMessage());

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      //  AppVariables.ShowDialog(dialog,getString(R.string.somethingwntwronreinsrtvci),true,0);
                       // linearLayoutpass.setVisibility(View.VISIBLE);
                      /*  shimmerLayout =  findViewById(R.id.shimmer);
                        shimmerLayout.startShimmerAnimation();*/
                       // et.setText("");
                        Toast.makeText(context, "Unable to connect", Toast.LENGTH_SHORT).show();
                    }
                });
                BluetoothConversation.CollectLogData("@ state switch: "+e.getMessage());

                Log.e("error", e.getMessage());
            }

            super.run();
        }
    }

    public boolean responsehandler(byte[] data)
    {
        boolean resp = false;
        if (data.length < 8) {
            //do nothing
            resp = false;
        } else {
            byte[] decdataarr = null;
            // String decdata = null;
            byte[] key = TeAES_Lib.genkey;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = new Date();
            String strdate = sdf.format(date);
            try {
                Log.e("dec time before", strdate);
                decdataarr = TeAES_Lib.decrypt_data(data, key);
                date = new Date();
                strdate = sdf.format(date);
                Log.e("dec time after", strdate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            short storedatarnm = TeAES_Lib.randomnumber;
            short shrtdatarnm = 0x0000;
            shrtdatarnm = (short) (decdataarr[0] << 8);
            shrtdatarnm |= (short) (decdataarr[1] & 0xFF);

            if (shrtdatarnm == storedatarnm)
            {
                Log.e("random number","matched");
                resp = true;
            } else
            {
                Log.e("random number","NOt matched");
                resp = false;
            }
            //first two and last two
            //

        }
        return resp;
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
                bluetoothSemaphoreService.getResponse(response);
                aBoolean = true;
            }
            @Override
            public void ConnectionLost()
            {
                //Intent i = new Intent(getApplicationContext(), ConnectionLost.class);
                // startActivity(i);

            }
            @Override
            public void Connected(String str)
            {
                if(str.equals(AppVariables.CONNECTED_STR))
                {

                    UnLockSecurity unLockSecurity = new UnLockSecurity();
                    unLockSecurity.start();
                }
                else
                if(str.equals(AppVariables.NOT_CONNECTED_STR))
                {
                    //linearLayoutpass.setVisibility(View.VISIBLE);
                    //AppVariables.ShowDialog(dialog,"Unable to connect!",true,0);
                    Toast.makeText(context, "Unable to connect!", Toast.LENGTH_SHORT).show();
                    //et.setText("");
                }

            }
        });
    }


}
