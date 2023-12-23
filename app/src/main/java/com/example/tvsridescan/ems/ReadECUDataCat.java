package com.example.tvsridescan.ems;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;


public class ReadECUDataCat extends Activity
{
    Button b1,b2,b3,b4,b5,b6,b7,b8,b9;
    BluetoothBridge bridge;
    static String mResponse_data = null;
    static byte[] response;
    static boolean aBoolean = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_ecudata_cat);
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

        b1 =  findViewById(R.id.eng_his);
        b2 =  findViewById(R.id.obd);
        b3 =  findViewById(R.id.ecu_id);
        b4 =  findViewById(R.id.curr_status);
        b5 =  findViewById(R.id.cranking);
        b6 =  findViewById(R.id.intake);
        b7 =  findViewById(R.id.fuel_ignition);
        b8 =  findViewById(R.id.exhuast_system);
        b9 =  findViewById(R.id.sensor_switch);

        final Intent i = new Intent(getApplicationContext(),EMSReadDIDs.class);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EMSReadDIDs.filename = "engine history.csv";
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EMSReadDIDs.filename = "OBD.csv";
                startActivity(i);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EMSReadDIDs.filename = "ECU Identification.csv";
                startActivity(i);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EMSReadDIDs.filename = "Current Status.csv";
                startActivity(i);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EMSReadDIDs.filename = "Cranking.csv";
                startActivity(i);
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EMSReadDIDs.filename = "Intake.csv";
                startActivity(i);
            }
        });

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EMSReadDIDs.filename = "Fueling and Ignition.csv";
                startActivity(i);
            }
        });

        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EMSReadDIDs.filename = "Exhuast System.csv";
                startActivity(i);
            }
        });

        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EMSReadDIDs.filename = "Sensors and Switches.csv";
                startActivity(i);
            }
        });

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

            }
            @Override
            public void Connected(String str)
            {

            }
        });
    }
}
