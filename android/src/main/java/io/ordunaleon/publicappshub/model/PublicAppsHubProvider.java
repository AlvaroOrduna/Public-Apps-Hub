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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.ordunaleon.publicappshub.model.PublicAppsHubContract.AppEntry;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.ImageEntry;

public class PublicAppsHubProvider extends ContentProvider {

    private PublicAppsHubDbHelper mOpenHelper;

    static final int APP = 100;
    static final int APP_ID = 101;
    static final int IMAGE = 200;
    static final int IMAGE_ID = 201;

    // app._id = ?
    private static final String sAppIdSelection =
            AppEntry.TABLE_NAME + "." + AppEntry._ID + " = ? ";

    // image._id = ?
    private static final String sImageIdSelection =
            ImageEntry.TABLE_NAME + "." + ImageEntry._ID + " = ? ";

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PublicAppsHubContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PublicAppsHubContract.PATH_APP, APP);
        matcher.addURI(authority, PublicAppsHubContract.PATH_APP + "/#", APP_ID);

        matcher.addURI(authority, PublicAppsHubContract.PATH_IMAGE, IMAGE);
        matcher.addURI(authority, PublicAppsHubContract.PATH_IMAGE + "/#", IMAGE_ID);


        return matcher;
    }

    private Cursor getApp(String[] projection, String selection,
                          String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                AppEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getAppById(Uri uri, String[] projection, String sortOrder) {
        String id = AppEntry.getAppIdFromUri(uri);

        String selection = sAppIdSelection;
        String[] selectionArgs = new String[]{id};

        return mOpenHelper.getReadableDatabase().query(
                AppEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getImage(String[] projection, String selection,
                            String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                ImageEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getImageById(Uri uri, String[] projection, String sortOrder) {
        String id = AppEntry.getAppIdFromUri(uri);

        String selection = sImageIdSelection;
        String[] selectionArgs = new String[]{id};

        return mOpenHelper.getReadableDatabase().query(
                ImageEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PublicAppsHubDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case APP:
                return AppEntry.CONTENT_TYPE;
            case APP_ID:
                return AppEntry.CONTENT_ITEM_TYPE;
            case IMAGE:
                return ImageEntry.CONTENT_TYPE;
            case IMAGE_ID:
                return ImageEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case APP:
                cursor = getApp(projection, selection, selectionArgs, sortOrder);
                break;
            case APP_ID:
                cursor = getAppById(uri, projection, sortOrder);
                break;
            case IMAGE:
                cursor = getImage(projection, selection, selectionArgs, sortOrder);
                break;
            case IMAGE_ID:
                cursor = getImageById(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case APP: {
                long _id = db.insert(AppEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = AppEntry.buildAppUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case IMAGE: {
                long _id = db.insert(ImageEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ImageEntry.buildImageUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case APP:
                rowsDeleted = db.delete(
                        AppEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case IMAGE:
                rowsDeleted = db.delete(
                        ImageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case APP:
                rowsUpdated = db.update(AppEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case IMAGE:
                rowsUpdated = db.update(ImageEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown update uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int returnCount = 0;

        switch (sUriMatcher.match(uri)) {
            case APP:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(AppEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            case IMAGE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ImageEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
