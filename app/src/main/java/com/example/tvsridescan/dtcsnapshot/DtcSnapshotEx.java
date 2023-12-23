package com.example.tvsridescan.dtcsnapshot;

import static com.example.tvsridescan.Library.AppVariables.DTCCode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothConversation;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.adapters.CustomAdapter_Snapshot;
import com.example.tvsridescan.adapters.DataModel_Snapshot;

import java.util.ArrayList;

public class DtcSnapshotEx extends AppCompatActivity
{


    BluetoothConversation bluetoothConnectionProcess;
    static String mResponse_data = null;
    static byte[] response,cmd;
    static boolean aBoolean = false;
    String[]  valarr = new String[0];
    String [] paramnames = new String[0];

    ArrayList<DataModel_Snapshot> dataModels;
    private static CustomAdapter_Snapshot adapter;

    TextView textView;

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtc_snapchat);
        textView = (TextView) findViewById(R.id.dtccode);
        textView.setText(DTCCode);
        Storedata();
        bluetoothConnectionProcess = SingleTone.getBluetoothConversation();
        bluetoothConnectionProcess.reshandle =4;

        SendCmd sendCmd = new SendCmd();
        sendCmd.start();

    }

    public class SendCmd extends Thread
    {
        @Override
        public void run()
        {
            cmd = ("1906"+DTCCode+"FF\r\n").getBytes();
            send_Request_Command(cmd);
            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }
            Log.e("res",mResponse_data);
            if(!mResponse_data.substring(0,2).equals("7F"))
            {
                SnapshotResponse(mResponse_data);
                UI();
            }

            super.run();
        }
    }
    public  void UI()
    {
        dataModels = new ArrayList<>();

        for(int i=0;i<paramnames.length;i++)
        {
            dataModels.add(new DataModel_Snapshot(valarr[i], paramnames[i]));
        }

        adapter = new CustomAdapter_Snapshot(dataModels, getApplicationContext());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView = (ListView) findViewById(R.id.lv);
                listView.setAdapter(adapter);

            }
        });


    }

    private void send_Request_Command(byte[] cmd) {
        bluetoothConnectionProcess.assigntoContolData(mHandler_Response);
        bluetoothConnectionProcess.sendRequest(cmd);
    }


    private  static  Handler mHandler_Response = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 11:// 11 Is ID for data receiving
                    response = (byte[]) msg.obj;//
                    mResponse_data = new String(response);
                    aBoolean = true;

            }
        }
    };


    public void SnapshotResponse(String res)
    {
    // 5904 + cmd 22 33 44 + aa +

        String str = AppVariables.parsecmd2(res,"5906");
        String dtcode = str.substring(0,8);
        String str1 = str.substring(8,12);//rec no
        String str2 = str.substring(12,16);//rec no
        String str3 = str.substring(16,20);//rec no

        String valparams = str1+","+str2+","+str3;
        valarr = valparams.split(",");

    }

    public String[] Storedata()
    {
       String data = "DTCExtendedDataRecordNumber Condition-Byte (CDB),DTCExtendedDataRecordNumber frequency counter(HZ),DTCExtendedDataRecordNumber healing counter (HLZ)";
       paramnames = data.split(",");
       return paramnames;
    }



}
