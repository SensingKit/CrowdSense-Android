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
import org.sensingkit.sensingkitlib.SensingKitLib;
import org.sensingkit.sensingkitlib.SensingKitLibInterface;
import org.sensingkit.sensingkitlib.modules.SensorModuleType;

import java.io.File;

public class SensingSession {

    @SuppressWarnings("unused")
    private static final String TAG = "SensingSession";

    // SensingKit
    private SensingKitLibInterface mSensingKitLib;

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
        mAudioLevelModelWriter = new ModelWriter(SensorModuleType.AUDIO_LEVEL, mSessionFolder, "Audio");
        mAccelerometerModelWriter = new ModelWriter(SensorModuleType.ACCELEROMETER, mSessionFolder, "Accelerometer");
        mGravityModelWriter = new ModelWriter(SensorModuleType.GRAVITY, mSessionFolder, "Gravity");
        mLinearAccelerationModelWriter = new ModelWriter(SensorModuleType.LINEAR_ACCELERATION, mSessionFolder, "LinearAcceleration");
        mGyroscopeModelWriter = new ModelWriter(SensorModuleType.GYROSCOPE, mSessionFolder, "Gyroscope");
        mRotationModelWriter = new ModelWriter(SensorModuleType.ROTATION, mSessionFolder, "Rotation");
        mMagnetometerModelWriter = new ModelWriter(SensorModuleType.MAGNETOMETER, mSessionFolder, "Magnetometer");

        // Register Sensors
        mSensingKitLib.registerSensorModule(SensorModuleType.AUDIO_LEVEL);
        mSensingKitLib.registerSensorModule(SensorModuleType.ACCELEROMETER);
        mSensingKitLib.registerSensorModule(SensorModuleType.GRAVITY);
        mSensingKitLib.registerSensorModule(SensorModuleType.LINEAR_ACCELERATION);
        mSensingKitLib.registerSensorModule(SensorModuleType.GYROSCOPE);
        mSensingKitLib.registerSensorModule(SensorModuleType.ROTATION);
        mSensingKitLib.registerSensorModule(SensorModuleType.MAGNETOMETER);

        // Subscribe ModelWriter
        mSensingKitLib.subscribeSensorDataListener(SensorModuleType.AUDIO_LEVEL, mAudioLevelModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SensorModuleType.ACCELEROMETER, mAccelerometerModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SensorModuleType.GRAVITY, mGravityModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SensorModuleType.LINEAR_ACCELERATION, mLinearAccelerationModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SensorModuleType.GYROSCOPE, mGyroscopeModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SensorModuleType.ROTATION, mRotationModelWriter);
        mSensingKitLib.subscribeSensorDataListener(SensorModuleType.MAGNETOMETER, mMagnetometerModelWriter);

    }

    public void start() throws SKException {

        mSensingKitLib.startContinuousSensingWithSensor(SensorModuleType.AUDIO_LEVEL);
        mSensingKitLib.startContinuousSensingWithSensor(SensorModuleType.ACCELEROMETER);
        mSensingKitLib.startContinuousSensingWithSensor(SensorModuleType.GRAVITY);
        mSensingKitLib.startContinuousSensingWithSensor(SensorModuleType.LINEAR_ACCELERATION);
        mSensingKitLib.startContinuousSensingWithSensor(SensorModuleType.GYROSCOPE);
        mSensingKitLib.startContinuousSensingWithSensor(SensorModuleType.ROTATION);
        mSensingKitLib.startContinuousSensingWithSensor(SensorModuleType.MAGNETOMETER);
    }

    public void stop() throws SKException {

        mSensingKitLib.stopContinuousSensingWithSensor(SensorModuleType.AUDIO_LEVEL);
        mSensingKitLib.stopContinuousSensingWithSensor(SensorModuleType.ACCELEROMETER);
        mSensingKitLib.stopContinuousSensingWithSensor(SensorModuleType.GRAVITY);
        mSensingKitLib.stopContinuousSensingWithSensor(SensorModuleType.LINEAR_ACCELERATION);
        mSensingKitLib.stopContinuousSensingWithSensor(SensorModuleType.GYROSCOPE);
        mSensingKitLib.stopContinuousSensingWithSensor(SensorModuleType.ROTATION);
        mSensingKitLib.stopContinuousSensingWithSensor(SensorModuleType.MAGNETOMETER);

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

        // Flush and Close (safer than just closing them)
        mAudioLevelModelWriter.flush();
        mAccelerometerModelWriter.flush();
        mGravityModelWriter.flush();
        mLinearAccelerationModelWriter.flush();
        mGyroscopeModelWriter.flush();
        mRotationModelWriter.flush();
        mMagnetometerModelWriter.flush();

        mAudioLevelModelWriter.close();
        mAccelerometerModelWriter.close();
        mGravityModelWriter.close();
        mLinearAccelerationModelWriter.close();
        mGyroscopeModelWriter.close();
        mRotationModelWriter.close();
        mMagnetometerModelWriter.close();

    }

    private File createFolder(final String folderName) throws SKException {

        // Create App folder
        File appFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CrowdSensing/");

        if (!appFolder.exists()) {
            if (!appFolder.mkdir()) {
                throw new SKException(TAG, "Folder could not be created.", SKExceptionErrorCode.UNKNOWN_ERROR);
            }
        }

        // Create current folder
        File folder = new File(appFolder, folderName);

        if (!folder.exists()) {
            if (!folder.mkdir()) {
                throw new SKException(TAG, "Folder could not be created.", SKExceptionErrorCode.UNKNOWN_ERROR);
            }
        }

        return folder;
    }

}
