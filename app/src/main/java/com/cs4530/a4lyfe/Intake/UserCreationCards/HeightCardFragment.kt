package com.cs4530.a4lyfe.Intake.UserCreationCards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.cs4530.a4lyfe.R
import com.cs4530.a4lyfe.UserEditFragment
import com.cs4530.a4lyfe.models.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val EXISTING_USER = "user"

/**
 * A simple [Fragment] subclass.
 * Use the [HeightCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class HeightCardFragment : UserEditFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_height_card, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val heightInches = view.findViewById(R.id.height_picker_inches) as NumberPicker?
        val heightFeet = view.findViewById(R.id.height_picker_feet) as NumberPicker?
        if (heightFeet != null || heightInches != null) {
            heightFeet!!.maxValue = 7
            heightFeet.minValue = 3
            heightInches!!.maxValue = 11
            heightInches.minValue = 0
            heightFeet.value = user?.feet?.toInt() ?: 5
            heightInches.value = user?.feet?.toInt() ?: 6

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param existing_user takes in an existing user from the activity; Used when editing
         * @return A new instance of fragment HeightCardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(existing_user: User?) =
            HeightCardFragment().apply {
                arguments = Bundle().apply {
                    if (existing_user != null) {
                        putString(EXISTING_USER, Json.encodeToString(existing_user))
                    }
                }
            }
    }

    override fun gatherData(): User {
        val tempUser = User()
        val heightFeet = view?.findViewById(R.id.height_picker_feet) as NumberPicker?
        val heightInches = view?.findViewById(R.id.height_picker_inches) as NumberPicker?

        if (heightFeet != null && heightInches != null) {
            tempUser.feet = heightFeet.value.toDouble()
            tempUser.inches = heightInches.value.toDouble()
            tempUser.height = (((heightFeet.value) * 12) + (heightInches.value)).toDouble()
        }
        return tempUser
    }
}