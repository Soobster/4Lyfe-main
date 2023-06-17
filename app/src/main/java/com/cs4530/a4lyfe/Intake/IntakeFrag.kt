package com.cs4530.a4lyfe.Intake

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cs4530.a4lyfe.GatherUserData
import com.cs4530.a4lyfe.R
import com.cs4530.a4lyfe.UserEditFragment
import com.cs4530.a4lyfe.models.User
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass. Use the [IntakeFrag.newInstance] factory method to create an
 * instance of this fragment.
 */
class IntakeFrag : UserEditFragment(), GatherUserData {
    private lateinit var viewPager: ViewPager2
    private lateinit var pageAdapter: IntakePageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_intake, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.view_pager)
        pageAdapter = IntakePageAdapter(this)
        viewPager.adapter = pageAdapter

        TabLayoutMediator(
            view.findViewById(R.id.tab_layout),
            view.findViewById(R.id.view_pager)
        ) { _, _ -> }.attach()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of this fragment using the provided
         * parameters.
         *
         * @return A new instance of fragment IntakeFragment.
         */
        @JvmStatic
        fun newInstance() =
            IntakeFrag().apply { arguments = Bundle().apply { } }
    }

    override fun gatherData(): User {
        return pageAdapter.gatherData()
    }
}
