package udb.edu.sv.firebase

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import udb.edu.sv.firebase.models.Grade

class GradeAdapter(
    private val grades: MutableList<Grade>,
    private val context: Context
) : RecyclerView.Adapter<GradeAdapter.GradeViewHolder>() {

    class GradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGradeSubject: TextView = itemView.findViewById(R.id.tvGradeSubject)
        val tvGradeValue: TextView = itemView.findViewById(R.id.tvGradeValue)
        val btnEditGrade: Button = itemView.findViewById(R.id.btnEditGrade)
        val btnDeleteGrade: Button = itemView.findViewById(R.id.btnDeleteGrade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grade, parent, false)
        return GradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        val grade = grades[position]
        holder.tvGradeSubject.text = "Materia: ${grade.subject}"
        holder.tvGradeValue.text = "Nota: ${grade.finalGrade}"

        // Lógica para eliminar una nota
        holder.btnDeleteGrade.setOnClickListener {
            val gradeRef = FirebaseDatabase.getInstance()
                .getReference("students")
                .child(grade.studentId)
                .child("grades")
                .child(grade.id)

            gradeRef.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Nota eliminada con éxito.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al eliminar nota: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Lógica para editar una nota
        holder.btnEditGrade.setOnClickListener {
            showEditGradeDialog(grade)
        }
    }

    override fun getItemCount(): Int = grades.size

    private fun showEditGradeDialog(grade: Grade) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_grade, null)
        val etNewGrade = dialogView.findViewById<EditText>(R.id.etNewGrade)
        etNewGrade.setText(grade.finalGrade.toString())

        AlertDialog.Builder(context)
            .setTitle("Editar Nota de ${grade.subject}")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val newGradeValue = etNewGrade.text.toString().toDoubleOrNull()
                if (newGradeValue != null && newGradeValue in 0.0..10.0) {
                    val gradeRef = FirebaseDatabase.getInstance()
                        .getReference("students")
                        .child(grade.studentId)
                        .child("grades")
                        .child(grade.id)
                    gradeRef.child("finalGrade").setValue(newGradeValue)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Nota actualizada con éxito.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al actualizar la nota.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Nota inválida. Ingrese un valor entre 0 y 10.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}