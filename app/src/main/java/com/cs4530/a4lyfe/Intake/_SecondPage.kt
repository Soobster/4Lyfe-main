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
import com.cs4530.a4lyfe.Intake.UserCreationCards.HeightCardFragment
import com.cs4530.a4lyfe.Intake.UserCreationCards.WeightCardFragment
import com.cs4530.a4lyfe.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass. Use the [SecondPage.newInstance] factory method to create an
 * instance of this fragment.
 */
class SecondPage : FragmentWithChildren(), GatherUserData {
    private val heightCardTag: String = "HEIGHT_CARD_TAG"
    private val weightCardTag: String = "WEIGHT_CARD_TAG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intake_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch(Dispatchers.Main) {
            whenCreated {
                val user = activityInterface?.repo?.userData?.value
                var heightCardFragment = childFragmentManager.findFragmentByTag(heightCardTag)
                var weightCardFragment = childFragmentManager.findFragmentByTag(weightCardTag)

                if (heightCardFragment == null) {
                    heightCardFragment = HeightCardFragment.newInstance(user)
                }
                if (weightCardFragment == null) {
                    weightCardFragment = WeightCardFragment.newInstance(user)
                }

                replaceChildFrag(R.id.height_card, heightCardFragment, heightCardTag, false)
                replaceChildFrag(R.id.weight_card, weightCardFragment, weightCardTag, false)
            }
        }
    }

    override fun gatherData(): User {
        val tempUser = User()
        val weightCard =
            childFragmentManager.findFragmentByTag(weightCardTag) as WeightCardFragment?
        val heightCard =
            childFragmentManager.findFragmentByTag(heightCardTag) as HeightCardFragment?

        if (heightCard != null) {
            tempUser.copyClass(heightCard.gatherData())
        }
        if (weightCard != null) {
            tempUser.copyClass(weightCard.gatherData())
        }

        return tempUser
    }

    companion object {
        /**
         * Use this factory method to create a new instance of this fragment using the provided
         * parameters.
         *
         * @return A new instance of fragment SecondPage.
         */
        @JvmStatic
        fun newInstance() =
            SecondPage().apply { arguments = Bundle().apply { } }
    }


}
