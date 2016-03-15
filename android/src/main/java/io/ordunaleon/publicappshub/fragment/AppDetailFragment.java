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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.AppEntry;


public class AppDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARGS_URI = "args_uri";
    public static final String ARGS_UPDATE_TITLE = "args_update_title";

    private static final int APP_DETAIL_LOADER = 0;

    private Uri mUri;
    private boolean mUpdateTitle;

    private TextView mNameText;
    private TextView mCategoryText;
    private TextView mDescriptionText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_detail, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARGS_URI)) {
            mUri = args.getParcelable(ARGS_URI);
            mUpdateTitle = args.getBoolean(ARGS_UPDATE_TITLE, false);
        }

        mNameText = (TextView) rootView.findViewById(R.id.app_detail_name);
        mCategoryText = (TextView) rootView.findViewById(R.id.app_detail_category);
        mDescriptionText = (TextView) rootView.findViewById(R.id.app_detail_description);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(APP_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(getActivity(), mUri, null, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // Get data form cursor
            String name = data.getString(data.getColumnIndex(AppEntry.COLUMN_APP_NAME));
            String category = data.getString(data.getColumnIndex(AppEntry.COLUMN_APP_CATEGORY));
            String description = data.getString(data.getColumnIndex(AppEntry.COLUMN_APP_DESCRIPTION));

            // Populate view with data obtained from cursor
            mNameText.setText(getString(R.string.app_detail_name_label, name));
            mCategoryText.setText(getString(R.string.app_detail_category_label, category));
            mDescriptionText.setText(getString(R.string.app_detail_description_label, description));

            // Update Activity title if required
            if (mUpdateTitle && name != null) {
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(name);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
