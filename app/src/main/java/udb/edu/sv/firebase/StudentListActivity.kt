package udb.edu.sv.firebase

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import udb.edu.sv.firebase.models.Student

class StudentListActivity : AppCompatActivity() {

    private lateinit var recyclerViewStudents: RecyclerView
    private lateinit var studentAdapter: StudentAdapter
    private val studentsList = mutableListOf<Student>()
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        database = FirebaseDatabase.getInstance()
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents)
        recyclerViewStudents.layoutManager = LinearLayoutManager(this)

        studentAdapter = StudentAdapter(studentsList)
        recyclerViewStudents.adapter = studentAdapter

        loadStudents()
    }

    private fun loadStudents() {
        val studentsRef = database.getReference("students")
        studentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentsList.clear()
                for (studentSnapshot in snapshot.children) {
                    val student = studentSnapshot.getValue(Student::class.java)
                    if (student != null) {
                        studentsList.add(student)
                    }
                }
                studentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StudentListActivity, "Error al cargar datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}