/*
 * Copyright (c) 2015. Queen Mary University of London
 * Kleomenis Katevas, k.katevas@qmul.ac.uk
 *
 * This file is part of CrowdSensing software.
 * For more information, please visit http://www.sensingkit.org
 *
 * CrowdSensing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CrowdSensing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CrowdSensing.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sensingkit.crowdsensing_android;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import org.sensingkit.sensingkitlib.SKException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SensingService extends Service {

    @SuppressWarnings("unused")
    protected static final String TAG = "SensingService";

    private final IBinder mBinder = new LocalBinder();

    private PowerManager.WakeLock mWakeLock;

    // Sensing Session
    private SensingSession mSensingSession;


    @Override
    public void onCreate() {
        super.onCreate();

        mSensingSession = createSensingSession();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onDestroy() {

        try {
            mSensingSession.stop();
            mSensingSession.close();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        SensingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SensingService.this;
        }
    }

    private SensingSession createSensingSession() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.UK);
        String folderName = dateFormat.format(new Date());

        SensingSession session;

        try {
            session = new SensingSession(this, folderName);
        }
        catch (SKException ex) {
            Log.e(TAG, ex.getMessage());
            session = null;
        }

        return session;
    }

    private void showNotification() {

        // The PendingIntent to launch our activity if the user selects this notification
        Intent intent = new Intent(this, CrowdSensing.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build the notification
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Crowd Sensing")
                .setContentText("Collecting data...")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .build();

        // Show the notification
        startForeground(1, notification);
    }

    private void hideNotification() {
        stopForeground(true);
    }

    // --- Wake Lock

    private void acquireWakeLock() {
        if ((mWakeLock == null) || (!mWakeLock.isHeld())) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
            mWakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    // --- Public API

    public void startSensing() {

       try {
           acquireWakeLock();
           mSensingSession.start();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

        // Show notification
        showNotification();
    }

    public void stopSensing() {

        try {
            releaseWakeLock();
            mSensingSession.stop();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

        // Hide notification
        hideNotification();
    }

}
