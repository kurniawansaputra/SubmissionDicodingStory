package com.example.dicodingstory.ui.activity.addstory

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.dicodingstory.data.remote.Result
import com.example.dicodingstory.databinding.ActivityAddStoryBinding
import com.example.dicodingstory.databinding.LayoutAddPhotoBinding
import com.example.dicodingstory.hawkstorage.HawkStorage
import com.example.dicodingstory.ui.activity.camera.CameraActivity
import com.example.dicodingstory.ui.activity.main.MainActivity
import com.example.dicodingstory.utils.createCustomTempFile
import com.example.dicodingstory.utils.hideLoading
import com.example.dicodingstory.utils.reduceFileImage
import com.example.dicodingstory.utils.rotateFile
import com.example.dicodingstory.utils.showLoading
import com.example.dicodingstory.utils.uriToFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var token: String
    private lateinit var description: String
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var myFile: File? = null
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String

    private val factory: AddStoryViewModelFactory = AddStoryViewModelFactory.getInstance()
    private val addStoryViewModel: AddStoryViewModel by viewModels {
        factory
    }

    private lateinit var binding: ActivityAddStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    // Permission to access camera
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Tidak mendapatkan permission.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun init() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setPref()
        setToolbar()
        setListener()
        textWatcher()
    }

    private fun setPref() {
        val user = HawkStorage.instance(this).getUser()
        token = user.loginResult?.token.toString()
        lat = -0.02807932546533127
        lon = 109.35006589013962
    }

    private fun setToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setListener() {
        binding.apply {
            ivPhoto.setOnClickListener {
                dialogAddPhoto()
            }
            buttonAdd.setOnClickListener {
                uploadStory()
            }
        }
    }

    private fun dialogAddPhoto() {
        val binding: LayoutAddPhotoBinding = LayoutAddPhotoBinding.inflate(layoutInflater)
        val builder: AlertDialog.Builder = AlertDialog.Builder(layoutInflater.context)
        builder.setView(binding.root)
        val dialog: AlertDialog = builder.create()
        binding.apply {
            labelCamerax.setOnClickListener {
                val intent = Intent(this@AddStoryActivity, CameraActivity::class.java)
                launcherIntentCameraX.launch(intent)
                dialog.dismiss()
            }
            labelCamera.setOnClickListener {
                startTakePhoto()
                dialog.dismiss()
            }
            labelGallery.setOnClickListener {
                startGallery()
                dialog.dismiss()
            }
        }
        dialog.setCancelable(true)
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.ivPhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }

            if (myFile!!.exists()) {
                binding.containerAddImage.visibility = View.GONE
            } else {
                binding.containerAddImage.visibility = View.VISIBLE
            }

            validateInput()
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.example.dicodingstory",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            myFile = File(currentPhotoPath)
            myFile.let { file ->
                if (file != null) {
                    rotateFile(file)
                }
                if (file != null) {
                    getFile = file
                    binding.ivPhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
                }
            }

            if (myFile!!.exists()) {
                binding.containerAddImage.visibility = View.GONE
            } else {
                binding.containerAddImage.visibility = View.VISIBLE
            }

            validateInput()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.ivPhoto.setImageURI(uri)

                if (myFile!!.exists()) {
                    binding.containerAddImage.visibility = View.GONE
                } else {
                    binding.containerAddImage.visibility = View.VISIBLE
                }

                validateInput()
            }
        }
    }

    private fun textWatcher() {
        binding.apply {
            etDesc.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    validateInput()
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
        }
    }

    private fun uploadStory() {
        if (getFile != null) {
            setLoading(true)
            val file = getFile as File
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                try {
                    val reducedFile = withContext(Dispatchers.IO) {
                        reduceFileImage(file)
                    }
                    addStoryViewModel.addStories(token, description, reducedFile, lat, lon).observe(this@AddStoryActivity) {
                        if (it != null) {
                            when (it) {
                                is Result.Loading -> {
                                    setLoading(true)
                                }
                                is Result.Success -> {
                                    setLoading(false)
                                    val error = it.data.error
                                    val message = it.data.message
                                    if (error == false) {
                                        goToMain()
                                        Toast.makeText(this@AddStoryActivity, "$message", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                is Result.Error -> {
                                    setLoading(false)
                                    Toast.makeText(this@AddStoryActivity, it.error, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    setLoading(false)
                    Toast.makeText(this@AddStoryActivity, "$e", Toast.LENGTH_SHORT).show()
                } finally {
                    setLoading(false)
                }
            }
        } else {
            Toast.makeText(this@AddStoryActivity, "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput() {
        binding.apply {
            description = etDesc.text.toString()

            val isPhotoNotEmpty = myFile != null && myFile!!.exists()
            val isDescriptionNotEmpty = description.isNotEmpty()

            buttonAdd.isEnabled = isPhotoNotEmpty && isDescriptionNotEmpty
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            showLoading(this)
        } else {
            hideLoading()
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}