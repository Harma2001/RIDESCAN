package com.example.tvsridescan.rtr1604v1chefi;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.KwpABS.KwpAbsMenu;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.BluetoothConversation;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.SecureAccess;
import com.example.tvsridescan.connection.ConnectionInterrupt;
import com.example.tvsridescan.ems.EmsMenu;

public class RTR1604v1chefiMenu extends AppCompatActivity {

    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;

    LinearLayout ll1,ll2,ll3;
    Animation animFadein1,animFadein2,animFadein3;
    MediaPlayer mp;
    Context context;
    private static final String TAG = "Rtr1604vchefiMenu.this";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtr1604v1chefi_menu);
        init();
    }

    private void init()
    {
        context = RTR1604v1chefiMenu.this;
        bridge = SingleTone.getBluetoothBridge();
        BluetoothConversation.reshandle =4;
        try
        {
            mp = MediaPlayer.create(this, R.raw.buttonclick);

            Resposne();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            animFadein1 = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.clickani);
            animFadein2 = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.clickani);
            animFadein3 = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.clickani);

            ll1 =  findViewById(R.id.ems);
            ll2 =  findViewById(R.id.abs);
            ll3 =  findViewById(R.id.secure);

            ll1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ll1.startAnimation(animFadein1);
                    mp.start();
                  //  SystemClock.sleep(100);
                    startActivity(new Intent(getApplicationContext(),EmsMenu.class));
                    overridePendingTransition(R.anim.out,R.anim.in);

                }
            });
            ll2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ll2.startAnimation(animFadein2);
                    mp.start();
                  //  SystemClock.sleep(100);
                    startActivity(new Intent(getApplicationContext(),KwpAbsMenu.class));
                    overridePendingTransition(R.anim.out,R.anim.in);

                }
            });

            ll3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(AppVariables.CheckInternet(getApplicationContext()))
                    {
                        ll3.startAnimation(animFadein3);
                        mp.start();
                       // SystemClock.sleep(100);
                        startActivity(new Intent(getApplicationContext(),SecureAccess.class));
                        overridePendingTransition(R.anim.out,R.anim.in);

                    }
                    else
                    {
                        Toast.makeText(context, getString(R.string.interntUnavail), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.somethingnotright)+e.getMessage(), Toast.LENGTH_SHORT).show();
            AppVariables.GenLogLine(TAG+e.getMessage());
        }


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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
