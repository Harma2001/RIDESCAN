package com.example.tvsridescan.ems;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.BluetoothBridge;
import com.example.tvsridescan.Library.DataConversion;
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

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Timer;


public class EmsGraphView extends AppCompatActivity
{
    private LineChart mChart1,mChart2,mChart3,mChart4;
    Float firstentry =0f,secondentry =0f,thirdentry =0f,fourthentry =0f;
    String frstgname="first ",scndgname="second",thrdgname="third",frthgname="fourth";
    String[] namesarr = {"","","",""};
    ArrayList names = new ArrayList();
    ArrayList units = new ArrayList();
    ArrayList<Double> entry  = new ArrayList<>();

    BluetoothBridge bridge;
    String mResponse_data = null;
    byte[] response;
    volatile  boolean aBoolean = false;
    //data collectorvo
    ArrayList collectdata;
    volatile boolean holdflag = false;
    public static String filename = null;
    boolean stoploop = false;
    String[] lines;

    TextView unit1,unit2,unit3,unit4;
    Timer myTimer = new Timer();


    String dtcdata[];

    String strlen ; // res length to copy
    String sign ; // sign of output value

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
            names.add(dtcdata[0]);
            units.add(dtcdata[5]);
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

        mChart1.setVisibility(View.VISIBLE);
        mChart2.setVisibility(View.VISIBLE);
        mChart3.setVisibility(View.VISIBLE);
        mChart4.setVisibility(View.VISIBLE);

        FirstChar();
        SecondChar();
        ThirdChart();
        FourthChart();

        Emsrp emsrp = new Emsrp();
        emsrp.start();
        //starTimer();
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
            mChart1.setVisibleXRangeMaximum(120);
            mChart1.setTouchEnabled(false);
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
            mChart3.setTouchEnabled(false);

            mChart3.setVisibleXRangeMaximum(120);
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
            mChart4.setVisibleXRangeMaximum(120);
            mChart4.setTouchEnabled(false);

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



    public class Emsrp extends Thread
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
                    try
                    {

                        for (String line : lines)
                        {
                            String dtcdata[] = line.split(",");

                            String strlen = dtcdata[3]; // res length to copy
                            String sign = dtcdata[5]; // sign of output value
                            Log.e("Req", dtcdata[1]);
                            aBoolean = false;
                            holdflag = false;
                            send_Request_Command(("22" + dtcdata[1] + "\r\n").getBytes());//sending command here
                            if (holdflag) {
                                break;
                            }
                            while (!aBoolean&& !holdflag) {
                                SystemClock.sleep(50);
                            }

                            Log.e("Res", mResponse_data); // getting the response here

                            mResponse_data = mResponse_data.replace(" ", "");//removing the spaces

                            if(!mResponse_data.contains("NODATA") && !mResponse_data.contains("@!"))
                            {
                                if (mResponse_data.contains("7F") && mResponse_data.length() == 6) {
                                    String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                                   // String[] val = data.split(",");
                                    res = data.replace(",", " ");
                                }
                                else {
                                    mResponse_data = AppVariables.parsecmd(mResponse_data, dtcdata[1]);
                                    switch (dtcdata[2]) {
                                        case "0": {
                                            int lenpkt = Integer.parseInt(strlen, 16);
                                            mResponse_data = mResponse_data.substring(0, lenpkt * 2);
                                            int val = Integer.parseInt(mResponse_data, 16);
                                            //dtcdata[4].toString()
                                            Expression e = new ExpressionBuilder(dtcdata[4])
                                                    .variables("A")
                                                    .build()
                                                    .setVariable("A", val);
                                            double result = e.evaluate();

                                            if (dtcdata[6].equals("1")) //NO DATA
                                            {
                                                res = String.valueOf((int) result);
                                            } else {
                                                res = AppVariables.TrimDouble(String.valueOf(result));
                                            }

                                            //j = 0;
                                            break;
                                        }
                                        case "1": {
                                            int lenpkt = Integer.parseInt(strlen, 16);
                                            mResponse_data = mResponse_data.substring(0, lenpkt * 2);
                                            String str = Integer.toBinaryString(Integer.parseInt(mResponse_data.substring(0, 2)));
                                            int i1 = Integer.parseInt(str, 16);
                                            String bin = Integer.toBinaryString(i1);
                                            String ressign = "";
                                            if (bin.length() > 7 && bin.substring(0, 1).equals("1")) {
                                                ressign = "-";
                                            } else {
                                                ressign = "";
                                            }
                                            int val = Integer.parseInt(mResponse_data, 16);
                                            //dtcdata[4].toString()
                                            Expression e = new ExpressionBuilder(dtcdata[4])
                                                    .variables("A")
                                                    .build()
                                                    .setVariable("A", val);
                                            double result = e.evaluate();


                                            if (dtcdata[6].equals("1")) //NO DATA
                                            {
                                                res = ressign + String.valueOf((int) result);
                                            } else {
                                                res = ressign + String.valueOf(result);
                                            }
                                            //j = 0;
                                            break;
                                        }
                                        case "2": {
                                            //direct to ascii
                                            //byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);
                                            BigInteger val = new BigInteger(mResponse_data, 16);
                                            res = String.valueOf(val);
                                            //j = 0;
                                            break;
                                        }
                                        case "3": {
                                            int val = Integer.parseInt(mResponse_data, 16);
                                            //
                                            res = String.valueOf(val);
                                            break;
                                        }
                                        case "4":
                                            res = mResponse_data;
                                            break;
                                        case "5": {
                                            BigInteger val = new BigInteger(mResponse_data, 16);
                                            res = String.valueOf(val);
                                            //j = 0;
                                            break;
                                        }
                                        case "7":
                                            res = mResponse_data;
                                            break;
                                        case "8":
                                            res = mResponse_data;
                                            break;
                                        case "9":
                                            res = mResponse_data;
                                            break;
                                        case "10":
                                            res = mResponse_data;
                                            break;
                                        case "11":
                                            res = mResponse_data;
                                            break;
                                        case "12":
                                            byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);

                                            res = new String(bytedata);
                                            break;
                                        case "13":
                                            res = mResponse_data;
                                            break;
                                        case "14":
                                            res = mResponse_data;
                                            break;
                                        default:
                                            //byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);
                                            res = mResponse_data;
                                            // j = 0;
                                            break;
                                    }
                                }
                                //i++;
                                if(!res.isEmpty())
                                {
                                    try {
                                        entry.add(Double.valueOf(res));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                {
                                    try {
                                        entry.add((double) 0);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            }
                            else
                            {
                                try {
                                    entry.add((double) 0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    }
                    finally
                    {
                        UI();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                super.run();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        stoploop = true;
        //stopTimer();
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
    public void send_Request_Command(final byte[] arr)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                bridge.SendCmd(arr);

            }
        });
        thread.start();
    }
    public void Resposne()
    {
        bridge.MethResponseInt(new BluetoothBridge.ResponseInterface()
        {
            @Override
            public void ResponseMeth(byte[] arr, String resStr)
            {
                response = arr;
                mResponse_data = resStr;
                aBoolean = true;

               /* mResponse_data = mResponse_data.replace(" ", "");//removing the spaces
                String res = "";

                //collectdata = new ArrayList();
                if (mResponse_data.contains("7F") && mResponse_data.length() == 6)
                {
                    String data = AppVariables.NegRes(mResponse_data.substring(4, 6));
                    String[] val = data.split(",");
                    res = data.replace(",", " ");
                    sign = "";
                    //j = 1;
                }
                else
                {
                    mResponse_data = AppVariables.parsecmd(mResponse_data, dtcdata[1]);
                    switch (dtcdata[2])
                    {
                        case "0": {
                            int lenpkt = Integer.parseInt(strlen, 16);
                            mResponse_data = mResponse_data.substring(0, lenpkt * 2);
                            int val = Integer.parseInt(mResponse_data, 16);
                            //dtcdata[4].toString()
                            Expression e = new ExpressionBuilder(dtcdata[4])
                                    .variables("A")
                                    .build()
                                    .setVariable("A", val);
                            double result = e.evaluate();

                            if (dtcdata[6].equals("1")) //NO DATA
                            {
                                res = String.valueOf((int) result);
                            } else {
                                res = AppVariables.TrimDouble(String.valueOf(result));
                            }

                            //j = 0;
                            break;
                        }
                        case "1": {
                            int lenpkt = Integer.parseInt(strlen, 16);
                            mResponse_data = mResponse_data.substring(0, lenpkt * 2);
                            String str = Integer.toBinaryString(Integer.parseInt(mResponse_data.substring(0, 2)));
                            int i1 = Integer.parseInt(str, 16);
                            String bin = Integer.toBinaryString(i1);
                            String ressign = "";
                            if (bin.length() > 7 && bin.substring(0, 1).equals("1")) {
                                ressign = "-";
                            } else {
                                ressign = "";
                            }
                            int val = Integer.parseInt(mResponse_data, 16);
                            //dtcdata[4].toString()
                            Expression e = new ExpressionBuilder(dtcdata[4])
                                    .variables("A")
                                    .build()
                                    .setVariable("A", val);
                            double result = e.evaluate();


                            if (dtcdata[6].equals("1")) //NO DATA
                            {
                                res = ressign + String.valueOf((int) result);
                            } else {
                                res = ressign + String.valueOf(result);
                            }
                            //j = 0;
                            break;
                        }
                        case "2": {
                            //direct to ascii
                            //byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);
                            BigInteger val = new BigInteger(mResponse_data, 16);
                            res = String.valueOf(val);
                            //j = 0;
                            break;
                        }
                        case "3": {
                            int val = Integer.parseInt(mResponse_data, 16);
                            //
                            res = String.valueOf(val);
                            break;
                        }
                        case "4":
                            res = mResponse_data;
                            break;
                        case "5": {
                            BigInteger val = new BigInteger(mResponse_data, 16);
                            res = String.valueOf(val);
                            //j = 0;
                            break;
                        }
                        case "7":
                            res = mResponse_data;
                            break;
                        case "8":
                            res = mResponse_data;
                            break;
                        case "9":
                            res = mResponse_data;
                            break;
                        case "10":
                            res = mResponse_data;
                            break;
                        case "11":
                            res = mResponse_data;
                            break;
                        case "12":
                            byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);

                            res = new String(bytedata);
                            break;
                        case "13":
                            res = mResponse_data;
                            break;
                        case "14":
                            res = mResponse_data;
                            break;
                        default:
                            //byte[] bytedata = DataConversion.hexStringToByteArray(mResponse_data);
                            res = mResponse_data;
                            // j = 0;
                            break;
                    }
                }
                //i++;
                *//*try {
                    entry.add(Double.valueOf(res));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UI();

                    }
                });*//*
             */


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

