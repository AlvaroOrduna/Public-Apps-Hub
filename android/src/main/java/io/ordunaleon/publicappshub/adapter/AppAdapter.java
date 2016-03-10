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

package io.ordunaleon.publicappshub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.model.App;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    // Store a member variable for the apps
    private List<App> mApps;

    public AppAdapter(List<App> apps) {
        mApps = apps;
    }

    @Override
    public AppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_app, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(AppAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        App contact = mApps.get(position);

        // Set item views based on the data model
        viewHolder.appName.setText(contact.getName());
        viewHolder.appDescription.setText(contact.getDescription());
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    /**
     * Provide a direct reference to each of the views within a data item.
     * Used to cache the views within the item layout for fast access.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView appName;
        public TextView appDescription;

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);

            appName = (TextView) view.findViewById(R.id.app_name);
            appDescription = (TextView) view.findViewById(R.id.app_description);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "onClick " + getAdapterPosition(), Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
