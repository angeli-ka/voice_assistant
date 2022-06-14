package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.db.NoteManager
import com.example.myapplication.models.Note
import kotlinx.android.synthetic.main.activity_note_screen.*

class NoteScreenActivity : AppCompatActivity() {
    var noteManager = NoteManager(this);
    var note: Note? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_screen)
        val noteIdExtra: Int? = intent.getIntExtra("note_id", 0);

        if (noteIdExtra != null)
        {
            Toast.makeText(this, noteIdExtra.toString(), Toast.LENGTH_LONG).show();
            noteManager.OpenDatabase();
            note = noteManager.GetNoteFromId(noteIdExtra);

            if (note != null)
            {
                val id = note?.NoteId;
                val body = note?.Body;

                if (id != null && body != null)
                {
                    noteName.text = "Заметка #" + note?.NoteId;
                    noteArea.setText(body);

                    deleteNote.setOnClickListener {
                        noteManager.RemoveNote(id);
                        finish();
                    }
                }

            }
        }
        else
        {
            Toast.makeText(this, "Put not found", Toast.LENGTH_LONG).show();

        }
    }
}