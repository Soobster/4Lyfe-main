package com.cs4530.a4lyfe.Intake

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.cs4530.a4lyfe.FragmentWithChildren
import com.cs4530.a4lyfe.GatherUserData
import com.cs4530.a4lyfe.R
import com.cs4530.a4lyfe.Intake.UserCreationCards.BirthdayCardFragment
import com.cs4530.a4lyfe.Intake.UserCreationCards.NameCardFragment
import com.cs4530.a4lyfe.Intake.UserCreationCards.SexCardFragment
import com.cs4530.a4lyfe.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TEST = "test"

/**
 * A simple [Fragment] subclass. Use the [FirstPage.newInstance] factory method to create an
 * instance of this fragment.
 */
class FirstPage : FragmentWithChildren(), GatherUserData {
    private val nameCardTag: String = "NAME_CARD_TAG"
    private val birthdayCardTag: String = "BIRTHDAY_CARD_TAG"
    private val sexCardTag: String = "SEX_CARD_TAG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intake_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        lifecycleScope.launch(Dispatchers.Main) {
            whenCreated {
                var nameCardFragment = childFragmentManager.findFragmentByTag(nameCardTag)
                var birthdayCardFragment = childFragmentManager.findFragmentByTag(birthdayCardTag)
                var sexCardFragment = childFragmentManager.findFragmentByTag(sexCardTag)

                val user = activityInterface?.repo?.userData?.value
                if (nameCardFragment == null) {
                    nameCardFragment = NameCardFragment.newInstance(user)
                }
                if (birthdayCardFragment == null) {
                    birthdayCardFragment = BirthdayCardFragment.newInstance(user)
                }
                if (sexCardFragment == null) {
                    sexCardFragment = SexCardFragment.newInstance(user)
                }
                replaceChildFrag(R.id.name_card, nameCardFragment, nameCardTag, false)
                replaceChildFrag(R.id.birthday_card, birthdayCardFragment, birthdayCardTag, false)
                replaceChildFrag(R.id.sex_card, sexCardFragment, sexCardTag, false)
            }
        }


    }


    override fun gatherData(): User {
        val tempUser = User()
        val nameCard = childFragmentManager.findFragmentById(R.id.name_card) as NameCardFragment?
        val birthdayCard =
            childFragmentManager.findFragmentById(R.id.birthday_card) as BirthdayCardFragment?
        val sexCard = childFragmentManager.findFragmentById(R.id.sex_card) as SexCardFragment?

        if (nameCard != null) {
            tempUser.copyClass(nameCard.gatherData())
        }
        if (birthdayCard != null) {
            tempUser.copyClass(birthdayCard.gatherData())
        }
        if (sexCard != null) {
            tempUser.copyClass(sexCard.gatherData())
        }

        return tempUser
    }

    companion object {
        /**
         * Use this factory method to create a new instance of this fragment using the provided
         * parameters.
         *
         * @return A new instance of fragment FirstPage.
         */
        @JvmStatic
        fun newInstance() =
            FirstPage().apply { arguments = Bundle().apply { } }
    }
}
