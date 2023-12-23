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

public class DtcSnapshot extends AppCompatActivity
{


    BluetoothConversation bluetoothConnectionProcess;
    static String mResponse_data = null;
    static byte[] response,cmd;
    static boolean aBoolean = false;

    String[]  valarr = new String[0];
    String[]  _valarr = new String[0];
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
            cmd = ("1904"+DTCCode+"FF\r\n").getBytes();
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

      /*  for(int i=0;i<paramnames.length;i++)
        {
            dataModels.add(new DataModel_Snapshot(_valarr[i], paramnames[i]));
        }*/
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

        String str = AppVariables.parsecmd2(res,"5904");
        String dtcode = str.substring(0,8);
        String str1 = str.substring(8,10);//rec no
        String str2 = str.substring(10,12);//rec no
        String str3 = str.substring(12,22);//rec no
        String str4 = str.substring(22,34);//rec no
        String str5 = str.substring(34,40);//rec no
        String str6 = str.substring(40,46);//rec no
        String str7 = str.substring(46,54);//rec no
        String str8 = str.substring(54,60);//rec no
        String str9 = str.substring(60,66);//rec no
        String str10 = str.substring(66,74);//rec no
        String str11 = str.substring(74,82);//rec no

       /* int len = 82;

        String _str1 = str.substring(len+8,len+10);//rec no
        String _str2 = str.substring(len+10,len+12);//rec no
        String _str3 = str.substring(len+12,len+22);//rec no
        String _str4 = str.substring(len+22,len+34);//rec no
        String _str5 = str.substring(len+34,len+40);//rec no
        String _str6 = str.substring(len+40,len+46);//rec no
        String _str7 = str.substring(len+46,len+54);//rec no
        String _str8 = str.substring(len+54,len+60);//rec no
        String _str9 = str.substring(len+60,len+66);//rec no
        String _str10 = str.substring(len+66,len+74);//rec no
        String _str11 = str.substring(len+74,len+82);//rec no*/

        String valparams = str1+","+str2+","+str3+","+str4+","+str5+","+str6+","+str7+","+str8+","+str9+","+str10+","+str11;
        //String _valparams = _str1+","+_str2+","+_str3+","+_str4+","+_str5+","+_str6+","+_str7+","+_str8+","+_str9+","+_str10+","+_str11;

        valarr = valparams.split(",");
       // _valarr = _valparams.split(",");

    }

    public String[] Storedata()
    {
       String data = "DTCSnapshotRecordNumber,DTCSnapshotRecordNumberOfIdentifiers,KM-Stand ID,System time ID,Status control unit,TVS Bike InfoID,Board supply voltage,Vref ID,Status-Byte (Switches),operation mode,Internal Fault Id";
       paramnames = data.split(",");
       return paramnames;
    }



}
