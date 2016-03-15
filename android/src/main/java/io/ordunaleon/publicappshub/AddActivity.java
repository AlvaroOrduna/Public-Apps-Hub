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

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> screenshotArray;

    private TextView screenshotCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Display "up" arrow in the ActionBar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        screenshotArray = (ArrayList<String>) getLastCustomNonConfigurationInstance();
        if (screenshotArray == null) {
            screenshotArray = new ArrayList<>();
        }

        Button addScreenshotButton = (Button) findViewById(R.id.add_input_screenshot_add);
        addScreenshotButton.setOnClickListener(this);

        screenshotCountTextView = (TextView) findViewById(R.id.add_input_screenshot_count);
        updateScreenshotCount();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return screenshotArray;
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
    public void onClick(View v) {
        if (v.getId() == R.id.add_input_screenshot_add) {
            screenshotArray.add("");
            updateScreenshotCount();
        }
    }

    private void updateScreenshotCount() {
        int count = screenshotArray.size();
        screenshotCountTextView.setText(getResources().getQuantityString(
                R.plurals.add_input_screenshot_count, count, count));
    }
}
