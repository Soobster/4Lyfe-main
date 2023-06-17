package com.cs4530.a4lyfe.pages.Weather

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.cs4530.a4lyfe.ObserverFragment
import com.cs4530.a4lyfe.R
import com.cs4530.a4lyfe.models.WeatherData
import com.cs4530.a4lyfe.models.decodeWeatherData
import com.cs4530.a4lyfe.models.viewModels.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalTime


/*
    Creates a WeatherFragment fragment. A WeatherFragment takes in the data inputted by the user
    and displays weather information related to that data on itself.
 */
class WeatherFragment : ObserverFragment<WeatherData>() {
    private var mTvTemp: TextView? = null
    private var mTvCondition: TextView? = null
    private var mTvUpdated: TextView? = null
    private var mTvAddress: TextView? = null
    private var mWeatherData: WeatherData? = null
    private var loader: ProgressBar? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        //Create the view model
        lifecycleScope.launch(Dispatchers.Main) {
            whenStarted {
                assert(activityInterface != null)
                activityInterface?.repo?.mUserDao?.allWeather?.collect {
                    launch(Dispatchers.Main) {
                        if (it.isNotEmpty()) {
                            val weather = it[0]
                            activityInterface?.repo?.weatherData?.value = decodeWeatherData(weather)
                        } else {
                            for (i in 0..2) {
                                try {
                                    activityInterface!!.getLocation()!!.latitude
                                } catch (e: Exception) {
                                    delay(2000)
                                }
                                val location = activityInterface?.getLocation()
                                if (location == null) {
                                    delay(1000)
                                } else {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        setWeatherLocation(location)
                                    }
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun setWeatherLocation(location: Location) {
        val mWeatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        mWeatherViewModel.setWeatherLocation(location)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onResume() {
        super.onResume()
        view?.requestLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTvTemp = view.findViewById(R.id.tv_temp) as TextView?
        mTvCondition = view.findViewById(R.id.tv_condition) as TextView?
        mTvUpdated = view.findViewById(R.id.updated) as TextView?
        mTvAddress = view.findViewById(R.id.address) as TextView?
        loader = view.findViewById(R.id.weather_loader) as ProgressBar?
    }

    override suspend fun setPageElements(objectToObserve: WeatherData?) {
        val view = requireView()
        lifecycleScope.launch(Dispatchers.Main) {
            mWeatherData = objectToObserve
            if (mWeatherData != null) {
                val kelvinToFahrenheit = mWeatherData!!.temperature.getTempInFahrenheit()
                val time = LocalTime.now()
                val condition = mWeatherData!!.currentCondition.descr
                mTvTemp?.setPadding(16, 0, 16, 0)
                mTvCondition?.setPadding(24, 0, 24, 0)
                loader?.visibility = View.GONE
                mTvTemp?.text = "$kelvinToFahrenheit °F"
                mTvCondition?.text = condition
                mTvUpdated?.text = time.toString()
                mTvAddress?.text = mWeatherData?.city
                view.requestLayout()
            }
        }
    }

    companion object {
        fun newInstance(): WeatherFragment {
            return WeatherFragment()
        }
    }

    override suspend fun observeDataBootstrap(data: WeatherData?) {
        val liveData = ViewModelProvider(this).get(
            WeatherViewModel::class.java
        ).weatherData
        liveData.observe(viewLifecycleOwner, observer)
        liveData.value?.let { setPageElements(it) }
    }

}