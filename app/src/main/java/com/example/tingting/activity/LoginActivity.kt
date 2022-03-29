package com.example.tingting.activity

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tingting.databinding.LoginFragmentBinding
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginFragmentBinding
    private val RC_SIGN_IN = 2
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        // Firebas
        auth = FirebaseAuth.getInstance()

        binding.btnLoginGoogle.setOnClickListener {
            signIn()
        }

        binding.btnSignIn.setOnClickListener {
            goToHomePage()
        }


    }

    private fun LoginActivity.signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == RC_SIGN_IN) {

            val credential = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (credential.isSuccessful) {
                val account = credential.result
                firebaseAuthWithGoogle(account?.idToken!!)
            } else {
                // account does not exist
                Toast.makeText(this, "Account doesn't register", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }


    fun goToHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // can't go back
        startActivity(intent)
    }


    fun firebaseAuthWithGoogle(idToken: String) {
        val authCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(authCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    goToHomePage()
                } else {
                    // If sign in fails, display a message to the user.
                    // ...
                    // show toast
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

    }


}