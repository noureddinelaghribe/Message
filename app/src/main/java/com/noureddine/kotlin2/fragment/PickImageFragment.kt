package com.noureddine.kotlin2.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.noureddine.kotlin2.Adapter.AdapterImgProfile
import com.noureddine.kotlin2.R
import com.noureddine.kotlin2.activity.MainActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PickImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PickImageFragment : Fragment() , AdapterImgProfile.OnImageClickListener{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var imagePerview: ShapeableImageView
    lateinit var recyclerView: RecyclerView
    lateinit var btnCamera: MaterialButton
    lateinit var btngaiiery: MaterialButton
    lateinit var btnSave: MaterialButton
    private lateinit var imageAdapter: AdapterImgProfile
    private lateinit var database: DatabaseReference
    val auth = Firebase.auth


    private var imageUri: Uri? = null

    // Register activity result launchers
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                imagePerview.setImageURI(uri)
                // Here you can also upload or process the image
            }
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri?.let { uri ->
                imagePerview.setImageURI(uri)
                // Here you can also upload or process the image
            }
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(context, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestGalleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(context, "Storage permission is required to select images", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_pick_image, container, false)

        imagePerview = v.findViewById(R.id.image_preview)
        recyclerView = v.findViewById(R.id.recyclerView)
        btnCamera = v.findViewById(R.id.btn_camera)
        btngaiiery = v.findViewById(R.id.btn_gallery)
        btnSave = v.findViewById(R.id.btn_Save)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        database = FirebaseDatabase.getInstance().reference

        val imageList = listOf(
            R.drawable.user1,
            R.drawable.user2,
            R.drawable.user3,
            R.drawable.user4,
            R.drawable.user5
        )

        imageAdapter = AdapterImgProfile(imageList, this)
        recyclerView.adapter = imageAdapter

        btngaiiery.setOnClickListener {
            checkGalleryPermissionAndOpen()
        }

        btnCamera.setOnClickListener {
            checkCameraPermissionAndOpen()
        }

        btnSave.setOnClickListener {
            database.child("users").child(auth.uid.toString()).child("img").setValue(imagePerview.getTag())
            var  i = Intent(activity, MainActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }



        return v
    }

    override fun onImageClick(imageItem: Int) {
        imagePerview.setImageResource(imageItem)
        imagePerview.setTag(imageItem)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PickImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PickImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch( android.Manifest.permission.CAMERA)
        }
    }

    private fun checkGalleryPermissionAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ we need READ_MEDIA_IMAGES permission
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                requestGalleryPermissionLauncher.launch( android.Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // For older versions we need READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                requestGalleryPermissionLauncher.launch( android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun openCamera() {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }

        imageUri = context?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        takePictureLauncher.launch(cameraIntent)
    }

}