package com.example.scannerapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.scannerapp.ui.theme.LeftShape
import com.example.scannerapp.ui.theme.RightShape
import com.example.scannerapp.ui.theme.ScannerAppTheme

class MainActivity : ComponentActivity() {
     private var t= mutableStateOf(false)
     private var text= mutableStateOf("")
     private val requestPermissionLauncher= registerForActivityResult(
         ActivityResultContracts.RequestPermission()
     ){
         isGranted ->
         run {
             if (isGranted) {
                 val i= Intent(this, CameraAction::class.java)
                 startActivity(i)
             } else {

             }
         }
     }



    override fun onCreate(savedInstanceState: Bundle?) {
        val extras = intent.extras
        if (extras != null) {
            text.value = extras.getString("Cpt").toString()
        }
        super.onCreate(savedInstanceState)
        setContent {
            ScannerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        onCamera = {
                            requestCameraPermission()
                        },
                        text=text.value
                    )
                }
            }
        }
    }
    private  fun requestCameraPermission(){
        when{
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            )== PackageManager.PERMISSION_GRANTED->{
                val i= Intent(this, CameraAction::class.java)
                startActivity(i)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
            )-> Log.i("kilo","permissions dialog")
            else->  requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCamera:()->Unit,
    text:String
){
     var ts by remember {
         mutableStateOf(text)
     }
     Scaffold(
         floatingActionButton = {
             Row (
                 modifier = Modifier.padding(16.dp)
             ){
                 FloatingActionButton(
                     onClick = { /*TODO*/ },
                     shape = LeftShape.small
                 ) {
                     Icon(imageVector = Icons.Filled.Image, contentDescription ="Image" )
                 }
                 FloatingActionButton(
                     onClick = {
                               onCamera()
                     },
                     shape = RightShape.small
                 ) {
                     Icon(imageVector = Icons.Filled.Camera, contentDescription ="Image" )
                 }
             }
         },
         topBar = {
             Box(
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(120.dp)
                     .background(color = Color.Cyan)
             ) {
                 Column(
                     modifier = Modifier
                         .fillMaxSize()
                 ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                    ) {
                        Text(text = "Image To Any", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(14.dp))
                    }
                     Row(
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(bottom = 12.dp)
                             .weight(1f),
                         verticalAlignment = Alignment.Bottom
                     ) {
                         Text(
                             text ="Word Dox",
                             style = MaterialTheme.typography.titleMedium,
                             modifier = Modifier.weight(1f),
                             textAlign = TextAlign.Center
                         )
                         Text(
                             text ="History",
                             style = MaterialTheme.typography.titleMedium,
                             modifier = Modifier.weight(1f),
                             textAlign = TextAlign.Center
                         )
                     }
                 }

             }
         }
     ) {
         padding->
         Column(modifier = Modifier.padding(padding)) {
             if(ts.isBlank()){

             }else {
                 TextField(value = ts, onValueChange = { ts = it }, modifier = Modifier.fillMaxSize(), label = { Text(
                     text = "Capture Text"
                 )})
             }
         }
     }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScannerAppTheme {
        HomeScreen({},"")
    }
}