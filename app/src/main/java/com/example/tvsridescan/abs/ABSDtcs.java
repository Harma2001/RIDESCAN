package com.example.tvsridescan.abs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.tvsridescan.dtcsnapshot.DtcSnapshot;
import com.example.tvsridescan.dtcsnapshot.DtcSnapshotEx;

import java.util.ArrayList;

public class ABSDtcs extends Activity
{
    //bt
    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;
    ListView listView;
    ArrayList al = new ArrayList();
    Button deliv;
    TextView tvstatus;
    ArrayList<DataModel_dtc> dataModels;
    private static CustomAdapter_dtc adapter;
    Button btnshow;
    public static  String Statusval= "PENDING",FilterVal =null;
    /* colorcode to hold color for status     */
    public static  int j= 0,colorcode =0;
    String[] dtccode = new String[0];
    ArrayList filteral;
    RelativeLayout relativeLayout;
    LinearLayout ll2,ll3,ll4,progressBarLa;
    TextView tvpending,tvconfirmed,tvhistory;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ems_dtcs);
        bridge = SingleTone.getBluetoothBridge();
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
            progressBarLa =  findViewById(R.id.pb);

            progressBar =  findViewById(R.id.progressbar);

            tvstatus =  findViewById(R.id.status) ;
            ll2 =  findViewById(R.id.llpending);
            ll3 =  findViewById(R.id.llconfirmed);
            ll4 =  findViewById(R.id.llhistory);

            tvpending =  findViewById(R.id.tvpending);
            tvconfirmed =  findViewById(R.id.tvconfirmed);
            tvhistory =  findViewById(R.id.tvhistory);
            tvpending.setTextColor(Color.parseColor("#ffc400"));


            relativeLayout =  findViewById(R.id.rl);
            btnshow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowDialogSearch();
                }
            });

            ll2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    colorcode = 1;
                    Statusval = getString(R.string.pending);

                //   tvactive.setTextColor(Color.WHITE);
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
                    Statusval = getString(R.string.present);

                  //  tvactive.setTextColor(Color.WHITE);
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

                   // tvactive.setTextColor(Color.WHITE);
                    tvpending.setTextColor(Color.WHITE);
                    tvconfirmed.setTextColor(Color.WHITE);
                    tvhistory.setTextColor(Color.parseColor("#ffc400"));
                    j =0;
                    UI();
                }
            });

            deliv =  findViewById(R.id.del);
            deliv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    ShowDialog();
                }
            });

           // tvactive.setTextColor(Color.parseColor("#ffc400"));
            tvpending.setTextColor(Color.parseColor("#ffc400"));
            tvconfirmed.setTextColor(Color.WHITE);
            tvhistory.setTextColor(Color.WHITE);

            Init init = new Init();
            init.start();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }

    }
    public class Init extends Thread
    {
        @Override
        public void run()
        {
            al = new ArrayList();
            dataModels = new ArrayList<>();
            Statusval = getString(R.string.pending);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   //
                    // tvactive.setTextColor(Color.parseColor("#ffc400"));
                    tvpending.setTextColor(Color.parseColor("#ffc400"));
                    tvconfirmed.setTextColor(Color.WHITE);
                    tvhistory.setTextColor(Color.WHITE);
                    j =0;
                }
            });


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarLa.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.INVISIBLE);
                }
            });
            SystemClock.sleep(1000);
            send_Request_Command("1902FF\r\n".getBytes());

            super.run();
        }
    }
    public  void UI()
    {
        dataModels = new ArrayList<>();
        tvstatus.setText(getString(R.string.status));
        filteral = new ArrayList();
        dtccode = new String[al.size()];

        for(int i=0;i<al.size();i++)
        {
            String[] arr = al.get(i).toString().split(",");

            if(arr[2].contains(Statusval))
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

            }

        }

        adapter = new CustomAdapter_dtc(dataModels, getApplicationContext());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView =  findViewById(R.id.lv);
                progressBarLa.setVisibility(View.INVISIBLE);

                listView.setAdapter(adapter);
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
    public  void UISearch()
    {
        dataModels = new ArrayList<>();

        dtccode = new String[al.size()];

        for(int i=0;i<al.size();i++)
        {
            String[] arr = al.get(i).toString().split(",");

            dtccode[i] =arr[0];
            if(arr[2].contains(Statusval))
            {
//                if(Statusval!=null){arr[2].contains(Statusval)}
                if(!FilterVal.equals(null) && arr[0].contains(FilterVal))
                {
                    //dtccode[j] = arr[0];
                    filteral.add(arr[0]);
                    if(arr[1].equals("null"))
                    {
                        arr[1] = "Data Not Available";
                    }
                    dataModels.add(new DataModel_dtc(arr[0], arr[1], arr[2],colorcode));
                }
                else
                {

                }

            }
        }
        adapter = new CustomAdapter_dtc(dataModels, getApplicationContext());
        listView =  findViewById(R.id.lv);
        listView.setAdapter(adapter);

    }



    public class ClearDTC extends Thread
    {
        @Override
        public void run()
        {
            byte[] cmd = "14FFFFFF\r\n".getBytes();
            send_Request_Command(cmd);
            super.run();
        }
    }

    public void ParseDTCs(String strdata)
    {
        String str = AppVariables.ParseDTCs(strdata,"5902FF");

        String piece = null,dtcs,status;
        ArrayList arrayListstatus = new ArrayList();
        int activedtcs =0;
        int a=0,b=8;
        if(str.length()>7)
        {
            for(int i=0;i<str.length()+1;)
            {
                piece= str.substring(a,b);
                dtcs = piece.substring(0,6);
                status = piece.substring(6,8);
                int decimal = Integer.parseInt(status, 16);
                String bin = Integer.toBinaryString(decimal);
                int len = bin.length();

                arrayListstatus = new ArrayList();

               /* if(bin.substring(len-1,len).equals("1") && bin.substring(len-3,len-2).equals("0")&& bin.substring(len-4,len-3).equals("0") )
                {
                    arrayListstatus.add(getString(R.string.active));
                    activedtcs++;
                }
*/
                if(bin.substring(len-1,len).equals("1") && bin.substring(len-3,len-2).equals("1")&& bin.substring(len-4,len-3).equals("0") )
                {
                    arrayListstatus.add(getString(R.string.pending));
                }

                if(bin.substring(len-4,len-3).equals("1"))
                {
                    arrayListstatus.add(getString(R.string.present));
                }

                if(!bin.substring(len-1,len).equals("1")  && !bin.substring(len-3,len-2).equals("1") && !bin.substring(len-4,len-3).equals("1"))
                {
                    arrayListstatus.add(getString(R.string.history));
                }

                String dtccode = "0x"+dtcs;

                for(int i1=0;i1<arrayListstatus.size();i1++)
                {
                    al.add(dtccode+","+AppVariables.dtclist.get(dtccode)+","+arrayListstatus.get(i1));
                }
                a=b;
                b = b+8;
                i =b;
            }
        }
        colorcode = 0;
        j =0;
        UI();
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

    Dialog dialog1;
    public void ShowDialogSearch()
    {
        final String [] dtccode = new String[filteral.size()];
        for(int i=0;i<filteral.size();i++)
        {
            dtccode[i] = filteral.get(i).toString();
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,dtccode);

        dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.write_dtc_search);
        final AutoCompleteTextView autoCompleteTextView = dialog1.findViewById(R.id.autodtc);
        try
        {
            autoCompleteTextView.setThreshold(1);//will start working from first character
            autoCompleteTextView.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
            // actv.setTextColor(Color.RED);
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    dialog1.hide();
                    FilterVal = dtccode[i];
                    autoCompleteTextView.setText("");
                    UISearch();
                }
            });
        }
        catch (Exception e)
        {
                e.printStackTrace();
        }
        dialog1.show();

    }
    Dialog dialog3;
    public void ShowDialogSnapshot()
    {
        dialog3 = new Dialog(this);
        dialog3.setContentView(R.layout.write_dtc_snapchat);
        Button b1,b2;
        b1= (Button) dialog3.findViewById(R.id.snap1);
        b2= (Button) dialog3.findViewById(R.id.snap2);
        try
        {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    dialog3.hide();
                    Intent i = new Intent(getApplicationContext(), DtcSnapshot.class);
                    startActivity(i);


                }
            });
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    dialog3.hide();
                    Intent i = new Intent(getApplicationContext(), DtcSnapshotEx.class);
                    startActivity(i);
                }
            });
        }
        catch (Exception e)
        {

        }
        dialog3.show();
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
                mResponse_data = str;
                aBoolean = true;
                if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                {
                    if(mResponse_data.contains("59"))
                    {
                        Log.e("faults",mResponse_data);
                        ParseDTCs(mResponse_data);
                        /*byte[] cmd = "14FFFFFF\r\n".getBytes();
                        send_Request_Command(cmd);*/
                        aBoolean = true;

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
                else
                {
                    progressBarLa.setVisibility(View.VISIBLE);

                    TextView textView =  findViewById(R.id.message);
                    textView.setText(getString(R.string.ecunotresponding));

                    ProgressBar progressBar = findViewById(R.id.progressbar);
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void ConnectionLost()
            {
                //Connection Lost.
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
