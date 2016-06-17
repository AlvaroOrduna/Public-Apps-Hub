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

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.IOException;

import io.ordunaleon.publicappshub.utils.FileUtils;

@ParseClassName("author")
public class Author extends ParseObject {

    private static final String LOG_TAG = "AuthorParseObject";

    public static final String CLASS_NAME = "author";
    private static final String KEY_CODE_ID = "code_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_ORGANIZATION = "organization";
    private static final String KEY_WEB = "web";
    private static final String KEY_IMAGE_URL = "image";

    private ParseFile image = null;

    private int donePercentages;
    private int lastNotifiedPercentage = 0;

    public Author() {
    }

    public Author(Context context, String codeId, String name, String location, String organization, String web, Uri imageUri) {
        setCodeId(codeId);
        setName(name);
        setLocation(location);
        setOrganization(organization);
        setWeb(web);

        if (imageUri != null) {
            byte[] imageBytes = new byte[0];
            String fileName = null;
            try {
                imageBytes = FileUtils.readBytes(context, imageUri);
                fileName = FileUtils.getFileName(context, imageUri);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            if (imageBytes != null && fileName != null) {
                image = new ParseFile(getName(), imageBytes);
            }
        }
    }

    private void setCodeId(String codeId) {
        if (codeId != null) put(KEY_CODE_ID, codeId);
    }

    private void setName(String name) {
        if (name != null) put(KEY_NAME, name);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    private void setLocation(String location) {
        if (location != null) put(KEY_LOCATION, location);
    }

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    private void setOrganization(String organization) {
        if (organization != null) put(KEY_ORGANIZATION, organization);
    }

    public String getOrganization() {
        return getString(KEY_ORGANIZATION);
    }

    private void setWeb(String web) {
        if (web != null) put(KEY_WEB, web);
    }

    public String getWeb() {
        return getString(KEY_WEB);
    }

    private void setImageUrl(String imageUrl) {
        if (imageUrl != null) put(KEY_IMAGE_URL, imageUrl);
    }

    public String getImageUrl() {
        return getString(KEY_IMAGE_URL);
    }

    /**
     * Store hole author in Parse server.
     *
     * @param storeCallback Callback that allows UI to be notified of store progress and errors.
     */
    public void store(final StoreCallback storeCallback) {
        if (image != null) {
            image.saveInBackground(
                    new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                onImageStored(storeCallback, image.getUrl());
                            } else {
                                storeCallback.onStoreError(e);
                            }
                        }
                    }, new ProgressCallback() {
                        @Override
                        public void done(Integer percentDone) {
                            // Update donePercentage of the current screenshot
                            donePercentages = percentDone;

                            // Notify new donePercentage if required
                            if (donePercentages != lastNotifiedPercentage) {
                                lastNotifiedPercentage = donePercentages;
                                storeCallback.onStoreProgress(donePercentages);
                            }
                        }
                    });
        } else {
            // No image has been added, so upload new data
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
    }

    private void onImageStored(final StoreCallback storeCallback, String url) {
        setImageUrl(url);
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

    public static ParseQuery<Author> getQuery() {
        return ParseQuery.getQuery(Author.CLASS_NAME);
    }

    public static ParseQuery<Author> getQuery(String codeID) {
        ParseQuery<Author> query = ParseQuery.getQuery(Author.CLASS_NAME);
        query.whereEqualTo(KEY_CODE_ID, codeID);
        return query;
    }

    /**
     * A callback interface that allows UI to be notified of store progress and errors.
     */
    public interface StoreCallback {

        void onStoreFinish();

        void onStoreProgress(Integer donePercentage);

        void onStoreError(ParseException e);
    }
}
