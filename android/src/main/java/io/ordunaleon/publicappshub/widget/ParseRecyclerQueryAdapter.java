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
import android.util.Log;

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

    /**
     * ParseRecyclerQueryAdapter constructor
     *
     * @param factory {@link QueryFactory} to build custom {@link ParseQuery} for fetching objects.
     */
    public ParseRecyclerQueryAdapter(final QueryFactory<T> factory) {
        mItems = new ArrayList<>();
        mFactory = factory;

        loadObjects();
    }

    /**
     * ParseRecyclerQueryAdapter constructor
     *
     * @param className The name of the class to retrieve {@link ParseObject}s for.
     */
    public ParseRecyclerQueryAdapter(final String className) {
        mItems = new ArrayList<>();
        mFactory = new QueryFactory<T>() {
            @Override
            public ParseQuery<T> create() {
                return ParseQuery.getQuery(className);
            }
        };

        loadObjects();
    }

    /**
     * Load objects in background
     */
    public void loadObjects() {
        final ParseQuery<T> query = mFactory.create();
        query.findInBackground(new FindCallback<T>() {
            @Override
            public void done(List<T> queriedItems, @Nullable ParseException e) {
                if (e == null) {
                    mItems.clear();
                    mItems.addAll(queriedItems);
                    notifyDataSetChanged();
                } else {
                    Log.e(LOG_TAG, e.getMessage(), e);
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
}
