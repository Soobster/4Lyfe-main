package com.cs4530.a4lyfe.models.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cs4530.a4lyfe.models.Repository
import com.cs4530.a4lyfe.models.User

class UserViewModel(application: Application?) : AndroidViewModel(
    application!!
) {
    val userData: MutableLiveData<User>
    private val mRepository: Repository = application?.let { Repository.getInstance(it) }!!

    init {
        userData = mRepository.userData
    }
}
