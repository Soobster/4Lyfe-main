package com.cs4530.a4lyfe.Intake.UserCreationCards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
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
 * Use the [WeightGoalCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class WeightGoalCardFragment : UserEditFragment() {
    private val weighChanges = arrayOf<Double?>(-2.0, -1.5, -1.0, -0.5, 0.0, +0.5, +1.0, +1.5, +2.0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weight_goal_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val goalSpinner = view.findViewById(R.id.weight_change_spinner) as Spinner?

        val ad = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, weighChanges)
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        if (goalSpinner != null) {
            goalSpinner.adapter = ad
            ad.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
            val pos = weighChanges.indexOf(user?.goal)
            if (pos == -1) {
                goalSpinner.setSelection(0)
            } else {
                goalSpinner.setSelection(pos)
            }
            goalSpinner.invalidate()
        }

    }

    override fun gatherData(): User {
        val tempUser = User()

        val goalSpinner = view?.findViewById(R.id.weight_change_spinner) as Spinner?

        if (goalSpinner != null) {
            tempUser.goal = goalSpinner.selectedItem as Double
        }
        return tempUser
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param existing_user takes in an existing user from the activity; Used when editing
         * @return A new instance of fragment WeightGoalCardFragment.
         */
        @JvmStatic
        fun newInstance(existing_user: User?) =
            WeightGoalCardFragment().apply {
                arguments = Bundle().apply {
                    if (existing_user != null) {
                        putString(EXISTING_USER, Json.encodeToString(existing_user))
                    }
                }
            }
    }


}