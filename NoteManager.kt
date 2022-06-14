package com.example.myapplication.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.icu.text.CaseMap.Title
import android.provider.BaseColumns
import com.example.myapplication.models.Note

class NoteManager(val context: Context) {
    val applicationDbContext = ApplicationDbContext(context);
    var db: SQLiteDatabase? = null;

    fun OpenDatabase()
    {
        db = applicationDbContext.writableDatabase;
    }

    fun CloseDatabase()
    {
        applicationDbContext.close();
    }

    fun AddNote(note: Note)
    {
       // db = applicationDbContext.writableDatabase;
        val values = ContentValues().apply {
            put(DbUtilClass.COLUMN_NAME_BODY, note.Body);
            //put(DbUtilClass.COLUMN_NAME_CREATE_DATE, note.CreateTime);
        }
        db?.insert(DbUtilClass.TABLE_NAME, null, values);
    }

    fun GetNotes() : ArrayList<Note>
    {
        db = applicationDbContext.readableDatabase;
        val result = ArrayList<Note>();
        val cursor = db?.query(DbUtilClass.TABLE_NAME, null, null, null, null, null, null);

        with(cursor)
        {
            while (this?.moveToNext()!!)
            {
                val body = this?.getString(this.getColumnIndex(DbUtilClass.COLUMN_NAME_BODY).toInt());
                val notId = this?.getInt(this.getColumnIndex(BaseColumns._ID).toInt());

                result.add(Note(notId, body, null));
            }
        }

        return result;
    }

    fun GetNotesCount() : Int {
        return GetNotes().count().toInt();
    }

    /*
    fun getOneName(name: String): Contact? {
    val db = this.writableDatabase
    val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $colName = ?"
    db.rawQuery(selectQuery, arrayOf(name)).use { // .use requires API 16
        if (it.moveToFirst()) {
            val result = Contact()
            result.id = it.getInt(it.getColumnIndex(colId))
            result.name = it.getString(it.getColumnIndex(colName))
            return result
        }
    }
    return null
}
     */

    fun GetNoteFromId(note_id: Int) : Note? {
        db = applicationDbContext.writableDatabase;
        val selectQuery = "SELECT * FROM ${DbUtilClass.TABLE_NAME} WHERE ${BaseColumns._ID} = ?";
        db?.rawQuery(selectQuery, arrayOf(note_id.toString())).use {
            if (it!!.moveToFirst()) {
                val id = it.getInt(it.getColumnIndex(BaseColumns._ID).toInt());
                val body = it.getString(it.getColumnIndex(DbUtilClass.COLUMN_NAME_BODY).toInt());

                return Note(id, body, null);
            }
        }
        return null;
    }

    fun RemoveNote(note_id: Int) {
        db = applicationDbContext.writableDatabase;
        db?.delete(DbUtilClass.TABLE_NAME, BaseColumns._ID + "=?", arrayOf(note_id.toString()));
    }
}