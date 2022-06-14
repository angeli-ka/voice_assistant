package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.models.Note

class NoteAdapter(private val notes: List<Note>, private val context: Context) : RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var itemButton: Button = itemView.findViewById(R.id.itemButton)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.note_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemButton.text = "Заметка №" + (notes[position].NoteId);

        holder.itemButton.setOnClickListener {
            val intent = Intent(context, NoteScreenActivity::class.java);
            intent.putExtra("note_id", notes[position].NoteId);

            context.startActivity(intent);
        }
    }

    override fun getItemCount(): Int {
        return notes.size;
    }

}