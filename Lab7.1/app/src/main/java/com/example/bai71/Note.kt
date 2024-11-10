package com.example.bai71

data class Note(
    var id: String? = null,
    var title: String? = null,
    var content: String? = null,
    var isStudy: Boolean = false,       // Học tập
    var isEntertainment: Boolean = false, // Giải trí
    var isOther: Boolean = false,        // Khác
    var isImportant: Boolean = false,
    var timestamp: String? = null
)