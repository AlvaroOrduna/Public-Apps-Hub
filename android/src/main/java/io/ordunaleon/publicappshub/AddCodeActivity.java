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

import android.net.Uri;
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

import java.util.ArrayList;

import io.ordunaleon.publicappshub.fragment.AppDetailFragment;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract;

public class AddCodeActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private ScrollView mScrollView;

    private EditText mNameEditText;
    private EditText mAuthorEditText;
    private EditText mSourceEditText;

    private CheckBox mWebCheckbox;
    private CheckBox mAndroidCheckbox;
    private CheckBox mIosCheckbox;

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_code);

        Bundle args = getIntent().getExtras();
        if (args != null && args.containsKey(AppDetailFragment.ARGS_URI)) {
            mUri = args.getParcelable(AppDetailFragment.ARGS_URI);
        }

        // Lookup the ScrollView
        mScrollView = (ScrollView) findViewById(R.id.add_code_scrollview);

        // Lookup the name EditText and set the listener
        mNameEditText = (EditText) findViewById(R.id.add_code_name);
        if (mNameEditText != null) {
            mNameEditText.setOnFocusChangeListener(this);
        }

        // Lookup the author EditText and set the listener
        mAuthorEditText = (EditText) findViewById(R.id.add_code_author);
        if (mAuthorEditText != null) {
            mAuthorEditText.setOnFocusChangeListener(this);
        }

        // Lookup the source EditText and set the listener
        mSourceEditText = (EditText) findViewById(R.id.add_code_source);
        if (mSourceEditText != null) {
            mSourceEditText.setOnFocusChangeListener(this);
        }

        // Lookup the platforms checkboxes
        mWebCheckbox = (CheckBox) findViewById(R.id.add_code_platform_web);
        mAndroidCheckbox = (CheckBox) findViewById(R.id.add_code_platform_android);
        mIosCheckbox = (CheckBox) findViewById(R.id.add_code_platform_ios);

        // Lookup the FloatingActionButton
        FloatingActionButton floatingActionButton =
                (FloatingActionButton) findViewById(R.id.add_code_fab);

        // Set the FloatingActionButton listener
        if (floatingActionButton != null) {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFormValid()) {
                        if (storeNewRecord()) {
                            onBackPressed();
                        } else {
                            Snackbar.make(mScrollView, R.string.add_code_unknown_error,
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
            case R.id.add_code_name:
                if (!hasFocus) isNameValid();
                break;
            case R.id.add_code_author:
                if (!hasFocus) isAuthorValid();
                break;
            case R.id.add_code_source:
                if (!hasFocus) isSourceValid();
                break;
        }
    }

    private boolean isNameValid() {
        String name = mNameEditText.getText().toString();
        if (!name.isEmpty()) {
            // TODO: check that there is no other record with the same name for the same app
            return true;
        }

        mNameEditText.setError(getString(R.string.add_code_name_error_empty));
        return false;
    }

    private boolean isAuthorValid() {
        String author = mAuthorEditText.getText().toString();
        if (!author.isEmpty()) {
            // TODO: check that there is no other record with the same author for the same app
            return true;
        }

        mAuthorEditText.setError(getString(R.string.add_code_author_error_empty));
        return false;
    }

    private boolean isSourceValid() {
        String source = mSourceEditText.getText().toString();
        if (!source.isEmpty()) {
            // TODO: check that there is no other record with the same source for the same app
            return true;
        }

        mSourceEditText.setError(getString(R.string.add_code_source_error_empty));
        return false;
    }

    /**
     * Check all necessary fields when submitting new code implementation
     *
     * @return Boolean indicating whether the data is valid
     */
    private boolean isFormValid() {
        return isNameValid() && isAuthorValid() && isSourceValid();
    }

    /**
     * Stores the new record in the database
     *
     * @return True if the record is added correctly, false in other case
     */
    private boolean storeNewRecord() {
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

        Log.d(getLocalClassName(), "app: " + PublicAppsHubContract.AppEntry.getAppIdFromUri(mUri));
        Log.d(getLocalClassName(), "name: " + name);
        Log.d(getLocalClassName(), "author: " + author);
        Log.d(getLocalClassName(), "source: " + source);
        Log.d(getLocalClassName(), "platforms: " + platforms);

        return true;
    }
}
