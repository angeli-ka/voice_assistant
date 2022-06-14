package com.example.myapplication.db

import android.provider.BaseColumns

object DbUtilClass : BaseColumns {
    const val TABLE_NAME = "notes";
    const val COLUMN_NAME_BODY = "Body";
    const val COLUMN_NAME_CREATE_DATE = "CreateTime";

    const val DATABASE_VERSION = 1;
    const val DATABASE_SRC = "voiceHelper.db";
    const val CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, ${COLUMN_NAME_BODY} TEXT)"
}