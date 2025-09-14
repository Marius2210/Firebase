package udb.edu.sv.firebase

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import udb.edu.sv.firebase.models.Student
import udb.edu.sv.firebase.models.Grade

class RegisterNotesActivity : AppCompatActivity() {

    private lateinit var spinnerStudents: Spinner
    private lateinit var spinnerSubjects: Spinner
    private lateinit var etFinalGrade: EditText
    private lateinit var btnSaveGrade: Button

    private lateinit var database: FirebaseDatabase
    private val studentsList = mutableListOf<Student>()
    private val studentNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_notes)

        database = FirebaseDatabase.getInstance()

        spinnerStudents = findViewById(R.id.spinnerStudents)
        spinnerSubjects = findViewById(R.id.spinnerSubjects)
        etFinalGrade = findViewById(R.id.etFinalGrade)
        btnSaveGrade = findViewById(R.id.btnSaveGrade)

        loadStudentsIntoSpinner()
        setupSubjectsSpinner()

        btnSaveGrade.setOnClickListener {
            saveGrade()
        }
    }

    private fun loadStudentsIntoSpinner() {
        val studentsRef = database.getReference("students")
        studentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentsList.clear()
                studentNames.clear()
                for (studentSnapshot in snapshot.children) {
                    val student = studentSnapshot.getValue(Student::class.java)
                    if (student != null) {
                        studentsList.add(student)
                        studentNames.add(student.name)
                    }
                }
                val adapter = ArrayAdapter(this@RegisterNotesActivity, android.R.layout.simple_spinner_item, studentNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerStudents.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RegisterNotesActivity, "Error al cargar estudiantes: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSubjectsSpinner() {
        val subjects = arrayOf("Matemáticas", "Ciencias", "Lenguaje", "Sociales") // You can change these subjects
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubjects.adapter = adapter
    }

    private fun saveGrade() {
        val selectedStudentIndex = spinnerStudents.selectedItemPosition
        if (selectedStudentIndex == -1 || studentsList.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un estudiante.", Toast.LENGTH_SHORT).show()
            return
        }

        val studentId = studentsList[selectedStudentIndex].id
        val subject = spinnerSubjects.selectedItem.toString()
        val finalGradeStr = etFinalGrade.text.toString().trim()

        if (finalGradeStr.isEmpty()) {
            Toast.makeText(this, "Debe ingresar una nota.", Toast.LENGTH_SHORT).show()
            return
        }

        val finalGrade = finalGradeStr.toDouble()

        if (finalGrade < 0 || finalGrade > 10) {
            Toast.makeText(this, "La nota debe estar entre 0 y 10.", Toast.LENGTH_SHORT).show()
            return
        }

        val gradesRef = database.getReference("students").child(studentId).child("grades")
        val gradeId = gradesRef.push().key ?: return

        val grade = Grade(gradeId, studentId, subject, finalGrade)

        gradesRef.child(gradeId).setValue(grade)
            .addOnSuccessListener {
                Toast.makeText(this, "Nota guardada exitosamente.", Toast.LENGTH_SHORT).show()
                etFinalGrade.setText("")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar nota: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}