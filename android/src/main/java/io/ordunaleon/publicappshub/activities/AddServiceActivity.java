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

package io.ordunaleon.publicappshub.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.parse.ParseException;

import java.util.List;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.model.Code;
import io.ordunaleon.publicappshub.model.Service;

public class AddServiceActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private static final String LOG_TAG = "AddServiceActivity";

    public static final String EXTRA_APP_ID = "extra_app_id";
    public static final String EXTRA_CODE_ID = "extra_code_id";

    private String mAppId;
    private String mCodeId;

    private ScrollView mScrollView;
    private EditText mNameEditText;
    private Spinner mCodeSpinner;
    private EditText mManagementEditText;
    private Spinner mGeoCountry;
    private Spinner mGeoRegion;
    private FloatingActionButton mDoneButton;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        // Lookup all the views
        mScrollView = (ScrollView) findViewById(R.id.add_service_scrollview);
        mNameEditText = (EditText) findViewById(R.id.add_service_name);
        mCodeSpinner = (Spinner) findViewById(R.id.add_service_code);
        mManagementEditText = (EditText) findViewById(R.id.add_service_management);
        mGeoCountry = (Spinner) findViewById(R.id.add_service_geo_country);
        mGeoRegion = (Spinner) findViewById(R.id.add_service_geo_region);
        mDoneButton = (FloatingActionButton) findViewById(R.id.add_service_fab);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(EXTRA_APP_ID)) {
                mAppId = extras.getString(EXTRA_APP_ID);
            }
            if (extras.containsKey(EXTRA_CODE_ID)) {
                mCodeId = extras.getString(EXTRA_CODE_ID);
            }
        }

        // Set text fields listeners
        if (mNameEditText != null) {
            mNameEditText.setOnFocusChangeListener(this);
        }
        if (mManagementEditText != null) {
            mManagementEditText.setOnFocusChangeListener(this);
        }

        // Set goe data
        ArrayAdapter<String> countryAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.add_service_countries));
        mGeoCountry.setAdapter(countryAdapter);
        ArrayAdapter<String> regionAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.add_service_regions));
        mGeoRegion.setAdapter(regionAdapter);

        // Set done button listener
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    mDoneButton.setClickable(false);
                    try {
                        storeNewData();
                    } catch (ParseException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }
            }
        });

        // Create ProgressDialog to show new code store progress
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.add_service_progress_dialog));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);

        try {
            setCodeSpinnerOptions();
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private void setCodeSpinnerOptions() throws ParseException {
        if (mCodeId != null) {
            Code associatedCode = Code.getQuery().get(mCodeId);
            ArrayAdapter<Code> associatedCodeAdapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                            new Code[]{associatedCode});
            mCodeSpinner.setAdapter(associatedCodeAdapter);
        } else if (mAppId != null) {
            List<Code> associatedCodeList = Code.getQuery(mAppId).find();
            ArrayAdapter<Code> associatedCodeAdapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                            associatedCodeList);
            mCodeSpinner.setAdapter(associatedCodeAdapter);
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
                if (!hasFocus) isNameValid(false);
                break;
            case R.id.add_service_management:
                if (!hasFocus) isManagementValid(false);
                break;
        }
    }

    /**
     * Check all necessary fields when submitting new service deployment
     *
     * @return Boolean indicating whether the data is valid
     */
    private boolean isFormValid() {
        return isNameValid(true) && isManagementValid(true);
    }

    private boolean isNameValid(boolean showSnackBar) {
        String name = mNameEditText.getText().toString();
        if (!name.isEmpty()) {
            return true;
        }

        if (showSnackBar) {
            Snackbar.make(mScrollView, R.string.add_service_name_error_empty, Snackbar.LENGTH_LONG)
                    .show();
        }

        mNameEditText.setError(getString(R.string.add_service_name_error_empty));
        return false;
    }

    private boolean isManagementValid(boolean showSnackBar) {
        String source = mManagementEditText.getText().toString();
        if (!source.isEmpty()) {
            return true;
        }

        if (showSnackBar) {
            Snackbar.make(mScrollView, R.string.add_service_management_error_empty, Snackbar.LENGTH_LONG)
                    .show();
        }

        mManagementEditText.setError(getString(R.string.add_service_management_error_empty));
        return false;
    }

    /**
     * Stores new data in the database
     */
    private void storeNewData() throws ParseException {
        // Get name
        String name = mNameEditText.getText().toString();

        // Get management
        String management = mManagementEditText.getText().toString();

        // Get associated code id
        String codeId = ((Code) mCodeSpinner.getSelectedItem()).getObjectId();

        // Get country
        String country = (String) mGeoCountry.getSelectedItem();

        // Get region
        String region = (String) mGeoRegion.getSelectedItem();

        // Instantiate new code
        Service sercive = new Service(codeId, name, management, country, region);

        // Store code data
        mProgressDialog.show();
//        sercive.store(this);
    }
}
