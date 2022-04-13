package com.example.tingting.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tingting.R
import com.example.tingting.SignUpActivity
import com.example.tingting.databinding.ActivityLoginBinding
import com.example.tingting.utils.Entity.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
//    private lateinit var binding: LoginFragmentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var auth2: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.clientId))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        // Firebase
        auth = Firebase.auth
        auth2 = FirebaseAuth.getInstance()

        binding.btnLoginGoogle.setOnClickListener {
            signIn()
        }

        var mDatabaseReference: DatabaseReference
        binding.btnSignIn.setOnClickListener {
            // get email and password
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isEmpty() && password.isEmpty())
                goToHomePage()
            else
                loginWithEmail(email, password)
        }


        binding.btnLoginFB.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            auth2.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{task ->
                    if (task.isSuccessful) {
                        goToFirstLoginPage()
                    }
                    else
                        Log.i("hihi", "failed")
                }
        }

//        var database = FirebaseDatabase.getInstance().reference
//        var user = database.child("Users").child("-LrDEBoLokW-5mhaT3ys")
//
//        user.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//
//                val post = dataSnapshot.getValue(User::class.java)
//                Log.i("LoginActicvity", "$post")
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//        })

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
//                Toast.makeText(this, "firebaseAuthWithGoogle:" + account.id, Toast.LENGTH_SHORT)
//                    .show()
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
//                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun goToHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // can't go back
        startActivity(intent)
        finish()
    }

    fun goToFirstLoginPage() {
        val intent = Intent(this, FirstLogin::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // can't go back
        startActivity(intent)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
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

    fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    goToHomePage()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // ...
            }

    }



    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}
//////////SIGN UP/////////////////////////
//val email = binding.etEmail.text.toString()
//val password = binding.etPassword.text.toString()
//auth2.createUserWithEmailAndPassword(email, password)
//.addOnCompleteListener{task ->
//    Log.i("hihi", auth2.currentUser?.uid.toString())
//    if (task.isSuccessful) {
//        goToFirstLoginPage()
//        val user = User(
//            "",
//            "",
//            "",
//            "",
//            auth2.currentUser?.uid,
//            emptyList(),
//            emptyList(),
//            "",
//            "",
//            ""
//        )
//        mDatabaseReference = FirebaseDatabase.getInstance().reference
//        mDatabaseReference.child("Users").child(auth2.currentUser?.uid.toString()).setValue(user)
//    }
//    else
//        Log.i("hihi", "failed")
//}