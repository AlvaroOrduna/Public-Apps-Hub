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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


@ParseClassName("service")
public class Service extends ParseObject {

    private static final String LOG_TAG = "ServiceParseObject";

    public static final String CLASS_NAME = "service";
    public static final String KEY_CODE_ID = "code_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_MANAGEMENT = "management";
    public static final String KEY_URL = "url";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_REGION = "region";

    public Service() {
    }

    public Service(String codeId, String name, String management, String url, String country, String region) {
        setCodeId(codeId);
        setName(name);
        setManagement(management);
        setUrl(url);
        setCountry(country);
        setRegion(region);
        Log.v(LOG_TAG, "codeId: " + codeId);
        Log.v(LOG_TAG, "name: " + name);
        Log.v(LOG_TAG, "management: " + management);
        Log.v(LOG_TAG, "url: " + url);
        Log.v(LOG_TAG, "country: " + country);
        Log.v(LOG_TAG, "region: " + region);
    }

    private void setCodeId(String codeId) {
        if (codeId != null) put(KEY_CODE_ID, codeId);
    }

    public String getCodeId() {
        return getString(KEY_CODE_ID);
    }

    private void setName(String name) {
        if (name != null) put(KEY_NAME, name);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    private void setManagement(String management) {
        if (management != null) put(KEY_MANAGEMENT, management);
    }

    public String getManagement() {
        return getString(KEY_MANAGEMENT);
    }

    private void setUrl(String url) {
        if (url != null) put(KEY_URL, url);
    }

    public String getUrl() {
        return getString(KEY_URL);
    }

    private void setCountry(String country) {
        if (country != null) put(KEY_COUNTRY, country);
    }

    public String getCountry() {
        return getString(KEY_COUNTRY);
    }

    private void setRegion(String region) {
        if (region != null) put(KEY_REGION, region);
    }

    public String getRegion() {
        return getString(KEY_REGION);
    }

    public String getLocation() {
        return getCountry() + " (" + getRegion() + ")";
    }

    public static ParseQuery<Service> getQuery() {
        return ParseQuery.getQuery(CLASS_NAME);
    }

    /**
     * Store hole service in Parse server.
     *
     * @param storeCallback Callback that allows UI to be notified of store progress and errors.
     */
    public void store(final StoreCallback storeCallback) {
        saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    storeCallback.onStoreFinish();
                } else {
                    storeCallback.onStoreError(e);
                }
            }
        });
    }

    /**
     * A callback interface that allows UI to be notified of store progress and errors.
     */
    public interface StoreCallback {

        void onStoreFinish();

        void onStoreError(ParseException e);
    }

    @Override
    public String toString() {
        return getName();
    }
}
