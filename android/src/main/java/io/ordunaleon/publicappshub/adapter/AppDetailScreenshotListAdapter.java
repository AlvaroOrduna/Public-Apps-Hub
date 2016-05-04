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
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.widget.RecyclerViewArrayAdapter;

public class AppDetailScreenshotListAdapter extends RecyclerViewArrayAdapter<String,
        AppDetailScreenshotListAdapter.ViewHolder> {

    private final Context mContext;
    private final OnLoadHandler mLoadHandler;
    private final OnClickHandler mClickHandler;

    private int mLoadedScreenshotCount = 0;

    public AppDetailScreenshotListAdapter(Context context, List<String> objects,
                                          OnLoadHandler loadHandler, OnClickHandler clickHandler) {
        super(objects);
        mContext = context;
        mLoadHandler = loadHandler;
        mClickHandler = clickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_detail_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(getItem(position))
                .into(holder.screenshot, new Callback() {
                    @Override
                    public void onSuccess() {
                        mLoadedScreenshotCount++;
                        if (mLoadedScreenshotCount == getItemCount()) {
                            mLoadHandler.onLoadFinished();
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    /**
     * Interface definition for a callback to be invoked when all items are loaded.
     */
    public interface OnLoadHandler {
        /**
         * Called when all items load is finished.
         */
        void onLoadFinished();
    }

    /**
     * Interface definition for a callback to be invoked when a item is clicked.
     */
    public interface OnClickHandler {
        /**
         * Called when a item has been clicked.
         *
         * @param string String of the clicked item.
         */
        void onClick(String string);
    }

    /**
     * Provide a direct reference to each of the views within a data item.
     * Used to cache the views within the item layout for fast access.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView screenshot;

        public ViewHolder(View itemView) {
            super(itemView);

            screenshot = (ImageView) itemView.findViewById(R.id.screenshot);
            screenshot.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String url = getItem(getAdapterPosition());
            mClickHandler.onClick(url);
        }
    }
}
