package com.cs4530.a4lyfe.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.cs4530.a4lyfe.ObserverFragment
import com.cs4530.a4lyfe.R
import com.cs4530.a4lyfe.models.Repository
import com.cs4530.a4lyfe.models.User
import com.cs4530.a4lyfe.models.decodeLocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import kotlin.math.pow
import kotlin.math.roundToLong

class FitnessFragment : ObserverFragment<User>() {
    private var bmiUpdated: TextView? = null
    private var stepsUpdated: TextView? = null
    private var bmrUpdated: TextView? = null
    private var calorieUpdated: TextView? = null

    override suspend fun observeDataBootstrap(data: User?) {
        val liveData = Repository.getInstance(requireActivity().application)?.userData
        liveData?.observe(viewLifecycleOwner, observer)
        liveData?.value?.let { setPageElements(it) }
    }

    override suspend fun setPageElements(objectToObserve: User?) {
        if (objectToObserve != null) {
            val view = requireView()

            val weight = objectToObserve.weight ?: 0.0
            val height = objectToObserve.height ?: 0.0

            //Find BMR
            val sex: String? = objectToObserve.sex
            val now = LocalDate.now()
            val birthday = decodeLocalDate(objectToObserve.birthday!!)
            val p = Period.between(birthday, now)
            val age = p.years
            val bmr =
                when (sex) {
                    "Male" -> (66.47 + 6.24 * weight + 12.7 * height - 6.755 * age).roundToLong()
                        .toDouble()
                    "Female" -> (655.1 + 4.35 * weight + 4.7 * height - 4.7 * age).roundToLong()
                        .toDouble()
                    else -> {
                        //Using male for non-binary until equation is found
                        (66.47 + 6.24 * weight + 12.7 * height - 6.755 * age).roundToLong()
                            .toDouble()
                    }
                }
            //Find Calorie Goal
            val lifestyle = objectToObserve.lifestyle
            val goal = objectToObserve.goal ?: 0.0
            val calories = if (lifestyle == "Sedentary") {
                bmr * 1.2 + goal * 500
            } else {
                bmr * 1.6 + goal * 500
            }
            val bmi = (weight / height.pow(2.0) * 703).roundToLong().toDouble()

            //Update UI
            lifecycleScope.launch(Dispatchers.Main) {

                stepsUpdated = view.findViewById<View>(R.id.steps_updated) as TextView?
                stepsUpdated?.text = "0"
                bmiUpdated = view.findViewById<View>(R.id.bmi_updated) as TextView?
                bmiUpdated?.text = bmi.toString()
                bmrUpdated = view.findViewById<View>(R.id.bmr_updated) as TextView?
                bmrUpdated?.text = bmr.toString()
                calorieUpdated = view.findViewById<View>(R.id.calorie_updated) as TextView?
                if (calories < 1200 && sex == "male") {
                    calorieUpdated?.text =
                        "Too few calories"
                } else if (calories < 1000 && sex == "Female") {
                    calorieUpdated?.text =
                        "Too few calories"
                } else {
                    calorieUpdated?.text = calories.roundToLong().toDouble().toString()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fitness, container, false)
    }

    companion object {
        fun newInstance(): FitnessFragment {
            return FitnessFragment()
        }
    }
}