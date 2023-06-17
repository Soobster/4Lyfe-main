package com.cs4530.a4lyfe

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import com.cs4530.a4lyfe.Intake.IntakeFrag
import com.cs4530.a4lyfe.models.Repository
import com.cs4530.a4lyfe.models.User
import com.cs4530.a4lyfe.pages.SettingsFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*

private const val MAIN_PAGE = "4Lyfe"
private const val SETTINGS_PAGE = "Settings"
private const val MAIN_STACK = "Main"
private const val USER_INSTANCE = "UserInstance"
private const val NAV_STACK = "NavStack"
private const val LOCATION = "Location"

class MainActivity : AppCompatActivity(), ActivityInterface, LocationListener,
    ActivityCompat.OnRequestPermissionsResultCallback,
    CoroutineScope by MainScope() {
    private var user: User? = null
    override lateinit var repo: Repository
    private lateinit var locationManager: LocationManager
    private var mapsStarted: Boolean = false
    private lateinit var navStack: LinkedList<String>
    private var loc: Location? = null

    private var mSensorManager: SensorManager? = null
    private var mStepCounter: Sensor? = null
    private var stepEnabled: Boolean = false


    private var userObserver: Observer<User?> = Observer<User?>() {
        setPageElements(it)
    }
    var con: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        repo = Repository.getInstance(this.application)!!


        navStack = LinkedList()
        if (savedInstanceState != null) {
            savedInstanceState.getStringArray(NAV_STACK)?.forEach { item ->
                navStack.add(item)
            }

            loc = savedInstanceState.getParcelable(LOCATION)
            reloadBackButton()
            setTitle(navStack.peek())
        }
        else {
            val parent = this
            lifecycleScope.launchWhenCreated {
                repo.userData.observe(parent, userObserver)
                launch(Dispatchers.IO) {
                    repo.mUserDao.deleteAllWeather()
                    repo.mUserDao.deleteAllHikes()
                    repo.mUserDao.allUsers.collect {
                        launch(Dispatchers.Main) {
                            if (it.isNotEmpty()) {
                                val user = it[0]
                                repo.userData.value = user
                            } else {
                                val user = User()
                                repo.userData.value = user
                            }
                        }
                    }
                }

            }
            try {
                Amplify.addPlugin(AWSCognitoAuthPlugin())
                Amplify.addPlugin(AWSS3StoragePlugin())
                //issue configuring amplify
                Amplify.configure(applicationContext)
                Log.i("MyAmplifyApp", "Initialized Amplify")
            } catch (error: AmplifyException) {
                Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
            }
        }


        // this is where the user data is going to get stored. If it is null, or does
        // not contain what we need, then we know that we need to show the intake page


        setContentView(R.layout.activity_main)


        // Step counter stuff
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mStepCounter = mSensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USER_INSTANCE, Json.encodeToString(user))
        outState.putStringArray(NAV_STACK, navStack.toTypedArray())
        outState.putParcelable(LOCATION, loc)
        uploadFile()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                launch {
                    startLocation()
                }
                break
            }

        }

    }


    // Handler for changing fragments
    override fun loadToMainPage(
        fragment: Fragment,
        savedInstanceState: Bundle?,
        tag: String,
        canGoBack: Boolean,
        permissions: Array<String>?
    ) {
        // Invalidate menu to recheck if we show the settings gear
        invalidateOptionsMenu()
        // Clear the stack if we're going home
        if (tag == "4Lyfe") {
            supportFragmentManager.clearBackStack(MAIN_STACK)
            navStack.clear()
        }

        // Check the string of permissions passed to see if we need any to access what we're about to load
        if (permissions != null) {
            for (permission in permissions) {
                val isAllowed = ActivityCompat.checkSelfPermission(this, permission)
                if (isAllowed != PackageManager.PERMISSION_GRANTED) {
                    showAlert(
                        "Permissions Error",
                        "There were missing permissions. Please check your settings to allow the app permissions to use this functionality",
                        "OK"
                    )
                    return
                }
            }
        }


        var savedFrag: Fragment? = null
        if (savedInstanceState != null) {
            savedFrag = supportFragmentManager.findFragmentByTag(tag)
        }
        if (savedFrag != null) {
            replaceFragment(R.id.main_frag, savedFrag, tag, canGoBack)

        } else {
            replaceFragment(R.id.main_frag, fragment, tag, canGoBack)
        }


        // push the name of the
        navStack.push(tag)
        setTitle(tag)
        // If we're not at the home page and can go back, show the back button; otherwise hide the back button
        if (canGoBack && navStack.peek() != MAIN_PAGE) {
            showBackButton(true)

        } else {
            showBackButton(false)
        }
    }

    private fun reloadBackButton() {
        if (navStack.peek() != MAIN_PAGE) {
            showBackButton(true)

        } else {
            showBackButton(false)
        }
    }

    override fun saveUser() {
        val signUpFrag: IntakeFrag? =
            supportFragmentManager.findFragmentByTag("Sign Up") as IntakeFrag?
        val editFrag = supportFragmentManager.findFragmentByTag("Edit User") as IntakeFrag?
        if(editFrag != null) {
            val data = editFrag.gatherData()
            data.registered = true
            repo.userData.value = data
            repo.createUser()
        }
        else {
            val data = signUpFrag?.gatherData()
            if (data != null) {
                data.registered = true
                repo.userData.value = data
                repo.createUser()
            }
        }


    }


    override fun replaceFragment(
        containerViewId: Int,
        fragment: Fragment,
        tag: String?,
        canGoBack: Boolean
    ) {
        runBlocking {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(containerViewId, fragment, tag)
                if (canGoBack) {
                    addToBackStack(MAIN_STACK)
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        invalidateOptionsMenu()
        when (item.itemId) {
            android.R.id.home -> {
                navStack.pop()
                // if the page navigating to is the home page, don't show the back button
                if (navStack.peek() == MAIN_PAGE) {
                    showBackButton(false)
                }
                // Navigate back and set the title to the fragments tag (which is doubling as the title of the page)
                supportFragmentManager.popBackStack()
                setTitle(navStack.peek())
            }
            R.id.action_settings -> {
                if (navStack.peek() != SETTINGS_PAGE) {
                    loadToMainPage(SettingsFragment.newInstance(), null, SETTINGS_PAGE, true)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // don't show the settings gear if we're not at the home page
        if (navStack.peek() != MAIN_PAGE) {
            return false
        }
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun showBackButton(show: Boolean) {
        val actionBarContainer = supportActionBar

        if (actionBarContainer != null) {
            actionBarContainer.setDisplayHomeAsUpEnabled(show)
            actionBarContainer.setHomeButtonEnabled(show)
        }

    }

    private fun setTitle(title: String?) {
        if (title == null) {
            val actionBarContainer = supportActionBar
            if (actionBarContainer != null) {
                actionBarContainer.title = MAIN_PAGE
            }
        } else {
            val actionBarContainer = supportActionBar
            if (actionBarContainer != null) {
                actionBarContainer.title = title
            }
        }
    }

    override fun showAlert(title: String, message: String, acceptButtonString: String) {
        // build alert
        val dialogBuilder = AlertDialog.Builder(this)

        // configure alert box
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(acceptButtonString, null)
        // create and show alert
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
    }

    override fun islocationAccessible(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )
        return false
    }

    // Checks if camera is accessible, otherwise requests permission
    override fun isCameraAccessible(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA), 0
        )
        return false
    }


    @SuppressLint("MissingPermission")
    override fun getLocation(): Location? {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (islocationAccessible()) {
                if (!mapsStarted) {
                    launch {
                        startLocation()
                    }
                }
                return loc
            }
        }
        return null
    }

    @SuppressLint("MissingPermission")
    override suspend fun startLocation() {
        val parent = this
        coroutineScope {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (islocationAccessible()) launch(Dispatchers.IO) {
                    Looper.prepare()
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        500F,
                        parent
                    )
                    Looper.loop()
                }
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
        loc = p0
    }

    private fun setPageElements(user: User?) {
        // use this code to determine whether or not to show the intake page

        if (user == null || !user.registered) {
            val intro = IntakeFrag.newInstance()
            loadToMainPage(intro, null, "Sign Up", false)
        } else {
            val mainPage = MainPage.newInstance()
            launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    startLocation()
                }
            }
            loadToMainPage(mainPage, null, MAIN_PAGE, false)
        }
    }


    private fun uploadFile() {
        val file = File(applicationContext.filesDir, "key")
        try {
            val writer = BufferedWriter(FileWriter(file))
            writer.append(repo.userData.toString() + '\n' + repo.hikeData.toString())
            writer.close()
        } catch (exception: Exception) {
            Log.e("MyAmplifyApp", "Upload failed", exception)
        }

        Amplify.Storage.uploadFile(
            "key",
            file,
            { result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.key) })
        { storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure) }
    }

    override fun getContext(): Context {
        return this.applicationContext
    }


    private val mListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            //user?.stepCount = ("" + sensorEvent.values[0].toString())
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    override fun startStepCounter() {
        if (mStepCounter != null) {
            mSensorManager!!.registerListener(
                mListener,
                mStepCounter,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun stopStepCounter() {
        if (mStepCounter != null) {
            mSensorManager!!.unregisterListener(mListener)
        }
    }

    override fun getStepCountEnabled(): Boolean {
        return stepEnabled
    }

    override fun setStepCountEnabled() {
        if(stepEnabled){
            stepEnabled = false
        }
        else{
            stepEnabled = true
        }
    }


}
