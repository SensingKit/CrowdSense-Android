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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.sensingkit.sensingkitlib.SensingKitLib;

public class CrowdSensing extends ActionBarActivity {

    private static final String TAG = "CrowdSensing";

    private TextView mStatus;
    private Button mStartSensing;
    private Button mStopSensing;

    SensingKitLib mSensingKitLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowd_sensing);

        try {
            mSensingKitLib = SensingKitLib.getSensingKitLib(this);
        } catch (Exception ex) {
            System.out.println("Exception...");
        }

        // get refs to the TextViews
        mStatus = (TextView)findViewById(R.id.status);

        // get ref to the buttons and add actions
        mStartSensing = (Button)findViewById(R.id.start_sensing);
        mStartSensing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mStatus.setText("Enabled");

                // Start sensing!!
                try {
                    mSensingKitLib.startSensing();
                }
                catch (Exception ex)
                {
                    Log.e(TAG, ex.getLocalizedMessage());
                }
            }
        });

        mStopSensing = (Button)findViewById(R.id.stop_sensing);
        mStopSensing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mStatus.setText("Disabled");

                // Stop sensing!!
                mSensingKitLib.stopSensing();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crowd_sensing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
