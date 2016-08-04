package com.raceflight.mobileconfigurator;

import android.app.Application;
import android.util.Log;

import com.raceflight.mobileconfigurator.communication.SerialFTDI;

/**
 * Created by Alex on 19/07/2016.
 */
public class App extends Application {
    private static final String TAG = App.class.getName();

    public static final String SERIAL_PORT_BAUD_RATE_MW = "SerialPortBaudRateMW1";
    public int SerialPortBaudRateMW = 115200;

    public static final int COMMUNICATION_TYPE_SERIAL_FTDI = 1;
    public SerialFTDI commMW;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate init");
        init();
    }

    public void init() {

            commMW = new SerialFTDI(getApplicationContext());

    }
}
