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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.ordunaleon.publicappshub.model.PublicAppsHubContract.AppEntry;
import io.ordunaleon.publicappshub.model.PublicAppsHubContract.ImageEntry;

public class PublicAppsHubDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "public_apps_hub.db";

    private static final int DATABASE_VERSION = 5;

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

    public PublicAppsHubDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_APP_TABLE);
        db.execSQL(SQL_CREATE_IMAGE_TABLE);

        insertDummyData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is just for testing, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + AppEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ImageEntry.TABLE_NAME);
        onCreate(db);
    }

    /**
     * Add some dummy data
     *
     * @param db Database to which add new data
     */
    private void insertDummyData(SQLiteDatabase db) {
        ContentValues newValues = new ContentValues();

        newValues.put(AppEntry.COLUMN_APP_NAME, "Mi villavesa");
        newValues.put(AppEntry.COLUMN_APP_CATEGORY, "Transportation");
        newValues.put(AppEntry.COLUMN_APP_DESCRIPTION, "Información de las villavesas en tiempo real. Consulta todos los datos del servicio de transporte urbano de la Mancomunidad de la Comarca de Pamplona.");
        db.insertOrThrow(AppEntry.TABLE_NAME, null, newValues);

        newValues.put(AppEntry.COLUMN_APP_NAME, "Taxi app");
        newValues.put(AppEntry.COLUMN_APP_CATEGORY, "Transportation");
        newValues.put(AppEntry.COLUMN_APP_DESCRIPTION, "Es la primera taxi App del mundo que te permite pedir un taxi en solo unos segundos desde tu Smartphone.");
        db.insertOrThrow(AppEntry.TABLE_NAME, null, newValues);
    }
}
