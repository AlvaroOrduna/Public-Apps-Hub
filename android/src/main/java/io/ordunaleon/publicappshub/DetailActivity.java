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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.ordunaleon.publicappshub.fragment.AppDetailFragment;

public class DetailActivity extends AppCompatActivity implements AppDetailFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Display "up" arrow in the ActionBar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle args = new Bundle();
        args.putParcelable(AppDetailFragment.ARGS_URI, getIntent().getData());
        args.putBoolean(AppDetailFragment.ARGS_UPDATE_TITLE, true);

        // Create AppDetailFragment according to the info displayed in it
        AppDetailFragment fragment = new AppDetailFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.app_detail_container, fragment)
                .commit();
    }

    @Override
    public void onImageSelected(Uri imageUri) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(ImageActivity.ARGS_URI, imageUri);
        startActivity(intent);
    }

    @Override
    public void onCodeSelected(Uri codeUri) {
        Intent intent = new Intent(this, CodeDetailActivity.class);
        intent.putExtra(CodeDetailActivity.ARGS_URI, codeUri);
        startActivity(intent);
    }

    @Override
    public void onServiceSelected(Uri serviceUri) {
//        Intent intent = new Intent(this, CodeDetailActivity.class);
//        intent.putExtra(CodeDetailActivity.ARGS_URI, serviceUri);
//        startActivity(intent);
        Log.d(getLocalClassName(), serviceUri.toString());
    }
}
