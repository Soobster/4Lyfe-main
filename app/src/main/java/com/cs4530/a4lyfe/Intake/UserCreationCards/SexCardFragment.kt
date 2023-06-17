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
 * Use the [SexCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class SexCardFragment : UserEditFragment() {
    private val sexes = arrayOf<String?>("Female", "Male", "Non-Binary")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sex_card, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // sex spinner (nice)
        val spinner = view.findViewById(R.id.sex_spinner) as Spinner?
        if (spinner != null) {
            val ad = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, sexes)
            ad.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
            spinner.adapter = ad
            val pos = sexes.indexOf(user?.sex)
            if (pos != -1) {
                spinner.setSelection(pos)
            } else {
                spinner.setSelection(0)
            }
            spinner.invalidate()
        }
    }

    override fun gatherData(): User {
        val tempUser = User()
        val spinner = view?.findViewById(R.id.sex_spinner) as Spinner?
        if (spinner != null) {
            tempUser.sex = spinner.selectedItem.toString()
        }
        return tempUser
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param existing_user takes in an existing user from the activity; Used when editing
         * @return A new instance of fragment SexCardFragment.
         */
        @JvmStatic
        fun newInstance(existing_user: User?) =
            SexCardFragment().apply {
                arguments = Bundle().apply {
                    if (existing_user != null) {
                        putString(EXISTING_USER, Json.encodeToString(existing_user))
                    }
                }
            }
    }


}