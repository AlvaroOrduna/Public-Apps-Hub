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
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.CodeEntry;

public class CodeListAdapter extends CursorRecyclerViewAdapter<CodeListAdapter.ViewHolder> {

    final private OnClickHandler mClickHandler;

    public CodeListAdapter(Context context, Cursor cursor, OnClickHandler dh) {
        super(context, cursor);
        mClickHandler = dh;
    }

    @Override
    public CodeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_code, parent, false);

        // Return a new holder instance
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        // Get fields from cursor
        String name = cursor.getString(cursor.getColumnIndex(CodeEntry.COLUMN_CODE_NAME));
        String platforms = cursor.getString(cursor.getColumnIndex(CodeEntry.COLUMN_CODE_PLATFORMS));

        // Fill views with cursor data
        viewHolder.name.setText(name);
        viewHolder.platforms.setText(platforms);
    }

    public interface OnClickHandler {
        void onClick(Uri codeUri);
    }

    /**
     * Provide a direct reference to each of the views within a data item.
     * Used to cache the views within the item layout for fast access.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView platforms;

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            name = (TextView) view.findViewById(R.id.item_code_name);
            platforms = (TextView) view.findViewById(R.id.item_code_platforms);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            getCursor().moveToPosition(adapterPosition);
            String codeName = getCursor().getString(getCursor().getColumnIndex(CodeEntry._ID));
            mClickHandler.onClick(CodeEntry.buildCodeUri(Long.valueOf(codeName)));
        }
    }
}
