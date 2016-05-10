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

package io.ordunaleon.publicappshub.widget;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter.QueryFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of ParseQueryAdapter for RecyclerView instead of ListView
 */
public abstract class ParseRecyclerQueryAdapter<T extends ParseObject, U extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<U> {

    private static final String LOG_TAG = "RecyclerQueryAdapter";

    private final List<T> mItems;
    private final QueryFactory<T> mFactory;

    private final OnLoadHandler mLoadHandler;

    /**
     * ParseRecyclerQueryAdapter constructor
     *
     * @param factory {@link QueryFactory} to build custom {@link ParseQuery} for fetching objects.
     */
    public ParseRecyclerQueryAdapter(final QueryFactory<T> factory, OnLoadHandler onLoadHandler) {
        mItems = new ArrayList<>();
        mFactory = factory;
        mLoadHandler = onLoadHandler;

        loadObjects();
    }

    /**
     * Load objects in background
     */
    public void loadObjects() {
        mLoadHandler.onLoadStart();
        final ParseQuery<T> query = mFactory.create();
        query.findInBackground(new FindCallback<T>() {
            @Override
            public void done(List<T> queriedItems, @Nullable ParseException e) {
                if (e == null) {
                    mItems.clear();
                    mItems.addAll(queriedItems);
                    notifyDataSetChanged();
                    mLoadHandler.onLoadFinish();
                } else {
                    mLoadHandler.onLoadError(e);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Interface definition for a callback to be invoked when objects load is started and finished.
     */
    public interface OnLoadHandler {
        /**
         * Called when objects load is started.
         */
        void onLoadStart();

        /**
         * Called when objects load is finished.
         */
        void onLoadFinish();

        /**
         * Called when objects load throws an error.
         */
        void onLoadError(ParseException e);
    }
}
