package com.cs4530.a4lyfe

//import android.R

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.cs4530.a4lyfe.models.Repository
import com.cs4530.a4lyfe.models.User
import com.cs4530.a4lyfe.pages.FitnessFragment
import com.cs4530.a4lyfe.pages.HikeFragment
import com.cs4530.a4lyfe.pages.Weather.WeatherFragment


private const val weatherPageName = "Weather"
private const val hikePageName = "Hikes"
private const val bmiPageName = "Fitness"
private lateinit var mDetector: GestureDetectorCompat
private const val DEBUG_TAG = "Gestures"


private var actInt: ActivityInterface? = null

/**
 * A simple [Fragment] subclass.
 * Use the [MainPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainPage : ObserverFragment<User>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_main_page, container, false)



        val temperatureLong= activityInterface?.repo?.weatherData?.value?.temperature?.getTempInFahrenheit()

        lateinit var temperature: String
        if(temperatureLong == null) {
            temperature = ""
        }
        else {
            temperature = temperatureLong.toString()
        }

        // display weather button fragment
        activityInterface?.replaceFragment(
            R.id.weather_button, MainPageButtonFrag.newInstance(
                WeatherFragment::class.java,
                weatherPageName,
                temperature,
                R.drawable.ic_baseline_cloud_24,
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            ), weatherPageName, true
        )

        // display hiking button fragment
        activityInterface?.replaceFragment(
            R.id.hike_button,
            MainPageButtonFrag.newInstance(
                HikeFragment::class.java,
                hikePageName,
                null,
                R.drawable.ic_baseline_nature_people_24,
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            ),
            hikePageName, true,
        )

        // display BMI button fragment
        activityInterface?.replaceFragment(
            R.id.bmi_button, MainPageButtonFrag.newInstance(
                FitnessFragment::class.java,
                bmiPageName,
                null,
                R.drawable.ic_baseline_auto_graph_24,
                null
            ),
            bmiPageName, true
        )

        actInt = activityInterface
        mDetector = GestureDetectorCompat(activityInterface?.getContext(), MyGestureListener())
        view.setOnTouchListener(touchListener)

        return view
    }

    // This touch listener passes everything on to the gesture detector.
    // That saves us the trouble of interpreting the raw touch events
    // ourselves.
    @SuppressLint("ClickableViewAccessibility")
    private var touchListener: View.OnTouchListener = View.OnTouchListener { _, event -> // pass the events to the gesture detector
        // a return value of true means the detector is handling it
        // a return value of false means the detector didn't
        // recognize the event
        mDetector.onTouchEvent(event)
    }

    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    internal class MyGestureListener : SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            //Log.d("TAG", "onDown: ")
            // don't return false here or else none of the other
            // gestures will work
            return true
        }
        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.i("TAG", "onDoubleTap: ")

            if(actInt?.getStepCountEnabled() as Boolean){
                actInt?.setStepCountEnabled()
                actInt?.startStepCounter()
                actInt?.showAlert("Step Counter Has Been Disabled",
                    "Double Tap again to enable", "ok")
            }

            else{
                actInt?.setStepCountEnabled()
                actInt?.stopStepCounter()
                actInt?.showAlert("Step Counter Has Been Enabled",
                    "Double Tap again to disable", "ok")
            }


            return true
        }
    }
    override suspend fun observeDataBootstrap(data: User?) {
        val liveData = Repository.getInstance(requireActivity().application)?.userData
        liveData?.observe(viewLifecycleOwner, observer)
        liveData?.value?.let { setPageElements(it) }
    }

    override suspend fun setPageElements(objectToObserve: User?) {
        val welcome = requireView().findViewById<TextView>(R.id.user_welcome_text)
        if (welcome != null) {
            ("Welcome, " + objectToObserve?.firstName).also { welcome.text = it }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MainPage.
         */
        @JvmStatic
        fun newInstance() =
            MainPage().apply {
                arguments = Bundle().apply {
                }
            }
    }
}