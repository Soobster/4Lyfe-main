package com.cs4530.a4lyfe.Intake


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cs4530.a4lyfe.GatherUserData
import com.cs4530.a4lyfe.models.User


class IntakePageAdapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment),
    GatherUserData {


    private var user = User()
    private var firstPage = FirstPage.newInstance()
    private var secondPage = SecondPage.newInstance()
    private var thirdPage = ThirdPage.newInstance()

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val page =
            when (position) {
                0 -> firstPage
                1 -> secondPage
                2 -> thirdPage
                else -> {
                    firstPage
                }
            }

        return page
    }

    override fun gatherData(): User {
        val firstPageData = firstPage.gatherData()
        val secondPageData = secondPage.gatherData()
        val thirdPageData = thirdPage.gatherData()

        user.copyClass(firstPageData)
        user.copyClass(secondPageData)
        user.copyClass(thirdPageData)

        return user
    }


}
