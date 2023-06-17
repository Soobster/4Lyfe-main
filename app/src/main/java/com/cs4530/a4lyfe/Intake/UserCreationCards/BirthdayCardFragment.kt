package com.cs4530.a4lyfe.Intake.UserCreationCards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.cs4530.a4lyfe.R
import com.cs4530.a4lyfe.UserEditFragment
import com.cs4530.a4lyfe.models.User
import com.cs4530.a4lyfe.models.encodeLocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val EXISTING_USER = "user"

/**
 * A simple [Fragment] subclass.
 * Use the [BirthdayCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class BirthdayCardFragment : UserEditFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_birthday_card, container, false)
    }

    override fun gatherData(): User {
        val tempUser = User()
        val birthdayPicker = view?.findViewById<DatePicker>(R.id.birthday_DP)
        if (birthdayPicker != null) {
            tempUser.birthday =
                encodeLocalDate(
                    LocalDate.of(
                        birthdayPicker.year,
                        birthdayPicker.month,
                        birthdayPicker.dayOfMonth
                    )
                )
        }
        return tempUser
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param existing_user takes in an existing user from the activity; Used when editing
         * @return A new instance of fragment BirthdayCardFragment.
         */
        @JvmStatic
        fun newInstance(existing_user: User?) =
            BirthdayCardFragment().apply {
                arguments = Bundle().apply {
                    if (existing_user != null) {
                        putString(EXISTING_USER, Json.encodeToString(existing_user))
                    }
                }
            }
    }


}