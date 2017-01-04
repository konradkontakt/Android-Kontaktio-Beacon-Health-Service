package com.example.konradbujak.kontaktiobeaconhealthservice;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.ScanMode;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.listeners.ScanStatusListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleScanStatusListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleSecureProfileListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;
import com.kontakt.sdk.android.common.profile.ISecureProfile;

/**
 * Created by Konrad.Bujak on 19.10.2016.
 */

public class Service extends IntentService {
    private static final String TAG = "MyActivity";
    //Replace (Your Secret API key) with your API key acquired from the Kontakt.io Web Panel
    public static String API_KEY = "Your Secret API key";
    public int i;
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
        KontaktioManager.configuration().monitoringEnabled(true)
                .activityCheckConfiguration(ActivityCheckConfiguration.DISABLED)
                .deviceUpdateCallbackInterval(3000)
                .scanMode(ScanMode.BALANCED)
                .scanPeriod(ScanPeriod.RANGING);
        KontaktioManager.setScanStatusListener(createScanStatusListener());
        //iBeacon scanner ( listener)
        KontaktioManager.setIBeaconListener(new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                if(ibeacon.getUniqueId()!=null)
                    Log.d(TAG, "IBeacon: " + ibeacon.getUniqueId());
            }
        });
        //Eddystone scanner ( listener)
        KontaktioManager.setEddystoneListener(new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                if(eddystone.getUniqueId()!=null)
                    Log.d(TAG, "Eddystone: " + eddystone.getUniqueId());            }
        });
        KontaktioManager.setKontaktSecureProfileListener((new SimpleSecureProfileListener(){
            @Override
            public void onProfileDiscovered(ISecureProfile profile) {
                i++;
                Log.d(TAG, "Beacon Pro: " + profile.getUniqueId());

            }
        }));
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
