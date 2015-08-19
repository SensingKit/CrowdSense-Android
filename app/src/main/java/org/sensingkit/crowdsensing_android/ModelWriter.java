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

import android.util.Log;

import org.sensingkit.sensingkitlib.SKException;
import org.sensingkit.sensingkitlib.SKExceptionErrorCode;
import org.sensingkit.sensingkitlib.SKSensorDataListener;
import org.sensingkit.sensingkitlib.SKSensorModuleType;
import org.sensingkit.sensingkitlib.data.SKSensorData;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ModelWriter implements SKSensorDataListener {

    @SuppressWarnings("unused")
    private static final String TAG = "ModelWriter";

    private final SKSensorModuleType moduleType;

    private File mFile;
    private BufferedOutputStream mFileBuffer;

    public ModelWriter(SKSensorModuleType moduleType, File sessionFolder, String filename) throws SKException {

        this.moduleType = moduleType;
        this.mFile = createFile(sessionFolder, filename);

        try {
            this.mFileBuffer = new BufferedOutputStream(new FileOutputStream(mFile));
        }
        catch (FileNotFoundException ex) {
            throw new SKException(TAG, "File could not be found.", SKExceptionErrorCode.UNKNOWN_ERROR);
        }

    }

    public void flush() throws SKException {

        try {
            mFileBuffer.flush();
        }
        catch (IOException ex) {
            throw new SKException(TAG, ex.getMessage(), SKExceptionErrorCode.UNKNOWN_ERROR);
        }
    }

    public void close() throws SKException {

        try {
            mFileBuffer.close();
        }
        catch (IOException ex) {
            throw new SKException(TAG, ex.getMessage(), SKExceptionErrorCode.UNKNOWN_ERROR);
        }
    }

    private File createFile(File sessionFolder, String filename) throws SKException {

        File file = new File(sessionFolder, filename + ".csv");

        try {
            if (!file.createNewFile()) {
                throw new SKException(TAG, "File could not be created.", SKExceptionErrorCode.UNKNOWN_ERROR);
            }
        }
        catch (IOException ex) {
            throw new SKException(TAG, ex.getMessage(), SKExceptionErrorCode.UNKNOWN_ERROR);
        }

        // Make file visible
        //MediaScannerConnection.scanFile(getBaseContext(), new String[]{file.getAbsolutePath()}, null, null);

        return file;
    }

    @Override
    public void onDataReceived(SKSensorModuleType moduleType, SKSensorData moduleData) {

        if (mFileBuffer != null) {

            // Build the data line
            String dataLine = moduleData.getDataInCSV() + "\n";

            // Write in the FileBuffer
            try {
                mFileBuffer.write(dataLine.getBytes());
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

}
