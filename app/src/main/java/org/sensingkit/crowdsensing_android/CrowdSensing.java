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

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.sensingkit.sensingkitlib.SKException;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrowdSensing extends ActionBarActivity {

    private static final String TAG = "CrowdSensing";

    private enum ButtonStatus {
        Started,
        Stopped
    }

    // UI Elements
    private TextView mStatus;
    private Button mAudioCalibrationButton;
    private Button mSensingButton;

    ButtonStatus mAudioCalibrationButtonStatus = ButtonStatus.Stopped;
    ButtonStatus mSensingButtonStatus = ButtonStatus.Stopped;

    // Sensing Session
    SensingSession mSensingSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowd_sensing);

        // Init a SensingSession
        mSensingSession = createSensingSession();

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

    private void startSensing() {

        try {
            mSensingSession.start();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

        mStatus.setText("Sensing");
        mSensingButton.setText("Stop Sensing");
        mSensingButtonStatus = ButtonStatus.Started;
    }

    private void stopSensing() {

        try {
            mSensingSession.stop();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

        mStatus.setText("Stopped");
        mSensingButton.setText("Start Sensing");
        mSensingButtonStatus = ButtonStatus.Stopped;
    }

    private void startCalibrating() {

        try {
            mSensingSession.calibrateStart();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

        mAudioCalibrationButton.setText("Stop Audio Calibration");
        mAudioCalibrationButtonStatus = ButtonStatus.Started;
    }

    private void stopCalibrating() {

        try {
            mSensingSession.calibrateStop();
        }
        catch (SKException ex) {
            ex.printStackTrace();
        }

        mAudioCalibrationButton.setText("Audio Calibration");
        mAudioCalibrationButtonStatus = ButtonStatus.Stopped;
    }

}
