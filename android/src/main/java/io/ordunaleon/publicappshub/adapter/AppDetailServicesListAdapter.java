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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.model.Service;
import io.ordunaleon.publicappshub.widget.ParseRecyclerQueryAdapter;

public class AppDetailServicesListAdapter extends ParseRecyclerQueryAdapter<Service, AppDetailServicesListAdapter.ViewHolder> {

    private final OnClickHandler mClickHandler;

    public AppDetailServicesListAdapter(final String codeId,
                                        OnLoadHandler loadHandler, OnClickHandler clickHandler) {
        super(new ParseQueryAdapter.QueryFactory<Service>() {
            @Override
            public ParseQuery<Service> create() {
                ParseQuery<Service> query = Service.getQuery();

                if (codeId != null) {
                    query = query.whereEqualTo(Service.KEY_CODE_ID, codeId);
                }

                return query;
            }
        }, loadHandler);

        mClickHandler = clickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get current position object
        Service service = getItem(position);

        // Fill content views
        holder.url.setText(service.getUrl());
        holder.location.setText(service.getLocation());
    }

    /**
     * Interface definition for a callback to be invoked when a item is clicked.
     */
    public interface OnClickHandler {
        /**
         * Called when a item has been clicked.
         *
         * @param objectId The id of the object in the view.
         */
        void onClick(String objectId);
    }

    /**
     * Provide a direct reference to each of the views within a data item.
     * Used to cache the views within the item layout for fast access.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView url;
        public TextView location;

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            url = (TextView) view.findViewById(R.id.item_service_url);
            location = (TextView) view.findViewById(R.id.item_service_location);
        }

        @Override
        public void onClick(View v) {
            ParseObject item = getItem(getAdapterPosition());
            mClickHandler.onClick(item.getObjectId());
        }
    }
}
