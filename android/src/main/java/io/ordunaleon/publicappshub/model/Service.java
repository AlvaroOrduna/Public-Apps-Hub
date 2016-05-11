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

package io.ordunaleon.publicappshub.model;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;


@ParseClassName("service")
public class Service extends ParseObject {

    private static final String LOG_TAG = "ServiceParseObject";

    public Service() {
    }

    public Service(String codeId, String name, String management, String url, String country, String region) {
        Log.v(LOG_TAG, "codeId: " + codeId);
        Log.v(LOG_TAG, "name: " + name);
        Log.v(LOG_TAG, "management: " + management);
        Log.v(LOG_TAG, "url: " + url);
        Log.v(LOG_TAG, "country: " + country);
        Log.v(LOG_TAG, "region: " + region);
    }
}
