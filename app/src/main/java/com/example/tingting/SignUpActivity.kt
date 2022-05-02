package com.example.tingting

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tingting.activity.FirstLogin
import com.example.tingting.databinding.ActivitySignUpBinding
import com.example.tingting.utils.Entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivTogglePswd.setOnClickListener {
            // toogle password visibility
            binding.etMobilePassword.transformationMethod =
                if (binding.etMobilePassword.transformationMethod == null) {
                    android.text.method.PasswordTransformationMethod.getInstance()
                } else {
                    null
                }
        }

        binding.ivReTogglePswd.setOnClickListener {
            // toogle password visibility
            binding.etReMobilePassword.transformationMethod =
                if (binding.etReMobilePassword.transformationMethod == null) {
                    android.text.method.PasswordTransformationMethod.getInstance()
                } else {
                    null
                }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etMobilePassword.text.toString()
            val passwordConfirm = binding.etMobilePassword.text.toString()
            if (password != passwordConfirm) {
                Toast.makeText(
                    this,
                    "Password and confirm password are not same",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (email.isEmpty() || password.isEmpty())
            // check that email and password are not empty
            {
                // check that email is valid format

                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.etEmail.error = "Please enter a valid email address"
                    binding.etEmail.requestFocus()
                } else {
                    signUpWithEmail(email, password)
                }

            }
        }
    }

    // sign up with email and password
    private fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val currentUser = auth.currentUser

                    val user = User(
                        currentUser!!.uid,
                        null,
                        "",
                        "",
                        "",
                        "",
                        "",
                        firstTimeLogin = true
                    )

                    Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, FirstLogin::class.java))

                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            val mDatabaseReference = FirebaseDatabase.getInstance().reference
                            mDatabaseReference.child("Users").child(currentUser.uid).setValue(user)

                        }
                    }
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}