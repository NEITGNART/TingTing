package com.example.tingting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitInstance
import com.example.tingting.utils.Entity.User
import kotlinx.coroutines.launch


private const val TAG = "UserInfoViewModel"

class UserInfoViewModel : ViewModel() {
    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user
    init {
        viewModelScope.launch {
            try {
                val fetchUser = RetrofitInstance.api.getUser()
                _user.value = fetchUser
                Log.i(TAG, "$fetchUser")
            } catch (
                e: Exception
            ) {
                Log.e(TAG, "Exception $e")
            }
        }
    }
}