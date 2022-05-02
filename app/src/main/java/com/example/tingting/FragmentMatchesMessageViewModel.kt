package com.example.tingting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tingting.utils.Entity.User

private const val TAG = "Matches"

class FragmentMatchesMessageViewModel : ViewModel() {

    private val _matches: MutableLiveData<List<User?>> = MutableLiveData()
    val matches: LiveData<List<User?>> = _matches

    init {

    }

}
