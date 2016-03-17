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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.AppEntry;

public class AppListAdapter extends CursorRecyclerViewAdapter<AppListAdapter.ViewHolder> {

    final private OnClickHandler mClickHandler;

    public AppListAdapter(Context context, Cursor cursor, OnClickHandler dh) {
        super(context, cursor);
        mClickHandler = dh;
    }

    @Override
    public AppListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);

        // Return a new holder instance
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        if (displaySubhead(cursor)) {
            String category = cursor.getString(cursor.getColumnIndex(AppEntry.COLUMN_APP_CATEGORY));
            viewHolder.subhead.setText(category);
            viewHolder.subhead.setVisibility(View.VISIBLE);
        } else {
            viewHolder.subhead.setVisibility(View.GONE);
        }

        // Get fields from cursor
        String name = cursor.getString(cursor.getColumnIndex(AppEntry.COLUMN_APP_NAME));
        String description = cursor.getString(cursor.getColumnIndex(AppEntry.COLUMN_APP_DESCRIPTION));

        // Set item views based on the data model
        viewHolder.appName.setText(name);
        viewHolder.appDescription.setText(description);
    }

    private boolean displaySubhead(Cursor cursor) {
        int position = cursor.getPosition();

        if (position == 0) {
            // We are in first position, so we have to display de subhead
            return true;
        }

        int categoryColumnIndex = cursor.getColumnIndex(AppEntry.COLUMN_APP_CATEGORY);

        // Get current position's category
        String category = cursor.getString(categoryColumnIndex);

        // Get previous position's category
        cursor.moveToPrevious();
        String categoryPrevious = cursor.getString(categoryColumnIndex);

        // Reset cursor position
        cursor.moveToNext();

        return !category.equals(categoryPrevious);
    }

    public interface OnClickHandler {
        void onClick(long appId);
    }

    /**
     * Provide a direct reference to each of the views within a data item.
     * Used to cache the views within the item layout for fast access.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView subhead;

        public LinearLayout itemLayout;
        public TextView appName;
        public TextView appDescription;

        public ViewHolder(View view) {
            super(view);

            subhead = (TextView) view.findViewById(R.id.app_list_subheader);

            itemLayout = (LinearLayout) view.findViewById(R.id.app_item_layout);
            appName = (TextView) view.findViewById(R.id.app_name);
            appDescription = (TextView) view.findViewById(R.id.app_description);

            itemLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            getCursor().moveToPosition(adapterPosition);
            int idColumnIndex = getCursor().getColumnIndex(AppEntry._ID);
            mClickHandler.onClick(getCursor().getLong(idColumnIndex));
        }
    }
}
