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

@ParseClassName("code")
public class Code extends ParseObject {

    private static final String LOG_TAG = "CodeParseObject";

    public static final String CLASS_NAME = "code";
    public static final String KEY_APP_ID = "app_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_PLATFORMS = "platforms";

    public Code() {
    }

    public Code(String appId, String name, String author, String source, String platforms) {
        setAppId(appId);
        setName(name);
        setAuthor(author);
        setSource(source);
        setPlatforms(platforms);

        Log.v(LOG_TAG, "appId: " + appId);
        Log.v(LOG_TAG, "name: " + name);
        Log.v(LOG_TAG, "author: " + author);
        Log.v(LOG_TAG, "source: " + source);
        Log.v(LOG_TAG, "platforms: " + platforms);
    }

    public void setAppId(String appId) {
        if (appId != null) put(KEY_APP_ID, appId);
    }

    public String getAppId() {
        return getString(KEY_APP_ID);
    }

    public void setName(String name) {
        if (name != null) put(KEY_NAME, name);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setAuthor(String author) {
        if (author != null) put(KEY_AUTHOR, author);
    }

    public String getAuthor() {
        return getString(KEY_AUTHOR);
    }

    public void setSource(String source) {
        if (source != null) put(KEY_SOURCE, source);
    }

    public String getSource() {
        return getString(KEY_SOURCE);
    }

    public void setPlatforms(String platforms) {
        if (platforms != null) put(KEY_PLATFORMS, platforms);
    }

    public String getPlatforms() {
        return getString(KEY_PLATFORMS);
    }

    public static ParseQuery<Code> getQuery() {
        return ParseQuery.getQuery(Code.CLASS_NAME);
    }

    public static ParseQuery<Code> getQuery(String appId) {
        ParseQuery<Code> query = getQuery();
        query.orderByAscending(KEY_NAME);
        query.whereEqualTo(KEY_APP_ID, appId);
        return query;
    }

    /**
     * Store hole code in Parse server.
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
}
