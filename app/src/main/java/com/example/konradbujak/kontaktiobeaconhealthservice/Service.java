package com.example.konradbujak.kontaktiobeaconhealthservice;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.ScanMode;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;


public class Service extends IntentService {
    private static final String TAG = "MyActivity";
    public Service() {
        super("Service");
    }
    private ProximityManager KontaktioManager;
    @Override
    protected void onHandleIntent(Intent intent) {
        //to use our sdk properly you have to add here your API KEY
        KontaktSDK.initialize("Put Here your secret API Key");
        if (KontaktSDK.isInitialized())
            Log.i(TAG, "SDK initialised");
        KontaktioManager = new ProximityManager(this);
        KontaktioManager.configuration().monitoringEnabled(true); // enabled by default
        KontaktioManager.configuration().scanMode(ScanMode.BALANCED);
        KontaktioManager.configuration().scanPeriod(ScanPeriod.RANGING);
        //iBeacon scanner ( listener)
        KontaktioManager.setIBeaconListener(new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                Log.v(TAG, "IBeacon: " + ibeacon.getUniqueId());
            }
        });
        //Eddystone scanner ( listener)
        KontaktioManager.setEddystoneListener(new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Log.v(TAG, "Eddystone: " + eddystone.getUniqueId());            }
        });
        //calling the function that start scanning
        startScanning();
    }
    private void startScanning(){
        KontaktioManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                KontaktioManager.startScanning();
                if (KontaktioManager.isScanning())
                    Log.i(TAG, "Scan started");
            }
        });

    }
}
