package com.cs4530.a4lyfe

import android.content.Context
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.cs4530.a4lyfe.models.Repository
import com.cs4530.a4lyfe.models.User
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


interface GatherUserData {
    fun gatherData(): User
}

interface ActivityInterface {

    fun loadToMainPage(
        fragment: Fragment,
        savedInstanceState: Bundle?,
        tag: String,
        canGoBack: Boolean,
        permissions: Array<String>? = null
    )
    fun saveUser()
    var repo: Repository
    fun showAlert(title: String, message: String, acceptButtonString: String)
    fun replaceFragment(containerViewId: Int, fragment: Fragment, tag: String?, canGoBack: Boolean)
    fun islocationAccessible(): Boolean
    fun isCameraAccessible(): Boolean
    fun getLocation(): Location?
    suspend fun startLocation()
    fun getContext(): Context

    fun getStepCountEnabled(): Boolean
    fun setStepCountEnabled()
    fun startStepCounter()
    fun stopStepCounter()
}

fun View.setMargins(marginLeft: Int, marginTop: Int, marginRight: Int, marginBottom: Int) {
    if (this.layoutParams is RelativeLayout.LayoutParams) {
        val params: RelativeLayout.LayoutParams =
            RelativeLayout.LayoutParams(this.layoutParams)
        params.setMargins(marginLeft.dp, marginTop, marginRight.dp, marginBottom.dp)
        this.foregroundGravity = this.foregroundGravity
        this.layoutParams = params
    } else if (this.layoutParams is LinearLayout.LayoutParams) {
        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(this.layoutParams)
        params.setMargins(marginLeft.dp, marginTop.dp, marginRight.dp, marginBottom.dp)
        this.foregroundGravity = this.foregroundGravity
        this.layoutParams = params
    }
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

open class ActivityFragment : Fragment() {
    protected var activityInterface: ActivityInterface? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityInterface) {
            activityInterface = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        activityInterface = null
    }
}

open class FragmentWithChildren : ActivityFragment() {
    fun replaceChildFrag(
        containerViewId: Int,
        fragment: Fragment,
        tag: String,
        canGoBack: Boolean
    ) {
        val existing: Fragment? = childFragmentManager.findFragmentByTag(tag)
        if (existing != null) {
            childFragmentManager.commit {
                setReorderingAllowed(true)
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(containerViewId, existing, tag)
                if (canGoBack) {
                    addToBackStack("main")
                }
            }
        } else {
            childFragmentManager.commit {
                setReorderingAllowed(true)
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(containerViewId, fragment, tag)
                if (canGoBack) {
                    addToBackStack("main")
                }
            }
        }
    }
}


private const val EXISTING_USER = "user"

abstract class UserEditFragment : FragmentWithChildren(), GatherUserData {
    var user: User? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (user != null) {
            outState.putString(EXISTING_USER, Json.encodeToString(user!!))
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            arguments?.let {
                user = it.getString(EXISTING_USER)?.let { it1 -> Json.decodeFromString(it1) }
            }
        } else {
            user = savedInstanceState.getString(EXISTING_USER)
                ?.let { it1 -> Json.decodeFromString(it1) }

        }
    }
}


abstract class ObserverFragment<T : Any?> : FragmentWithChildren() {
    var data: T? = null
    protected open var observer: Observer<T> = Observer<T> {
        data = it
        runBlocking { setPageElements(data) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (data is User) {
            outState.putString(EXISTING_USER, Json.encodeToString(data as User?))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            observeDataBootstrap(data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            setPageElements(data)
        }
    }

    abstract suspend fun setPageElements(objectToObserve: T?)
    abstract suspend fun observeDataBootstrap(data: T?)
}




