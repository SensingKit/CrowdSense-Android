/*
 * Copyright (c) 2014. Queen Mary University of London
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

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CrowdSensing extends ActionBarActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "CrowdSensing";

    private enum SensingStatus {
        Stopped,
        Sensing,
        Paused
    }

    // UI Elements
    private TextView mStatus;
    private Button mSensingButton;
    private Button mPauseButton;

    // Button Statuses
    SensingStatus mSensingStatus;

    // Services
    SensingService mSensingService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowd_sensing);

        // get refs to the Status TextView
        mStatus = (TextView)findViewById(R.id.status);

        mSensingButton = (Button)findViewById(R.id.sensing);
        mSensingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                switch (mSensingStatus) {

                    case Stopped:
                        startSensing();
                        setSensingStatus(SensingStatus.Sensing);
                        break;

                    case Paused:
                    case Sensing:
                        stopSensing();
                        setSensingStatus(SensingStatus.Stopped);
                        break;

                }

            }
        });

        mPauseButton = (Button)findViewById(R.id.pause);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (mSensingStatus) {

                    case Stopped:
                        Log.e(TAG, "Case Stopped should not be available.");

                        break;

                    case Paused:
                        continueSensing();
                        setSensingStatus(SensingStatus.Sensing);
                        break;

                    case Sensing:
                        pauseSensing();
                        setSensingStatus(SensingStatus.Paused);
                        break;

                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG, "onStart()");


    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i(TAG, "onStop()");

        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, SensingService.class);

        if (!isSensingServiceRunning()) {

            // Start the SensingService
            startService(intent);
        }

        // Bind SensingService
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            Log.i(TAG, "onServiceConnected()");

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SensingService.LocalBinder binder = (SensingService.LocalBinder) service;
            mSensingService = binder.getService();
            mBound = true;

            // Update the UI
            switch (mSensingService.getSensingStatus()) {

                case Stopped:
                    setSensingStatus(SensingStatus.Stopped);
                    break;

                case Sensing:
                    setSensingStatus(SensingStatus.Sensing);
                    break;

                case Paused:
                    setSensingStatus(SensingStatus.Paused);
                    break;

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            Log.i(TAG, "onServiceDisconnected()");

            mBound = false;
        }

    };

    private boolean isSensingServiceRunning() {

        ActivityManager manager = (ActivityManager) getSystemService (Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (SensingService.class.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;

    }

    private void setSensingStatus(SensingStatus status) {

        switch (status) {

            case Stopped:

                mStatus.setText("Stopped");
                mSensingButton.setText("Start Sensing");
                mPauseButton.setText("Pause");
                mPauseButton.setEnabled(false);
                break;

            case Sensing:

                mStatus.setText("Sensing...");
                mSensingButton.setText("Stop Sensing");
                mPauseButton.setText("Pause");
                mPauseButton.setEnabled(true);
                break;

            case Paused:

                mStatus.setText("Paused");
                mSensingButton.setText("Stop Sensing");
                mPauseButton.setText("Continue");
                mPauseButton.setEnabled(true);
                break;

            default:
                Log.i(TAG, "Unknown SensingStatus: " + status);

        }

        mSensingStatus = status;
    }



    private void startSensing() {

        // Start Sensing
        mSensingService.startSensing();
    }

    private void pauseSensing() {

        // Pause Sensing
        mSensingService.pauseSensing();
    }

    private void continueSensing() {

        // Continue Sensing
        mSensingService.continueSensing();
    }

    private void stopSensing() {

        // Stop Sensing
        mSensingService.stopSensing();

    }

}
