package com.example.tvsridescan.Library;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class BluetoothSemaphoreService {
    byte[] btResponse;
    Semaphore btSemaphore;
    BluetoothBridge bridge;


    public byte[] SendTbusCommand(byte sid,byte did, byte[] command, short length,boolean doConversion)
    {
        btSemaphore = new Semaphore(0);
        bridge = SingleTone.getBluetoothBridge();

        /*Forming the Tbus Command*/
        byte[] tbusFrame = Tbus.formCommand(sid,did,command,length);

        bridge.SendCmd(tbusFrame);

        /*Block on semaphore till response comes*/
        try {
            btSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*parsing the Tbus response*/
        btResponse=Tbus.parseResponse(btResponse);
        if(doConversion && btResponse!=null)
        {
            /*Converting response from pan To ByteArray*/
            btResponse = DataConversion._PanToByteArray(btResponse,btResponse.length/2);
        }
        return btResponse;
    }
    public byte[] SendTbusCommandwithTimeout(byte sid, byte did, byte[] command, short length, boolean doConversion, int timeout)
    {
        btSemaphore = new Semaphore(0);
       bridge = SingleTone.getBluetoothBridge();

        /*Forming the Tbus Command*/
        byte[] tbusFrame = Tbus.formCommand(sid,did,command,length);

        bridge.SendCmd(tbusFrame);
        /*Block on semaphore till response comes*/
        try {
            if(! btSemaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS))
            {
                btSemaphore.release(0);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*parsing the Tbus response*/
        if(btResponse!=null)
        {
            btResponse=Tbus.parseResponse(btResponse);

            if(doConversion && btResponse!=null)
            {
                /*Converting response from pan To ByteArray*/
                btResponse = DataConversion._PanToByteArray(btResponse,btResponse.length/2);
            }

            return btResponse;
        }
        return null;

    }

    public  byte[] SendCommand(byte[] command,boolean doConversion )
    {
        btSemaphore = new Semaphore(0);
       bridge = SingleTone.getBluetoothBridge();
        bridge.SendCmd(command);
        /*Block on semaphore till response comes*/
        try {
            btSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*parsing the Tbus response*/
        btResponse=Tbus.parseResponse(btResponse);

        if(doConversion && btResponse!=null)
        {
            /*Converting response from pan To ByteArray*/
            btResponse = DataConversion._PanToByteArray(btResponse,btResponse.length/2);
        }

        return btResponse;
    }
    public byte[] SendCommandwithTimeout(byte [] command,boolean parseRepsonse, boolean doConversion, int timeout)
    {
        btSemaphore = new Semaphore(0);
        bridge = SingleTone.getBluetoothBridge();
        bridge.SendCmd(command);

        /*Block on semaphore till response comes*/
        try {
            if(! btSemaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS))
            {
                btSemaphore.release(0);
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if(parseRepsonse)
        {
            /*parsing the Tbus response*/
            btResponse= Tbus.parseResponse(btResponse);
        }
        if(doConversion && btResponse!=null)
        {
            /*Converting response from pan To ByteArray*/
            btResponse = DataConversion._PanToByteArray(btResponse,btResponse.length/2);
        }
        return btResponse;
    }


    public void getResponse(byte[] response)
    {

        if(btSemaphore!=null)
        {
            btResponse= response;  /*Copying response*/

            btSemaphore.release();  /*releasing  semaphore */
        }
    }

    public void hardRelease()
    {
        btSemaphore.release();
    }

}
