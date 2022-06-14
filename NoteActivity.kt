package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.db.NoteManager
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity() {
    var noteManager = NoteManager(this);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        noteManager.OpenDatabase();
        recyclerView.adapter = NoteAdapter(noteManager.GetNotes(), this);
    }

    override fun onResume() {
        //Toast.makeText(this, "Вы нажали !", Toast.LENGTH_LONG).show();
        noteManager.OpenDatabase();
        recyclerView.adapter = NoteAdapter(noteManager.GetNotes(), this);
        super.onResume()
    }



}