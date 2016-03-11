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

import io.ordunaleon.publicappshub.fragment.AppDetailFragment;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String title = getIntent().getExtras().getString(EXTRA_TITLE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Display "up" arrow in the ActionBar
            actionBar.setDisplayHomeAsUpEnabled(true);

            // Set Activity title according to the info displayed in it
            actionBar.setTitle(title);
        }

        // Set Fragment content according to the info displayed in it
        AppDetailFragment appDetailFragment = ((AppDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_app_detail));
        appDetailFragment.setText(title);
    }
}
