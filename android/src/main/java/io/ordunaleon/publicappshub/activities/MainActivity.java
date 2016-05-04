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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.fragments.AppDetailFragment;
import io.ordunaleon.publicappshub.fragments.AppsListFragment;

public class MainActivity extends AppCompatActivity implements AppsListFragment.Callback {

    private static final String STATE_SELECTED_OBJECT_ID = "state_selected_object_id";

    private boolean mTwoPane;

    private String mSelectedObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The app detail container view will be present only in the large-screen layouts
        // (res/layout-sw600dp). If this view is present, then the activity should be
        // in two-pane mode.
        mTwoPane = findViewById(R.id.fragment_app_detail_container) != null;

        if (savedInstanceState != null) {
            onItemSelected(savedInstanceState.getString(STATE_SELECTED_OBJECT_ID));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SELECTED_OBJECT_ID, mSelectedObjectId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onItemSelected(savedInstanceState.getString(STATE_SELECTED_OBJECT_ID));
    }

    @Override
    public void onItemSelected(String objectId) {
        mSelectedObjectId = objectId;
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_app_detail_container,
                            AppDetailFragment.newInstance(mSelectedObjectId, false))
                    .commit();
        } else {
            // In one-pane mode, show the detail view in a new activity.
            Intent intent = new Intent(this, AppDetailActivity.class);
            intent.putExtra(AppDetailActivity.EXTRA_OBJECT_ID, mSelectedObjectId);
            startActivity(intent);
        }
    }
}
