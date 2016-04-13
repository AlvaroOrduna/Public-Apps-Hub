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
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.ServiceEntry;

public class ServiceListAdapter extends CursorRecyclerViewAdapter<ServiceListAdapter.ViewHolder> {

    final private OnClickHandler mClickHandler;

    public ServiceListAdapter(Context context, Cursor cursor, OnClickHandler dh) {
        super(context, cursor);
        mClickHandler = dh;
    }

    @Override
    public ServiceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);

        // Return a new holder instance
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        // Get fields from cursor
        String url = cursor.getString(cursor.getColumnIndex(ServiceEntry.COLUMN_SERVICE_URL));
        String country = cursor.getString(cursor.getColumnIndex(ServiceEntry.COLUMN_SERVICE_COUNTRY));

        // Fill views with cursor data
        viewHolder.url.setText(url);
        viewHolder.country.setText(country);
    }

    public interface OnClickHandler {
        void onClick(Uri serviceUri);
    }

    /**
     * Provide a direct reference to each of the views within a data item.
     * Used to cache the views within the item layout for fast access.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView url;
        public TextView country;

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            url = (TextView) view.findViewById(R.id.item_service_url);
            country = (TextView) view.findViewById(R.id.item_service_country);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            getCursor().moveToPosition(adapterPosition);
            String serviceId = getCursor().getString(getCursor().getColumnIndex(ServiceEntry._ID));
            mClickHandler.onClick(ServiceEntry.buildServiceUri(Long.valueOf(serviceId)));
        }
    }
}
