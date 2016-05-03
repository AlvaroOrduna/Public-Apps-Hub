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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.activities.AddAppActivity;
import io.ordunaleon.publicappshub.adapter.AppsListAdapter;

public class AppsListFragment extends Fragment implements AppsListAdapter.OnLoadHandler {

    private SwipeRefreshLayout mRefreshLayout;

    private AppsListAdapter mAppsListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps_list, container, false);

        // Lookup the SwipeRefreshLayout, RecyclerView and the FloatingActionButton
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.apps_list_refresh);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.apps_list_recyclerview);
        FloatingActionButton floatingActionButton =
                (FloatingActionButton) view.findViewById(R.id.apps_list_fab);

        if (mRefreshLayout != null) {
            // Set SwipeRefreshLayout colors
            mRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

            // Set SwipeRefreshLayout OnRefreshListener to load objects
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mAppsListAdapter.loadObjects();
                }
            });
        }

        // Instantiate Apps Adapter
        mAppsListAdapter = new AppsListAdapter(this, new AppsListAdapter.OnClickHandler() {
            @Override
            public void onClick(String appId) {
                ((Callback) getActivity()).onItemSelected(appId);
            }
        });

        if (recyclerView != null) {
            // Set the recyclerView adapter
            recyclerView.setAdapter(mAppsListAdapter);

            // Set RecyclerView's LayoutManager to organize the items
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        if (floatingActionButton != null) {
            // Set the FloatingActionButton listener
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddAppActivity.class);
                    startActivity(intent);
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load objects when fragment is visible to the user and actively running.
        mAppsListAdapter.loadObjects();
    }

    @Override
    public void onLoadStart() {
        // Workaround for SwipeRefreshLayout indicator does not appear when setRefreshing(true) is
        // called before SwipeRefreshLayout#onMeasure().
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onLoadFinish() {
        mRefreshLayout.setRefreshing(false);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * Called when a item has been selected.
         *
         * @param objectId The id of the selected object.
         */
        void onItemSelected(String objectId);
    }
}
