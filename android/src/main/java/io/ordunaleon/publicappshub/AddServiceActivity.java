/*
 * Copyright (C) 2016 Álvaro Orduna León
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.ordunaleon.publicappshub;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import io.ordunaleon.publicappshub.model.PublicAppsHubContract;

public class AddServiceActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    public static final String ARGS_CODE_ID = "arg_code_uri";
    public static final String ARGS_APP_ID = "arg_app_id";

    private ScrollView mScrollView;

    private EditText mNameEditText;
    private EditText mUrlEditText;
    private EditText mManagementEditText;

    private String mCodeId;
    private String mAppId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        // TODO: Add country and region spinner

        // Get extras
        mCodeId = getIntent().getStringExtra(ARGS_CODE_ID);
        mAppId = getIntent().getStringExtra(ARGS_APP_ID);

        // Lookup the ScrollView
        mScrollView = (ScrollView) findViewById(R.id.add_service_scrollview);

        // Lookup the name EditText and set the listener
        mNameEditText = (EditText) findViewById(R.id.add_service_name);
        if (mNameEditText != null) {
            mNameEditText.setOnFocusChangeListener(this);
        }

        // Lookup the url EditText and set the listener
        mUrlEditText = (EditText) findViewById(R.id.add_service_url);
        if (mUrlEditText != null) {
            mUrlEditText.setOnFocusChangeListener(this);
        }

        // Lookup the management EditText and set the listener
        mManagementEditText = (EditText) findViewById(R.id.add_service_management);
        if (mManagementEditText != null) {
            mManagementEditText.setOnFocusChangeListener(this);
        }

        // Lookup the FloatingActionButton
        FloatingActionButton floatingActionButton =
                (FloatingActionButton) findViewById(R.id.add_service_fab);

        // Set the FloatingActionButton listener
        if (floatingActionButton != null) {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFormValid()) {
                        if (storeNewRecord()) {
                            onBackPressed();
                        } else {
                            Snackbar.make(mScrollView, R.string.add_service_unknown_error,
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.add_service_name:
                if (!hasFocus) isNameValid();
                break;
            case R.id.add_service_url:
                if (!hasFocus) isUrlValid();
                break;
            case R.id.add_service_management:
                if (!hasFocus) isManagementValid();
                break;
        }
    }

    private boolean isNameValid() {
        String name = mNameEditText.getText().toString();
        if (!name.isEmpty()) {
            // TODO
            return true;
        }

        mNameEditText.setError(getString(R.string.add_service_name_error_empty));
        return false;
    }

    private boolean isUrlValid() {
        String url = mUrlEditText.getText().toString();
        if (!url.isEmpty()) {
            return true;
        }

        mUrlEditText.setError(getString(R.string.add_service_url_error_empty));
        return false;
    }

    private boolean isManagementValid() {
        String management = mManagementEditText.getText().toString();
        if (!management.isEmpty()) {
            return true;
        }

        mManagementEditText.setError(getString(R.string.add_service_management_error_empty));
        return false;
    }

    /**
     * Check all necessary fields when submitting new service deployment
     *
     * @return Boolean indicating whether the data is valid
     */
    private boolean isFormValid() {
        return isNameValid() && isUrlValid() && isManagementValid();
    }

    /**
     * Stores the new record in the database
     *
     * @return True if the record is added correctly, false in other case
     */
    private boolean storeNewRecord() {
        // Get name
        String name = mNameEditText.getText().toString();

        // Get url
        String url = mUrlEditText.getText().toString();

        // Get management
        String management = mManagementEditText.getText().toString();

        // Get country
        String country = "prueba"; // TODO: get country form spinner

        // Get region
        String region = "prueba"; // TODO: get region form spinner

        // Add new service deployment record to the database
        ContentValues record = new ContentValues();
        record.put(PublicAppsHubContract.ServiceEntry.COLUMN_SERVICE_APP_KEY, mAppId);
        record.put(PublicAppsHubContract.ServiceEntry.COLUMN_SERVICE_CODE_KEY, mCodeId);
        record.put(PublicAppsHubContract.ServiceEntry.COLUMN_SERVICE_NAME, name);
        record.put(PublicAppsHubContract.ServiceEntry.COLUMN_SERVICE_URL, url);
        record.put(PublicAppsHubContract.ServiceEntry.COLUMN_SERVICE_MANAGEMENT, management);
        record.put(PublicAppsHubContract.ServiceEntry.COLUMN_SERVICE_COUNTRY, country);
        record.put(PublicAppsHubContract.ServiceEntry.COLUMN_SERVICE_REGION, region);
        Uri serviceUri = getContentResolver().insert(PublicAppsHubContract.ServiceEntry.CONTENT_URI, record);

        Log.d(getLocalClassName(), "new record uri: " + serviceUri);
        Log.d(getLocalClassName(), "app: " + mAppId);
        Log.d(getLocalClassName(), "code: " + mCodeId);
        Log.d(getLocalClassName(), "name: " + name);
        Log.d(getLocalClassName(), "url: " + url);
        Log.d(getLocalClassName(), "management: " + management);
        Log.d(getLocalClassName(), "country: " + country);
        Log.d(getLocalClassName(), "region: " + region);

        return true;
    }
}
