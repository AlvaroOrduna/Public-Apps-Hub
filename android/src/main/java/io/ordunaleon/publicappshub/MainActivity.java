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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import io.ordunaleon.publicappshub.adapter.AppListAdapter;
import io.ordunaleon.publicappshub.fragment.AppDetailFragment;
import io.ordunaleon.publicappshub.fragment.AppListFragment;
import io.ordunaleon.publicappshub.model.App;

public class MainActivity extends AppCompatActivity implements AppListFragment.Callback {

    private AppListAdapter mAppListAdapter;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize app list
        List<App> appArrayList = App.createNewList(this, 8);

        // Create adapter passing in the dummy data
        mAppListAdapter = new AppListAdapter(this, appArrayList);

        if (findViewById(R.id.app_detail_container) != null) {
            // The app detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.app_detail_container, new AppDetailFragment(),
                                AppDetailFragment.APP_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onItemSelected(int position) {
        App app = mAppListAdapter.getItem(position);

        if (app != null) {
            if (mTwoPane) {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a
                // fragment transaction.
                Bundle args = new Bundle();
                args.putParcelable(AppDetailFragment.ARGS_APP, app);

                AppDetailFragment fragment = new AppDetailFragment();
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.app_detail_container, fragment,
                                AppDetailFragment.APP_DETAIL_FRAGMENT_TAG)
                        .commit();
            } else {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_APP, app);
                startActivity(intent);
            }
        }
    }

    public AppListAdapter getAppListAdapter() {
        return mAppListAdapter;
    }
}
