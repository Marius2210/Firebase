package udb.edu.sv.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import udb.edu.sv.firebase.models.Student

class UpdateDeleteStudentActivity : AppCompatActivity() {

    private lateinit var etStudentName: EditText
    private lateinit var etStudentAge: EditText
    private lateinit var etStudentAddress: EditText
    private lateinit var etStudentPhone: EditText
    private lateinit var btnUpdateStudent: Button
    private lateinit var btnManageNotes: Button

    private lateinit var database: FirebaseDatabase
    private var studentId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_delete_student)

        database = FirebaseDatabase.getInstance()
        studentId = intent.getStringExtra("studentId") ?: ""

        etStudentName = findViewById(R.id.etStudentName)
        etStudentAge = findViewById(R.id.etStudentAge)
        etStudentAddress = findViewById(R.id.etStudentAddress)
        etStudentPhone = findViewById(R.id.etStudentPhone)
        btnUpdateStudent = findViewById(R.id.btnUpdateStudent)
        btnManageNotes = findViewById(R.id.btnManageNotes)

        loadStudentData()

        btnUpdateStudent.setOnClickListener {
            updateStudent()
        }

        btnManageNotes.setOnClickListener {
            val intent = Intent(this, UpdateDeleteNotesActivity::class.java)
            intent.putExtra("studentId", studentId)
            startActivity(intent)
        }
    }

    private fun loadStudentData() {
        if (studentId.isEmpty()) {
            Toast.makeText(this, "ID de estudiante no encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val studentRef = database.getReference("students").child(studentId)
        studentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val student = snapshot.getValue(Student::class.java)
                if (student != null) {
                    etStudentName.setText(student.name)
                    etStudentAge.setText(student.age.toString())
                    etStudentAddress.setText(student.address)
                    etStudentPhone.setText(student.phone)
                } else {
                    Toast.makeText(this@UpdateDeleteStudentActivity, "Estudiante no encontrado.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UpdateDeleteStudentActivity, "Error al cargar datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateStudent() {
        val name = etStudentName.text.toString().trim()
        val ageStr = etStudentAge.text.toString().trim()
        val address = etStudentAddress.text.toString().trim()
        val phone = etStudentPhone.text.toString().trim()

        if (name.isEmpty() || ageStr.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
            return
        }

        val age = ageStr.toInt()

        val studentData = mapOf(
            "name" to name,
            "age" to age,
            "address" to address,
            "phone" to phone
        )

        val studentRef = database.getReference("students").child(studentId)
        studentRef.updateChildren(studentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Estudiante actualizado con éxito.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar estudiante: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}