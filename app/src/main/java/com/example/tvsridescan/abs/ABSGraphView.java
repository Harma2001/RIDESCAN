package com.example.tvsridescan.abs;

import static com.example.tvsridescan.Library.AppVariables.parsecmd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.SingleTone;
import com.example.tvsridescan.R;
import com.example.tvsridescan.connection.ConnectionInterrupt;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ABSGraphView extends AppCompatActivity
{
        private LineChart mChart1,mChart2,mChart3,mChart4;
        Float firstentry =0f,secondentry =0f,thirdentry =0f,fourthentry =0f;
        String frstgname="first ",scndgname="second",thrdgname="third",frthgname="fourth";
        String[] namesarr = {"","","",""};
        ArrayList names = new ArrayList();
        ArrayList<Double> entry  = new ArrayList<>();
        ArrayList units = new ArrayList();
        BluetoothBridge bridge;
        String mResponse_data = null;
        byte[] response;
        boolean aBoolean = false;
        //data collector
        ArrayList collectdata;
        boolean holdflag = false;
        public static String filename = null;
        boolean stoploop = false;
        String[] lines;
        TextView unit1,unit2,unit3,unit4;
        @SuppressLint("ResourceAsColor")
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ems_graph_view);
            ImageView ivss = (ImageView) findViewById(R.id.ss);
            ivss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View viewss = getWindow().getDecorView().getRootView();
                    AppVariables.takeScreenshot(viewss);
                }
            });

            bridge = SingleTone.getBluetoothBridge();
            Resposne();
            lines = AppVariables.Sorteddata.split(";");
            mChart1 = (LineChart) findViewById(R.id.chart1);
            mChart2 = (LineChart) findViewById(R.id.chart2);
            mChart3 = (LineChart) findViewById(R.id.chart3);
            mChart4 = (LineChart) findViewById(R.id.chart4);

            unit1 = (TextView) findViewById(R.id.unit1);
            unit2 = (TextView) findViewById(R.id.unit2);
            unit3 = (TextView) findViewById(R.id.unit3);
            unit4 = (TextView) findViewById(R.id.unit4);

            for(int i=0;i<lines.length;i++)
            {
                String dtcdata[] = lines[i].split(",");
                names.add(dtcdata[1]);
                units.add(dtcdata[3]);
            }

            int unitcount = units.size();

            switch (unitcount)
            {
                case 1:
                    unit1.setText(units.get(0).toString());
                    break;

                case 2:
                    unit1.setText(units.get(0).toString());
                    unit2.setText(units.get(1).toString());
                    break;
                case 3:
                    unit1.setText(units.get(0).toString());
                    unit2.setText(units.get(1).toString());
                    unit3.setText(units.get(2).toString());
                    break;

                case 4:
                    unit1.setText(units.get(0).toString());
                    unit2.setText(units.get(1).toString());
                    unit3.setText(units.get(2).toString());
                    unit4.setText(units.get(3).toString());
                    break;

            }


            for(int i=0;i<names.size();i++)
            {
                namesarr[i] = names.get(i).toString();
            }



            FirstChar();
            SecondChar();
            ThirdChart();
            FourthChart();

            SendCmds sendCmds = new SendCmds();
            sendCmds.start();
        }

    public void FirstChar()
    {
        mChart1.getDescription().setEnabled(true);
        mChart1.setBackgroundColor(Color.rgb(0, 0, 0));
        mChart1.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        mChart1.setData(data);

        Legend l = mChart1.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.CYAN);

        XAxis xl = mChart1.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart1.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart1.getAxisRight();
        rightAxis.setEnabled(false);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    addEntry1();
                    SystemClock.sleep(150);
                }
            }
        });
        t.start();
    }
    public void SecondChar()
    {
        mChart2.getDescription().setEnabled(true);
        mChart2.setBackgroundColor(Color.rgb(0, 0, 0));
        mChart2.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        mChart2.setData(data);

        Legend l = mChart2.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.CYAN);

        XAxis xl = mChart2.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart2.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart2.getAxisRight();
        rightAxis.setEnabled(false);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    addEntry2();
                    SystemClock.sleep(150);
                }
            }
        });
        t.start();
    }
    public void ThirdChart()
    {
        mChart3.getDescription().setEnabled(true);
        mChart3.setBackgroundColor(Color.rgb(0, 0, 0));
        mChart3.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        mChart3.setData(data);

        Legend l = mChart3.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.CYAN);

        XAxis xl = mChart3.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart3.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart3.getAxisRight();
        rightAxis.setEnabled(false);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    addEntry3();
                    SystemClock.sleep(150);
                }
            }
        });
        t.start();
    }
    public void FourthChart()
    {
        mChart4.getDescription().setEnabled(true);
        mChart4.setBackgroundColor(Color.rgb(0, 0, 0));
        mChart4.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        mChart4.setData(data);

        Legend l = mChart4.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.CYAN);

        XAxis xl = mChart4.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart4.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart4.getAxisRight();
        rightAxis.setEnabled(false);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    addEntry4();
                    SystemClock.sleep(150);
                }
            }
        });
        t.start();
    }

    private void addEntry1()
    {

        LineData data = mChart1.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet1();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), firstentry), 0);
            data.notifyDataChanged();
            mChart1.notifyDataSetChanged();
            mChart1.setTouchEnabled(false);

            mChart1.setVisibleXRangeMaximum(120);
            mChart1.moveViewToX(data.getEntryCount());
        }
    }
    private void addEntry2()
    {

        LineData data = mChart2.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet2();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), secondentry), 0);
            data.notifyDataChanged();
            mChart2.notifyDataSetChanged();
            mChart2.setTouchEnabled(false);

            mChart2.setVisibleXRangeMaximum(120);
            mChart2.moveViewToX(data.getEntryCount());
        }
    }
    private void addEntry3()
    {

        LineData data = mChart3.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet3();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), thirdentry), 0);
            data.notifyDataChanged();
            mChart3.notifyDataSetChanged();
            mChart3.setVisibleXRangeMaximum(120);
            mChart3.setTouchEnabled(false);

            mChart3.moveViewToX(data.getEntryCount());
        }
    }
    private void addEntry4()
    {


        LineData data = mChart4.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet4();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), fourthentry), 0);
            data.notifyDataChanged();
            mChart4.notifyDataSetChanged();
            mChart4.setTouchEnabled(false);

            mChart4.setVisibleXRangeMaximum(120);
            mChart4.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet1() {

        LineDataSet set = new LineDataSet(null, namesarr[0]);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(1f);
        set.setCircleRadius(2f);
        set.setFillAlpha(65);

        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(255,0,0));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
    private LineDataSet createSet2() {

        LineDataSet set = new LineDataSet(null, namesarr[1]);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("FF2E50EA"));
        set.setValueTextSize(25f);
        set.setLineWidth(1f);
        set.setCircleRadius(2f);
        set.setFillAlpha(65);

        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(255,0,0));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
    private LineDataSet createSet3() {

        LineDataSet set = new LineDataSet(null,namesarr[2]);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("FFEABB2E"));
        set.setLineWidth(1f);
        set.setCircleRadius(2f);
        set.setFillAlpha(65);

        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(255,0,0));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
    private LineDataSet createSet4() {

        LineDataSet set = new LineDataSet(null, namesarr[3]);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("FF06CF10"));
        set.setLineWidth(1f);
        set.setCircleRadius(2f);
        set.setFillAlpha(65);

        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(255,0,0));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    protected void onDestroy()
    {
        holdflag =true;
        stoploop = true;
        super.onDestroy();
    }

    public void UI()
    {
        int count = entry.size();

        switch (count)
        {
            case 1:
                firstentry = entry.get(0).floatValue();
                break;
            case 2:
                firstentry = entry.get(0).floatValue();
                secondentry = entry.get(1).floatValue();
                break;
            case 3:
                firstentry = entry.get(0).floatValue();
                secondentry = entry.get(1).floatValue();
                thirdentry = entry.get(2).floatValue();
                break;
            case 4:
                firstentry = entry.get(0).floatValue();
                secondentry = entry.get(1).floatValue();
                thirdentry = entry.get(2).floatValue();
                fourthentry = entry.get(3).floatValue();
                break;
        }

        entry.clear();
    }
    public class SendCmds extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {

                if(stoploop)
                {
                    break;
                }
                try
                {
                    //int i = 0, j = 0;
                    String res = null;
                    collectdata = new ArrayList();
                    try {

                        for (String line : lines)
                        {
                            String dtcdata[] = line.split(",");

                            //String strlen = dtcdata[3]; // res length to copy
                            //String sign = dtcdata[4]; // sign of output value
                            Log.e("Req", dtcdata[1]);
                            aBoolean = false;
                            holdflag = false;

                            send_Request_Command(("22" +dtcdata[2] + "\r\n").getBytes());//sending command here
                            if (holdflag) {
                                break;
                            }
                            while (!aBoolean&& !holdflag) {
                                SystemClock.sleep(50);
                            }

                            if(!mResponse_data.contains("NO DATA") && !mResponse_data.contains("@!"))
                            {
                                Log.e("Res", mResponse_data);
                                String valstr  = parsecmd(mResponse_data, dtcdata[2]);
                                String finalval = OutputMeth(valstr, dtcdata[0]);
                                Log.e("RESPONSES",finalval);

                                try {
                                    entry.add(Double.valueOf(finalval));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                try {
                                    entry.add(Double.valueOf("0"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {

                        try
                        {
                            UI();
                        }
                        catch (Exception e) {
                            //log the exception
                            e.printStackTrace();
                        }

                }
                super.run();
            }
        }
    }

    public String OutputMeth(String str,String num)
    {
        String output ="";

        switch (num)
        {
            case "1":
                output = KilometerStand(str);
                break;

            case "2":
                output = SystemTime(str);
                break;

            case "3":
                output = BatteryVoltage(str);
                break;

            case "4":
                output = ABSRefVel(str);
                break;

            case "5":
                output = StatusECU(str);
                break;

            case "6":
                output = Status_pump(str);
                break;

            case "7":
                output = Status_ABS_switch(str);
                break;

        }
        return output;
    }

    public String KilometerStand(String str)
    {
        String data = null;
        int a = Integer.parseInt(str,16);
        data = String.valueOf(a);
        return  data;
    }

    public String SystemTime(String str)
    {
        String data = null;
        int a = Integer.parseInt(str,16);
        data = String.valueOf(a);
        return  data;
    }
    public String BatteryVoltage(String str)
    {
        String data = null;
        double a = (Integer.parseInt(str,16)*0.001);
        data = String.valueOf(a);
        return  data;
    }

    public String ABSRefVel(String str)
    {
        String data = null;
        int a = (int) (Integer.parseInt(str,16));
        data = String.valueOf(a);
        return  data;
    }

    public String StatusECU(String str)
    {
        String data1 = str.substring(0, 2);
        int a = Integer.parseInt(data1,16);
        return String.valueOf(a);
    }

    public String Status_pump(String str)
    {
        int a = Integer.parseInt(str,16);
        return String.valueOf(a);
    }

    public String Status_ABS_switch(String str)
    {
        int a = Integer.parseInt(str.substring(0,2),16);
        return String.valueOf(a);
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

}

