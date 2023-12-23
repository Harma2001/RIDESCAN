package com.example.tvsridescan;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.BluetoothConversation;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.connection.ConnectionInterrupt;
import com.example.tvsridescan.utility.NewProductModel;
import com.example.tvsridescan.utility.NewProductRecyclerAdapter;

import java.util.ArrayList;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class SelectVehicle extends AppCompatActivity
{
    BluetoothBridge bridge;
    static String mResponse_data = null;
    static boolean aBoolean = true;
    static byte[] response;
    ImageView iv1,iv2;
    BluetoothAdapter bluetoothAdapter;
    Animation animFadein1;
    Animation animFadein2;
    Animation animFadein3 ,animFadein4,animFadein5,animFadein6,animFadein7,animFadein8,animFadein9;
    MediaPlayer mp;
    int i;
    ScrollingPagerIndicator recyclerIndicator ;
   // Button rr310,rtr2004v2chcarb,rtr2004v2chefi,rtr1604v1chcarb,rtr1604v1chefi,rtr2004v1chcarb,rtr2004v1chefi;
          //  ,rtr1802v1ch,rtr1602v1ch,

    private NewProductModel newProductModel;
    private ArrayList<NewProductModel> newProductModelArrayList = new ArrayList<>();
    private NewProductRecyclerAdapter newProductRecyclerAdapter;
    RecyclerView recyclerView_newProduct;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_vehicle);


        context =  SelectVehicle.this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle("Vehicle Details");
        mp = MediaPlayer.create(this, R.raw.buttonclick);
        /*animFadein1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein2 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein3 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein4 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein5 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein6 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein7 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein8 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);
        animFadein9 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.clickani);*/
        bridge = SingleTone.getBluetoothBridge();
        Resposne();

       /* rr310 =  findViewById(R.id.rr310);
        *//*NEW PLATFORM
        * *//*
        rtr2004v2chcarb =  findViewById(R.id.rtr2004v2chcarb);
        rtr2004v2chefi =  findViewById(R.id.rtr2004v2chefi);
        rtr1604v1chcarb =  findViewById(R.id.rtr1604v1chcarb);
        rtr1604v1chefi =  findViewById(R.id.rtr1604v1chefi);
       // rtr1802v1ch =  findViewById(R.id.rtr1802v1ch);
        //rtr1602v1ch =  findViewById(R.id.rtr1602v1ch);
        rtr2004v1chcarb = findViewById(R.id.rtr2004v1chcarb);
        rtr2004v1chefi = findViewById(R.id.rtr2004v1chefi);

        rr310.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                AppVariables.BikeModel_str="RR310";
                AppVariables.BikeModel = 1;
                rr310.startAnimation(animFadein1);
                startActivity(new Intent(getApplicationContext(), StartDiagnosis.class));
                overridePendingTransition(R.anim.out,R.anim.in);
            }
        });
        rtr2004v2chcarb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();

                AppVariables.BikeModel_str="RTR2004v_2CH_CARB";  //                                         RTR2004v_2CH_CARB
                AppVariables.BikeModel = 2;
                rtr2004v2chcarb.startAnimation(animFadein2);
                startActivity(new Intent(getApplicationContext(), ABSMenu.class));
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });
        rtr2004v2chefi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();

                AppVariables.BikeModel_str="RTR200FI4V";  //                                                      RTR2004v_2CH_EFI
                AppVariables.BikeModel = 3;
                rtr2004v2chefi.startAnimation(animFadein3);
                startActivity(new Intent(getApplicationContext(), RTR2004vchefi.class));
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });

        rtr1604v1chcarb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();

                AppVariables.BikeModel_str="RTR1604v_1CH_CARB";
                AppVariables.BikeModel = 4;
                rtr1604v1chcarb.startAnimation(animFadein4);
                startActivity(new Intent(getApplicationContext(), KwpAbsMenu.class));
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });

        rtr1604v1chefi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();

                AppVariables.BikeModel_str="RTR200FI4V";                                    //  RTR200FI4V        RTR1604v_1CH_EFI
                AppVariables.BikeModel = 5;
                rtr1604v1chefi.startAnimation(animFadein5);
                startActivity(new Intent(getApplicationContext(), RTR1604v1chefiMenu.class));
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });

      *//*  rtr1802v1ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();

                AppVariables.BikeModel_str="RTR1802v_1CH";
                AppVariables.BikeModel = 6;
                rtr1802v1ch.startAnimation(animFadein6);
                startActivity(new Intent(getApplicationContext(), RTR1602v1chMenu.class));
            }
        });

        rtr1602v1ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();

                AppVariables.BikeModel_str="RTR1602v_1CH";
                AppVariables.BikeModel = 7;
                rtr1602v1ch.startAnimation(animFadein7);
                startActivity(new Intent(getApplicationContext(), RTR1602v1chMenu.class));
                        overridePendingTransition(R.anim.out,R.anim.in);

            }
        });*//*

        rtr2004v1chcarb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();

                AppVariables.BikeModel_str="RTR2004v_1CH_CARB";
                AppVariables.BikeModel = 8;
                rtr2004v1chcarb.startAnimation(animFadein8);
                startActivity(new Intent(getApplicationContext(), KwpAbsMenu.class));
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });
        rtr2004v1chefi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();

                AppVariables.BikeModel_str="RTR200FI4V";                             //            RTR200FI4V  RTR2004v_1CH_EFI
                AppVariables.BikeModel = 9;
                rtr2004v1chefi.startAnimation(animFadein9);
                startActivity(new Intent(getApplicationContext(), RTR2004v1chefiMenu.class));
                overridePendingTransition(R.anim.out,R.anim.in);

            }
        });*/
        initRecyclerView();
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
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.out,R.anim.in);

    }

    private void initRecyclerView()
    {
        recyclerView_newProduct = findViewById(R.id.recycler_newProd);
        recyclerIndicator = findViewById(R.id.indicator);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        recyclerView_newProduct.setLayoutManager(linearLayoutManager);
        recyclerView_newProduct.setItemAnimator(new DefaultItemAnimator());

        newProductRecyclerAdapter = new NewProductRecyclerAdapter(this,newProductModelArrayList,getScreenWidth());
        recyclerView_newProduct.setAdapter(newProductRecyclerAdapter );

        newProductModel = new NewProductModel("1", getString(R.string.apacherr310),R.drawable.rr310bike );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("2", getString(R.string.rtr2004v2chcarb),R.drawable.rtr2004vbike1 );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("3", getString(R.string.rtr2004v2chefi),R.drawable.rtr2004vbike1 );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("4", getString(R.string.rtr1604v1chcarb),R.drawable.rtr1604vbike1 );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("5", getString(R.string.rtr1604v1chefi),R.drawable.rtr1604vbike1 );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("6", getString(R.string.rtr1602v1ch),R.drawable.rtr1604vabs );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("7", getString(R.string.rtr2004v1chcarb),R.drawable.rtr2004vbike2 );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("8", getString(R.string.rtr2004v1chefi),R.drawable.rtr2004vbike2 );
        newProductModelArrayList.add(newProductModel);

        newProductModel = new NewProductModel("9", getString(R.string.rtr1802v1ch),R.drawable.rtr180 );
        newProductModelArrayList.add(newProductModel);

        newProductRecyclerAdapter.notifyDataSetChanged();

        recyclerIndicator.attachToRecyclerView(recyclerView_newProduct);

        // pageIndicator.setCount(8);
       // pageIndicator.attachTo(recyclerView_newProduct);

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
            overridePendingTransition(R.anim.out,R.anim.in);

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

    public int getScreenWidth()
    {

        int width = 100;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;

        return width;
    }

}
