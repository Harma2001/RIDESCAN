package com.example.tvsridescan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tvsridescan.Configuration.LocaleHelper;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.abs.ABSGraphView;
import com.example.tvsridescan.connection.ConnectionInterrupt;
import com.example.tvsridescan.ems.EmsGraphView;
import com.example.tvsridescan.icu.ClusterGraphView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LiveSelectParams extends AppCompatActivity {
    ListView listview;

    Context context;
    public  static StringBuilder stringBuilder;
    ArrayList collection = new ArrayList();
    Button b1;
    ArrayList filteral = new ArrayList();
    public static String filename = "";
    int count =0;

    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ems_select_params);
        context = this;
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
        LoadData();
    }
    int val =0;
    public void SortData()
    {
        BufferedReader reader = null;
        stringBuilder = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open(filename)));
            count = 0;
            // do reading, usually loop until end of file reading
            String mLine;

            if(filename.contains("Ems"))
            {
                val =0;
            }
            else
            {
                val = 1;
            }
            while ((mLine = reader.readLine()) != null)
            {

                String data[] = mLine.split(",");
                if(filteral.contains(data[val]))
                {
                    stringBuilder.append(mLine+";");
                }
                collection.add(data[val]);
                count =0;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

            Intent intent = null;
            AppVariables.Sorteddata = stringBuilder.toString();
            if(LiveParameterCat.cat ==1)
            {
            intent = new Intent(getApplicationContext(),EmsGraphView.class);
            }
            else
            if(LiveParameterCat.cat ==2)
            {
            intent = new Intent(getApplicationContext(), ABSGraphView.class);
            }
            else
            if(LiveParameterCat.cat ==3)
            {
            intent = new Intent(getApplicationContext(),ClusterGraphView.class);
            }
            finish();
            startActivity(intent);
        }

    }
    int val1=0;
    public void LoadData()
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open(filename)));
            count = 0;
            // do reading, usually loop until end of file reading
            String mLine;

            if(!filename.contains("Ems"))
            {
                val1=1;
            }
            else
            {
                val1=0;
            }
            while ((mLine = reader.readLine()) != null)
            {
                String dtcdata[] = mLine.split(",");
                collection.add(dtcdata[val1]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            UI();
        }

    }
    String[] arr = new String[0];
    public void UI()
    {
        listview = findViewById(R.id.listView1);

        arr = new String[collection.size()];
        for(int i=0;i<collection.size();i++)
        {
            arr[i] = collection.get(i).toString();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item1, arr);
        listview.setAdapter(adapter);
        listview.setItemsCanFocus(false);
        // we want multiple clicks


        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listview.setOnItemClickListener(new CheckBoxClick());
        b1 =  findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(count==4)
                {
                    SortData();
                }
                else
                {
                    Toast.makeText(context, getString(R.string.pleaseselectonly4params), Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    public class CheckBoxClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            CheckedTextView ctv = (CheckedTextView)arg1;
            String data = arr[arg2];
            if(ctv.isChecked())
            {
                count ++;
                if(count>4)
                {
                    Toast.makeText(context, getString(R.string.pleaseselectonly4params), Toast.LENGTH_LONG).show();
                    b1.setEnabled(true);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.selecedparamis)+data, Toast.LENGTH_LONG).show();
                    filteral.add(data);
                    b1.setEnabled(true);
                }

            }
            else
            {
                filteral.remove(data);
                b1.setEnabled(true);
                count --;
                Toast.makeText(getApplicationContext(), getString(R.string.removed) +data, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
