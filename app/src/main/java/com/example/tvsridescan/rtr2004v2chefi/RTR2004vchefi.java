package com.example.tvsridescan.rtr2004v2chefi;

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
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.SecureAccess;
import com.example.tvsridescan.StartDiagnosis;
import com.example.tvsridescan.abs.ABSMenu;
import com.example.tvsridescan.connection.ConnectionInterrupt;
import com.example.tvsridescan.ems.EmsMenu;

public class RTR2004vchefi extends AppCompatActivity {


    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;

    LinearLayout ll1,ll2,ll3;
    Animation animFadein1,animFadein2,animFadein3;
    MediaPlayer mp;
    Context context;
    private static final String TAG = "RTR2004vchefi.this";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtr2004vchefi);
        init();
    }

    private void init()
    {
        context = RTR2004vchefi.this;
        bridge = SingleTone.getBluetoothBridge();
        try
        {
           // AppVariables.loadNegativeResponses(RTR2004vchefi.this,"negres.csv");
            StartDiagnosis.LoadCANNegtiveRespnseAcctoLanguage(context);
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
                    startActivity(new Intent(getApplicationContext(),EmsMenu.class));
                    overridePendingTransition(R.anim.out,R.anim.in);

                }
            });
            ll2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ll2.startAnimation(animFadein2);
                    mp.start();
                    startActivity(new Intent(getApplicationContext(),ABSMenu.class));
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
            Toast.makeText(context, "something went wrong "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                finish();
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
