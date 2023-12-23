package com.example.tvsridescan.Library;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class BluetoothBridge
{

    private String mResponse_data = null;
    private byte[] response;

    private ResponseInterface mresponseInterface;
    private BluetoothConversation bluetoothConversation;
    private Context mContext;

    public BluetoothBridge(Context context )
    {
        mContext = context;
        bluetoothConversation = new BluetoothConversation(mContext);
        bluetoothConversation.assigntoContolData(mHandler_Response);
    }
    public void EstaConn(Context mContext1,String add)
    {

        //bluetoothConversation.assigntoContolData(mHandler_Response);
        mContext =mContext1;
        bluetoothConversation = new BluetoothConversation(mContext,add);

    }
    public synchronized void SendCmd(byte[] arr)
    {
        if(bluetoothConversation!=null)
        {
            bluetoothConversation.sendRequest(arr);
            //Log.e("cmd sent / bridge","send method");

        }
    }
    Handler mHandler_Response = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what)
            {
                case 1:// 11 Is ID for data receiving
                    response= (byte[]) message.obj;//
                    mResponse_data = new String(response);
                    Log.e("res at handler / bridge",mResponse_data);
                    mresponseInterface.ResponseMeth(response,mResponse_data);
                    break;
                case 2:
                    try
                    {
                        mresponseInterface.ConnectionLost();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    break;
                case 3:
                    mResponse_data = (String) message.obj;//
                    Log.e("res at handler / bridge",mResponse_data);
                    mresponseInterface.Connected(mResponse_data);
                    break;
            }
            return true;
        }
    });

    public interface ResponseInterface
    {
         void ResponseMeth(byte[] arr, String str);
         void ConnectionLost();
         void Connected(String str);
    }
    public void MethResponseInt(ResponseInterface responseInterface)
    {
        mresponseInterface = responseInterface;
    }


}

