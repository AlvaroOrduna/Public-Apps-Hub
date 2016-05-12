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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.adapter.AppDetailServicesListAdapter;
import io.ordunaleon.publicappshub.model.Code;

public class CodeDetailActivity extends AppCompatActivity implements GetCallback<Code> {

    private static final String LOG_TAG = "CodeDetailActivity";

    public static final String EXTRA_OBJECT_ID = "extra_object_id";

    private static final String STATE_OBJECT_ID = "state_object_id";

    private String mObjectId;

    private AppDetailServicesListAdapter mServiceListAdapter;

    private AppDetailServicesListAdapter.OnLoadHandler sServicesLoadHandler =
            new AppDetailServicesListAdapter.OnLoadHandler() {

                @Override
                public void onLoadStart() {
                    mServicesEmpty.setVisibility(View.GONE);
                    mServicesProgress.setVisibility(View.VISIBLE);
                    mServicesList.setVisibility(View.GONE);
                }

                @Override
                public void onLoadFinish() {
                    if (mServiceListAdapter == null || mServiceListAdapter.getItemCount() == 0) {
                        mServicesEmpty.setVisibility(View.VISIBLE);
                        mServicesProgress.setVisibility(View.GONE);
                        mServicesList.setVisibility(View.GONE);
                    } else {
                        mServicesEmpty.setVisibility(View.GONE);
                        mServicesProgress.setVisibility(View.GONE);
                        mServicesList.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onLoadError(ParseException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    mError.setText(getString(R.string.download_error, e.getMessage()));
                    mServicesEmpty.setText(getString(R.string.download_error, e.getMessage()));

                    mServicesEmpty.setVisibility(View.VISIBLE);
                    mServicesProgress.setVisibility(View.GONE);
                    mServicesList.setVisibility(View.GONE);
                }
            };

    private AppDetailServicesListAdapter.OnClickHandler sServicesClickHandler =
            new AppDetailServicesListAdapter.OnClickHandler() {

                @Override
                public void onClick(String objectId) {
                    Intent intent = new Intent(CodeDetailActivity.this, ServiceDetailActivity.class);
                    intent.putExtra(ServiceDetailActivity.EXTRA_OBJECT_ID, objectId);
                    startActivity(intent);
                }
            };

    private ProgressBar mProgress;
    private TextView mError;
    private ScrollView mContent;
    private TextView mName;
    private TextView mAuthor;
    private TextView mSource;
    private TextView mPlatforms;
    private RecyclerView mServicesList;
    private ProgressBar mServicesProgress;
    private TextView mServicesEmpty;
    private Button mServiceAddButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_detail);

        // Lookup all the views
        mProgress = (ProgressBar) findViewById(R.id.code_detail_progress);
        mError = (TextView) findViewById(R.id.code_detail_error);
        mContent = (ScrollView) findViewById(R.id.code_detail_content);
        mName = (TextView) findViewById(R.id.code_detail_name);
        mAuthor = (TextView) findViewById(R.id.code_detail_author);
        mSource = (TextView) findViewById(R.id.code_detail_source);
        mPlatforms = (TextView) findViewById(R.id.code_detail_platforms);
        mServicesList = (RecyclerView) findViewById(R.id.code_detail_services_recyclerview);
        mServicesProgress = (ProgressBar) findViewById(R.id.code_detail_services_progress);
        mServicesEmpty = (TextView) findViewById(R.id.code_detail_services_empty);
        mServiceAddButton = (Button) findViewById(R.id.code_detail_services_add_button);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_OBJECT_ID)) {
            mObjectId = extras.getString(EXTRA_OBJECT_ID);
        }

        // Instantiate service list adapter
        mServiceListAdapter = new AppDetailServicesListAdapter(null, mObjectId, sServicesLoadHandler, sServicesClickHandler);

        // Set services recycler view adapter and layout manager
        mServicesList.setAdapter(mServiceListAdapter);
        mServicesList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Set add service button listener
        mServiceAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CodeDetailActivity.this, AddServiceActivity.class);
                intent.putExtra(AddServiceActivity.EXTRA_CODE_ID, mObjectId);
                startActivity(intent);
            }
        });

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

        ParseQuery<Code> query = Code.getQuery();
        query.getInBackground(mObjectId, this);

        mServiceListAdapter.loadObjects();
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
    public void done(Code code, ParseException e) {
        if (e == null) {
            // Get data from object
            String name = code.getName();
            String author = code.getAuthor();
            String source = code.getSource();
            String platforms = code.getPlatforms();

            // Set activity title if necessary
            setTitle(name);

            // Fill views wih object data
            mName.setText(name);
            mAuthor.setText(author);
            mSource.setText(source);
            mPlatforms.setText(platforms);

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
