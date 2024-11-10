package com.example.bai71

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private val notesList = mutableListOf<Note>()
    private val database = FirebaseDatabase.getInstance().getReference("notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        noteAdapter = NoteAdapter(notesList, this::onNoteClick)
        recyclerView.adapter = noteAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<FloatingActionButton>(R.id.fabAddNote).setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            startActivity(intent)
        }

        loadNotes()
    }

    private fun loadNotes() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    note?.let { notesList.add(it) }
                }

                notesList.sortByDescending { it.isImportant }
                noteAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Lỗi tải ghi chú!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onNoteClick(note: Note) {
        val intent = Intent(this, EditNoteActivity::class.java).apply {
            putExtra("noteId", note.id)
        }
        startActivity(intent)
    }
}
