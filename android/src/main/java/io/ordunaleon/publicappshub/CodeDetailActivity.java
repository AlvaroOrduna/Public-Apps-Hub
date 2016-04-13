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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.ordunaleon.publicappshub.fragment.AppDetailFragment;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.CodeEntry;

public class CodeDetailActivity extends AppCompatActivity {

    public static final String ARGS_URI = "args_uri";

    private Uri mCodeUri;
    private String mAppId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_detail);

        Bundle args = getIntent().getExtras();
        if (args != null && args.containsKey(AppDetailFragment.ARGS_URI)) {
            mCodeUri = args.getParcelable(AppDetailFragment.ARGS_URI);
        }

        // Get basic info views
        TextView nameText = (TextView) findViewById(R.id.code_detail_name);
        TextView authorText = (TextView) findViewById(R.id.code_detail_author);
        TextView sourceText = (TextView) findViewById(R.id.code_detail_source);
        TextView platformsText = (TextView) findViewById(R.id.code_detail_platforms);

        // Fill views with code implementation data
        Cursor cursor = getContentResolver().query(mCodeUri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Get app id from cursor
                mAppId = cursor.getString(cursor.getColumnIndex(CodeEntry.COLUMN_CODE_APP_KEY));

                // Get data form cursor
                String name = cursor.getString(cursor.getColumnIndex(CodeEntry.COLUMN_CODE_NAME));
                String author = cursor.getString(cursor.getColumnIndex(CodeEntry.COLUMN_CODE_AUTHOR));
                String source = cursor.getString(cursor.getColumnIndex(CodeEntry.COLUMN_CODE_SOURCE));
                String platforms = cursor.getString(cursor.getColumnIndex(CodeEntry.COLUMN_CODE_PLATFORMS));

                // Populate view with data obtained from cursor
                nameText.setText(name);
                authorText.setText(author);
                sourceText.setText(source);
                platformsText.setText(platforms);

                // Update Activity title
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(name);
                }
            }

            cursor.close();
        }

        // Get service add button
        Button serviceAddButton = (Button) findViewById(R.id.code_detail_service_add_button);
        if (serviceAddButton != null) {
            serviceAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CodeDetailActivity.this, AddServiceActivity.class);
                    intent.putExtra(AddServiceActivity.ARGS_CODE_ID, CodeEntry.getCodeIdFromUri(mCodeUri));
                    intent.putExtra(AddServiceActivity.ARGS_APP_ID, mAppId);
                    startActivity(intent);
                }
            });
        }
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
