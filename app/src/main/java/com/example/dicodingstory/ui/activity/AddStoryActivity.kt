package com.example.dicodingstory.ui.activity

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.databinding.ActivityAddStoryBinding
import com.example.dicodingstory.databinding.LayoutAddPhotoBinding
import com.example.dicodingstory.hawkstorage.HawkStorage
import com.example.dicodingstory.util.createCustomTempFile
import com.example.dicodingstory.util.hideLoading
import com.example.dicodingstory.util.reduceFileImage
import com.example.dicodingstory.util.rotateFile
import com.example.dicodingstory.util.showLoading
import com.example.dicodingstory.util.uriToFile
import com.example.dicodingstory.viewmodel.AddStoryViewModel
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var token: String
    private lateinit var description: String
    private var myFile: File? = null
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String
    private lateinit var addStoryViewModel: AddStoryViewModel

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

        addStoryViewModel = ViewModelProvider(this)[AddStoryViewModel::class.java]
        addStoryViewModel.isLoading.observe(this) {
            setLoading(it)
        }
        addStoryViewModel.onFailure.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        addStoryViewModel.add.observe(this) {
            val error = it.error
            val message = it.message
            if (error == false) {
                goToMain()
                Toast.makeText(this, "$message", Toast.LENGTH_SHORT).show()
            }
        }

        setPref()
        setToolbar()
        setListener()
        textWatcher()
    }

    private fun setPref() {
        val user = HawkStorage.instance(this).getUser()
        token = user.loginResult?.token.toString()
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
            val file = reduceFileImage(getFile as File)
            addStoryViewModel.addStories(token, description, file)
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