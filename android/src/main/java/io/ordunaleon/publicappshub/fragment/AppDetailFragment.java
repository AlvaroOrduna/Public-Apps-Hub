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

package io.ordunaleon.publicappshub.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.adapter.ImageListAdapter;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.AppEntry;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.ImageEntry;


public class AppDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARGS_URI = "args_uri";
    public static final String ARGS_UPDATE_TITLE = "args_update_title";

    private static final int APP_IMAGES_LOADER = 0;

    private Uri mUri;
    private boolean mUpdateTitle;

    private TextView mNameText;
    private TextView mCategoryText;
    private TextView mDescriptionText;
    private TextView mVisualDescriptionText;
    private TextView mCodeText;
    private TextView mServiceText;

    private Button mCodeAddButton;
    private Button mServiceAddButton;

    private RecyclerView mImagesRecyclerView;
    private RecyclerView mCodesRecyclerView;
    private RecyclerView mServicesRecyclerView;

    private ImageListAdapter mImageListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_detail, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARGS_URI)) {
            mUri = args.getParcelable(ARGS_URI);
            mUpdateTitle = args.getBoolean(ARGS_UPDATE_TITLE, false);
        } else {
            return rootView;
        }

        // Get basic info views
        mNameText = (TextView) rootView.findViewById(R.id.app_detail_name);
        mCategoryText = (TextView) rootView.findViewById(R.id.app_detail_category);
        mDescriptionText = (TextView) rootView.findViewById(R.id.app_detail_description);

        // Get visual description views
        mVisualDescriptionText = (TextView) rootView.findViewById(R.id.app_detail_visual_description);
        mImagesRecyclerView = (RecyclerView) rootView.findViewById(R.id.app_detail_images_recyclerview);
        configureVisualDescription();

        // Get code implementations views
        mCodeText = (TextView) rootView.findViewById(R.id.app_detail_code_text);
        mCodeAddButton = (Button) rootView.findViewById(R.id.app_detail_code_add_button);
        mCodesRecyclerView = (RecyclerView) rootView.findViewById(R.id.app_detail_code_recyclerview);
        configureCodeImplementations();

        // Get service deployment views
        mServiceText = (TextView) rootView.findViewById(R.id.app_detail_service_text);
        mServiceAddButton = (Button) rootView.findViewById(R.id.app_detail_service_add_button);
        mServicesRecyclerView = (RecyclerView) rootView.findViewById(R.id.app_detail_service_recyclerview);
        configureServiceDeployment();

        return rootView;
    }

    private void configureVisualDescription() {
        mImageListAdapter = new ImageListAdapter(getActivity(), null, new ImageListAdapter.OnClickHandler() {
            @Override
            public void onClick(Uri imageUri) {
                ((Callback) getActivity()).onImageSelected(imageUri);
            }
        });

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        mImagesRecyclerView.setLayoutManager(linearLayoutManager);
        mImagesRecyclerView.setAdapter(mImageListAdapter);

        // Fill views with app data
        Cursor cursor = getActivity().getContentResolver().query(mUri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Get data form cursor
                String name = cursor.getString(cursor.getColumnIndex(AppEntry.COLUMN_APP_NAME));
                String category = cursor.getString(cursor.getColumnIndex(AppEntry.COLUMN_APP_CATEGORY));
                String description = cursor.getString(cursor.getColumnIndex(AppEntry.COLUMN_APP_DESCRIPTION));

                // Populate view with data obtained from cursor
                mNameText.setText(name);
                mCategoryText.setText(category);
                mDescriptionText.setText(description);

                // Update Activity title if required
                if (mUpdateTitle && name != null) {
                    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(name);
                    }
                }
            }

            cursor.close();
        }

        // Init app's images loader
        getLoaderManager().initLoader(APP_IMAGES_LOADER, null, this);
    }

    private void configureCodeImplementations() {
        // TODO: set RecyclerView layout manager, set RecyclerView adapter and init loader
    }

    private void configureServiceDeployment() {
        // TODO: set RecyclerView layout manager, set RecyclerView adapter and init loader
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == APP_IMAGES_LOADER) {
            if (mUri != null) {
                return new CursorLoader(
                        getActivity(),
                        ImageEntry.CONTENT_URI,
                        null,
                        ImageEntry.COLUMN_IMAGE_APP_KEY + " = ?",
                        new String[]{AppEntry.getAppIdFromUri(mUri)},
                        null);
            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == APP_IMAGES_LOADER) {
            if (data != null && data.getCount() > 0) {
                mVisualDescriptionText.setVisibility(View.GONE);
                mImageListAdapter.swapCursor(data);
            } else {
                mVisualDescriptionText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == APP_IMAGES_LOADER) {
            mImageListAdapter.swapCursor(null);
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of image
     * selections.
     */
    public interface Callback {
        /**
         * AppDetailFragmentCallback for when an app has been selected.
         */
        void onImageSelected(Uri imageUri);
    }
}
