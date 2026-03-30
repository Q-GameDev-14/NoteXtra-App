package com.example.notextra.domain.model

// Data class untuk opsi dropdown
data class DropdownOption(
    val label: String,
    val colorHex: String // Disimpan sebagai String Hex, misal: "#DCFCE7"
)

// Data class untuk struktur kolom
data class ColumnConfig(
    var name: String = "",
    var width: String = "Normal",
    var type: String = "Text",
    var dropdownOptions: List<DropdownOption> = emptyList()
)