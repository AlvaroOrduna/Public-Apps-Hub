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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.ordunaleon.publicappshub.MainActivity;
import io.ordunaleon.publicappshub.R;

public class AppListFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * AppDetailFragmentCallback for when an app has been selected.
         */
        void onItemSelected(int position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_list, container, false);

        // Lookup the RecyclerView and the FloatingActionButton
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.apps_recyclerview);
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.apps_fab);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Optimize RecyclerView setting fixed size
        mRecyclerView.setHasFixedSize(true);

        // Attach the adapter to the RecyclerView to populate items
        mRecyclerView.setAdapter(((MainActivity) getActivity()).getAppListAdapter());

        // Set RecyclerView's LayoutManager to organize the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set the FloatingActionButton listener
        mFloatingActionButton.setOnClickListener(this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.apps_fab) {
            Toast.makeText(getActivity(), "FAB was pressed", Toast.LENGTH_SHORT).show();
        }
    }
}
