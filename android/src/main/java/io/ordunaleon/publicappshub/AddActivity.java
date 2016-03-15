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
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {

    private static final int IMAGE_PICKER_REQUEST = 0;

    private static final String MIME_TYPE_IMAGE = "image/*";

    private ArrayList<Uri> mScreenshotArray;

    private ScrollView mScrollView;
    private TextView mScreenshotCountTextView;

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

        Button addScreenshotButton = (Button) findViewById(R.id.add_input_screenshot_add);
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
                // TODO: get and check new data
                finish();
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
}
