package com.feritkeskin.instaclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.feritkeskin.instaclone.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        supportActionBar?.hide()
        auth = Firebase.auth

        binding.btnSignUp.setOnClickListener {


            val userEmail = binding.signUpEmail.text.toString()
            val password = binding.signUpPassword.text.toString()

            if (userEmail.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(userEmail, password).addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            val intent = Intent(applicationContext, FeedActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}