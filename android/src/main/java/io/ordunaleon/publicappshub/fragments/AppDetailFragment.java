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

package io.ordunaleon.publicappshub.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.activities.AddCodeActivity;
import io.ordunaleon.publicappshub.activities.AppDetailActivity;
import io.ordunaleon.publicappshub.activities.ScreenshotActivity;
import io.ordunaleon.publicappshub.adapter.AppDetailCodesListAdapter;
import io.ordunaleon.publicappshub.adapter.AppDetailScreenshotListAdapter;
import io.ordunaleon.publicappshub.model.App;

public class AppDetailFragment extends Fragment implements GetCallback<App> {

    private static final String LOG_TAG = "AppDetailFragment";

    private static final String EXTRA_OBJECT_ID = "extra_object_id";
    private static final String EXTRA_UPDATE_TITLE = "extra_update_title";

    private static final String STATE_OBJECT_ID = "state_object_id";
    private static final String STATE_UPDATE_TITLE = "state_update_title";

    private String mObjectId;
    private boolean mUpdateTitle;

    private AppDetailScreenshotListAdapter mScreenshotListAdapter;

    private AppDetailScreenshotListAdapter.OnLoadHandler sScreenshotLoadHandler =
            new AppDetailScreenshotListAdapter.OnLoadHandler() {

                @Override
                public void onLoadFinished() {
                    mScreenshotProgress.setVisibility(View.GONE);
                }
            };

    private AppDetailScreenshotListAdapter.OnClickHandler sScreenshotClickHandler =
            new AppDetailScreenshotListAdapter.OnClickHandler() {

                @Override
                public void onClick(String screenshotUrl) {
                    Intent intent = new Intent(getActivity(), ScreenshotActivity.class);
                    intent.putExtra(ScreenshotActivity.EXTRA_URL, screenshotUrl);
                    startActivity(intent);
                }
            };

    private AppDetailCodesListAdapter mCodesListAdapter;

    private AppDetailCodesListAdapter.OnLoadHandler sCodesLoadHandler =
            new AppDetailCodesListAdapter.OnLoadHandler() {

                @Override
                public void onLoadStart() {
                    mCodesEmpty.setVisibility(View.GONE);
                    mCodesProgress.setVisibility(View.VISIBLE);
                    mCodesList.setVisibility(View.GONE);
                }

                @Override
                public void onLoadFinish() {
                    if (mCodesListAdapter == null || mCodesListAdapter.getItemCount() == 0) {
                        mCodesEmpty.setVisibility(View.VISIBLE);
                        mCodesProgress.setVisibility(View.GONE);
                        mCodesList.setVisibility(View.GONE);
                    } else {
                        mCodesEmpty.setVisibility(View.GONE);
                        mCodesProgress.setVisibility(View.GONE);
                        mCodesList.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onLoadError(ParseException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    mError.setText(getString(R.string.download_error, e.getMessage()));
                    mCodesEmpty.setText(getString(R.string.download_error, e.getMessage()));

                    mCodesEmpty.setVisibility(View.VISIBLE);
                    mCodesProgress.setVisibility(View.GONE);
                    mCodesList.setVisibility(View.GONE);
                }
            };

    private AppDetailCodesListAdapter.OnClickHandler sCodesClickHandler =
            new AppDetailCodesListAdapter.OnClickHandler() {

                @Override
                public void onClick(String objectId) {
                    Toast.makeText(getActivity(), objectId, Toast.LENGTH_SHORT).show();
                }
            };

    private ProgressBar mProgress;
    private TextView mError;
    private ScrollView mContent;
    private TextView mName;
    private TextView mCategory;
    private TextView mDescriptionText;
    private TextView mScreenshotEmpty;
    private ProgressBar mScreenshotProgress;
    private RecyclerView mScreenshotList;
    private TextView mCodesEmpty;
    private ProgressBar mCodesProgress;
    private RecyclerView mCodesList;
    private Button mCodesAddButton;
    private TextView mServicesEmpty;
    private ProgressBar mServicesProgress;
    private RecyclerView mServicesList;
    private Button mServicesAddButton;

    public AppDetailFragment() {
    }

    public static AppDetailFragment newInstance(String objectId, boolean updateTitle) {
        AppDetailFragment fragment = new AppDetailFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_OBJECT_ID, objectId);
        args.putBoolean(EXTRA_UPDATE_TITLE, updateTitle);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_detail, container, false);

        // Lookup all the views
        mProgress = (ProgressBar) view.findViewById(R.id.app_detail_progress);
        mError = (TextView) view.findViewById(R.id.app_detail_error);
        mContent = (ScrollView) view.findViewById(R.id.app_detail_content);
        mName = (TextView) view.findViewById(R.id.app_detail_name);
        mCategory = (TextView) view.findViewById(R.id.app_detail_category);
        mDescriptionText = (TextView) view.findViewById(R.id.app_detail_description);
        mScreenshotEmpty = (TextView) view.findViewById(R.id.app_detail_screenshot_empty);
        mScreenshotProgress = (ProgressBar) view.findViewById(R.id.app_detail_screenshot_progress);
        mScreenshotList = (RecyclerView) view.findViewById(R.id.app_detail_screenshot_recyclerview);
        mCodesEmpty = (TextView) view.findViewById(R.id.app_detail_codes_empty);
        mCodesProgress = (ProgressBar) view.findViewById(R.id.app_detail_codes_progress);
        mCodesList = (RecyclerView) view.findViewById(R.id.app_detail_codes_recyclerview);
        mCodesAddButton = (Button) view.findViewById(R.id.app_detail_codes_add_button);
        mServicesEmpty = (TextView) view.findViewById(R.id.app_detail_services_empty);
        mServicesProgress = (ProgressBar) view.findViewById(R.id.app_detail_services_progress);
        mServicesList = (RecyclerView) view.findViewById(R.id.app_detail_services_recyclerview);
        mServicesAddButton = (Button) view.findViewById(R.id.app_detail_services_add_button);

        Bundle extras = getArguments();
        if (extras != null && extras.containsKey(AppDetailActivity.EXTRA_OBJECT_ID)) {
            mObjectId = extras.getString(EXTRA_OBJECT_ID);
            mUpdateTitle = extras.getBoolean(EXTRA_UPDATE_TITLE);
        }

        // Instantiate screenshot list adapter
        mScreenshotListAdapter = new AppDetailScreenshotListAdapter(getActivity(),
                new ArrayList<String>(), sScreenshotLoadHandler, sScreenshotClickHandler);

        // Set screenshot recycler view adapter and layout manager
        mScreenshotList.setAdapter(mScreenshotListAdapter);
        mScreenshotList.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Set add code button listener
        mCodesAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCodeActivity.class);
                intent.putExtra(AddCodeActivity.EXTRA_OBJECT_ID, mObjectId);
                startActivity(intent);
            }
        });

        // Instantiate codes list adapter
        mCodesListAdapter = new AppDetailCodesListAdapter(sCodesLoadHandler, sCodesClickHandler);

        // Set codes recycler view adapter and layout manager
        mCodesList.setAdapter(mCodesListAdapter);
        mCodesList.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Set add service button listener
        mServicesAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Service add button was clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Show progress and hide content
        mProgress.setVisibility(View.VISIBLE);
        mError.setVisibility(View.GONE);
        mContent.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load objects when fragment is visible to the user and actively running.
        mCodesListAdapter.loadObjects();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mObjectId = savedInstanceState.getString(STATE_OBJECT_ID);
            mUpdateTitle = savedInstanceState.getBoolean(STATE_UPDATE_TITLE);
        }

        ParseQuery<App> query = App.getQuery();
        query.getInBackground(mObjectId, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_OBJECT_ID, mObjectId);
        outState.putBoolean(STATE_UPDATE_TITLE, mUpdateTitle);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            mObjectId = savedInstanceState.getString(STATE_OBJECT_ID);
            mUpdateTitle = savedInstanceState.getBoolean(STATE_UPDATE_TITLE);
        }
    }

    @Override
    public void done(App app, ParseException e) {
        if (getActivity() == null) {
            // Fragment has been detached from activity so don't even try to update UI
            return;
        }

        if (e == null) {
            // Get data from object
            String name = app.getName();
            String category = app.getCategory();
            String descriptionText = app.getDescriptionText();
            JSONArray descriptionVisual = app.getDescriptionVisual();

            // Set activity title if necessary
            if (mUpdateTitle) {
                setTitle(name);
            }

            // Fill views wih object data
            mName.setText(name);
            mCategory.setText(category);
            mDescriptionText.setText(descriptionText);

            // Fill visual description
            if (descriptionVisual == null || descriptionVisual.length() == 0) {
                mScreenshotEmpty.setVisibility(View.VISIBLE);
                mScreenshotProgress.setVisibility(View.GONE);
                mScreenshotList.setVisibility(View.GONE);
            } else {
                mScreenshotEmpty.setVisibility(View.GONE);
                mScreenshotProgress.setVisibility(View.VISIBLE);
                mScreenshotList.setVisibility(View.VISIBLE);
                for (int i = 0; i < descriptionVisual.length(); i++) {
                    try {
                        mScreenshotListAdapter.add(descriptionVisual.getString(i));
                    } catch (JSONException eJSON) {
                        Log.e(LOG_TAG, eJSON.getMessage(), eJSON);
                        mError.setText(getString(R.string.download_error, eJSON.getMessage()));

                        mProgress.setVisibility(View.GONE);
                        mError.setVisibility(View.VISIBLE);
                        mContent.setVisibility(View.GONE);
                    }
                }
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

    /**
     * Sets activity title.
     *
     * @param title New activity title.
     */
    private void setTitle(String title) {
        ((Callback) getActivity()).onTitleUpdate(title);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of fragment
     * events or actions.
     */
    public interface Callback {
        /**
         * Called when the activity title have been updated.
         *
         * @param title New activity title.
         */
        void onTitleUpdate(String title);
    }
}
