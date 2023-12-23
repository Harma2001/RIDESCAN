package com.example.tvsridescan.Configuration;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tvsridescan.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class CloudDBRetrieve extends AppCompatActivity {

    String[] dataarr;
    EditText editText;
    Button b1;
    String strid="";
    public static String[] valuesarr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_dbretrieve);
        editText = (EditText) findViewById(R.id.et);
        b1 = (Button) findViewById(R.id.search);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                strid = editText.getText().toString();
                BackgrounTask backgrounTask = new BackgrounTask();
                backgrounTask.execute();
            }
        });
    }

    class BackgrounTask extends AsyncTask<Void, Void, String> {

        String json_url;
        @Override
        protected void onPreExecute()
        {
            json_url = "http://pickhr.com/i_connect_api/get_device/?email=raju@gmail.com ";//"http://www.pickhr.com/i_connect_api/get_device/?product_id="+strid;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String res;
            try {
                URL url = new URL(json_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((res = bufferedReader.readLine()) != null) {
                    stringBuilder.append(res + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            TextView textView = (TextView) findViewById(R.id.textView);
            ParseJson(s);
            String[] values = ParseJson(s);
            for(int i=0;i<values.length;i++)
            {
                textView.append("\n");
                textView.append(values[i]);

            }

           /* Intent i = new Intent(getApplicationContext(),DummyUserDetails.class);
            startActivity(i);*/
            super.onPostExecute(s);
        }
    }


    public String[] ParseJson(String str)
    {
        String arr[] = str.replace("[{","").replace("}]","").replace("\"","").split(",");
        dataarr = arr;
        valuesarr = arr;
        return arr;
    }
    public String[] SeperateValue()
    {
        String[] arr = dataarr;
        String[] temparr;
        String[] values = new String[arr.length];
        /*for(int i=0;i<arr.length;i++)
        {
            String str = arr[i];
            temparr = str.split(":");
            values[i] = temparr[1];

        }
        valuesarr = values;*/
        return values;
    }
}