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

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.ordunaleon.publicappshub.AddActivity;
import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.adapter.AppListAdapter;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.AppEntry;

public class AppListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int APP_LIST_LOADER = 0;

    private AppListAdapter mAppListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_list, container, false);

        // Lookup the RecyclerView and the FloatingActionButton
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.apps_recyclerview);
        FloatingActionButton floatingActionButton =
                (FloatingActionButton) rootView.findViewById(R.id.apps_fab);

        // Create a new AppListAdapter
        mAppListAdapter = new AppListAdapter(getActivity(), null, new AppListAdapter.OnClickHandler() {
            @Override
            public void onClick(long appId) {
                ((Callback) getActivity()).onItemSelected(AppEntry.buildAppUri(appId));
            }
        });

        // Optimize RecyclerView setting fixed size
        recyclerView.setHasFixedSize(true);

        // Attach the adapter to the RecyclerView to populate items
        recyclerView.setAdapter(mAppListAdapter);

        // Set RecyclerView's LayoutManager to organize the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set the FloatingActionButton listener
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppListFragment.this.getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(APP_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AppEntry.CONTENT_URI,
                null,
                null,
                null,
                AppEntry.COLUMN_APP_CATEGORY + " ASC, " + AppEntry.COLUMN_APP_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAppListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAppListAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * AppDetailFragmentCallback for when an app has been selected.
         */
        void onItemSelected(Uri contentUri);
    }
}
