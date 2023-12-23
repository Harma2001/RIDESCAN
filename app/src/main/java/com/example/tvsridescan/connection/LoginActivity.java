package com.example.tvsridescan.connection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.tvsridescan.Configuration.ChangePassword;
import com.example.tvsridescan.Configuration.ChooseLangActivity;
import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Configuration.ValvcisnoActivity;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.BluetoothConversation;
import com.example.tvsridescan.Library.BluetoothSemaphoreService;
import com.example.tvsridescan.Library.DataConversion;
import com.example.tvsridescan.Library.HttpCommunication;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.Library.TeAES_Lib;
import com.example.tvsridescan.R;
import com.example.tvsridescan.SelectVehicle;
import com.example.tvsridescan.SplashScreen;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoginActivity extends Activity implements AdapterView.OnItemSelectedListener {
    EditText et;
    String userid = null;
    String pass = "";
    public static String TAG = LoginActivity.class.getSimpleName();
    //bt variables
    BluetoothAdapter bluetoothAdapter;
    BluetoothSemaphoreService bluetoothSemaphoreService;
    private Set<BluetoothDevice> pairedDevices;

    boolean flag = true;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;
    TextView load;
    LinearLayout linearLayoutpass;
    String[] serilnum;
    HashMap<String, String> hmap = new HashMap<String, String>();
    String itemvci;
    boolean DiscFlag = true;
    TextView txtpass,txtupdate;
    BluetoothBridge bridge;
    Dialog dialog;
    LinearLayout llint;
    ProgressBar progressBar;
    Set set = null;
   // ShimmerLayout shimmerLayout;
   // LinearLayout utilityBtn;

    LinearLayout changeLanguageBtn;

    Animation animFadein1;
    MediaPlayer mp;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar =  findViewById(R.id.pb);
        bridge = new BluetoothBridge(getApplicationContext());
        linearLayoutpass =  findViewById(R.id.linearLayout2);
        dialog = new Dialog(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        accessLocationPermission();
        BluetoothConversation.reshandle =1;
        progressBar.setVisibility(View.INVISIBLE);
        set = new HashSet();
        linearLayoutpass.setVisibility(View.VISIBLE);

        animFadein1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        mp = MediaPlayer.create(this, R.raw.buttonclick);

        changeLanguageBtn = findViewById(R.id.changelanglayout);
        changeLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeLanguageBtn.startAnimation(animFadein1);
                mp.start();
                Intent intent = new Intent(LoginActivity.this,ChooseLangActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });
    /*   utilityBtn = findViewById(R.id.updateutilitybtn);
        utilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            utilityBtn.startAnimation(animFadein1);
            mp.start();
            Intent intent = new Intent(LoginActivity.this,UpdateUtility.class);
            startActivity(intent);

            }
        });
        shimmerLayout =  findViewById(R.id.shimmer);
        shimmerLayout.startShimmerAnimation();*/

        set = AppVariables.RetSet(getApplicationContext());
        if(set!=null && set.size()!=0)
        {
            AppVariables.vcino = new ArrayList(set);
        }
        else
        {
            AppVariables.vcino = new ArrayList();
            startActivity(new Intent(getApplicationContext(),ValvcisnoActivity.class));
            overridePendingTransition(R.anim.out,R.anim.in);

        }

        UI();
    }

    public void UI()
    {
        List<String> categories = new ArrayList<>();
        et =  findViewById(R.id.et);
        txtpass =  findViewById(R.id.password);
        txtupdate =  findViewById(R.id.update);

        Spinner spinner =  findViewById(R.id.spinner);
        txtpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    HttpCommunication.PostUrl =HttpCommunication.Forgot_Password;
                    Intent i = new Intent(getApplicationContext(), ChangePassword.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.out,R.anim.in);
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, getString(R.string.interntUnavail), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        txtupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(AppVariables.CheckInternet(getApplicationContext()))
                {
                    HttpCommunication.PostUrl = HttpCommunication.Verify_Email;
                    Intent i = new Intent(getApplicationContext(), ValvcisnoActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.out,R.anim.in);

                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, getString(R.string.interntUnavail), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        spinner.setOnItemSelectedListener(this);
        // Spinner Drop down elements

        categories.add("Select ID");
        String[] arr;

        if(SplashScreen.CheckSessionDate())
        {
            serilnum = new String[HttpCommunication.Device.size()];
            for(int i = 0; i< HttpCommunication.Device.size(); i++)
            {
                arr = HttpCommunication.Device.get(i).toString().split(";");
                serilnum[i] = arr[0];
                hmap.put(arr[0],arr[1]);
            }
        }
        else
        {
            serilnum = new String[AppVariables.vcino.size()];
            for(int i = 0; i< AppVariables.vcino.size(); i++)
            {
                arr = AppVariables.vcino.get(i).toString().split(";");
                serilnum[i] = arr[0];
                hmap.put(arr[0],arr[1]);
            }
        }

        ArrayAdapter<String> dataAdapter =  new ArrayAdapter<String>(this, R.layout.changelangspinnertext, serilnum);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
            @Override
            public void afterTextChanged(Editable s)
            {

                pass = et.getText().toString();
                if(pass.length()==4)
                {
                    if(SplashScreen.CheckSessionDate())
                    {
                        if(AppVariables.CheckInternet(LoginActivity.this))
                        {
                            HttpCommunication.password = pass;
                            Verify_Login  verify_login = new Verify_Login();
                            verify_login.start();
                        }
                        else {

                        }
                    }
                    else
                    {
                        if(ValidateDetails(Integer.parseInt(pass)))
                        {
                            //compare password with existing
                            StartDiscover();
                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, getString(R.string.pwddidntmatch), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }

                }

            }
        });

    }

    public class Verify_Login extends Thread
    {
        @Override
        public void run()
        {
            HttpCommunication.JSONDATA="{\"api_key\":\""+HttpCommunication.API_KEY+"\",\"email\":\"" + AppVariables.email_id + "\",\"password\":\""+HttpCommunication.password+"\",\"device_id\":\""+HttpCommunication.Device_id+"\"}" ;
            HttpCommunication.PostUrl =HttpCommunication.Login_URL;
            String Getop = HttpCommunication.POSTDATA();
            try {
                JSONObject object = new JSONObject(Getop);
                HttpCommunication.Status = object.getString("status");

                String Err_MSG = "";
                if (HttpCommunication.Status.equals("Success"))
                {
                    //  Err_MSG = object.getString("message");
                    //final String finalErr_MSG = Err_MSG;
                    JSONObject object1 = new JSONObject(object.getString("data"));
                    String status_expire = object1.getString("expire");
                    if(!status_expire.equals("true"))
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                StartDiscover();
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(getApplicationContext(), getString(R.string.devexpiredcontactmahle), Toast.LENGTH_LONG).show();
                                et.setText("");
                                et.requestFocus( );
                            }
                        });
                    }
                    // Log.e("False", Err_MSG);
                }
                else if (HttpCommunication.Status.equals("false"))
                {
                    Err_MSG = object.getString("message");
                    final String finalErr_MSG1 = Err_MSG;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), finalErr_MSG1, Toast.LENGTH_SHORT).show();
                            et.setText("");
                            et.requestFocus();
                        }
                    });
                }
            }
            catch (final Exception e)
            {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            super.run();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        itemvci = parent.getItemAtPosition(position).toString();

        if(itemvci.contains("TVS"))
        {
            HttpCommunication.Device_id  = itemvci;
        }
        else
        {
            et.setEnabled(false);
            Toast.makeText(parent.getContext(), getString(R.string.pleaseselectvci), Toast.LENGTH_LONG).show();
        }

    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    public void StartDiscover()
    {
        closeKeyPad();
        flag =true;
        linearLayoutpass.setVisibility(View.INVISIBLE);
        /*shimmerLayout =  findViewById(R.id.shimmer);
        shimmerLayout.stopShimmerAnimation();*/

        AppVariables.ShowDialog(dialog,getString(R.string.pleasewait),false,2);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getApplicationContext().registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getApplicationContext().registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        pairedDevices = bluetoothAdapter.getBondedDevices();
        doDiscovery();
    }

    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            try
            {
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.e(TAG,"Searching..");
                    // If it's already paired, skip it, because it's been listed already
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                    {
                        if(device.getName().equals(itemvci))//MIC43_FLASHER_2 //AppVariables.BluetoothDeviceName
                        {
                            if(flag)
                            {
                                flag = false;
                                AppVariables.BluetoothDeviceAdress = device.getAddress();
                                bridge.EstaConn(getApplicationContext(),device.getAddress());
                                Resposne();
                                bluetoothAdapter.cancelDiscovery();
                            }
                        }
                    }

                }
                if(!BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    bluetoothAdapter.cancelDiscovery();
                    et.setText("");
                    et.requestFocus();
                    if(dialog!=null && dialog.isShowing())
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                linearLayoutpass.setVisibility(View.VISIBLE);
                              //  shimmerLayout.startShimmerAnimation();
                                AppVariables.ShowDialog(dialog,getString(R.string.vcinotfound),true,0);
                                et.setText("");
                            }
                        });
                    }
                }
            }
            catch (Exception e)
            {
                bluetoothAdapter.cancelDiscovery();
                linearLayoutpass.setVisibility(View.VISIBLE);
             //   shimmerLayout.startShimmerAnimation();
                // layoutpb.setVisibility(View.INVISIBLE);
                bluetoothAdapter.cancelDiscovery();
                et.setText("");
                et.requestFocus();
                if(dialog!=null && dialog.isShowing())
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            linearLayoutpass.setVisibility(View.VISIBLE);
                           // shimmerLayout.startShimmerAnimation();
                            AppVariables.ShowDialog(dialog,getString(R.string.vcinotfound),true,0);
                            et.setText("");
                        }
                    });
                }

                Toast.makeText(context, getString(R.string.vcinotfound), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
     /*   if(dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }*/
    }
    // Start device discover with the BluetoothAdapter
    private void doDiscovery()
    {
        // Get a set of currently paired devices
        pairedDevices = bluetoothAdapter.getBondedDevices();
        String btaddress = null;
        DiscFlag=true;
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                if(!device.getName().equals(itemvci))
                {
                   // unpairDevice(device);
                }
                else
                {
                    btaddress = device.getAddress();
                    DiscFlag = false;
                }

            }
        }

        if(DiscFlag)
        {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            // Request discover from BluetoothAdapter
            bluetoothAdapter.startDiscovery();
        }
        else
        {
            bridge.EstaConn(getApplicationContext(),btaddress);
            AppVariables.BluetoothDeviceAdress = btaddress;
            SystemClock.sleep(500);
            Resposne();
        }
    }

    public class UnLockSecurity extends Thread
    {
        @Override
        public void run()
        {

            SystemClock.sleep(1000);
            SingleTone.setBluetoothBridge(bridge);
            try {

                BluetoothConversation.reshandle = 1;
                boolean flagresponse = true;

                bluetoothSemaphoreService = new BluetoothSemaphoreService();

                //write ref data
                String cmdstr = AppVariables.FormCmd(AppVariables.PING, "");
                byte[] cmd = cmdstr.getBytes();
                aBoolean = false;
                /*send_Request_Command(cmd);
                while (aBoolean != true)
                {
                    SystemClock.sleep(50);
                };*/
                mResponse_data = "";
                byte[] semaphoreRequest = bluetoothSemaphoreService.SendCommandwithTimeout(cmd,false,false,2000);

                if(semaphoreRequest!=null && semaphoreRequest.length>=5 && !mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                {
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
                       // Log.e("key",DataConversion.bytesToHexstr(TeAES_Lib.genkey));
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


                                if(AppVariables.CheckInternet(getApplicationContext()))
                                {
                                    AppVariables.Sessioncount = 0;
                                    AppVariables.StoreSSCount(getApplicationContext());
                                }
                                else
                                {
                                    AppVariables.StoreSSCount(getApplicationContext());
                                }

                                SendCmd sendCmd = new SendCmd();
                                sendCmd.start();
                            }
                            else
                            {
                                try {
                                    BluetoothConversation.ConnectionCheck=true;
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        linearLayoutpass.setVisibility(View.VISIBLE);
                                       // shimmerLayout =  findViewById(R.id.shimmer);
                                       // shimmerLayout.startShimmerAnimation();
                                        AppVariables.ShowDialog(dialog,getString(R.string.unabletovalidatevci),true,0);
                                        et.setText("");
                                    }
                                });


                            }
                        } else {
                            try {
                                BluetoothConversation.ConnectionCheck=true;
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    linearLayoutpass.setVisibility(View.VISIBLE);
                                  //  shimmerLayout =  findViewById(R.id.shimmer);
                                   // shimmerLayout.startShimmerAnimation();
                                    AppVariables.ShowDialog(dialog,getString(R.string.unabletovalidatevci),true,0);
                                    et.setText("");
                                }
                            });

                        }
                    }
                }
                else
                {
                    try {
                        BluetoothConversation.ConnectionCheck=true;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
                            linearLayoutpass.setVisibility(View.VISIBLE);
                          //  shimmerLayout =  findViewById(R.id.shimmer);
                           // shimmerLayout.startShimmerAnimation();
                            et.setText("");
                        }
                    });
                }

            }
            catch (Exception e)
            {
                try {
                    BluetoothConversation.ConnectionCheck=true;
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppVariables.ShowDialog(dialog,getString(R.string.somethingwntwronreinsrtvci),true,0);
                        linearLayoutpass.setVisibility(View.VISIBLE);
                      //  shimmerLayout =  findViewById(R.id.shimmer);
                      //  shimmerLayout.startShimmerAnimation();
                        et.setText("");
                    }
                });
                e.printStackTrace();
            }
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
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

    public class SendCmd extends Thread
    {

        @Override
        public void run()
        {
            try
            {
                BluetoothConversation.reshandle = 4;
                bluetoothSemaphoreService = new BluetoothSemaphoreService();


                byte[] cmd = "XTEA0\r\n".getBytes();//header for ems
                aBoolean = false;

                mResponse_data = "";
                byte[] semaphoreRequest = bluetoothSemaphoreService.SendCommandwithTimeout(cmd,false,false,2000);
                if(semaphoreRequest!=null  && mResponse_data.contains("OK") )
                {
                    cmd = "XTSH7E0\r\n".getBytes();
                    aBoolean = false;

                    mResponse_data = "";
                    semaphoreRequest = new byte[0];
                    semaphoreRequest = bluetoothSemaphoreService.SendCommandwithTimeout(cmd, false, false, 2000);
                    if(semaphoreRequest!=null  && mResponse_data.contains("OK"))
                    {


                        cmd = "XTRH7E8\r\n".getBytes();
                        aBoolean = false;

                        mResponse_data = "";
                        semaphoreRequest = new byte[0];
                        semaphoreRequest = bluetoothSemaphoreService.SendCommandwithTimeout(cmd, false, false, 2000);
                        if(semaphoreRequest!=null  && mResponse_data.contains("OK"))
                        {
                            cmd = "XTH0\r\n".getBytes();
                            mResponse_data = "";
                            semaphoreRequest = new byte[0];
                            semaphoreRequest = bluetoothSemaphoreService.SendCommandwithTimeout(cmd, false, false, 2000);
                            if(semaphoreRequest!=null  && mResponse_data.contains("OK"))
                            {
                                cmd = "XTE0\r\n".getBytes();
                                aBoolean = false;
                                mResponse_data = "";
                                semaphoreRequest = new byte[0];
                                semaphoreRequest = bluetoothSemaphoreService.SendCommandwithTimeout(cmd, false, false, 2000);
                                if(semaphoreRequest!=null  && mResponse_data.contains("OK"))
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AppVariables.ShowDialog(dialog,getString(R.string.connected),true,1);
                                        }
                                    });
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
                                            AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
                                            linearLayoutpass.setVisibility(View.VISIBLE);
                                  /*  shimmerLayout =  findViewById(R.id.shimmer);
                                    shimmerLayout.startShimmerAnimation();*/
                                            et.setText("");
                                        }
                                    });
                                }



                            }
                            else {
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
                                        AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
                                        linearLayoutpass.setVisibility(View.VISIBLE);
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
                                    AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
                                    linearLayoutpass.setVisibility(View.VISIBLE);
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
                                AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
                                linearLayoutpass.setVisibility(View.VISIBLE);
                               /* shimmerLayout =  findViewById(R.id.shimmer);
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
                            AppVariables.ShowDialog(dialog,getString(R.string.vcinotrespondingtryagain),true,0);
                            linearLayoutpass.setVisibility(View.VISIBLE);
                           /* shimmerLayout =  findViewById(R.id.shimmer);
                            shimmerLayout.startShimmerAnimation();*/
                            et.setText("");
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
                        AppVariables.ShowDialog(dialog,getString(R.string.somethingwntwronreinsrtvci),true,0);
                        linearLayoutpass.setVisibility(View.VISIBLE);
                      /*  shimmerLayout =  findViewById(R.id.shimmer);
                        shimmerLayout.startShimmerAnimation();*/
                        et.setText("");
                    }
                });
                BluetoothConversation.CollectLogData("@ state switch: "+e.getMessage());

                Log.e("error", e.getMessage());
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
                    bluetoothSemaphoreService = new BluetoothSemaphoreService();
                    UnLockSecurity unLockSecurity = new UnLockSecurity();
                    unLockSecurity.start();
                }
                else
                if(str.equals(AppVariables.NOT_CONNECTED_STR))
                {
                    linearLayoutpass.setVisibility(View.VISIBLE);
                  /*  shimmerLayout =  findViewById(R.id.shimmer);
                    shimmerLayout.startShimmerAnimation();*/
                    AppVariables.ShowDialog(dialog,getString(R.string.unabletoconnect),true,0);
                    et.setText("");
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    private void accessLocationPermission() {
        int accessCoarseLocation = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            accessCoarseLocation = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        int accessFineLocation   = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listRequestPermission = new ArrayList<String>();

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listRequestPermission.isEmpty()) {
            String[] strRequestPermission = listRequestPermission.toArray(new String[listRequestPermission.size()]);
            requestPermissions(strRequestPermission, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int gr : grantResults) {
                        // Check if request is granted or not
                        if (gr != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }

                    //TODO - Add your code here to start Discovery

                }
                break;
            default:
                return;
        }
    }

    public void closeKeyPad()
    {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    boolean doubleBackToExitPressedOnce = false;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            finishAffinity();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.pleaseclickbackagain), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    boolean responseflag = true;
    StringBuilder responsestr  = new StringBuilder();

    public boolean ValidateDetails(int password)
    {
        boolean result = true;
        if(password==AppVariables.pwd)
        {
            result = true;
        }
        else
        {
            result = false;
        }
        return result;
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}