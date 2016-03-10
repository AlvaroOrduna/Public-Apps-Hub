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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.model.App;

public class AppAdapter extends ArrayAdapter<App> {

    public AppAdapter(Context context, ArrayList<App> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        App app = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_home_listview, parent, false);
        }

        // Lookup view for data population
        TextView titleView = (TextView) convertView.findViewById(R.id.app_name);
        TextView descriptionView = (TextView) convertView.findViewById(R.id.app_description);

        // Populate the data into the template view using the data object
        titleView.setText(app.getName());
        descriptionView.setText(app.getDescription());

        // Return the completed view to render on screen
        return convertView;
    }
}
