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
import android.view.MenuItem;

import io.ordunaleon.publicappshub.fragment.AppDetailFragment;
import io.ordunaleon.publicappshub.model.App;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_APP = "extra_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        App app = getIntent().getExtras().getParcelable(EXTRA_APP);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Display "up" arrow in the ActionBar
            actionBar.setDisplayHomeAsUpEnabled(true);

            // Set Activity title according to the info displayed in it
            actionBar.setTitle(app.getName());
        }

        // Create AppDetailFragment according to the info displayed in it
        Bundle args = new Bundle();
        args.putParcelable(AppDetailFragment.ARGS_APP, app);

        AppDetailFragment fragment = new AppDetailFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.app_detail_container, fragment,
                        AppDetailFragment.APP_DETAIL_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
