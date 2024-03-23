package com.bouchenna.rv_weather

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextMdp: TextInputEditText
    private lateinit var button: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var toConnection: TextView

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editTextMdp = findViewById(R.id.textInputEditTextMdpInscription)
        editTextEmail = findViewById(R.id.textInputEditTextPseudoInscription)
        button = findViewById(R.id.buttonInscription)
        auth = FirebaseAuth.getInstance()
        toConnection = findViewById(R.id.textViewToConnection)

        toConnection.setOnClickListener{
            val intent = Intent(this, ConnexionActivity::class.java)
            startActivity(intent)
            finish()
        }

        button.setOnClickListener{
            var email: String = editTextEmail.text.toString()
            var mdp: String = editTextMdp.text.toString()

            if(email.isEmpty()){
                Toast.makeText(this, "Saisir un email", Toast.LENGTH_SHORT).show()
            }
            else if(mdp.isEmpty()){
                Toast.makeText(this, "Saisir un mot de passe", Toast.LENGTH_SHORT).show()
            }
            else{
                auth.createUserWithEmailAndPassword(email, mdp)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            finish()

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()

                        }
                    }
            }
        }
    }
}