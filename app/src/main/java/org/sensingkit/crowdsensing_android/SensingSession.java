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

import android.content.Context;
import android.os.Environment;

import org.sensingkit.sensingkitlib.SKException;
import org.sensingkit.sensingkitlib.SKExceptionErrorCode;
import org.sensingkit.sensingkitlib.SKSensorModuleType;
import org.sensingkit.sensingkitlib.SensingKitLib;
import org.sensingkit.sensingkitlib.SensingKitLibInterface;

import java.io.File;

public class SensingSession {

    @SuppressWarnings("unused")
    private static final String TAG = "SensingSession";

    // SensingKit
    private SensingKitLibInterface mSensingKitLib;
    private boolean isSensing = false;

    // Session Folder
    private File mSessionFolder;

    // Models
    private ModelWriter mAudioLevelModelWriter;
    private ModelWriter mAccelerometerModelWriter;
    private ModelWriter mGravityModelWriter;
    private ModelWriter mLinearAccelerationModelWriter;
    private ModelWriter mGyroscopeModelWriter;
    private ModelWriter mRotationModelWriter;
    private ModelWriter mMagnetometerModelWriter;

    public SensingSession(final Context context, final String folderName) throws SKException {

        // Init SensingKit
        mSensingKitLib = SensingKitLib.getSensingKitLib(context);

        // Create the folder
        mSessionFolder = createFolder(folderName);

        // Init ModelWriters
        mAudioLevelModelWriter = new ModelWriter(SKSensorModuleType.AUDIO_LEVEL, mSessionFolder, "Audio");
        mAccelerometerModelWriter = new ModelWriter(SKSensorModuleType.ACCELEROMETER, mSessionFolder, "Accelerometer");
        mGravityModelWriter = new ModelWriter(SKSensorModuleType.GRAVITY, mSessionFolder, "Gravity");
        mLinearAccelerationModelWriter = new ModelWriter(SKSensorModuleType.LINEAR_ACCELERATION, mSessionFolder, "LinearAcceleration");
        mGyroscopeModelWriter = new ModelWriter(SKSensorModuleType.GYROSCOPE, mSessionFolder, "Gyroscope");
        mRotationModelWriter = new ModelWriter(SKSensorModuleType.ROTATION, mSessionFolder, "Rotation");
        mMagnetometerModelWriter = new ModelWriter(SKSensorModuleType.MAGNETOMETER, mSessionFolder, "Magnetometer");

        // Register Sensors
        mSensingKitLib.registerSensorModule(SKSensorModuleType.AUDIO_LEVEL);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.GRAVITY);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.LINEAR_ACCELERATION);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.GYROSCOPE);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.ROTATION);
        mSensingKitLib.registerSensorModule(SKSensorModuleType.MAGNETOMETER);

        // Subscribe ModelWriters
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.AUDIO_LEVEL, mAudioLevelModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.ACCELEROMETER, mAccelerometerModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.GRAVITY, mGravityModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.LINEAR_ACCELERATION, mLinearAccelerationModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.GYROSCOPE, mGyroscopeModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.ROTATION, mRotationModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SKSensorModuleType.MAGNETOMETER, mMagnetometerModelWriter);

    }

    public void start() throws SKException {

        this.isSensing = true;

        // Start
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.AUDIO_LEVEL);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.GRAVITY);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.LINEAR_ACCELERATION);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.GYROSCOPE);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.ROTATION);
        mSensingKitLib.startContinuousSensingWithSensor(SKSensorModuleType.MAGNETOMETER);
    }

    public void stop() throws SKException {

        this.isSensing = false;

        // Stop
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.AUDIO_LEVEL);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.GRAVITY);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.LINEAR_ACCELERATION);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.GYROSCOPE);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.ROTATION);
        mSensingKitLib.stopContinuousSensingWithSensor(SKSensorModuleType.MAGNETOMETER);

        // Flush
        mAudioLevelModelWriter.flush();
        mAccelerometerModelWriter.flush();
        mGravityModelWriter.flush();
        mLinearAccelerationModelWriter.flush();
        mGyroscopeModelWriter.flush();
        mRotationModelWriter.flush();
        mMagnetometerModelWriter.flush();
    }

    public void close() throws SKException {

        // Unsubscribe ModelWriters
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.AUDIO_LEVEL, mAudioLevelModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.ACCELEROMETER, mAccelerometerModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.GRAVITY, mGravityModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.LINEAR_ACCELERATION, mLinearAccelerationModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.GYROSCOPE, mGyroscopeModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.ROTATION, mRotationModelWriter);
        mSensingKitLib.unsubscribeSensorDataListener(SKSensorModuleType.MAGNETOMETER, mMagnetometerModelWriter);

        // Deregister Sensors
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.AUDIO_LEVEL);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.ACCELEROMETER);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.GRAVITY);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.LINEAR_ACCELERATION);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.GYROSCOPE);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.ROTATION);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.MAGNETOMETER);
        mSensingKitLib.deregisterSensorModule(SKSensorModuleType.BLUETOOTH);

        // Close
        mAudioLevelModelWriter.close();
        mAccelerometerModelWriter.close();
        mGravityModelWriter.close();
        mLinearAccelerationModelWriter.close();
        mGyroscopeModelWriter.close();
        mRotationModelWriter.close();
        mMagnetometerModelWriter.close();
    }

    public boolean isSensing() {
        return this.isSensing;
    }

    private File createFolder(final String folderName) throws SKException {

        // Create App folder: CrowdSensing
        File appFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CrowdSensing/");

        if (!appFolder.exists()) {
            if (!appFolder.mkdir()) {
                throw new SKException(TAG, "Folder could not be created.", SKExceptionErrorCode.UNKNOWN_ERROR);
            }
        }

        // Create session folder
        File folder = new File(appFolder, folderName);

        if (!folder.exists()) {
            if (!folder.mkdir()) {
                throw new SKException(TAG, "Folder could not be created.", SKExceptionErrorCode.UNKNOWN_ERROR);
            }
        }

        return folder;
    }

}
