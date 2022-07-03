/*
 * Copyright (C) 2016 The OmniROM Project
 *               2022 The Evolution X Project
 * SPDX-License-Identifier: GPL-2.0-or-later
 */

package org.lineageos.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.UserHandle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import org.lineageos.settings.OPlusExtras;
import org.lineageos.settings.services.AutoHBMService;

public class FileUtils {

    private static boolean mServiceEnabled = false;
    private static final String TAG = FileUtils.class.getSimpleName();

    private static void startService(Context context) {
        context.startServiceAsUser(new Intent(context, AutoHBMService.class),
                UserHandle.CURRENT);
        mServiceEnabled = true;
    }

    private static void stopService(Context context) {
        mServiceEnabled = false;
        context.stopServiceAsUser(new Intent(context, AutoHBMService.class),
                UserHandle.CURRENT);
    }

    public static void enableService(Context context) {
        if (OPlusExtras.isAUTOHBMEnabled(context) && !mServiceEnabled) {
            startService(context);
        } else if (!OPlusExtras.isAUTOHBMEnabled(context) && mServiceEnabled) {
            stopService(context);
        }
    }

    /**
     * Write a string value to the specified file.
     * @param filename      The filename
     * @param value         The value
     */
    public static void writeValue(String filename, String value) {
        if (filename == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(new File(filename));
            fos.write(value.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the specified file exists.
     * @param filename      The filename
     * @return              Whether the file exists or not
     */
    public static boolean fileExists(String filename) {
        if (filename == null) {
            return false;
        }
        return new File(filename).exists();
    }

    public static boolean fileWritable(String filename) {
        return fileExists(filename) && new File(filename).canWrite();
    }

    public static String readLine(String filename) {
        if (filename == null) {
            return null;
        }
        BufferedReader br = null;
        String line = null;
        try {
            br = new BufferedReader(new FileReader(filename), 1024);
            line = br.readLine();
			if (line != null) {
            line = line.replaceAll(".+= ", "");
            }
        } catch (IOException e) {
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return line;
    }

    public static boolean getFileValueAsBoolean(String filename, boolean defValue) {
        return getFileValueAsBoolean(filename, defValue, "1", "0");
    }

    public static boolean getFileValueAsBoolean(String filename, boolean defValue,
        String trueVal, String falseVal) {

        String fileValue = readLine(filename);
        if(fileValue!=null){
            if (fileValue.equals(trueVal)) {
                return true;
            }
            if (fileValue.equals(falseVal)) {
                return false;
            }
        }
        return defValue;
    }

    public static String getFileValue(String filename, String defValue) {
        String fileValue = readLine(filename);
        if(fileValue!=null){
            return fileValue;
        }
        return defValue;
    }

    /**
     * Reads the first line of text from the given file.
     * Reference {@link BufferedReader#readLine()} for clarification on what a line is
     *
     * @return the read line contents, or null on failure
     */
    public static String readOneLine(String fileName) {
        String line = "0";
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(fileName), 512);
            line = reader.readLine();
        } catch (FileNotFoundException e) {
            Log.w(TAG, "No such file " + fileName + " for reading", e);
        } catch (IOException e) {
            Log.e(TAG, "Could not read from file " + fileName, e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Ignored, not much we can do anyway
            }
        }
        return line;
    }

    /**
     * Checks whether the given file is readable
     *
     * @return true if readable, false if not
     */
    public static boolean isFileReadable(String fileName) {
        final File file = new File(fileName);
        return file.exists() && file.canRead();
    }
}
