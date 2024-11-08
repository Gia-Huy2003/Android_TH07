package com.example.bai71

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class EditNoteActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button
    private lateinit var checkBoxImportant: CheckBox // CheckBox để đánh dấu quan trọng
    private val database = FirebaseDatabase.getInstance().getReference("notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete)
        checkBoxImportant = findViewById(R.id.checkBoxImportant) // Khởi tạo CheckBox

        val noteId = intent.getStringExtra("noteId")

        if (noteId != null) {
            loadNote(noteId)
        }

        buttonSave.setOnClickListener { saveNote() }
        buttonDelete.setOnClickListener { deleteNote() }
    }

    private fun loadNote(noteId: String) {
        database.child(noteId).get().addOnSuccessListener {
            val note = it.getValue(Note::class.java)
            note?.let {
                editTextTitle.setText(it.title)
                editTextContent.setText(it.content)
                checkBoxImportant.isChecked = it.isImportant // Load trạng thái của CheckBox
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Không thể tải ghi chú!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNote() {
        val title = editTextTitle.text.toString().trim()
        val content = editTextContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        val noteId = intent.getStringExtra("noteId") ?: UUID.randomUUID().toString()
        val note = Note(
            id = noteId,
            title = title,
            content = content,
            isImportant = checkBoxImportant.isChecked // Lưu trạng thái của CheckBox
        )

        database.child(note.id!!).setValue(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Ghi chú đã được lưu!", Toast.LENGTH_SHORT).show()
                finish() // Quay lại activity trước đó
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lưu ghi chú thất bại!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteNote() {
        val noteId = intent.getStringExtra("noteId")
        if (noteId != null) {
            database.child(noteId).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Ghi chú đã được xóa!", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Xóa ghi chú thất bại!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
