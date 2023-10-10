package com.example.scannerapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

import com.example.scannerapp.ui.theme.ScannerAppTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import okio.IOException
import java.io.File
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraAction:ComponentActivity(){
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File
    private lateinit var  photoUri:Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(true)

    @OptIn(ExperimentalCoilApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScannerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (shouldShowCamera.value) {
                        CameraView(
                            outputDirectory = outputDirectory,
                            executor = cameraExecutor,
                            onImageCaptured = ::handleImageCapture,
                            onError = { Log.e("kilo", "View error:", it) }
                        )
                    }

                    if (shouldShowPhoto.value) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            Image(
                                painter = rememberImagePainter(photoUri),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(3f)
                                    .padding(bottom = 12.dp)
                            )
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {

                                        Button(onClick = {
                                            val de=deleteImage(photoUri)
                                            if(de){
                                                shouldShowCamera.value=true
                                                shouldShowPhoto.value=false
                                            }
                                        },
                                            modifier = Modifier.weight(1f).padding(10.dp)
                                        ) {
                                            Text(text = "Re Capture")
                                        }
                                        Button(onClick = {
                                            processImage(photoUri)
                                        },
                                            modifier = Modifier.weight(1f).padding(10.dp)
                                        ) {
                                            Text(text = "Generate")
                                        }


                                }
                            }

                        }

                    }

                }
            }
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }
    private fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        shouldShowCamera.value = false

        photoUri = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }
    private fun processImage(fileUri: Uri){

        try {
            val inputImage = InputImage.fromFilePath(this, fileUri)
            val recognizer = TextRecognition.getClient()
            //Image Processing
             recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    // Task completed successfully
                    //This VisionText holds the actual text information
                    Log.d("T1", "processImage:success ")
                    val resultText = visionText.text
                    Log.d("T2", "processImage: extractedText:$resultText")
                    val i=Intent(this,MainActivity::class.java)
                    i.putExtra("Cpt",resultText)
                    startActivity(i)
                    finish()
                    if(TextUtils.isEmpty(resultText)){

                    }
                    else
                    {

                    }


                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.e("T3", "processImage: failure:"+e.message )
                }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    private fun deleteImage(imageUri:Uri):Boolean{
        val file = File(imageUri.path)
        if (file.delete()){
            return true
        }
       return false
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
