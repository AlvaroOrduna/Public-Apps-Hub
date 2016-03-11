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
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import io.ordunaleon.publicappshub.R;

public class App implements Parcelable {

    private final String name;
    private final String description;
    private final String category;

    public App(String title, String description, String category) {
        this.name = title;
        this.description = description;
        this.category = category;
    }

    protected App(Parcel in) {
        name = in.readString();
        description = in.readString();
        category = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public static ArrayList<App> createNewList(Context context, int num) {
        ArrayList<App> appArrayList = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            appArrayList.add(new App("App " + i, context.getString(R.string.lorem), null));
        }

        return appArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(category);
    }

    public static final Parcelable.Creator<App> CREATOR = new Parcelable.Creator<App>() {

        @Override
        public App createFromParcel(Parcel in) {
            return new App(in);
        }

        @Override
        public App[] newArray(int size) {
            return new App[size];
        }
    };
}
