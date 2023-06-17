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
 * Use the [NameCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class WeightCardFragment : UserEditFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weight_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weightPicker = view.findViewById<NumberPicker>(R.id.weight_picker)
        weightPicker.maxValue = 500
        weightPicker.minValue = 0
        weightPicker.value = user?.inches?.toInt() ?: 200

    }

    override fun gatherData(): User {
        val tempUser = User()
        val weightPicker = view?.findViewById(R.id.weight_picker) as NumberPicker?
        if (weightPicker != null) {
            tempUser.weight = weightPicker.value.toDouble()
        }
        return tempUser
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param existing_user takes in an existing user from the activity; Used when editing
         * @return A new instance of fragment WeightCardFragment.
         */
        @JvmStatic
        fun newInstance(existing_user: User?) =
            WeightCardFragment().apply {
                arguments = Bundle().apply {
                    if (existing_user != null) {
                        putString(EXISTING_USER, Json.encodeToString(existing_user))
                    }
                }
            }
    }


}