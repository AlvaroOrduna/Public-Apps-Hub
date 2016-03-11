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

package io.ordunaleon.publicappshub.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.ordunaleon.publicappshub.R;
import io.ordunaleon.publicappshub.model.App;


public class AppDetailFragment extends Fragment {

    public static final String APP_DETAIL_FRAGMENT_TAG = "ADFT";

    public static final String ARGS_APP = "args_app";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_detail, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARGS_APP)) {
            ((TextView) rootView).setText(((App) args.getParcelable(ARGS_APP)).getName());
        }

        return rootView;
    }
}