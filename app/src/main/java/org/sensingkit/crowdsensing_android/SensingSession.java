/*
 * Copyright (c) 2014. Kleomenis Katevas
 * Kleomenis Katevas, hello@sensingkit.org
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

import org.sensingkit.sensingkitlib.SKException;
import org.sensingkit.sensingkitlib.SKExceptionErrorCode;
import org.sensingkit.sensingkitlib.SKSensorType;
import org.sensingkit.sensingkitlib.SensingKitLib;
import org.sensingkit.sensingkitlib.SensingKitLibInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SensingSession {

    @SuppressWarnings("unused")
    private static final String TAG = "SensingSession";

    // SensingKit
    private SensingKitLibInterface mSensingKitLib;
    private boolean isSensing = false;

    // Session Folder
    private File mSessionFolder;

    private SKSensorType[] sensors;
    private List<ModelWriter> modelWriterList;

    public SensingSession(final Context context, final  SKSensorType[] sensors, final String folderName) throws SKException {

        this.sensors = sensors;
        this.modelWriterList = new ArrayList<>(sensors.length);

        // Init SensingKit
        mSensingKitLib = SensingKitLib.sharedSensingKitLib(context);

        // Create the folder
        mSessionFolder = createFolder(folderName, context);

        // Register Sensors
        for (SKSensorType sensor : sensors) {
            mSensingKitLib.registerSensor(sensor);
        }

        // Init and Subscribe modelWriters
        for (SKSensorType sensor : sensors) {
            ModelWriter modelWriter = new ModelWriter(sensor, mSessionFolder, sensor.getNonspacedName());
            mSensingKitLib.subscribeSensorDataHandler(SKSensorType.ACCELEROMETER, modelWriter);
            modelWriterList.add(modelWriter);
        }
    }

    public void start() throws SKException {

        this.isSensing = true;

        // Start
        mSensingKitLib.startContinuousSensingWithAllRegisteredSensors();
    }

    public void stop() throws SKException {

        this.isSensing = false;

        // Stop
        mSensingKitLib.stopContinuousSensingWithAllRegisteredSensors();

        // Flush
        for (ModelWriter modelWriter : modelWriterList) {
            modelWriter.flush();
        }
    }

    public void close() throws SKException {

        // Unsubscribe ModelWriters
        for (SKSensorType sensor : sensors) {
            mSensingKitLib.unsubscribeAllSensorDataHandlers(sensor);
        }

        // Deregister Sensors
        for (SKSensorType sensor : sensors) {
            mSensingKitLib.deregisterSensor(sensor);
        }

        // Close
        for (ModelWriter modelWriter : modelWriterList) {
            modelWriter.close();
        }
    }

    public boolean isSensing() {
        return this.isSensing;
    }

    private File createFolder(final String folderName, Context context) throws SKException {

        // Create session folder
        File folder = new File(context.getExternalFilesDir(null), folderName);
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                throw new SKException(TAG, "Session Folder could not be created.", SKExceptionErrorCode.UNKNOWN_ERROR);
            }
        }

        return folder;
    }
}
