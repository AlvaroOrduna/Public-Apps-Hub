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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.fragments.AppDetailFragment;

public class AppDetailActivity extends AppCompatActivity implements AppDetailFragment.Callback {

    public static final String EXTRA_OBJECT_ID = "extra_object_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_OBJECT_ID)) {
            String objectId = extras.getString(EXTRA_OBJECT_ID);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_app_detail_container,
                            AppDetailFragment.newInstance(objectId))
                    .commit();
        }
    }

    @Override
    public void setActivityTitle(String title) {
        setTitle(title);
    }
}
