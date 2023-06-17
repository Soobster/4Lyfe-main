package com.cs4530.a4lyfe.Intake

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.cs4530.a4lyfe.FragmentWithChildren
import com.cs4530.a4lyfe.GatherUserData
import com.cs4530.a4lyfe.R
import com.cs4530.a4lyfe.Intake.UserCreationCards.LifestyleCardFragment
import com.cs4530.a4lyfe.Intake.UserCreationCards.PhotoCardFragment
import com.cs4530.a4lyfe.Intake.UserCreationCards.WeightGoalCardFragment
import com.cs4530.a4lyfe.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass. Use the [ThirdPage.newInstance] factory method to create an
 * instance of this fragment.
 */
class ThirdPage : FragmentWithChildren(), GatherUserData {
    private val photoCardTag: String = "PHOTO_CARD_TAG"
    private val goalCardTag: String = "GOAL_CARD_TAG"
    private val lifestyleCardTag: String = "LIFESTYLE_CARD_TAG"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intake_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        lifecycleScope.launch(Dispatchers.Main) {
            whenCreated {
                val user = activityInterface?.repo?.userData?.value

                var photoCardFragment = childFragmentManager.findFragmentByTag(photoCardTag)
                var weightGoalCardFragment = childFragmentManager.findFragmentByTag(goalCardTag)
                var lifestyleCardFragment = childFragmentManager.findFragmentByTag(lifestyleCardTag)

                if (photoCardFragment == null) {
                    photoCardFragment = PhotoCardFragment.newInstance(user)
                }
                if (weightGoalCardFragment == null) {
                    weightGoalCardFragment = WeightGoalCardFragment.newInstance(user)
                }
                if (lifestyleCardFragment == null) {
                    lifestyleCardFragment = LifestyleCardFragment.newInstance(user)
                }
                replaceChildFrag(R.id.photo_card, photoCardFragment, photoCardTag, false)
                replaceChildFrag(R.id.weight_goal_card, weightGoalCardFragment, goalCardTag, false)
                replaceChildFrag(
                    R.id.lifestyle_card,
                    lifestyleCardFragment,
                    lifestyleCardTag,
                    false
                )

                val button: Button = view.findViewById(R.id.finish_intake)


                button.setOnClickListener {
                    activityInterface?.saveUser()
                }

                // make the location button invisible
                val locBtn: Button = view.findViewById(R.id.button_loc)
                locBtn.visibility = View.GONE

            }


        }


    }

    override fun onResume() {
        super.onResume()
        view?.requestLayout()
    }

    override fun gatherData(): User {
        val tempUser = User()

        val photoCardFragment =
            childFragmentManager.findFragmentByTag(photoCardTag) as PhotoCardFragment?
        val weightGoalCardFragment =
            childFragmentManager.findFragmentByTag(goalCardTag) as WeightGoalCardFragment?
        val lifestyleCardFragment =
            childFragmentManager.findFragmentByTag(lifestyleCardTag) as LifestyleCardFragment?

        if (weightGoalCardFragment != null) {
            tempUser.copyClass(weightGoalCardFragment.gatherData())
        }
        if (photoCardFragment != null) {
            tempUser.copyClass(photoCardFragment.gatherData())
        }
        if (lifestyleCardFragment != null) {
            tempUser.copyClass(lifestyleCardFragment.gatherData())
        }

        return tempUser
    }

    companion object {
        /**
         * Use this factory method to create a new instance of this fragment using the provided
         * parameters.
         *
         * @return A new instance of fragment ThirdPage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ThirdPage().apply { arguments = Bundle().apply { } }
    }


}
