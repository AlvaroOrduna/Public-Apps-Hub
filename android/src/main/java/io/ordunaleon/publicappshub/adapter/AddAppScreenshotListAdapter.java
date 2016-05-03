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

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.widget.RecyclerViewArrayAdapter;

public class AddAppScreenshotListAdapter extends RecyclerViewArrayAdapter<Uri, AddAppScreenshotListAdapter.ViewHolder> {

    private final OnLongClickHandler mLongClickHandler;

    public AddAppScreenshotListAdapter(List<Uri> objects, OnLongClickHandler longClickHandler) {
        super(objects);
        mLongClickHandler = longClickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_add_app_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.screenshot.setImageURI(getItem(position));
    }

    /**
     * Interface definition for a callback to be invoked when a item is long clicked.
     */
    public interface OnLongClickHandler {
        /**
         * Called when a item has been long clicked.
         *
         * @param uri Uri to be removed.
         * @return true if the callback consumed the long click, false otherwise.
         */
        boolean onLongClick(Uri uri);
    }

    /**
     * Provide a direct reference to each of the views within a data item.
     * Used to cache the views within the item layout for fast access.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public ImageView screenshot;

        public ViewHolder(View itemView) {
            super(itemView);

            screenshot = (ImageView) itemView.findViewById(R.id.screenshot);
            screenshot.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            Uri uri = getItem(getAdapterPosition());
            return mLongClickHandler.onLongClick(uri);
        }
    }
}
