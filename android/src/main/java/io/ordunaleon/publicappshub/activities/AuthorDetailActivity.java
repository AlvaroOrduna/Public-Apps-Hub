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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.model.Author;

public class AuthorDetailActivity extends AppCompatActivity implements GetCallback<Author> {

    private static final String LOG_TAG = "authorDetailActivity";

    public static final String EXTRA_OBJECT_ID = "extra_object_id";

    private static final String STATE_OBJECT_ID = "state_object_id";

    private String mObjectId;

    private ProgressBar mProgress;
    private TextView mError;
    private ScrollView mContent;
    private TextView mName;
    private TextView mLocation;
    private TextView mOrganization;
    private TextView mWeb;
    private ImageView mImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);

        // Lookup all the views
        mProgress = (ProgressBar) findViewById(R.id.author_detail_progress);
        mError = (TextView) findViewById(R.id.author_detail_error);
        mContent = (ScrollView) findViewById(R.id.author_detail_content);
        mName = (TextView) findViewById(R.id.author_detail_name);
        mLocation = (TextView) findViewById(R.id.author_detail_location);
        mOrganization = (TextView) findViewById(R.id.author_detail_organization);
        mWeb = (TextView) findViewById(R.id.author_detail_web);
        mImage = (ImageView) findViewById(R.id.author_detail_image);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_OBJECT_ID)) {
            mObjectId = extras.getString(EXTRA_OBJECT_ID);
        }

        // Show progress and hide content
        mProgress.setVisibility(View.VISIBLE);
        mError.setVisibility(View.GONE);
        mContent.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ParseQuery<Author> query = Author.getQuery();
        query.getInBackground(mObjectId, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_OBJECT_ID, mObjectId);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            mObjectId = savedInstanceState.getString(STATE_OBJECT_ID);
        }
    }

    @Override
    public void done(Author author, ParseException e) {
        if (e == null) {
            // Get data from object
            String name = author.getName();
            String location = author.getLocation();
            String organization = author.getOrganization();
            String web = author.getWeb();
            String imageUrl = author.getImageUrl();

            // Set activity title if necessary
            setTitle(name);

            // Fill views wih object data
            mName.setText(name);
            mLocation.setText(location);
            mOrganization.setText(organization);
            mWeb.setText(web);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.with(this).load(imageUrl).into(mImage);
            }

            mProgress.setVisibility(View.GONE);
            mError.setVisibility(View.GONE);
            mContent.setVisibility(View.VISIBLE);
        } else {
            Log.e(LOG_TAG, e.getMessage(), e);
            mError.setText(getString(R.string.download_error, e.getMessage()));

            mProgress.setVisibility(View.GONE);
            mError.setVisibility(View.VISIBLE);
            mContent.setVisibility(View.GONE);
        }
    }
}
