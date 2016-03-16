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

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import io.ordunaleon.publicappshub.model.PublicAppsHubContract;

public class AddActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private static final int IMAGE_PICKER_REQUEST = 0;

    private static final String MIME_TYPE_IMAGE = "image/*";

    private ArrayList<Uri> mScreenshotArray;

    private ScrollView mScrollView;
    private TextView mScreenshotCountTextView;

    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private RadioGroup mCategoryRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Display "up" arrow in the ActionBar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mScreenshotArray = (ArrayList<Uri>) getLastCustomNonConfigurationInstance();
        if (mScreenshotArray == null) {
            mScreenshotArray = new ArrayList<>();
        }

        mScrollView = (ScrollView) findViewById(R.id.add_scrollview);

        mNameEditText = (EditText) mScrollView.findViewById(R.id.add_input_name);
        mNameEditText.setOnFocusChangeListener(this);

        mDescriptionEditText = (EditText) mScrollView.findViewById(R.id.add_input_description);
        mDescriptionEditText.setOnFocusChangeListener(this);

        mCategoryRadioGroup = (RadioGroup) mScrollView.findViewById(R.id.add_input_category_group);

        Button addScreenshotButton = (Button) mScrollView.findViewById(R.id.add_input_screenshot_add);
        addScreenshotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MIME_TYPE_IMAGE);
                startActivityForResult(intent, IMAGE_PICKER_REQUEST);
            }
        });

        mScreenshotCountTextView = (TextView) findViewById(R.id.add_input_screenshot_count);
        updateScreenshotCount();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mScreenshotArray;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (isFormValid()) {
                    if (storeNewRecord()) {
                        finish();
                    } else {
                        Snackbar.make(mScrollView, R.string.add_input_unknown_error,
                                Snackbar.LENGTH_SHORT).show();
                    }
                }
                return true;
            case R.id.action_cancel:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK) {
            addScreenshot(data.getData());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.add_input_name:
                if (!hasFocus) isNameValid();
                break;
            case R.id.add_input_description:
                if (!hasFocus) isDescriptionValid();
                break;
        }
    }

    /**
     * Add screenshot's Uri to the array and show a Snackbar
     *
     * @param uri Uri to be added to the array
     */

    private void addScreenshot(final Uri uri) {
        mScreenshotArray.add(uri);
        updateScreenshotCount();
        Snackbar.make(mScrollView, R.string.add_input_screenshot_added, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mScreenshotArray.remove(uri);
                        updateScreenshotCount();
                    }
                })
                .show();
    }

    private void updateScreenshotCount() {
        int count = mScreenshotArray.size();
        mScreenshotCountTextView.setText(getResources().getQuantityString(
                R.plurals.add_input_screenshot_count, count, count));
    }

    /**
     * Check all necessary fields when submitting new app data
     *
     * @return Boolean indicating whether the data is valid
     */
    private boolean isFormValid() {
        // Check name
        boolean isNameValid = isNameValid();

        // Check description
        boolean isDescriptionValid = isDescriptionValid();

        return isNameValid && isDescriptionValid;
    }

    private boolean isNameValid() {
        String name = mNameEditText.getText().toString();
        if (!name.isEmpty()) {
            Cursor cursor = getContentResolver()
                    .query(PublicAppsHubContract.AppEntry.CONTENT_URI,
                            new String[]{PublicAppsHubContract.AppEntry.COLUMN_APP_NAME},
                            PublicAppsHubContract.AppEntry.COLUMN_APP_NAME + " = ?",
                            new String[]{String.valueOf(name)},
                            null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    // A record with that name exists in database
                    mNameEditText.setError(getString(R.string.add_input_name_error_empty));
                    cursor.close();
                } else {
                    cursor.close();
                    return true;
                }
            }
        }

        mNameEditText.setError(getString(R.string.add_input_name_error_empty));
        return false;
    }

    private boolean isDescriptionValid() {
        String description = mDescriptionEditText.getText().toString();
        if (!description.isEmpty()) {
            return true;
        }

        mDescriptionEditText.setError(getString(R.string.add_input_description_error_empty));
        return false;
    }

    /**
     * Stores the new reocord in the database
     *
     * @return True if the record is added correctly, false in other case
     */
    private boolean storeNewRecord() {
        // Get app name
        String name = mNameEditText.getText().toString();

        // Get app description
        String description = mDescriptionEditText.getText().toString();

        // Get app category
        int buttonId = mCategoryRadioGroup.getCheckedRadioButtonId();
        String category = null;
        switch (buttonId) {
            case R.id.add_input_category_education:
                category = getString(R.string.add_input_category_education);
                break;
            case R.id.add_input_category_health:
                category = getString(R.string.add_input_category_health);
                break;
            case R.id.add_input_category_transportation:
                category = getString(R.string.add_input_category_transportation);
                break;
        }

        if (category == null) {
            return false;
        }

        Log.i(getLocalClassName(), "Name: " + name);
        Log.i(getLocalClassName(), "Description: " + description);
        Log.i(getLocalClassName(), "Category: " + category);
        for (Uri uri : mScreenshotArray) {
            Log.i(getLocalClassName(), "Screenshot: " + uri);
        }

        // TODO: add new record to database

        return true;
    }
}
