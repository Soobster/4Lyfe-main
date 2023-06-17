package com.cs4530.a4lyfe.models.viewModels

import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cs4530.a4lyfe.models.HikeData
import com.cs4530.a4lyfe.models.Repository
import com.cs4530.a4lyfe.models.Repository.Companion.getInstance

class HikeViewModel(application: Application?) : AndroidViewModel(
    application!!
) {
    val hikesData: MutableLiveData<String>
    private val mRepository: Repository? = getInstance(application!!)

    //Hike
    suspend fun setHikeLocation(context: Context, location: Location?) {
        mRepository!!.setHikeLocation(context, location)
    }
    init {
        hikesData = mRepository!!.hikeData
    }
}