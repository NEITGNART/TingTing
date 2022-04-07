package com.example.tingting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tingting.utils.Entity.Matched
import com.example.tingting.utils.Entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val TAG = "Matches"

class FragmentMatchesMessageViewModel : ViewModel() {

    private val _matches: MutableLiveData<List<User?>> = MutableLiveData()
    val matches: LiveData<List<User?>> = _matches

    init {

    }

}
