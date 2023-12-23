package com.example.tvsridescan.abs;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tvsridescan.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ABSSelectParams extends AppCompatActivity {
    ListView listview;
    Context context;
    public  static StringBuilder stringBuilder;
    ArrayList collection = new ArrayList();
    Button b1;
    ArrayList filteral = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ems_select_params);
        context = this;
        LoadData();
    }
    public void SortData()
    {
        BufferedReader reader = null;
        stringBuilder = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open("absliveparameters.csv")));
            count = 0;
            // do reading, usually loop until end of file reading
            String mLine;

            while ((mLine = reader.readLine()) != null)
            {

                String data[] = mLine.split(",");
                if(filteral.contains(data[0]))
                {
                    stringBuilder.append(mLine+";");
                }
                collection.add(data[0]);
                count =0;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            finish();
            overridePendingTransition(R.anim.out,R.anim.in);

            /*Intent i = new Intent(getApplicationContext(),EmsGraphView.class);
            startActivity(i);*/
        }

    }
    public void LoadData()
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open("absliveselectparams.csv")));
            count = 0;
            // do reading, usually loop until end of file reading
            String mLine;

            while ((mLine = reader.readLine()) != null)
            {
                String dtcdata[] = mLine.split(",");
                collection.add(dtcdata[1]);
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
        listview = (ListView)findViewById(R.id.listView1);

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
        b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                  SortData();
            }
        });
    }

    int count =0;
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
                    b1.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Max 4 Items ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    filteral.add(data);
                    b1.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Checkted "+data, Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                filteral.remove(data);
                b1.setEnabled(true);
                count --;
                Toast.makeText(getApplicationContext(), "now it is un checked"+data, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

    }
}
