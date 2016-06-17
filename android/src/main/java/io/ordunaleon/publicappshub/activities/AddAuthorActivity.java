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

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.parse.ParseException;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.model.Author;

public class AddAuthorActivity extends AppCompatActivity implements View.OnFocusChangeListener,
        Author.StoreCallback {

    public static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    private static final int IMAGE_PICKER_REQUEST = 0;
    private static final String MIME_TYPE_IMAGE = "image/*";

    private static final String LOG_TAG = "AddAuthorActivity";

    public static final String EXTRA_OBJECT_ID = "extra_object_id";

    private String mObjectId;

    private ScrollView mScrollView;
    private EditText mNameEditText;
    private EditText mLocalEditText;
    private EditText mOrganizationEditText;
    private EditText mWebEditText;
    private Button mImageButton;
    private FloatingActionButton mDoneButton;

    private ProgressDialog mProgressDialog;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_author);

        // Lookup all the views
        mScrollView = (ScrollView) findViewById(R.id.add_author_scrollview);
        mNameEditText = (EditText) findViewById(R.id.add_author_name);
        mLocalEditText = (EditText) findViewById(R.id.add_author_local);
        mOrganizationEditText = (EditText) findViewById(R.id.add_author_organization);
        mWebEditText = (EditText) findViewById(R.id.add_author_web);
        mImageButton = (Button) findViewById(R.id.add_author_add_image);
        mDoneButton = (FloatingActionButton) findViewById(R.id.add_author_fab);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_OBJECT_ID)) {
            mObjectId = extras.getString(EXTRA_OBJECT_ID);
        }

        // Set text fields listeners
        if (mNameEditText != null) {
            mNameEditText.setOnFocusChangeListener(this);
        }
        if (mLocalEditText != null) {
            mLocalEditText.setOnFocusChangeListener(this);
        }
        if (mOrganizationEditText != null) {
            mOrganizationEditText.setOnFocusChangeListener(this);
        }
        if (mWebEditText != null) {
            mWebEditText.setOnFocusChangeListener(this);
        }

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int readPermission = ActivityCompat.checkSelfPermission(AddAuthorActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);

                if (readPermission == PackageManager.PERMISSION_GRANTED) {
                    showImagePicker();
                } else {
                    ActivityCompat.requestPermissions(AddAuthorActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_EXTERNAL_STORAGE);
                }
            }
        });

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

        // Create ProgressDialog to show new author store progress
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.add_author_progress_dialog));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Show activity to pick an image.
                showImagePicker();
            } else {
                // Permission has been denied. Inform the user with a SnackBar.
                Snackbar.make(mScrollView, R.string.add_app_permission_denied, Snackbar.LENGTH_LONG)
                        .show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MIME_TYPE_IMAGE);
        startActivityForResult(intent, IMAGE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.add_author_name:
                if (!hasFocus) isNameValid(false);
                break;
            case R.id.add_author_local:
                if (!hasFocus) isLocalValid(false);
                break;
            case R.id.add_author_organization:
                if (!hasFocus) isOrganizationValid(false);
                break;
            case R.id.add_author_web:
                if (!hasFocus) isWebValid(false);
                break;
        }
    }

    /**
     * Check all necessary fields when submitting new author implementation
     *
     * @return Boolean indicating whether the data is valid
     */
    private boolean isFormValid() {
        return isNameValid(true) && isLocalValid(true) && isOrganizationValid(true) && isWebValid(true);
    }

    private boolean isNameValid(boolean showSnackBar) {
        String name = mNameEditText.getText().toString();
        if (!name.isEmpty()) {
            return true;
        }

        if (showSnackBar) {
            Snackbar.make(mScrollView, R.string.add_author_name_error_empty, Snackbar.LENGTH_LONG)
                    .show();
        }

        mNameEditText.setError(getString(R.string.add_author_name_error_empty));
        return false;
    }

    private boolean isLocalValid(boolean showSnackBar) {
        String author = mLocalEditText.getText().toString();
        if (!author.isEmpty()) {
            return true;
        }

        if (showSnackBar) {
            Snackbar.make(mScrollView, R.string.add_author_local_error_empty, Snackbar.LENGTH_LONG)
                    .show();
        }

        mLocalEditText.setError(getString(R.string.add_author_local_error_empty));
        return false;
    }

    private boolean isOrganizationValid(boolean showSnackBar) {
        String source = mOrganizationEditText.getText().toString();
        if (!source.isEmpty()) {
            return true;
        }

        if (showSnackBar) {
            Snackbar.make(mScrollView, R.string.add_author_organization_error_empty, Snackbar.LENGTH_LONG)
                    .show();
        }

        mOrganizationEditText.setError(getString(R.string.add_author_organization_error_empty));
        return false;
    }

    private boolean isWebValid(boolean showSnackBar) {
        String source = mWebEditText.getText().toString();
        if (!source.isEmpty()) {
            return true;
        }

        if (showSnackBar) {
            Snackbar.make(mScrollView, R.string.add_author_web_error_empty, Snackbar.LENGTH_LONG)
                    .show();
        }

        mWebEditText.setError(getString(R.string.add_author_web_error_empty));
        return false;
    }

    /**
     * Stores new data in the database
     */
    private void storeNewData() {
        // Get name
        String name = mNameEditText.getText().toString();

        // Get location
        String location = mLocalEditText.getText().toString();

        // Get source
        String organization = mOrganizationEditText.getText().toString();

        // Get source
        String web = mWebEditText.getText().toString();

        // Instantiate new author
        Author author = new Author(this, mObjectId, name, location, organization, web, mImageUri);

        // Store author data
        mProgressDialog.show();
        author.store(this);
    }

    @Override
    public void onStoreFinish() {
        mProgressDialog.dismiss();
        onBackPressed();
    }

    @Override
    public void onStoreProgress(Integer donePercentage) {
        mProgressDialog.setProgress(donePercentage);
    }

    @Override
    public void onStoreError(ParseException e) {
        Log.e(LOG_TAG, e.getMessage(), e);

        Snackbar.make(mScrollView, getString(R.string.add_author_upload_error, e.getMessage()), Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();

        mProgressDialog.dismiss();
        mDoneButton.setClickable(true);
    }
}
