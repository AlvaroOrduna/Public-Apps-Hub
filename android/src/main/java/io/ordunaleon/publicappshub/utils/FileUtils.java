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

package io.ordunaleon.publicappshub.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    /**
     * Gets file complete name, including file extension
     *
     * @param uri File Uri
     * @return File extension
     */
    public static String getFileName(final Context context, final Uri uri) {
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            String fileName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            c.close();
            return fileName;
        }

        return null;
    }

    /**
     * Gets file content as array of bytes
     *
     * @param uri File Uri
     * @return File as array of bytes
     * @throws IOException
     */
    public static byte[] readBytes(final Context context, final Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        if (inputStream == null) {
            return null;
        }

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}
