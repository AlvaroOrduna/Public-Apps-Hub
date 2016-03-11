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
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import io.ordunaleon.publicappshub.adapter.AppListAdapter;
import io.ordunaleon.publicappshub.fragment.AppListFragment;
import io.ordunaleon.publicappshub.model.App;

public class MainActivity extends AppCompatActivity implements AppListFragment.Callback {

    private AppListAdapter mAppListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize app list
        List<App> appArrayList = App.createNewList(this, 8);

        // Create adapter passing in the dummy data
        mAppListAdapter = new AppListAdapter(this, appArrayList);
    }

    @Override
    public void onItemSelected(int position) {
        App app = mAppListAdapter.getItem(position);
        String msg = "onClick " + position;
        if (app != null) {
            msg = app.getName();
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public AppListAdapter getAppListAdapter() {
        return mAppListAdapter;
    }
}
