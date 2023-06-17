package com.cs4530.a4lyfe.Intake.UserCreationCards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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


class NameCardFragment : UserEditFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_name_card, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firstNameET = view.findViewById<EditText>(R.id.first_name_ET)
        val lastNameET = view.findViewById<EditText>(R.id.last_name_ET)
        if (user != null) {
            firstNameET?.setText(user!!.firstName)
            lastNameET?.setText(user!!.lastName)
        }


    }

    override fun gatherData(): User {
        val tempUser = User()
        val firstNameET = view?.findViewById<EditText>(R.id.first_name_ET)
        val lastNameET = view?.findViewById<EditText>(R.id.last_name_ET)
        if (firstNameET != null && lastNameET != null) {
            tempUser.firstName = firstNameET.text.toString()
            tempUser.lastName = lastNameET.text.toString()
        }
        return tempUser
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param existing_user takes in an existing user from the activity; Used when editing
         * @return A new instance of fragment NameCardFragment.
         */
        @JvmStatic
        fun newInstance(existing_user: User?) =
            NameCardFragment().apply {
                arguments = Bundle().apply {
                    if (existing_user != null) {
                        putString(EXISTING_USER, Json.encodeToString(existing_user))
                    }
                }
            }
    }


}