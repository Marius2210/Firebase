package udb.edu.sv.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var btnSignOut: Button
    private lateinit var btnRegisterStudent: Button
    private lateinit var btnRegisterNotes: Button
    private lateinit var btnStudentList: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        btnSignOut = findViewById(R.id.btnSignOut)
        btnRegisterStudent = findViewById(R.id.btnRegisterStudent)
        btnRegisterNotes = findViewById(R.id.btnRegisterNotes)
        btnStudentList = findViewById(R.id.btnStudentList)

        btnSignOut.setOnClickListener {
            signOutUser()
        }

        btnRegisterStudent.setOnClickListener {
            val intent = Intent(this, RegisterStudentActivity::class.java)
            startActivity(intent)
        }

        btnRegisterNotes.setOnClickListener {
            val intent = Intent(this, RegisterNotesActivity::class.java)
            startActivity(intent)
        }

        btnStudentList.setOnClickListener {
            val intent = Intent(this, StudentListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signOutUser() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Toast.makeText(this, "Has cerrado sesión correctamente. ¡Hasta pronto! 👋", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}