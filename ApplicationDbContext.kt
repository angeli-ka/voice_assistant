package com.example.myapplication.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ApplicationDbContext (context: Context) : SQLiteOpenHelper(context, DbUtilClass.DATABASE_SRC, null, DbUtilClass.DATABASE_VERSION) {
    // Метод, вызываемый при создании бд
    override fun onCreate(db: SQLiteDatabase?) {

    // Создаем таблицу с заметками
        CreateTable(db, DbUtilClass.CREATE_TABLE_QUERY);
    }


    // Созданный нами метод для создания таблиц
    private fun CreateTable(db: SQLiteDatabase?, query: String)
    {
        db?.execSQL(query);
    }

    // Метод, вызываемый при обновлении БД
    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${DbUtilClass.TABLE_NAME}");
        CreateTable(db, DbUtilClass.CREATE_TABLE_QUERY);
    }
}