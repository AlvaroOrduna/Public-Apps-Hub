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

import java.util.ArrayList;

import io.ordunaleon.publicappshub.R;

public class App {

    private final String mName;
    private final String mDescription;
    private final String mCategory;

    public App(String title, String description, String category) {
        this.mName = title;
        this.mDescription = description;
        this.mCategory = category;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getCategory() {
        return mCategory;
    }

    public static ArrayList<App> createNewList(Context context, int num) {
        ArrayList<App> contacts = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            contacts.add(new App("App " + i, context.getString(R.string.lorem), null));
        }

        return contacts;
    }
}
