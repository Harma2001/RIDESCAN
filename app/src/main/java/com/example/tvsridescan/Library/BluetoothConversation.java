package com.example.tvsridescan.Library;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.example.tvsridescan.connection.ConnectionInterrupt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by SAIKUMAR on 3/15/2017.
 */

public class BluetoothConversation
{
    public static int reshandle = 0;
    ConnectedThread connectedThread;
    ArrayList<Integer> arr_byte = new ArrayList<Integer>();
    boolean resflag = false;
    public static int delay = 2000;
    static StringBuilder stringBuilderLog = new StringBuilder();

    static File file;
    static Date d1 = new Date();
    static String time;
    static boolean stopflag = false;
    public static boolean FlagLog = false;

    //---

    //object for bluetooth Device
    public BluetoothDevice bluetoothDevice;

    //object for bluetoothsocket
    public BluetoothSocket bluetoothSocket;

    //object for bluetooth adapter
    public BluetoothAdapter bluetoothAdapter;

    //object for thread
    public CreateSocket createSocket;

    //UUID for Bluetooth Connection
    private static final UUID UUID_OTHER_DEVICE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
      //00001101-0000-1000-8000-00805F9B34FB

    //Stream for input and output

    //
    public BluetoothSocket mBluetoothSocket;

    //bluetooth inputstream
    public InputStream inputStream;

    //bluetooth outputstream
    public OutputStream outputStream;

    //object for context
    public Context mContext;

    //string to store data
    String strlog = null;
    //---end of variables creation---

    //constructor 1
    //this constructor will call when user want to establish bluetooth connection
    public BluetoothConversation(Context context, String add)
    {
        mContext = context;
        createSocket = new  CreateSocket(add);
        createSocket.start();
        CheckConnection checkConnection = new CheckConnection();
        checkConnection.start();
    }

    //constructor 2
    //For Normal communication use this Constructor
    public BluetoothConversation(Context context)
    {
        mContext = context;
    }

    //thread is used to establish connection
    public class CreateSocket extends Thread
    {
        String btaddress;
        public CreateSocket(String Device)
        {
            btaddress = Device;
        }
        @Override
        public void run()
        {
            //
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(btaddress);
            try
            {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID_OTHER_DEVICE);

                if(bluetoothSocket.isConnected())
                {
                    bluetoothSocket.close();
                }

                bluetoothSocket.connect();
                SessionTime sessionTime = new SessionTime();
                sessionTime.start();

                //SystemClock.sleep(2000);
                //connection success acknowledment to user.
                mHandler_CheckResponse.obtainMessage(3, AppVariables.CONNECTED_STR).sendToTarget();

            }
            catch (IOException e)
            {
                //connection failed acknowledment to user.
                mHandler_CheckResponse.obtainMessage(3,  AppVariables.NOT_CONNECTED_STR).sendToTarget();
                e.printStackTrace();
            }

            //start thread which create streams for request and response
            connectedThread = new ConnectedThread(bluetoothSocket);
            connectedThread.start();

            super.run();
        }
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e)
            {

            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer;
            // Keep listening to the InputStream while connected
            while (true)
            {
                try
                {
                    int res=0;
                    res   = mmInStream.read();
                    if (reshandle == 4)
                    {
                        if (res == '#' || res =='>')
                        {
                            resflag = true;
                            buffer = new byte[arr_byte.size()];

                            for (int i = 0; i < arr_byte.size(); i++)
                            {
                                buffer[i] = arr_byte.get(i).byteValue();
                            }
                            if(FlagLog)
                            {
                                strlog = new String(buffer);
                                CollectLogData(" RX :"+strlog);
                            }
                            arr_byte.clear();
                            String response = new String(buffer);
                            response = response.replace(" ","");
                            Log.e("res @BC",response);

                            if(response.length()==6 && "7F".equals(response.substring(0,2)) && "78".equals(response.substring(4,6)))
                            {
                                Log.e("BC","7F  78");
                                AppVariables.GenLogLine("BC :7F  78");
                                SystemClock.sleep(delay);

                                // byte[] cmd = "XTRQ\r\n".getBytes();
                                //sendRequest(cmd);
                                //sendRequest(cmd);
                            }
                            else if(response.equals("") && (0==buffer.length))
                            {
                                Log.e("BC","00");
                                AppVariables.GenLogLine("BC :00");
                                SystemClock.sleep(delay);
                               //
                               // SystemClock.sleep(delay);
                                //byte[] cmd = "XTRQ\r\n".getBytes();
                                //  Log.e("resblock","empty response with >");
                                // sendRequest(cmd);
                            /*  byte[] cmd = "XTRQ\r\n".getBytes();
                                Log.e("resblock","empty response with >");
                                sendRequest(cmd);*/
                            }
                            else
                            {
                                Log.e("hander at bt","entered");
                                mHandler_CheckResponse.obtainMessage(1, buffer).sendToTarget();
                            }

                        }
                        else
                        if(res== 13 ||res == 10){}
                        else
                        {
                            arr_byte.add(res);
                        }

                    }

                    else if (reshandle == 1)
                    {
                        if (res == 13)
                        {
                            buffer = new byte[arr_byte.size()];
                            for (int i = 0; i < arr_byte.size(); i++)
                            {
                                buffer[i] = arr_byte.get(i).byteValue();
                            }
                            mHandler_CheckResponse.obtainMessage(1, buffer).sendToTarget();
                            arr_byte = new ArrayList<Integer>();

                        }
                        else if (res == 10)
                        {

                        }
                        else
                        {
                            arr_byte.add(res);
                        }
                    }

                }
                catch (Exception e)
                {
                    GenLog();
                    try
                    {
                        if(BluetoothConversation.ConnectionCheck!=true)
                        {
                            ConnectionInterrupt.msg_txt = "Disconnected from VCI";
                            mHandler_CheckResponse.obtainMessage(2, "Connection Lost").sendToTarget();
                        }
                        else
                        {
                            DisconnectBt();
                        }

                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }

                    e.printStackTrace();
                    break;

                }
               // catch (Blu )

            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer)
        {
            try
            {
             mmOutStream.write(buffer);
             if(FlagLog)
             {
                 strlog = new String(buffer);
                 CollectLogData(" TX :"+strlog);
             }
             Log.e("cmd send",new String(buffer));
            }
            catch (IOException e)
            {
                Log.e("cmd send","not sent : "+new String(buffer)+e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        public void cancel() {
            try
            {
                stopflag = true;
                mmSocket.close();

            } catch (IOException e) {
                //Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    public static Handler mHandler_CheckResponse;
    /**
     * @param pHandler_Response
     */
    public void assigntoContolData(Handler pHandler_Response)
    {
        mHandler_CheckResponse = pHandler_Response;
    }

    public void sendRequest(byte[] arr)
    {
        connectedThread.write(arr);
    }
    public  void DisconnectBt()
    {
        connectedThread.cancel();

        String temString = "Session";
        ConnectionInterrupt.msg_txt = temString;
        mHandler_CheckResponse.obtainMessage(2, "Connection Lost").sendToTarget();
    }

    //Log Creation
    public static  void resetLog()
    {
        stringBuilderLog =  new StringBuilder();
    }
    public static void  CollectLogData(String str)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:aa");
        String formattedDate = dateFormat.format(new Date());
        stringBuilderLog.append("\n");
        stringBuilderLog.append("["+formattedDate+"] "+str);
        stringBuilderLog.append("\n");

    }
    public static void GenLog()
    {

        try
        {
            //first create file
            file = new File("/sdcard/RideScan/");

            file.mkdir();

            file = new File("/sdcard/RideScan/LogCat.txt");

            //this will create file in internal storage....
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            //is an op stream to write data to file
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            //outputstream have file now write data
            String datatowrite = stringBuilderLog.toString();
            outputStreamWriter.append(datatowrite);
            //  Toast.makeText(context, "Log Created Successfully", Toast.LENGTH_SHORT).show();
            outputStreamWriter.close();
            fileOutputStream.close();
        }
        catch (IOException e)
        {
            // Toast.makeText(context, "Log Created Failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public static class Duration extends Thread
    {
        @Override
        public void run()
        {
            while(true)
            {
                Date d2 = new Date();

                long difference = d2.getTime() - d1.getTime();

                long millis = difference;
                time = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                super.run();
                if(stopflag)
                {
                    break;
                }
            }

        }
    }
    public static void StartTimeDuration()
    {
        Duration duration = new Duration();
        duration.start();
    }

    public static int Sessioncount = 0;
    public static class SessionTime extends Thread
    {
        @Override
        public void run()
        {
            while(true)
            {
                SystemClock.sleep(1000);
                Sessioncount = Sessioncount+1;
                GenLog();
                if(Sessioncount>7200)//2hours
                {
                    try
                    {
                        ConnectionCheck = true;
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                    break;
                }
            }

        }
    }
    public static boolean ConnectionCheck = false;

    public class CheckConnection extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                if(ConnectionCheck)
                {
                    ConnectionCheck = false;
                    try
                    {
                        DisconnectBt();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    break;
                }
                super.run();
            }

        }
    }

}
