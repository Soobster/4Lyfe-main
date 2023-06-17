package com.cs4530.a4lyfe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.cs4530.a4lyfe.models.ExtendableFragmentData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


private const val MODEL = "model"

/**
 * A simple [Fragment] subclass.
 * Use the [MainPageButtonFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainPageButtonFrag : ActivityFragment() {
    // TODO: Rename and change types of parameters
    private var mainText: String? = null
    private var subText: String? = null
    private var icon: Int? = null
    private var fragmentToLoad: Fragment? = null
    private var permissions: Array<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var className: String? = null
        arguments?.let {
            val modelData =
                Json.decodeFromString(ExtendableFragmentData.serializer(), it.getString(MODEL)!!)
            mainText = modelData.mainText
            subText = modelData.subText
            icon = modelData.icon
            className = modelData.clsName
            permissions = modelData.permissions
        }


        val frag = className?.let {
            FragmentFactory.loadFragmentClass(
                Class.forName(it).classLoader as ClassLoader,
                it
            )
        }
        fragmentToLoad = frag?.newInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_page_button, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainTextView = view.findViewById<TextView>(R.id.main_text)
        val subTextView = view.findViewById<TextView>(R.id.sub_text)
        val buttonFrag = view.findViewById<FrameLayout>(R.id.extended_button)

        val iconView = view.findViewById<ImageView>(R.id.icon)
        icon?.let { iconView.setBackgroundResource(it) }

        mainTextView.text = mainText
        subTextView.text = subText

        buttonFrag.setOnClickListener(View.OnClickListener {


            fragmentToLoad?.let { it1 ->
                mainText?.let { it2 ->
                    activityInterface?.loadToMainPage(
                        it1, savedInstanceState,
                        it2, true,
                        permissions
                    )
                }
            }

        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param mainText Parameter 1.
         * @param subText Parameter 2.
         * @param icon an icon for the button
         * @return A new instance of fragment MainPageButtonFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun <T> newInstance(
            fragment: Class<T>,
            mainText: String,
            subText: String? = null,
            icon: Int? = null,
            permissions: Array<String>?
        ) =
            MainPageButtonFrag().apply {
                val modelData = Json.encodeToString(
                    ExtendableFragmentData(
                        fragment::getName.invoke(),
                        mainText,
                        subText,
                        icon,
                        permissions
                    )
                )
                val bundle = Bundle()
                arguments = (bundle.apply {
                    putString(MODEL, modelData)
                })

            }
    }
}