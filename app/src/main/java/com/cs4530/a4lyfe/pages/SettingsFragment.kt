package com.cs4530.a4lyfe.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.cs4530.a4lyfe.ObserverFragment
import com.cs4530.a4lyfe.R
import com.cs4530.a4lyfe.Intake.IntakeFrag
import com.cs4530.a4lyfe.models.Repository
import com.cs4530.a4lyfe.models.User
import com.cs4530.a4lyfe.models.encodeBitMapFromString

class SettingsFragment : ObserverFragment<User>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override suspend fun observeDataBootstrap(data: User?) {
        val liveData = Repository.getInstance(requireActivity().application)?.userData
        liveData?.observe(viewLifecycleOwner, observer)
        liveData?.value?.let { setPageElements(it) }
    }

    override suspend fun setPageElements(objectToObserve: User?) {

        // display user's info
        val userInfo = view?.findViewById(R.id.user_info) as TextView?
        userInfo?.text = ("" + objectToObserve?.birthday + "\n\n"
                + "" + objectToObserve?.sex + "\n\n"
                + "" + objectToObserve?.feet + " Feet, " + "" + objectToObserve?.inches + " Inches\n\n"
                + "" + objectToObserve?.weight + " lbs\n\n"
                + "" + objectToObserve?.goal + "\n\n"
                + "" + objectToObserve?.lifestyle + "\n\n")
        // display pfp image
        val pfp = view?.findViewById<ImageView>(R.id.settings_profile_image)
        pfp?.setImageBitmap(objectToObserve?.profileImage?.let { encodeBitMapFromString(it) })

        // display user's name on card
        val welcome = view?.findViewById<TextView>(R.id.settings_user_name_text)
        welcome?.text = "" + objectToObserve?.firstName + " " + objectToObserve?.lastName
        view?.requestLayout()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)

        // open the intake page after clicking the edit settings image
        val editSettingsBtn = view.findViewById<ImageButton>(R.id.edit_settings_button)
        editSettingsBtn.setOnClickListener {
            val intro = IntakeFrag.newInstance()
            activityInterface?.loadToMainPage(intro, savedInstanceState, "Edit User", true)
        }

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MainPage.
         */
        @JvmStatic
        fun newInstance() =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}