package com.example.konradbujak.kontaktiobeaconhealthservice;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.ScanMode;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.listeners.ScanStatusListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleScanStatusListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

/**
 * Created by Konrad.Bujak on 19.10.2016.
 */

public class Service extends IntentService {
    private static final String TAG = "MyActivity";
    //Replace (Your Secret API key) with your API key aquierd from the Kontakt.io Web Panel
    public static String API_KEY = "Your Secret API key";
    public Service() {
        super("Service");
    }
    private ProximityManager KontaktioManager;

    @Override
    protected void onHandleIntent(Intent intent) {
        KontaktSDK.initialize(API_KEY);
        if (KontaktSDK.isInitialized())
            Log.i(TAG, "SDK initialised");
        KontaktioManager = new ProximityManager(this);
        KontaktioManager.configuration().monitoringEnabled(true); // enabled by default
        KontaktioManager.configuration().scanMode(ScanMode.BALANCED);
        KontaktioManager.configuration().scanPeriod(ScanPeriod.RANGING);
        KontaktioManager.setScanStatusListener(createScanStatusListener());
        //iBeacon scanner ( listener)
        KontaktioManager.setIBeaconListener(new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                Log.d(TAG, "IBeacon: " + ibeacon.getUniqueId());
            }
        });
        //Eddystone scanner ( listener)
        KontaktioManager.setEddystoneListener(new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Log.d(TAG, "Eddystone: " + eddystone.getUniqueId());            }
        });
        //calling the function that start scanning
        startScanning();
    }
    private ScanStatusListener createScanStatusListener() {
        return new SimpleScanStatusListener() {
            @Override
            public void onScanStart()
            {
                Log.d(TAG,"Scanning started");
            }
            @Override
            public void onScanStop()
            {
                Log.d(TAG,"Scanning stopped");
            }
        };
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
