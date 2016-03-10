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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import io.ordunaleon.publicappshub.adapter.AppAdapter;
import io.ordunaleon.publicappshub.model.App;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Lookup the RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.apps_recyclerview);
        recyclerView.setHasFixedSize(true);

        // Initialize app list
        List<App> appArrayList = App.createNewList(this, 8);

        // Create adapter passing in the dummy data
        AppAdapter adapter = new AppAdapter(appArrayList);

        // Attach the adapter to the RecyclerView to populate items
        recyclerView.setAdapter(adapter);

        // Set LayoutManager to organize the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lookup the FloatingActionButton and set i the listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.apps_fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.apps_fab) {
            Toast.makeText(this, "FloatingActionButton was pressed", Toast.LENGTH_SHORT).show();
        }
    }
}
