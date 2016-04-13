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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.ordunaleon.publicappshub.model.PublicAppsHubContract.AppEntry;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.CodeEntry;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.ImageEntry;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.ServiceEntry;

public class PublicAppsHubDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "public_apps_hub.db";

    private static final int DATABASE_VERSION = 8;

    private static final String SQL_CREATE_APP_TABLE =
            "CREATE TABLE " + AppEntry.TABLE_NAME + " (" +
                    AppEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AppEntry.COLUMN_APP_NAME + " TEXT NOT NULL," +
                    AppEntry.COLUMN_APP_CATEGORY + " TEXT NOT NULL," +
                    AppEntry.COLUMN_APP_DESCRIPTION + " TEXT NOT NULL" +
                    ");";

    private static final String SQL_CREATE_IMAGE_TABLE =
            "CREATE TABLE " + ImageEntry.TABLE_NAME + " (" +
                    ImageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ImageEntry.COLUMN_IMAGE_APP_KEY + " INTEGER NOT NULL," +
                    ImageEntry.COLUMN_IMAGE_URI + " TEXT NOT NULL," +

                    // Set up the app_id column as a foreign key to app table.
                    " FOREIGN KEY (" + ImageEntry.COLUMN_IMAGE_APP_KEY + ") REFERENCES " +
                    AppEntry.TABLE_NAME + " (" + AppEntry._ID + ")" +
                    ");";

    private static final String SQL_CREATE_CODE_TABLE =
            "CREATE TABLE " + CodeEntry.TABLE_NAME + " (" +
                    CodeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CodeEntry.COLUMN_CODE_APP_KEY + " INTEGER NOT NULL," +
                    CodeEntry.COLUMN_CODE_NAME + " TEXT NOT NULL," +
                    CodeEntry.COLUMN_CODE_AUTHOR + " TEXT NOT NULL," +
                    CodeEntry.COLUMN_CODE_SOURCE + " TEXT NOT NULL," +
                    CodeEntry.COLUMN_CODE_PLATFORMS + " TEXT NOT NULL," +

                    // Set up the app_id column as a foreign key to app table.
                    " FOREIGN KEY (" + CodeEntry.COLUMN_CODE_APP_KEY + ") REFERENCES " +
                    AppEntry.TABLE_NAME + " (" + AppEntry._ID + ")" +
                    ");";

    private static final String SQL_CREATE_SERVICE_TABLE =
            "CREATE TABLE " + ServiceEntry.TABLE_NAME + " (" +
                    ServiceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ServiceEntry.COLUMN_SERVICE_APP_KEY + " INTEGER NOT NULL," +
                    ServiceEntry.COLUMN_SERVICE_CODE_KEY + " INTEGER NOT NULL," +
                    ServiceEntry.COLUMN_SERVICE_NAME + " TEXT NOT NULL," +
                    ServiceEntry.COLUMN_SERVICE_URL + " TEXT NOT NULL," +
                    ServiceEntry.COLUMN_SERVICE_MANAGEMENT + " TEXT NOT NULL," +
                    ServiceEntry.COLUMN_SERVICE_COUNTRY + " TEXT NOT NULL," +
                    ServiceEntry.COLUMN_SERVICE_REGION + " TEXT NOT NULL," +

                    // Set up the app_id column as a foreign key to app table.
                    " FOREIGN KEY (" + ServiceEntry.COLUMN_SERVICE_APP_KEY + ") REFERENCES " +
                    AppEntry.TABLE_NAME + " (" + AppEntry._ID + ")," +

                    // Set up the code_id column as a foreign key to code table.
                    " FOREIGN KEY (" + ServiceEntry.COLUMN_SERVICE_CODE_KEY + ") REFERENCES " +
                    CodeEntry.TABLE_NAME + " (" + CodeEntry._ID + ")" +
                    ");";

    public PublicAppsHubDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_APP_TABLE);
        db.execSQL(SQL_CREATE_IMAGE_TABLE);
        db.execSQL(SQL_CREATE_CODE_TABLE);
        db.execSQL(SQL_CREATE_SERVICE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is just for testing, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + AppEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ImageEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CodeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ServiceEntry.TABLE_NAME);
        onCreate(db);
    }
}
