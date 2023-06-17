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
 * Use the [LifestyleCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class LifestyleCardFragment : UserEditFragment() {
    private val lifestyles = arrayOf<String?>("Sedentary", "Active")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lifestyle_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lifestyleSpin = view.findViewById(R.id.lifestyle_spinner) as Spinner?

        if (lifestyleSpin != null) {
            val ad2 = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, lifestyles)
            lifestyleSpin.adapter = ad2
            val pos = lifestyles.indexOf(user?.lifestyle)
            if (pos != -1) {
                lifestyleSpin.setSelection(pos)
            } else {
                lifestyleSpin.setSelection(0)
            }
            lifestyleSpin.invalidate()

        }

    }

    override fun gatherData(): User {
        val tempUser = User()
        val lifestyleSpin = view?.findViewById(R.id.lifestyle_spinner) as Spinner?

        if (lifestyleSpin != null) {
            tempUser.lifestyle = lifestyleSpin.selectedItem as String
        }
        return tempUser
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param existing_user takes in an existing user from the activity; Used when editing
         * @return A new instance of fragment LifestyleCardFragment.
         */
        @JvmStatic
        fun newInstance(existing_user: User?) =
            LifestyleCardFragment().apply {
                arguments = Bundle().apply {
                    if (existing_user != null) {
                        putString(EXISTING_USER, Json.encodeToString(existing_user))
                    }
                }
            }
    }


}