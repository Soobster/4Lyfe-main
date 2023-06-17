package com.cs4530.a4lyfe.Intake.UserCreationCards

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.cs4530.a4lyfe.R
import com.cs4530.a4lyfe.UserEditFragment
import com.cs4530.a4lyfe.models.User
import com.cs4530.a4lyfe.models.encodeBitMap
import com.cs4530.a4lyfe.models.encodeBitMapFromString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.InputStream


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val EXISTING_USER = "user"

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class PhotoCardFragment : UserEditFragment() {

    private var image: Bitmap? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var rootView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    activityCallback(result.data)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_photo_card, container, false)

        return rootView
    }

    // Puts a listener on the choose image button, drops down a list to select
    // taking a new photo or selecting an existing one
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Handle the returned Uri
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<Button>(R.id.choose_image_button)
        button.setOnClickListener {
            val popupMenu = PopupMenu(activity, button)
            popupMenu.menuInflater.inflate(R.menu.photo_choice_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked
                if (menuItem.title.toString() == "Take New Photo") {
                    if (activityInterface?.isCameraAccessible() as Boolean) {
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        val REQUEST_IMAGE_CAPTURE = 1

                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                } else {
                    openImageActivity()
                }
                true
            }
            popupMenu.show()
        }

        if (user != null) {
            if(user?.profileImage != null) {
                val preview = view.findViewById<ImageView>(R.id.profile_image)
                image = encodeBitMapFromString(user!!.profileImage!!)
                preview?.setImageBitmap(image)
            }
        }
    }

    // Result function for Camera intent, displays photo in the circle
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == -1) {
            val preview = view?.findViewById<ImageView>(R.id.profile_image)
            val imageBitmap = data?.extras?.get("data") as Bitmap
            this.image = imageBitmap
            preview?.setImageBitmap(image)
        }
    }

    override fun gatherData(): User {
        val tempUser = User()
        tempUser.profileImage = image?.let { encodeBitMap(it) }
        return tempUser
    }

    private fun openImageActivity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        runBlocking {
            resultLauncher.launch(intent)
        }
    }

    private fun activityCallback(intent: Intent?) {
        lifecycle.coroutineScope.launch(Dispatchers.Main) {

            if (intent != null) {
                val imageURI = intent.data
                val input: InputStream? = imageURI?.let {
                    requireActivity().contentResolver.openInputStream(
                        it
                    )
                }
                if (input != null) {
                    compressBitMap(input)
                }
            }
        }
    }

    private fun loadImageToUI(image: Bitmap) {
        val preview = view?.findViewById<ImageView>(R.id.profile_image)
        this.image = image
        preview?.setImageBitmap(image)
        val button = view?.findViewById<Button>(R.id.choose_image_button)
        if (button != null) {
            button.text = getString(R.string.replace_image_button)
        }
    }

    private fun compressBitMap(input: InputStream) {
        lifecycleScope.launch(Dispatchers.IO) {
            Looper.prepare()
            val decodedImage = BitmapFactory.decodeStream(input)
            val thumb = ThumbnailUtils.extractThumbnail(decodedImage, 480, 480)
            val outputStream = ByteArrayOutputStream()
            thumb.compress(Bitmap.CompressFormat.JPEG, 25, outputStream)
            val outputByteArray = outputStream.toByteArray()
            val outputImage =
                BitmapFactory.decodeByteArray(outputByteArray, 0, outputByteArray.size)
            lifecycleScope.launch(Dispatchers.Main) {
                loadImageToUI(outputImage)
            }
            Looper.loop()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param existing_user takes in an existing user from the activity; Used when editing
         * @return A new instance of fragment PhotoCardFragment.
         */
        @JvmStatic
        fun newInstance(existing_user: User?) =
            PhotoCardFragment().apply {
                arguments = Bundle().apply {
                    if (existing_user != null) {
                        putString(EXISTING_USER, Json.encodeToString(existing_user))
                    }
                }
            }
    }


}