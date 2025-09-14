package udb.edu.sv.firebase

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import udb.edu.sv.firebase.models.Student

class StudentAdapter(
    private val students: MutableList<Student>
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvStudentDetails: TextView = itemView.findViewById(R.id.tvStudentDetails)
        val tvStudentGrades: TextView = itemView.findViewById(R.id.tvStudentGrades)
        val btnEditStudent: Button = itemView.findViewById(R.id.btnEditStudent)
        val btnDeleteStudent: Button = itemView.findViewById(R.id.btnDeleteStudent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.tvStudentName.text = student.name
        holder.tvStudentDetails.text = "Edad: ${student.age}, Dirección: ${student.address}, Teléfono: ${student.phone}"


        val gradesText = StringBuilder("Notas:\n")

        val gradesMap = student.grades
        if (gradesMap.isNullOrEmpty()) {
            gradesText.append("Sin notas registradas")
        } else {
            gradesMap.values.forEach { grade ->
                gradesText.append("- ${grade.subject}: ${grade.finalGrade}\n")
            }
        }

        holder.tvStudentGrades.text = gradesText.toString()

        holder.btnEditStudent.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateDeleteStudentActivity::class.java)
            intent.putExtra("studentId", student.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.btnDeleteStudent.setOnClickListener {
            val studentRef = FirebaseDatabase.getInstance().getReference("students").child(student.id)
            studentRef.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Estudiante eliminado con éxito.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Error al eliminar estudiante: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount(): Int = students.size
}