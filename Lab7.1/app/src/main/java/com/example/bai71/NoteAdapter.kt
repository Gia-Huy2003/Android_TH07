package com.example.bai71

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class NoteAdapter(
    private val notes: MutableList<Note>,
    private val onNoteClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.viewTitleText)
        val content: TextView = itemView.findViewById(R.id.viewContentText)
        val timestamp: TextView = itemView.findViewById(R.id.viewTimestamp)
        val checkboxStudy: CheckBox = itemView.findViewById(R.id.checkboxStudy)
        val checkboxEntertainment: CheckBox = itemView.findViewById(R.id.checkboxEntertainment)
        val checkboxOther: CheckBox = itemView.findViewById(R.id.checkboxOther)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.title.text = note.title
        holder.content.text = note.content
        holder.timestamp.text = note.timestamp

        holder.checkboxStudy.isChecked = note.isStudy
        holder.checkboxEntertainment.isChecked = note.isEntertainment
        holder.checkboxOther.isChecked = note.isOther

        holder.itemView.setOnClickListener { onNoteClick(note) }

        if (note.isImportant) {
            holder.itemView.setBackgroundColor(Color.GREEN)
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#E0FFFF"))
        }

        holder.buttonDelete.setOnClickListener {
            note.id?.let { id -> deleteNote(id) }
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    private fun deleteNote(noteId: String) {
        FirebaseDatabase.getInstance().getReference("notes").child(noteId).removeValue()
    }
}
