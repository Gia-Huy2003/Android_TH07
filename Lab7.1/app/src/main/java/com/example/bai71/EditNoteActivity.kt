package com.example.bai71

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class EditNoteActivity : AppCompatActivity() {

    // Khai báo các View
    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button
    private lateinit var checkBoxImportant: CheckBox
    private lateinit var radioGroupCategory: RadioGroup
    private lateinit var radioButtonStudy: RadioButton
    private lateinit var radioButtonEntertainment: RadioButton
    private lateinit var radioButtonOther: RadioButton

    // Firebase Database
    private val database = FirebaseDatabase.getInstance().getReference("notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        // Khởi tạo các View
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete)
        checkBoxImportant = findViewById(R.id.checkBoxImportant)
        radioGroupCategory = findViewById(R.id.radioGroupCategory)
        radioButtonStudy = findViewById(R.id.radioButtonStudy)
        radioButtonEntertainment = findViewById(R.id.radioButtonEntertainment)
        radioButtonOther = findViewById(R.id.radioButtonOther)

        // Lấy ID ghi chú từ Intent
        val noteId = intent.getStringExtra("noteId")

        // Nếu có noteId, tải dữ liệu ghi chú từ Firebase
        if (noteId != null) {
            loadNote(noteId)
        }

        // Xử lý nút lưu ghi chú
        buttonSave.setOnClickListener { saveNote() }

        // Xử lý nút xóa ghi chú
        buttonDelete.setOnClickListener { deleteNote() }
    }

    // Hàm tải dữ liệu ghi chú từ Firebase
    private fun loadNote(noteId: String) {
        database.child(noteId).get().addOnSuccessListener {
            val note = it.getValue(Note::class.java)
            note?.let {
                // Hiển thị thông tin ghi chú lên giao diện
                editTextTitle.setText(it.title)
                editTextContent.setText(it.content)
                checkBoxImportant.isChecked = it.isImportant

                // Chọn radio button tương ứng với category đã lưu
                when {
                    it.isStudy -> radioButtonStudy.isChecked = true
                    it.isEntertainment -> radioButtonEntertainment.isChecked = true
                    it.isOther -> radioButtonOther.isChecked = true
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Không thể tải ghi chú!", Toast.LENGTH_SHORT).show()
        }
    }

    // Hàm lưu ghi chú vào Firebase
    private fun saveNote() {
        val title = editTextTitle.text.toString().trim()
        val content = editTextContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        // Lấy thời gian hiện tại làm timestamp
        val currentDate = System.currentTimeMillis()
        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(currentDate))

        // Tạo hoặc lấy ID ghi chú
        val noteId = intent.getStringExtra("noteId") ?: UUID.randomUUID().toString()

        // Lấy giá trị của category đã chọn từ RadioGroup
        val isStudy = radioButtonStudy.isChecked
        val isEntertainment = radioButtonEntertainment.isChecked
        val isOther = radioButtonOther.isChecked

        // Tạo đối tượng ghi chú
        val note = Note(
            id = noteId,
            title = title,
            content = content,
            isImportant = checkBoxImportant.isChecked,
            isStudy = isStudy,
            isEntertainment = isEntertainment,
            isOther = isOther,
            timestamp = timestamp
        )

        // Lưu ghi chú vào Firebase
        database.child(note.id!!).setValue(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Ghi chú đã được lưu!", Toast.LENGTH_SHORT).show()
                finish()  // Kết thúc Activity và quay lại màn hình trước đó
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lưu ghi chú thất bại!", Toast.LENGTH_SHORT).show()
            }
    }

    // Hàm xóa ghi chú khỏi Firebase
    private fun deleteNote() {
        val noteId = intent.getStringExtra("noteId")
        if (noteId != null) {
            database.child(noteId).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Ghi chú đã được xóa!", Toast.LENGTH_SHORT).show()
                    finish()  // Kết thúc Activity và quay lại màn hình trước đó
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Xóa ghi chú thất bại!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}