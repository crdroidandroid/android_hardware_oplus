/*
 * Copyright (C) 2016 The CyanogenMod project
 *               2017-2020 The LineageOS Project
 *               2022 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.evolution.oplus.OPlusExtras;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.SearchIndexableResource;
import android.provider.SearchIndexablesProvider;

import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_CLASS_NAME;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_ICON_RESID;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_ACTION;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_RANK;
import static android.provider.SearchIndexablesContract.COLUMN_INDEX_XML_RES_RESID;
import static android.provider.SearchIndexablesContract.INDEXABLES_RAW_COLUMNS;
import static android.provider.SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS;
import static android.provider.SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS;

public class ConfigPanelSearchIndexablesProvider extends SearchIndexablesProvider {
    private static final String TAG = "ConfigPanelSearchIndexablesProvider";

    public static final int SEARCH_IDX_BUTTON_PANEL = 0;
    public static final int SEARCH_IDX_GESTURE_PANEL = 1;
    public static final int SEARCH_IDX_OCLICK_PANEL = 2;

    private static SearchIndexableResource[] INDEXABLE_RES = new SearchIndexableResource[]{
            new SearchIndexableResource(1, R.xml.main,
                    OPlusExtrasActivity.class.getName(),
                    R.drawable.ic_oplus_extras),
    };

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor queryXmlResources(String[] projection) {
        MatrixCursor cursor = new MatrixCursor(INDEXABLES_XML_RES_COLUMNS);
        return cursor;
    }

    private static Object[] generateResourceRef(SearchIndexableResource sir) {
        Object[] ref = new Object[7];
        ref[COLUMN_INDEX_XML_RES_RANK] = sir.rank;
        ref[COLUMN_INDEX_XML_RES_RESID] = sir.xmlResId;
        ref[COLUMN_INDEX_XML_RES_CLASS_NAME] = null;
        ref[COLUMN_INDEX_XML_RES_ICON_RESID] = sir.iconResId;
        ref[COLUMN_INDEX_XML_RES_INTENT_ACTION] = "com.android.settings.action.EXTRA_SETTINGS";
        ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE] = "org.evolution.oplus.OPlusExtras";
        ref[COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS] = sir.className;
        return ref;
    }

    @Override
    public Cursor queryRawData(String[] projection) {
        MatrixCursor cursor = new MatrixCursor(INDEXABLES_RAW_COLUMNS);
        return cursor;
    }

    @Override
    public Cursor queryNonIndexableKeys(String[] projection) {
        MatrixCursor cursor = new MatrixCursor(NON_INDEXABLES_KEYS_COLUMNS);
        return cursor;
    }
}
