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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.parse.ParseHelper;
import io.ordunaleon.publicappshub.widget.ParseRecyclerQueryAdapter;

public class AppsListAdapter extends ParseRecyclerQueryAdapter<ParseObject, AppsListAdapter.ViewHolder>
        implements ParseHelper.AppInterface {

    private final OnClickHandler mClickHandler;

    public AppsListAdapter(OnClickHandler mClickHandler) {
        super(new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS_NAME);
                query.orderByAscending(KEY_CATEGORY);
                query.addAscendingOrder(KEY_NAME);
                return query;
            }
        });

        this.mClickHandler = mClickHandler;
    }

    @Override
    public AppsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get current position object
        ParseObject app = getItem(position);

        // Fill and show head view if needed. If not, hide head view
        if (showHead(position)) {
            holder.head.setText(app.getString(KEY_CATEGORY));
            holder.head.setVisibility(View.VISIBLE);
        } else {
            holder.head.setVisibility(View.GONE);
        }

        // Fill content views
        holder.name.setText(app.getString(KEY_NAME));
        holder.description_text.setText(app.getString(KEY_DESCRIPTION_TEXT));
    }

    /**
     * Check if we need to show the head of the list
     *
     * @param position Current list position
     * @return True if you have to show the header
     */
    private boolean showHead(int position) {
        if (position == 0) {
            return true;
        }

        String currentCategory = getItem(position).getString(KEY_CATEGORY);
        String previousCategory = getItem(position - 1).getString(KEY_CATEGORY);
        return !currentCategory.equals(previousCategory);
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

        public TextView head;
        public TextView name;
        public TextView description_text;

        public ViewHolder(View view) {
            super(view);

            head = (TextView) view.findViewById(R.id.app_header);

            LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.app_content_layout);
            itemLayout.setOnClickListener(this);

            name = (TextView) itemLayout.findViewById(R.id.app_name);
            description_text = (TextView) itemLayout.findViewById(R.id.app_description_text);
        }

        @Override
        public void onClick(View v) {
            ParseObject item = getItem(getAdapterPosition());
            mClickHandler.onClick(item.getObjectId());
        }
    }
}
