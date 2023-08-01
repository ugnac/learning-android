package com.learn.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.learn.camera.util.checkSelfPermissionCompat
import com.learn.camera.util.requestPermissionsCompat
import com.learn.camera.util.shouldShowRequestPermissionRationaleCompat
import com.learn.camera.util.showSnackbar

const val PERMISSION_REQUEST_CAMERA = 0

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private lateinit var layout: View

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher. You can use either a val, as shown in this snippet,
    // or a lateinit var in your onAttach() or onCreate() method.
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission has been granted. Start camera preview Activity.
                layout.showSnackbar(
                    R.string.camera_permission_granted,
                    Snackbar.LENGTH_INDEFINITE,
                    R.string.ok
                ) {
                    startCamera()
                }
            } else {
                // Permission request was denied.
                layout.showSnackbar(
                    R.string.camera_permission_denied,
                    Snackbar.LENGTH_SHORT,
                    R.string.ok)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layout = findViewById(R.id.main_layout)

        // Register a listener for the 'Show Camera Preview' button.
        findViewById<Button>(R.id.button_open_camera).setOnClickListener { showCameraPreview() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                layout.showSnackbar(R.string.camera_permission_granted, Snackbar.LENGTH_SHORT)
                startCamera()
            } else {
                layout.showSnackbar(R.string.camera_permission_denied, Snackbar.LENGTH_SHORT)
            }
        }
    }

    private fun showCameraPreview() {
        // Check if the Camera permission has been granted
        if (checkSelfPermissionCompat(Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already available, start camera preview
            layout.showSnackbar(
                R.string.camera_permission_available,
                Snackbar.LENGTH_SHORT)
            layout.showSnackbar(
                R.string.camera_permission_available,
                Snackbar.LENGTH_INDEFINITE,
                R.string.ok)
            startCamera()
        } else {
            // Permission is missing and must be requested.
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.CAMERA)) {
            layout.showSnackbar(
                R.string.camera_access_required,
                Snackbar.LENGTH_INDEFINITE,
                R.string.ok
            ) {
                // Way 1:
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )

                // Way 2:
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        } else {
            layout.showSnackbar(R.string.camera_permission_not_available, Snackbar.LENGTH_SHORT)

            // Way 1:
            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissionsCompat(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)

            // Way 2:
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val intent = Intent(this, CameraPreviewActivity::class.java)
        startActivity(intent)
    }
}