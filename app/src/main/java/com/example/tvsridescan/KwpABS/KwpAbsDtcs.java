package com.example.tvsridescan.KwpABS;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.adapters.CustomAdapter_dtc;
import com.example.tvsridescan.adapters.DataModel_dtc;
import com.example.tvsridescan.connection.ConnectionInterrupt;

import java.util.ArrayList;

public class KwpAbsDtcs extends AppCompatActivity {



    ArrayList al = new ArrayList();
    BluetoothBridge bridge;
    String mResponse_data = "";
    boolean aBoolean = true;
    byte[] response;
    ListView listView;
    Button deliv;
    TextView tvstatus;
    ArrayList<DataModel_dtc> dataModels;
    private static CustomAdapter_dtc adapter;
    Button btnshow;
    public static  String Statusval= "ACTIVE",FilterVal =null;

    /* colorcode to hold color for status     */
    int j= 0,colorcode =0;
    String[] dtccode = new String[0];
    ArrayList filteral;
    RelativeLayout relativeLayout;
    LinearLayout progressBarLayout;
    ProgressBar progressBar;
    TextView tvProgessMessage;

    Context context;
    private static final String TAG = "KwpAbsDtcs";
    String[] dtcnamearr;
    ArrayList dtcnameal = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kwp_abs_dtcs);

        init();
    }


    private void init()
    {
        context = KwpAbsDtcs.this;
        bridge = SingleTone.getBluetoothBridge();
        progressBar =  findViewById(R.id.progressbar);
        Resposne();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try
        {
            TextView title =  findViewById(R.id.title);
            title.setText(getString(R.string.absreaddtcs));
            btnshow =  findViewById(R.id.buttonsearch);
            ImageView ivss =  findViewById(R.id.ss);
            ivss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View viewss = getWindow().getDecorView().getRootView();
                    AppVariables.takeScreenshot(viewss);
                }
            });

            progressBarLayout =  findViewById(R.id.pb);
            tvstatus =  findViewById(R.id.status) ;



            tvProgessMessage = findViewById(R.id.message);


            relativeLayout =  findViewById(R.id.rl);
            btnshow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 //  ShowDialogSearch();
                }
            });

            /*ll2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    colorcode = 1;
                    Statusval = getString(R.string.pending);

                    tvactive.setTextColor(Color.WHITE);
                    tvpending.setTextColor(Color.parseColor("#ffc400"));
                    tvconfirmed.setTextColor(Color.WHITE);
                    tvhistory.setTextColor(Color.WHITE);

                    j =0;
                    UI();
                }
            });
            ll3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    colorcode = 2;
                    Statusval = getString(R.string.confirmed);

                    tvactive.setTextColor(Color.WHITE);
                    tvpending.setTextColor(Color.WHITE);
                    tvconfirmed.setTextColor(Color.parseColor("#ffc400"));
                    tvhistory.setTextColor(Color.WHITE);

                    j =0;
                    UI();
                }
            });
            ll4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    colorcode = 3;
                    Statusval = getString(R.string.history);

                    tvactive.setTextColor(Color.WHITE);
                    tvpending.setTextColor(Color.WHITE);
                    tvconfirmed.setTextColor(Color.WHITE);
                    tvhistory.setTextColor(Color.parseColor("#ffc400"));
                    j =0;
                    UI();
                }
            });*/

            deliv =  findViewById(R.id.del);
            deliv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {

                    ShowDialog();
                }
            });

          /*  tvactive.setTextColor(Color.parseColor("#ffc400"));
            tvpending.setTextColor(Color.WHITE);
            tvconfirmed.setTextColor(Color.WHITE);
            tvhistory.setTextColor(Color.WHITE);*/

            Init init = new Init();
            init.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
                aBoolean = true;
                if(mResponse_data.contains("NO DATA") || mResponse_data.contains("ERROR") || mResponse_data.contains("@!")  )
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarLayout.setVisibility(View.VISIBLE);
                            relativeLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            if(tvProgessMessage!=null )
                            tvProgessMessage.setText(getString(R.string.ecunotresponding));
                        }
                    });
                }
                else
                {
                    if(mResponse_data.contains("58"))
                    {
                        Log.e("faults",mResponse_data);
                        parseKwpDtcs(response);
                        //aBoolean = true;
                    }
                    else
                    if(mResponse_data.contains("54"))
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),getString(R.string.dtccleared),Toast.LENGTH_LONG).show();
                            }
                        });

                        Init init = new Init();
                        init.start();
                    }
                }

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

    public class Init extends Thread
    {
        @Override
        public void run()
        {
            al = new ArrayList();
            dataModels = new ArrayList<>();
            Statusval = getString(R.string.status);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  /*  tvactive.setTextColor(Color.parseColor("#ffc400"));
                    tvpending.setTextColor(Color.WHITE);
                    tvconfirmed.setTextColor(Color.WHITE);
                    tvhistory.setTextColor(Color.WHITE);*/
                    j =0;
                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarLayout.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.INVISIBLE);
                }
            });

            SystemClock.sleep(500);
            send_Request_Command("1802FFFF\r\n".getBytes());


            super.run();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    public  void UI()
    {
        dataModels = new ArrayList<>();
        tvstatus.setText(Statusval);
        filteral = new ArrayList();
        dtccode = new String[al.size()];

        for(int i=0;i<al.size();i++)
        {
            String[] arr = al.get(i).toString().split(",");

           /* if(arr[2].contains(Statusval))
            {
//
                if(FilterVal!=null && arr[0].contains(FilterVal))
                {
                    filteral.add(arr[0]);

                    if(arr[2] =="null")
                    {
                        arr[2] = "Data Not Available";
                    }
                    dataModels.add(new DataModel_dtc(arr[0], arr[1], arr[2],colorcode));
                }
                else
                {
                    filteral.add(arr[0]);

                    if(arr[1].equals("null"))
                    {
                        arr[1] = "Data Not Available";
                    }
                    dataModels.add(new DataModel_dtc(arr[0], arr[1], arr[2],colorcode));
                    j++;
                }

            }*/
            filteral.add(arr[0]);

            String act = getString(R.string.present);
            String pen = getString(R.string.pending);
            String conf = getString(R.string.confirmed);
            String hist = getString(R.string.history);

            if(arr[2].equals(act))
            {
                dataModels.add(new DataModel_dtc(arr[0], arr[1], arr[2],0));
            }
            else if(arr[2].equals(pen))
            {
                dataModels.add(new DataModel_dtc(arr[0], arr[1], arr[2],1));
            }
            else if(arr[2].equals(conf))
            {
                dataModels.add(new DataModel_dtc(arr[0], arr[1], arr[2],2));
            }
            else if(arr[2].equals(hist))
            {
                dataModels.add(new DataModel_dtc(arr[0], arr[1], arr[2],3));
            }

        }

        adapter = new CustomAdapter_dtc(dataModels, getApplicationContext());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView =  findViewById(R.id.lv);

                listView.setAdapter(adapter);
                progressBarLayout.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //  DataModel_dtc dataModel= dataModels.get(position);
                // String valstr = dataModel.getDtccode().replace("0x","");
                // AppVariables.DTCCode =valstr;
                //  ShowDialogSnapshot();


            }
        });

    }

    private void parseKwpDtcs(byte[] response)
    {

        String tempRespnse = new String(response);
        tempRespnse = tempRespnse.replace(" ","");

        tempRespnse = AppVariables.parseKwpdtcs(tempRespnse);

        //int lenghtPacket = DataConversion._PanToByte(tempRespnse.getBytes());
        tempRespnse = tempRespnse.substring(2);
        int a=0,b=6;
        dtcnameal = new ArrayList();

        for(int i=0;i< (tempRespnse.length()/6 ); i++)
        {

            String piece  = tempRespnse.substring(a,b);
            String dtcnamestr = piece.substring(0, 4);
            dtcnameal.add(dtcnamestr);

            String  statusOfDtc =piece.substring(4, 6);

            int decimal = Integer.parseInt(statusOfDtc, 16);

            ArrayList<String> arrayListstatus = new ArrayList<>();


            if (getBit(decimal,5)== 1 &&  getBit(decimal,6)== 1)
            {
                arrayListstatus.add(getString(R.string.present));
            }

            else if (getBit(decimal,5)== 0 &&  getBit(decimal,6)== 0)
            {
                arrayListstatus.add(getString(R.string.pending));
            }
            else if (getBit(decimal,5)== 0 &&  getBit(decimal,6)== 1)
            {
                arrayListstatus.add(getString(R.string.confirmed));
            }
            else if (getBit(decimal,5)== 1 &&  getBit(decimal,6)== 0)
            {
                arrayListstatus.add(getString(R.string.history));
            }

            /*if (bin.substring(1,bin.length()-5).equals("00")) {
                arrayListstatus.add("PENDING");
            }*/
            String dtcode = "0x"+dtcnamestr;

            for(int i1=0;i1<arrayListstatus.size();i1++)
            {
                al.add(dtcode+","+AppVariables.dtclist.get(dtcode)+","+arrayListstatus.get(i1));
            }
            a = b;
            b = b + 6;

        }
        dtcnamearr = new String[dtcnameal.size()];
        for (int i = 0; i < dtcnameal.size(); i++) {
            dtcnamearr[i] = dtcnameal.get(i).toString();
        }
        colorcode = 0;
        Statusval = getString(R.string.status);
        j =0;
        UI();


    }

   public static int getBit(int n, int k) {
        return (n >> k) & 1;
    }

    Dialog dialog;
    public void ShowDialog()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.clear_dtc_dialog);
        final Button b1,b2;

        b1 =  dialog.findViewById(R.id.submit1);
        b2 =  dialog.findViewById(R.id.submit2);

        try
        {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(dialog.isShowing())
                    {
                        dialog.hide();
                    }

                }
            });

            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(dialog.isShowing())
                    {
                        dialog.hide();
                    }
                    ClearDTC ClearDTC = new ClearDTC();
                    ClearDTC.start();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("ABSDtcs.this",e.getMessage());
        }
        dialog.show();

    }
    public class ClearDTC extends Thread
    {
        @Override
        public void run()
        {
            byte[] cmd = "14FFFF\r\n".getBytes();
            send_Request_Command(cmd);

            super.run();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bridge = SingleTone.getBluetoothBridge();
        Resposne();
    }
/*
    public  String addZeroesIfneeded(String input)
    {
        String paddedString0 = input;
        String tempString="";
        int len = paddedString0.length();

        for(int i=0 ;i<paddedString0.length();i++)
        {
            if(paddedString0.charAt(i)=='.')
            {
                tempString = addDigitsAfterddot(tempString);
            }

            tempString+=paddedString0.charAt(i);

        }
        return  (tempString);
    }*/
@Override
protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(LocaleHelper.onAttach(newBase));
}
}
