package udb.edu.sv.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import udb.edu.sv.firebase.models.Grade

class UpdateDeleteNotesActivity : AppCompatActivity() {

    private lateinit var recyclerViewGrades: RecyclerView
    private lateinit var btnAddGrade: FloatingActionButton
    private lateinit var gradeAdapter: GradeAdapter
    private val gradesList = mutableListOf<Grade>()
    private lateinit var database: FirebaseDatabase
    private var studentId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_delete_notes)

        database = FirebaseDatabase.getInstance()
        studentId = intent.getStringExtra("studentId") ?: ""

        if (studentId.isEmpty()) {
            Toast.makeText(this, "ID de estudiante no recibido.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerViewGrades = findViewById(R.id.recyclerViewGrades)
        btnAddGrade = findViewById(R.id.btnAddGrade)

        recyclerViewGrades.layoutManager = LinearLayoutManager(this)
        gradeAdapter = GradeAdapter(gradesList, this)
        recyclerViewGrades.adapter = gradeAdapter

        loadGrades()

        btnAddGrade.setOnClickListener {
            val intent = Intent(this, RegisterNotesActivity::class.java)
            intent.putExtra("studentId", studentId)
            startActivity(intent)
        }
    }

    private fun loadGrades() {
        val gradesRef = database.getReference("students").child(studentId).child("grades")
        gradesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gradesList.clear()
                for (gradeSnapshot in snapshot.children) {
                    val grade = gradeSnapshot.getValue(Grade::class.java)
                    if (grade != null) {
                        gradesList.add(grade)
                    }
                }
                gradeAdapter.notifyDataSetChanged()
                if (gradesList.isEmpty()) {
                    Toast.makeText(this@UpdateDeleteNotesActivity, "No hay notas registradas para este estudiante.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UpdateDeleteNotesActivity, "Error al cargar notas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}