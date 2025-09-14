package udb.edu.sv.firebase.models

data class Student(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val address: String = "",
    val phone: String = "",
    val grades: MutableMap<String, Grade> = mutableMapOf()
)