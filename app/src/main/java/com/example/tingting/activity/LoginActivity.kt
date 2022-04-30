package com.example.tingting.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tingting.DAMapMarker
import com.example.tingting.R
import com.example.tingting.SignUpActivity
import com.example.tingting.databinding.ActivityLoginBinding
import com.example.tingting.utils.Entity.User
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityLoginBinding

    //    private lateinit var binding: LoginFragmentBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var callbackManager: CallbackManager

    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var mDatabaseReference: DatabaseReference

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

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


        binding.btnLoginGoogle.setOnClickListener {
            signIn()
        }


        binding.btnSignIn.setOnClickListener {
            getLocation()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty()) {
                binding.etEmail.error = "Please enter email"
                binding.etEmail.requestFocus()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Please enter valid email"
                binding.etEmail.requestFocus()
            } else if (password.isEmpty()) {
                binding.etPassword.error = "Please enter password"
                binding.etPassword.requestFocus()
            } else if (password.length < 6) {
                binding.etPassword.error = "Password should be at least 6 characters"
                binding.etPassword.requestFocus()
            } else {
                binding.pbLoading.visibility = android.view.View.VISIBLE

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            mDatabaseReference = FirebaseDatabase.getInstance().reference
                            mDatabaseReference
                                .child("Users")
                                .child(auth.currentUser?.uid.toString())
                                .child("firstTimeLogin")
                                .get().addOnSuccessListener {
                                    if (it.value == false) {
                                        goToHomePage()
                                    } else {
                                        goToFirstLoginPage()
                                    }
                                }
                        } else {

                            Toast.makeText(
                                this,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.pbLoading.visibility = android.view.View.GONE
                        }

                    }
            }

        }

        callbackManager = CallbackManager.Factory.create()


        // Login with Facebook

        binding.btnLoginFB.setReadPermissions("email", "public_profile")

        // Callback registration
        // Callback registration
        binding.btnLoginFB.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }

            override fun onSuccess(result: LoginResult?) {
                Log.d(TAG, "facebook:onSuccess:$result")
                result?.let { handleFacebookAccessToken(it.accessToken) }
            }
        })

        binding.lgFB.setOnClickListener {
            binding.btnLoginFB.performClick()
        }


        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        binding.pbLoading.visibility = android.view.View.VISIBLE
        val credential = FacebookAuthProvider.getCredential(token.token)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    mDatabaseReference = FirebaseDatabase.getInstance().reference
                    mDatabaseReference
                        .child("Users")
                        .child(auth.currentUser?.uid.toString())
                        .child("firstTimeLogin")
                        .get().addOnSuccessListener {
                            if (it.value == false) {
                                // get current avatar on facebook and store in user
                                goToHomePage()
                            } else
                                goToFirstLoginPage()
                        }


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    binding.pbLoading.visibility = android.view.View.GONE
                }
            }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

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
                    // check that user id in database
                    val user = auth.currentUser
                    val uid = user!!.uid
                    val database = FirebaseDatabase.getInstance().reference
                    val userRef = database.child("Users").child(uid)

                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            if (dataSnapshot.exists()) {
                                goToHomePage()
                            } else {

                                val user = User(uid)
                                userRef.setValue(user)
                                goToFirstLoginPage()

                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                        }
                    })

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

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }
    override fun onLocationChanged(location: Location) {
        Log.i("hihi", "Latitude: " + location.latitude + " , Longitude: " + location.longitude)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
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