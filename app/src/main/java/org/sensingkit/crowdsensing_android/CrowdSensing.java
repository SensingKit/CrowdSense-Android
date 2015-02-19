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

    private enum ButtonStatus {
        Started,
        Stopped
    }

    // UI Elements
    private TextView mStatus;
    private Button mAudioCalibrationButton;
    private Button mSensingButton;

    // Button Statuses
    ButtonStatus mAudioCalibrationButtonStatus = ButtonStatus.Stopped;
    ButtonStatus mSensingButtonStatus = ButtonStatus.Stopped;

    // Services
    SensingService mSensingService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowd_sensing);

        // get refs to the Status TextView
        mStatus = (TextView)findViewById(R.id.status);

        // get ref to the buttons and add actions
        mAudioCalibrationButton = (Button)findViewById(R.id.audioCalibration);
        mAudioCalibrationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (mAudioCalibrationButtonStatus == ButtonStatus.Started) {
                    stopCalibrating();
                }
                else if (mAudioCalibrationButtonStatus == ButtonStatus.Stopped) {
                    startCalibrating();
                }
            }
        });

        mSensingButton = (Button)findViewById(R.id.sensing);
        mSensingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSensingButtonStatus == ButtonStatus.Started) {
                    stopSensing();
                }
                else if (mSensingButtonStatus == ButtonStatus.Stopped) {
                    startSensing();
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
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            Log.i(TAG, "onServiceConnected()");

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SensingService.LocalBinder binder = (SensingService.LocalBinder) service;
            mSensingService = binder.getService();
            mBound = true;
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

    private void startSensing() {

        //
        mSensingService.startSensing();

        mStatus.setText("Sensing");
        mSensingButton.setText("Stop Sensing");
        mSensingButtonStatus = ButtonStatus.Started;
    }

    private void stopSensing() {

        //
        mSensingService.stopSensing();

        mStatus.setText("Stopped");
        mSensingButton.setText("Start Sensing");
        mSensingButtonStatus = ButtonStatus.Stopped;
    }

    private void startCalibrating() {

        //
        mSensingService.startCalibration();

        mAudioCalibrationButton.setText("Stop Audio Calibration");
        mAudioCalibrationButtonStatus = ButtonStatus.Started;
    }

    private void stopCalibrating() {

        //
        mSensingService.stopCalibration();

        mAudioCalibrationButton.setText("Audio Calibration");
        mAudioCalibrationButtonStatus = ButtonStatus.Stopped;
    }

}
