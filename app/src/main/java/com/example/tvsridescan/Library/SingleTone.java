package com.example.tvsridescan.Library;

/**
 * Created by SAIKUMAR on 6/20/2017.
 */

public class SingleTone
{
    public static final SingleTone instance = new SingleTone();

    public static SingleTone getInstance() {
        return instance;
    }

    public static BluetoothConversation getBluetoothConversation() {
        return bluetoothConversation;
    }

    public static void setBluetoothConversation(BluetoothConversation bluetoothConversation) {
        SingleTone.bluetoothConversation = bluetoothConversation;
    }

    public static BluetoothConversation bluetoothConversation;

    public int getSomeState() {
        return someState;
    }

    public void setSomeState(int someState) {
        this.someState = someState;
    }

    private int someState;

    public static BluetoothBridge getBluetoothBridge() {
        return bluetoothBridge;
    }

    public static void setBluetoothBridge(BluetoothBridge bluetoothBridge) {
        SingleTone.bluetoothBridge = bluetoothBridge;
    }

    public static BluetoothBridge bluetoothBridge;


}

