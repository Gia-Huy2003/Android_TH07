package com.example.bai71

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class NoteAdapter(
    private val notes: MutableList<Note>, // MutableList để có thể thay đổi dữ liệu
    private val onNoteClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.viewTitleText)
        val content: TextView = itemView.findViewById(R.id.viewContentText)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete) // Add delete button
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
        holder.itemView.setOnClickListener { onNoteClick(note) }

        // Đổi màu nền nếu ghi chú là quan trọng
        if (note.isImportant) {
            holder.itemView.setBackgroundColor(android.graphics.Color.GREEN) // Màu xanh cho quan trọng
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#E0FFFF"))// Màu trắng nếu không quan trọng
        }

        holder.buttonDelete.setOnClickListener {
            note.id?.let { id -> deleteNote(id) }
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    // Cập nhật danh sách ghi chú
    fun updateNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged() // Thông báo RecyclerView cập nhật dữ liệu
    }

    private fun deleteNote(noteId: String) {
        FirebaseDatabase.getInstance().getReference("notes").child(noteId).removeValue()
    }
}
