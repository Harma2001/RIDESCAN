package com.example.tvsridescan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.abs.ABSLiveParameters;
import com.example.tvsridescan.connection.ConnectionInterrupt;
import com.example.tvsridescan.ems.EMSLiveParameters;
import com.example.tvsridescan.icu.ClusterLiveParameters;

public class LiveParameterCat extends AppCompatActivity
{

    LinearLayout moduleButton, graphButton;
    public static int cat = 0;
    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ems_live_cat);

        context = LiveParameterCat.this;

        bridge = SingleTone.getBluetoothBridge();
        if(bridge!=null)
        {
            Resposne();
        }
        else
        {
            Intent i = new Intent(context, ConnectionInterrupt.class);
            startActivity(i);
            overridePendingTransition(R.anim.out,R.anim.in);
        }

        moduleButton =  findViewById(R.id.module);
        graphButton =  findViewById(R.id.graph);

        moduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(cat==1)
                {

                    Intent i  = new Intent(getApplicationContext(),EMSLiveParameters.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.out,R.anim.in);
                }
                else
                if(cat==2)
                {
                    Intent i2 = new Intent(getApplicationContext(),ABSLiveParameters.class);
                    startActivity(i2);
                    overridePendingTransition(R.anim.out,R.anim.in);
                }
                else
                if(cat==3)
                {
                    Intent i3 = new Intent(getApplicationContext(),ClusterLiveParameters.class);
                    startActivity(i3);
                    overridePendingTransition(R.anim.out,R.anim.in);
                }


            }
        });

        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent i = null;
                if(cat==1)
                {
                    if(AppVariables.BikeModel==1)
                    {
                        LiveSelectParams.filename = "EmsLiveParameters1.csv";
                    }
                    else
                    {
                        LiveSelectParams.filename = "EmsLiveParameters.csv";
                    }

                }
                else
                if(cat==2)
                {
                    LiveSelectParams.filename = "absliveselectparams.csv";
                }
                else
                if(cat==3)
                {
                    LiveSelectParams.filename = "clusterliveselectparams.csv";
                }

                i  = new Intent(getApplicationContext(),LiveSelectParams.class);
                startActivity(i);
                overridePendingTransition(R.anim.out,R.anim.in);
            }
        });
    }
    public  void send_Request_Command(byte[] arr)
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
                //response = arr;
               // mResponse_data = str;
               // aBoolean = true;
            }
            @Override
            public void ConnectionLost()
            {
                Intent i = new Intent(context, ConnectionInterrupt.class);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
