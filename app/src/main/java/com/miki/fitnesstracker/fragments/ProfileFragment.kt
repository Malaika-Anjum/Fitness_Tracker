package com.miki.fitnesstracker.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.data.PrefsManager
import com.miki.fitnesstracker.worker.WaterReminderWorker
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ProfileFragment : Fragment(R.layout.profile_fragment) {

    private lateinit var prefs: PrefsManager
    private lateinit var navController: NavController
    private lateinit var switchNotifications: androidx.appcompat.widget.SwitchCompat
    private lateinit var textUsername: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var detailAge: TextView
    private lateinit var detailHeight: TextView
    private lateinit var detailWeight: TextView
    private lateinit var ivProfile: ImageView
    private var imageUri: Uri? = null

    // Camera permission
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) launchCamera()
        }

    // Gallery picker
    private val pickFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                setCircularImageFromUri(it)
                saveProfileImageUri(it)
            }
        }

    // Camera capture
    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                setCircularImageFromUri(imageUri!!)
                saveProfileImageUri(imageUri!!)
            }
        }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)


        prefs = PrefsManager(requireContext())
        textUsername = view.findViewById(R.id.text_user_name)
        detailAge = view.findViewById(R.id.detail_age)
        detailHeight = view.findViewById(R.id.detail_height)
        detailWeight = view.findViewById(R.id.text_user_weight)
        ivProfile = view.findViewById(R.id.img_profile_avatar)
        switchNotifications = view.findViewById(R.id.switch_notifications)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)

        loadProfileImage()
        ivProfile.setOnClickListener { showImagePickerOptions() }

        // Load values
        textUsername.text = prefs.getUsername()
        detailAge.text = "${prefs.getAge()} years"
        detailHeight.text = "${prefs.getHeight()} cm"
        detailWeight.text = "${prefs.getCurrentWeight()} kg"
        switchNotifications.isChecked = prefs.isWaterReminderEnabled()

        // Editable fields
        makeEditable(textUsername, "Username") { prefs.setUsername(it) }
        makeEditable(detailAge, "Age", numeric = true) { it.toIntOrNull()?.let { v -> prefs.setAge(v); detailAge.text="$v years" } }
        makeEditable(detailHeight, "Height (cm)", numeric = true) { it.toIntOrNull()?.let { v -> prefs.setHeight(v); detailHeight.text="$v cm" } }
        makeEditable(detailWeight, "Weight (kg)", numeric = true) { it.toFloatOrNull()?.let { v -> prefs.setCurrentWeight(v); detailWeight.text="$v kg" } }

        // Water reminder toggle
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.setWaterReminderEnabled(isChecked)
            if (isChecked) startWaterReminder() else stopWaterReminder()
        }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { dialog, _ ->
                    auth.signOut()

                    val navOptions = androidx.navigation.NavOptions.Builder()
                        .setPopUpTo(R.id.homeFragment, true)
                        .build()

                    navController.navigate(R.id.signInFragment, null, navOptions)
                    dialog.dismiss()
                }
                .setNegativeButton("No", null)
                .show()
        }

    }

    private fun makeEditable(textView: TextView, hint: String, numeric: Boolean = false, onSave: (String) -> Unit) {
        textView.setOnClickListener {
            val input = EditText(requireContext())
            input.inputType = if (numeric) android.text.InputType.TYPE_CLASS_NUMBER else android.text.InputType.TYPE_CLASS_TEXT
            input.setText(textView.text.toString().filter { it.isDigit() || !numeric })
            input.setSelection(input.text.length)

            AlertDialog.Builder(requireContext())
                .setTitle("Edit $hint")
                .setView(input)
                .setPositiveButton("Save") { _, _ ->
                    val newValue = input.text.toString()
                    if (newValue.isNotBlank()) {
                        onSave(newValue)
                        textView.text = newValue
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun showImagePickerOptions() {
        val options = arrayOf("Select from Gallery", "Capture Photo")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Profile Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickFromGallery.launch("image/*")
                    1 -> checkCameraPermissionAndLaunch()
                }
            }.show()
    }

    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val photoFile = try { createImageFile() } catch (e: IOException) { e.printStackTrace(); null }
        photoFile?.let {
            imageUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", it)
            takePhoto.launch(imageUri)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(requireContext().cacheDir, "PROFILE_$timestamp.jpg").apply { createNewFile() }
    }

    private fun saveProfileImageUri(uri: Uri) {
        requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
            .edit().putString("profile_image_uri", uri.toString()).apply()
    }

    private fun loadProfileImage() {
        requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
            .getString("profile_image_uri", null)?.let {
                imageUri = Uri.parse(it)
                setCircularImageFromUri(imageUri!!)
            }
    }

    private fun setCircularImageFromUri(uri: Uri) {
        try {
            requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val drawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
                drawable.isCircular = true
                ivProfile.setImageDrawable(drawable)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // WorkManager for water reminder
    private fun startWaterReminder() {
        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork("water_reminder", ExistingPeriodicWorkPolicy.REPLACE, workRequest)
    }

    private fun stopWaterReminder() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("water_reminder")
    }
}
