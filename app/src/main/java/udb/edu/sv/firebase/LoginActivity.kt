package udb.edu.sv.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRedirectSignUp: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.editTextTextEmailAddress)
        etPass = findViewById(R.id.editTextTextPassword)
        btnLogin = findViewById(R.id.button_login)
        tvRedirectSignUp = findViewById(R.id.tvRedirectSignUp)

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            loginUser()
        }

        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val pass = etPass.text.toString().trim()

        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "⚠️ Email y contraseña no pueden estar vacíos.", Toast.LENGTH_LONG).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "⚠️ Por favor, ingresa un email válido.", Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "✅ ¡Inicio de sesión exitoso!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = task.exception?.message ?: "Error desconocido"
                    Toast.makeText(this, "❌ Error al iniciar sesión: $errorMessage", Toast.LENGTH_LONG).show()
                    println("FIREBASE LOGIN ERROR: $errorMessage")
                }
            }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}