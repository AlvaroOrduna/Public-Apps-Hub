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

package io.ordunaleon.publicappshub.util;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

public class StorageUtils {

    public static Uri copyImageToDataDir(Activity activity, Uri sourceUri) {
        // Get file extension from source file
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = mime.getExtensionFromMimeType(activity.getContentResolver().getType(sourceUri));

        // Get source file name
        String sourceFileName = getRealPathFromUri(activity, sourceUri);

        // Build target directory and target file names
        String targetDirName = activity.getApplicationInfo().dataDir + File.separator + "images";
        String targetFileName = targetDirName + File.separator +
                String.valueOf((new Date()).getTime()) + "." + extension;

        File sourceFile = new File(sourceFileName);
        File targetFile = new File(targetFileName);
        File targetDir = new File(targetDirName);

        targetDir.mkdirs();

        FileChannel source = null;
        FileChannel target = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            target = new FileOutputStream(targetFile).getChannel();
            if (target != null && source != null) {
                target.transferFrom(source, 0, source.size());
            }
            if (source != null) {
                source.close();
            }
            if (target != null) {
                target.close();
            }

            return Uri.fromFile(targetFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getRealPathFromUri(Activity activity, Uri contentUri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = activity.managedQuery(contentUri, projection, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
    }
}
