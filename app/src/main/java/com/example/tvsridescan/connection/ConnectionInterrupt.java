package com.example.tvsridescan.connection;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.BluetoothConversation;
import com.example.tvsridescan.R;

public class ConnectionInterrupt extends AppCompatActivity
{
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;
    BluetoothBridge bridge;
    Button b1,b2;
    TextView tvmsg;
    public static String msg_txt = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_interrupt);
        overridePendingTransition(R.anim.out,R.anim.in);

        BluetoothConversation.ConnectionCheck = false;
        bridge = new BluetoothBridge(this);
        b1 =  findViewById(R.id.buttonretry);
        b2 =  findViewById(R.id.buttonclose);
        tvmsg =  findViewById(R.id.textView7);

        tvmsg.setText(getString(R.string.disconnectedfromvci));
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetryConnection();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                BluetoothConversation.ConnectionCheck = true;
                finishAffinity();
                moveTaskToBack(true);

                //System.exit(0);

            }
        });
    }

    public void RetryConnection()
    {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        finish();
        startActivity(i);
    }
    boolean doubleBackToExitPressedOnce = false;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            BluetoothConversation.ConnectionCheck = true;
            finishAffinity();
            moveTaskToBack(true);
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
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
