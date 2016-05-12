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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;

import com.parse.ParseException;

import java.util.ArrayList;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.model.Code;

public class AddCodeActivity extends AppCompatActivity implements View.OnFocusChangeListener,
        Code.StoreCallback {

    private static final String LOG_TAG = "AddCodeActivity";

    public static final String EXTRA_OBJECT_ID = "extra_object_id";

    private String mObjectId;

    private ScrollView mScrollView;
    private EditText mNameEditText;
    private EditText mAuthorEditText;
    private EditText mSourceEditText;
    private CheckBox mWebCheckbox;
    private CheckBox mAndroidCheckbox;
    private CheckBox mIosCheckbox;
    private FloatingActionButton mDoneButton;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_code);

        // Lookup all the views
        mScrollView = (ScrollView) findViewById(R.id.add_code_scrollview);
        mNameEditText = (EditText) findViewById(R.id.add_code_name);
        mAuthorEditText = (EditText) findViewById(R.id.add_code_author);
        mSourceEditText = (EditText) findViewById(R.id.add_code_source);
        mWebCheckbox = (CheckBox) findViewById(R.id.add_code_platform_web);
        mAndroidCheckbox = (CheckBox) findViewById(R.id.add_code_platform_android);
        mIosCheckbox = (CheckBox) findViewById(R.id.add_code_platform_ios);
        mDoneButton = (FloatingActionButton) findViewById(R.id.add_code_fab);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_OBJECT_ID)) {
            mObjectId = extras.getString(EXTRA_OBJECT_ID);
        }

        // Set text fields listeners
        if (mNameEditText != null) {
            mNameEditText.setOnFocusChangeListener(this);
        }
        if (mAuthorEditText != null) {
            mAuthorEditText.setOnFocusChangeListener(this);
        }
        if (mSourceEditText != null) {
            mSourceEditText.setOnFocusChangeListener(this);
        }

        // Set done button listener
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    mDoneButton.setClickable(false);
                    storeNewData();
                }
            }
        });

        // Create ProgressDialog to show new code store progress
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.add_code_progress_dialog));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
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
            case R.id.add_code_name:
                if (!hasFocus) isNameValid(false);
                break;
            case R.id.add_code_author:
                if (!hasFocus) isAuthorValid(false);
                break;
            case R.id.add_code_source:
                if (!hasFocus) isSourceValid(false);
                break;
        }
    }

    /**
     * Check all necessary fields when submitting new code implementation
     *
     * @return Boolean indicating whether the data is valid
     */
    private boolean isFormValid() {
        return isNameValid(true) && isAuthorValid(true) && isSourceValid(true) && arePlatformsValid();
    }

    private boolean isNameValid(boolean showSnackBar) {
        String name = mNameEditText.getText().toString();
        if (!name.isEmpty()) {
            return true;
        }

        if (showSnackBar) {
            Snackbar.make(mScrollView, R.string.add_code_name_error_empty, Snackbar.LENGTH_LONG)
                    .show();
        }

        mNameEditText.setError(getString(R.string.add_code_name_error_empty));
        return false;
    }

    private boolean isAuthorValid(boolean showSnackBar) {
        String author = mAuthorEditText.getText().toString();
        if (!author.isEmpty()) {
            return true;
        }

        if (showSnackBar) {
            Snackbar.make(mScrollView, R.string.add_code_author_error_empty, Snackbar.LENGTH_LONG)
                    .show();
        }

        mAuthorEditText.setError(getString(R.string.add_code_author_error_empty));
        return false;
    }

    private boolean isSourceValid(boolean showSnackBar) {
        String source = mSourceEditText.getText().toString();
        if (!source.isEmpty()) {
            return true;
        }

        if (showSnackBar) {
            Snackbar.make(mScrollView, R.string.add_code_source_error_empty, Snackbar.LENGTH_LONG)
                    .show();
        }

        mSourceEditText.setError(getString(R.string.add_code_source_error_empty));
        return false;
    }

    private boolean arePlatformsValid() {
        if (mWebCheckbox.isChecked() || mAndroidCheckbox.isChecked() || mIosCheckbox.isChecked()) {
            return true;
        }

        Snackbar.make(mScrollView, R.string.add_code_platforms_error_empty, Snackbar.LENGTH_SHORT)
                .show();
        return false;
    }

    /**
     * Stores new data in the database
     */
    private void storeNewData() {
        // Get name
        String name = mNameEditText.getText().toString();

        // Get author
        String author = mAuthorEditText.getText().toString();

        // Get source
        String source = mSourceEditText.getText().toString();

        // Get platforms
        ArrayList<String> platformsArrayList = new ArrayList<>();
        if (mWebCheckbox.isChecked()) {
            platformsArrayList.add(getString(R.string.add_code_platform_web));
        }
        if (mAndroidCheckbox.isChecked()) {
            platformsArrayList.add(getString(R.string.add_code_platform_android));
        }
        if (mIosCheckbox.isChecked()) {
            platformsArrayList.add(getString(R.string.add_code_platform_ios));
        }
        String platforms = "";
        for (String platform : platformsArrayList) {
            platforms += platform + ", ";
        }
        platforms = platforms.substring(0, platforms.lastIndexOf(","));

        // Instantiate new code
        Code code = new Code(mObjectId, name, author, source, platforms);

        // Store code data
        mProgressDialog.show();
        code.store(this);
    }

    @Override
    public void onStoreFinish() {
        mProgressDialog.dismiss();
        onBackPressed();
    }

    @Override
    public void onStoreError(ParseException e) {
        Log.e(LOG_TAG, e.getMessage(), e);

        Snackbar.make(mScrollView, getString(R.string.add_code_upload_error, e.getMessage()), Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();

        mProgressDialog.dismiss();
        mDoneButton.setClickable(true);
    }
}
