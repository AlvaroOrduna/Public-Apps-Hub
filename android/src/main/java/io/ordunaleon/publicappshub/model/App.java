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

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.ordunaleon.publicappshub.utils.FileUtils;

@ParseClassName("app")
public class App extends ParseObject {

    private static final String LOG_TAG = "AppParseObject";

    public static final String CLASS_NAME = "app";
    public static final String KEY_NAME = "name";
    public static final String KEY_CATEGORY = "category";
    private static final String KEY_DESCRIPTION_TEXT = "description_text";
    private static final String KEY_DESCRIPTION_VISUAL = "description_visual";

    private List<byte[]> screenshotsBytesList;
    private ArrayList<String> screenshotsNameList;
    private JSONArray screenshotsUrlsJSONArray;

    private int[] donePercentages;
    private int lastNotifiedPercentage = 0;

    public App() {
    }

    public App(String name, String category, String descriptionText, JSONArray descriptionVisual) {
        setName(name);
        setCategory(category);
        setDescriptionText(descriptionText);
        setDescriptionVisual(descriptionVisual);
    }

    public void setName(String name) {
        if (name != null) put(KEY_NAME, name);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setCategory(String category) {
        if (category != null) put(KEY_CATEGORY, category);
    }

    public String getCategory() {
        return getString(KEY_CATEGORY);
    }

    public void setDescriptionText(String descriptionText) {
        if (descriptionText != null) put(KEY_DESCRIPTION_TEXT, descriptionText);
    }

    public String getDescriptionText() {
        return getString(KEY_DESCRIPTION_TEXT);
    }

    public void setDescriptionVisual(JSONArray descriptionVisual) {
        if (descriptionVisual != null) put(KEY_DESCRIPTION_VISUAL, descriptionVisual);
    }

    public JSONArray getDescriptionVisual() {
        return getJSONArray(KEY_DESCRIPTION_VISUAL);
    }

    private int getDonePercentage() {
        int percentage = 0;
        if (donePercentages != null && donePercentages.length != 0) {
            for (int i : donePercentages) {
                percentage += i / donePercentages.length;
            }
        }

        return percentage;
    }

    /**
     * Add screenshot to app data.
     *
     * @param context       Context of the application from where to read the file.
     * @param screenshotUri Requested screenshot uri.
     * @return Whether the screenshot was added correctly.
     */
    public boolean addScreenshotFromUri(Context context, Uri screenshotUri) {
        if (screenshotsBytesList == null) {
            screenshotsBytesList = new ArrayList<>();
        }

        if (screenshotsNameList == null) {
            screenshotsNameList = new ArrayList<>();
        }

        byte[] screenshotBytes = new byte[0];
        String fileName = null;
        try {
            screenshotBytes = FileUtils.readBytes(context, screenshotUri);
            fileName = FileUtils.getFileName(context, screenshotUri);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        if (screenshotBytes != null && fileName != null) {
            screenshotsBytesList.add(screenshotBytes);
            screenshotsNameList.add(fileName);
            return true;
        }

        return false;
    }

    /**
     * Store hole app in Parse server.
     *
     * @param storeCallback Callback that allows UI to be notified of store progress and errors.
     */
    public void store(final StoreCallback storeCallback) {
        if (screenshotsBytesList != null && screenshotsBytesList.size() > 0) {
            // Initialize screenshots urls array and done percentage array
            screenshotsUrlsJSONArray = new JSONArray();
            donePercentages = new int[screenshotsBytesList.size()];

            for (byte[] bytes : screenshotsBytesList) {
                final int index = screenshotsBytesList.indexOf(bytes);

                // For each screenshot, instantiate a new ParseFile, and save it in background
                final ParseFile file = new ParseFile(screenshotsNameList.get(index), bytes);

                file.saveInBackground(
                        new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    onScreenshotStored(storeCallback, file.getUrl());
                                } else {
                                    storeCallback.onStoreError(e);
                                }
                            }
                        }, new ProgressCallback() {
                            @Override
                            public void done(Integer percentDone) {
                                // Update donePercentage of the current screenshot
                                donePercentages[index] = percentDone;

                                // Notify new donePercentage if required
                                int currentPercentage = getDonePercentage();
                                if (currentPercentage != lastNotifiedPercentage) {
                                    lastNotifiedPercentage = currentPercentage;
                                    storeCallback.onStoreProgress(currentPercentage);
                                }
                            }
                        });
            }
        } else {
            // No screenshot has been added, so upload new data
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

    /**
     * When a screenshot is stored, check if all screenshots have been added. If so, store app data.
     *
     * @param storeCallback Callback that allows UI to be notified of store progress and errors.
     * @param screenshotUrl Url of the screenshot that have been added.
     */
    private void onScreenshotStored(final StoreCallback storeCallback, String screenshotUrl) {
        // Add screenshot url to urlArray
        screenshotsUrlsJSONArray.put(screenshotUrl);

        // Check if all screenshots are uploaded. If so, upload new app data
        if (screenshotsUrlsJSONArray.length() == screenshotsBytesList.size()) {
            setDescriptionVisual(screenshotsUrlsJSONArray);
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

    public static ParseQuery<App> getQuery() {
        return ParseQuery.getQuery(App.CLASS_NAME);
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
