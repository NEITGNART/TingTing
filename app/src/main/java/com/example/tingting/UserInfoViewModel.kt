package com.example.tingting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitInstance
import com.example.tingting.utils.Entity.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


private const val TAG = "UserInfoViewModel"

class UserInfoViewModel : ViewModel() {
    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    fun getUser(userId: String): LiveData<User> {
        viewModelScope.launch {
            try {
                val fetchUser = RetrofitInstance.api.getUser(userId)
                _user.value = fetchUser
            } catch (
                e: Exception
            ) {
                Log.e(TAG, "Exception $e")
            }
        }
        return _user
    }

}