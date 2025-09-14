package udb.edu.sv.firebase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import udb.edu.sv.firebase.models.Student

class RegisterStudentActivity : AppCompatActivity() {

    private lateinit var etStudentName: EditText
    private lateinit var etStudentAge: EditText
    private lateinit var etStudentAddress: EditText
    private lateinit var etStudentPhone: EditText
    private lateinit var btnSaveStudent: Button

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_student)

        database = FirebaseDatabase.getInstance()

        etStudentName = findViewById(R.id.etStudentName)
        etStudentAge = findViewById(R.id.etStudentAge)
        etStudentAddress = findViewById(R.id.etStudentAddress)
        etStudentPhone = findViewById(R.id.etStudentPhone)
        btnSaveStudent = findViewById(R.id.btnSaveStudent)

        btnSaveStudent.setOnClickListener {
            saveStudent()
        }
    }

    private fun saveStudent() {
        val name = etStudentName.text.toString().trim()
        val ageStr = etStudentAge.text.toString().trim()
        val address = etStudentAddress.text.toString().trim()
        val phone = etStudentPhone.text.toString().trim()

        // Validate that fields are not empty [cite: 31]
        if (name.isEmpty() || ageStr.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
            return
        }

        val age = ageStr.toInt()

        val studentsRef = database.getReference("students")
        val studentId = studentsRef.push().key ?: return

        val student = Student(studentId, name, age, address, phone)

        studentsRef.child(studentId).setValue(student)
            .addOnSuccessListener {
                Toast.makeText(this, "Estudiante guardado exitosamente.", Toast.LENGTH_SHORT).show()
                etStudentName.setText("")
                etStudentAge.setText("")
                etStudentAddress.setText("")
                etStudentPhone.setText("")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar estudiante: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}