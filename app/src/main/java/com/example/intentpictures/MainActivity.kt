package com.example.intentpictures

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MainActivity"
private val CAMERA_INTENT_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Getting the context of Button
        val cameraButton: Button = findViewById(R.id.camera_button)

        // On Click listener of the cameraButton
        cameraButton.setOnClickListener() {

            // Calling the Intent function
            dispatchCameraIntent()
        }
    }

    // Definition and Implementation of the  Camera Intent function
    private fun dispatchCameraIntent() {

        // Intent definition
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // ResolveActivity -> It returns the Activity component that should be used to handle this intent.
            takePictureIntent.resolveActivity(packageManager)?.also {

                // Attempt to create an image file
                val photoFile: File? = try {

                    // Calling the createImageFile function to create a file for us
                    createImageFile()
                }

                // Catching an exception
                catch (e: IOException) {
                    null
                }

                // If the photoFile is created successfully, then take the photoUri from the file provider
                photoFile?.also {
                    val photoUri: Uri =
                        FileProvider.getUriForFile(this, "com.example.intentpictures", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                    // Start the intent activity for result
                    startActivityForResult(takePictureIntent, CAMERA_INTENT_REQUEST_CODE)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Get the context of imageView
        val imageView: ImageView = findViewById(R.id.imageView)

        if (requestCode == CAMERA_INTENT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Collect the result and display
            // For small bitmap
//            val bitmapImage = data?.extras?.get("data") as Bitmap
//            imageView.setImageBitmap(bitmapImage)

            // Collect the result and display
            // For full size image
            BitmapFactory.decodeFile(currentPhotoPath)?.also { bitmap ->
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    // Declaring the variable for currentPhotoPath
    lateinit var currentPhotoPath: String
    private fun createImageFile(): File {
        // Create an Image Name with timestamp
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        // Get directory name to store the image into that directory
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Create file and return that file
        return File.createTempFile(
            "JPEG_${timestamp}_", /* Prefix */
            ".jpg", /* Suffix */
            storageDir /* Directory Name */
        ).apply {
            // Absolute path of the image
            currentPhotoPath = absolutePath
        }
    }


}