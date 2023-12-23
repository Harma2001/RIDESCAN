package com.example.tvsridescan.rtr1602v_1ch;

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
import com.example.tvsridescan.Library.BluetoothSemaphoreService;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.adapters.CustomAdapter_dtc;
import com.example.tvsridescan.adapters.DataModel_dtc;
import com.example.tvsridescan.connection.ConnectionInterrupt;

import java.util.ArrayList;

public class RTR1602v1chDtc extends AppCompatActivity {

    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    boolean aBoolean = false;

    ListView listView;
    ArrayList al = new ArrayList();
    Button deliv;
    ArrayList filteral;
    TextView tvstatus;
    ArrayList<DataModel_dtc> dataModels;
    private static CustomAdapter_dtc adapter;
    String [] dtccode;
    String[] dtcnamearr;
    byte[] cmd;
    Button btnshow;
    /* Statusval to hold status name && FilterVAl to hold searched value*/
    public static  String Statusval= "ACTIVE",FilterVal =null;

    ArrayList dtcnameal = new ArrayList();

    /* colorcode to hold color for status     */
    public static  int j= 0,colorcode =0;
    RelativeLayout relativeLayout;
    LinearLayout progressBarLa,ll1,ll2,ll3,ll4;
   // TextView tvactive,tvpending,tvconfirmed,tvhistory;

    BluetoothSemaphoreService bluetoothSemaphoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtr1602v1ch_dtc);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        bluetoothSemaphoreService= new BluetoothSemaphoreService();
        bridge = SingleTone.getBluetoothBridge();
        Resposne();
       // MainUI();


        TextView title =  findViewById(R.id.title);
        title.setText(getString(R.string.absreaddtcs));
        btnshow =  findViewById(R.id.buttonsearch);

        ImageView ivss = (ImageView) findViewById(R.id.ss);
        ivss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewss = getWindow().getDecorView().getRootView();
                AppVariables.takeScreenshot(viewss);
            }
        });
        progressBarLa =  findViewById(R.id.pb);


        tvstatus =  findViewById(R.id.status) ;


        /*tvactive =  findViewById(R.id.tvactive);
        tvpending =  findViewById(R.id.tvpending);
        tvconfirmed =  findViewById(R.id.tvconfirmed);
        tvhistory =  findViewById(R.id.tvhistory);*/


        relativeLayout =  findViewById(R.id.rl);
        btnshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogSearch();
            }
        });
        /*ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorcode = 0;
                Statusval = getString(R.string.active);

                tvactive.setTextColor(Color.parseColor("#ffc400"));
                tvpending.setTextColor(Color.WHITE);
                tvconfirmed.setTextColor(Color.WHITE);
                tvhistory.setTextColor(Color.WHITE);
                j =0;
                UI();
            }
        });
        ll2.setOnClickListener(new View.OnClickListener() {
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

    /*public void MainUI()
    {
        TextView title =  findViewById(R.id.title);
        title.setText(R.string.absreaddtcs);
        btnshow =  findViewById(R.id.buttonsearch);

        relativeLayout =  findViewById(R.id.rl);
        progressBarLa =  findViewById(R.id.pb);



        // tvstatus =  findViewById(R.id.status) ;
        ll1 =  findViewById(R.id.llactive);
        ll2 =  findViewById(R.id.llpending);
        ll3 =  findViewById(R.id.llconfirmed);
        ll4 =  findViewById(R.id.llhistory);

        tvactive =  findViewById(R.id.tvactive);
        tvpending =  findViewById(R.id.tvpending);
        tvconfirmed =  findViewById(R.id.tvconfirmed);
        tvhistory =  findViewById(R.id.tvhistory);

        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorcode = 0;
                Statusval = getString(R.string.active);

                tvactive.setTextColor(Color.parseColor("#ffc400"));
                tvpending.setTextColor(Color.WHITE);
                tvconfirmed.setTextColor(Color.WHITE);
                tvhistory.setTextColor(Color.WHITE);
                j =0;
                UI();
            }
        });
        ll2.setOnClickListener(new View.OnClickListener() {
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
        });
        deliv = (Button) findViewById(R.id.del);
        deliv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ShowDialog();
            }
        });

        tvactive.setTextColor(Color.parseColor("#ffc400"));
        tvpending.setTextColor(Color.WHITE);
        tvconfirmed.setTextColor(Color.WHITE);
        tvhistory.setTextColor(Color.WHITE);

        btnshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogSearch();
            }
        });
    }*/
    public class Init extends Thread
    {
        @Override
        public void run()
        {
            al = new ArrayList();
            Statusval = getString(R.string.present);
            colorcode = 0;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarLa.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.INVISIBLE);
                    /*tvactive.setTextColor(Color.parseColor("#ffc400"));
                    tvpending.setTextColor(Color.WHITE);
                    tvconfirmed.setTextColor(Color.WHITE);
                    tvhistory.setTextColor(Color.WHITE);*/



                 /*   tvactive.setTextColor(Color.parseColor("#ffc400"));
                    tvpending.setTextColor(Color.WHITE);
                    tvconfirmed.setTextColor(Color.WHITE);
                    tvhistory.setTextColor(Color.WHITE);*/
                    j =0;
                    UI();

                }
            });
           // Statusval= "";


            dataModels = new ArrayList<>();
            aBoolean=false;
            cmd = "1001\r\n".getBytes();
            send_Request_Command(cmd);
            while (!aBoolean)
            {
                SystemClock.sleep(10);
            }
            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!") && mResponse_data.contains("50")) {
                mResponse_data = mResponse_data.replace(" ", "");
                cmd = "19004000\r\n".getBytes();
                send_Request_Command(cmd);
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBarLa.setVisibility(View.VISIBLE);

                        TextView textView =  findViewById(R.id.message);
                        textView.setText(getString(R.string.ecunotresponding));

                        ProgressBar progressBar = findViewById(R.id.progressbar);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }


            super.run();
        }
    }

    @Override
    protected void onDestroy()
    {
        //finish();
        super.onDestroy();
    }

    public  void UI()
    {
         tvstatus.setText(getString(R.string.status));
        dataModels = new ArrayList<>();
        filteral = new ArrayList();
        dtccode = new String[al.size()];
        for(int i=0;i<al.size();i++)
        {
            String[] arr = al.get(i).toString().split(",");

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

               /* switch (arr[2])
                {

                    case act:
                        dataModels.add(new DataModel_dtc(arr[0], arr[1], arr[2],0));
                        break;
                    case pen:
                        break;
                    case "CONFIRMED" :
                        break;
                    case "HISTORY":
                        break;
                    default:
                        arr[2] = "Data Unavailable";
                        dataModels.add(new DataModel_dtc(arr[0],arr[1],arr[2],1));
                        break;
                }
*/



        }
        adapter = new CustomAdapter_dtc(dataModels, getApplicationContext());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView =  findViewById(R.id.lv);
                listView.setAdapter(adapter);
                progressBarLa.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // String valstr = dtcnamearr[position];
                //AppVariables.DTCCode =valstr;

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
                    if(AppVariables.BikeModel==1)
                    {
                        if(arr[1].equals("null"))
                        {
                            arr[1] = "Data Not Available";
                        }
                        dataModels.add(new DataModel_dtc(arr[0], arr[1], arr[2],colorcode));
                    }
                    else
                    {
                        if(arr[1].equals("null"))
                        {
                            arr[1] = "Data Not Available";
                        }
                        dataModels.add(new DataModel_dtc(arr[0], arr[1], arr[2],colorcode));
                    }
                }
                else
                {

                }

            }

        }
        adapter = new CustomAdapter_dtc(dataModels, getApplicationContext());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView =  findViewById(R.id.lv);
                listView.setAdapter(adapter);
            }
        });
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
                    b1.setAlpha((float) 0.5);
                    SystemClock.sleep(200);
                    dialog.dismiss();

                }
            });

            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    b1.setAlpha((float) 0.5);
                    SystemClock.sleep(200);
                    dialog.dismiss();
                    ClearDTC ClearDTC = new ClearDTC();
                    ClearDTC.start();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        dialog.show();

    }

    Dialog dialog1;
    public void ShowDialogSearch()
    {

        //String[] arr = list.toArray(new String[list.size()]);
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
            Log.e("@dialogsearchshowdtc",e.getMessage());
        }
        dialog1.show();

    }
    public class ClearDTC extends Thread
    {
        @Override
        public void run()
        {
            byte[] cmd = "144000\r\n".getBytes();
            send_Request_Command(cmd);
            super.run();
        }
    }
    public void ParseDTCs(String strdata)
    {
        if(strdata.length()>7)
        {
            String str = AppVariables.parseBosch19AAResposne(strdata);
            String piece = null,dtcs,status;
            int a=0,b=8;
            dtcnameal = new ArrayList();
            ArrayList arrayListstatus = new ArrayList();

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

                    Log.e("BINARY STATUS",bin);
                    if(bin.substring(1,2).equals("1") && bin.substring(2,3).equals("1"))  //65
                    {
                       // colorcode = 0;

                        arrayListstatus.add(getString(R.string.present));
                        //activedtcs++;
                    }
                    if(bin.substring(1,2).equals("0") && bin.substring(2,3).equals("0"))  //65
                    {
                        // colorcode = 0;

                        arrayListstatus.add(getString(R.string.pending));
                        //activedtcs++;
                    }
                    if(bin.substring(1,2).equals("1") && bin.substring(2,3).equals("0"))  //65
                    {

                        arrayListstatus.add(getString(R.string.confirmed));
                    }
                    if(bin.substring(1,2).equals("0") && bin.substring(2,3).equals("1"))   //65
                    {
                       // colorcode = 1;

                        arrayListstatus.add(getString(R.string.history));
                    }

                    String dtccode = "0x"+dtcs;
                    Log.e("DTC ",dtccode);

                    for(int i1=0;i1<arrayListstatus.size();i1++)
                    {
                        al.add(dtccode+","+AppVariables.dtclist.get(dtccode)+","+arrayListstatus.get(i1));
                    }
                    a=b;
                    b = b+8;
                    i =b;
                }
            }
        }

        colorcode = 0;
        j =0;
        UI();
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
                Intent i = new Intent(RTR1602v1chDtc.this, ConnectionInterrupt.class);
                startActivity(i);
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
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.out,R.anim.in);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
