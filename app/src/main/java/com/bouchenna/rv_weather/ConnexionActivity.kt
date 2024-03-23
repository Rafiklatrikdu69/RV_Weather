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

class ConnexionActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextMdp: TextInputEditText
    private lateinit var button: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var inscription: TextView

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
        setContentView(R.layout.activity_connexion)
        editTextMdp = findViewById(R.id.textInputEditTextMdp)
        editTextEmail = findViewById(R.id.textInputEditTextPseudo)
        button = findViewById(R.id.buttonConnexion)
        auth = FirebaseAuth.getInstance()
        inscription = findViewById(R.id.textViewToInscription)

        inscription.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
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
                auth.signInWithEmailAndPassword(email, mdp)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
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