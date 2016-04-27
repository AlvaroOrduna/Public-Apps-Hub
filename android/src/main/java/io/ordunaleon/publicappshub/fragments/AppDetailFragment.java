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


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.activities.AppDetailActivity;
import io.ordunaleon.publicappshub.parse.ParseHelper;

public class AppDetailFragment extends Fragment implements ParseHelper.AppInterface,
        GetCallback<ParseObject> {

    private static final String LOG_TAG = "AppDetailFragment";

    private String mObjectId;

    private TextView mName;
    private TextView mCategory;
    private TextView mDescriptionText;

    public AppDetailFragment() {
    }

    public static AppDetailFragment newInstance(String objectId) {
        AppDetailFragment fragment = new AppDetailFragment();

        Bundle args = new Bundle();
        args.putString(AppDetailActivity.EXTRA_OBJECT_ID, objectId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_detail, container, false);

        Bundle extras = getArguments();
        if (extras != null && extras.containsKey(AppDetailActivity.EXTRA_OBJECT_ID)) {
            mObjectId = extras.getString(AppDetailActivity.EXTRA_OBJECT_ID);
        }

        mName = (TextView) view.findViewById(R.id.app_detail_name);
        mCategory = (TextView) view.findViewById(R.id.app_detail_category);
        mDescriptionText = (TextView) view.findViewById(R.id.app_detail_description);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS_NAME);
            query.getInBackground(mObjectId, this);
        }
    }

    @Override
    public void done(ParseObject object, ParseException e) {
        if (e == null) {
            // Get data from object
            String name = object.getString(KEY_NAME);
            String category = object.getString(KEY_CATEGORY);
            String descriptionText = object.getString(KEY_DESCRIPTION_TEXT);

            // Set activity title
            setTitle(name);

            // Fill views wih object data
            mName.setText(name);
            mCategory.setText(category);
            mDescriptionText.setText(descriptionText);
        } else {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    /**
     * Sets activity title.
     *
     * @param title New activity title.
     */
    private void setTitle(String title) {
        ((Callback) getActivity()).setActivityTitle(title);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of fragment
     * events or actions.
     */
    public interface Callback {
        /**
         * Sets activity title.
         *
         * @param title New activity title.
         */
        void setActivityTitle(String title);
    }
}
