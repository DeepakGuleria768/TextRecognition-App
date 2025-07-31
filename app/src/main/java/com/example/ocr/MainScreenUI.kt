package com.example.ocr

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun MainScreenUI() {
    val scrollState = rememberScrollState()
    var recognizedText by remember { mutableStateOf("Recognized text will appear here.") }
    // Yeh state image URI store karega (jo user select karega gallery se)
    //Jab user image select kare, hume uska path chahiye hota hai OCR karne ke liye.
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//         what -->  Yeh Jetpack Compose ka tarika hai Android Context lene ka
//         why -->   ML Kit ya permissions jaise features ko context chahiye hota hai
    val context = LocalContext.current

    //Kya Hai?
    //gallaryLauncher ek intent launcher hai, jo gallery open karega
    //User jab image choose karega, uri milega.
    val gallaryLauncher: ManagedActivityResultLauncher<String, Uri?> =
        rememberLauncherForActivityResult(
            //GetContent() is a predefined contract to open gallery
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            selectedImageUri = uri
            uri?.let {
                //Jab image milti hai (uri), us image ko OCR function recognizeTextFromUri() me bhej dete hain
                TextRecognitionHelper.recognizeTextFromUri(context, uri) { result ->
                    //Jo text milega usse recognizedText me store kar denge → UI update ho jayega
                    recognizedText = result
                }
            }
        }

    //State banayi ye check karne ke liye: camera permission mila ya nahi
    // because --> Kuch devices me camera access ke liye runtime permission chahiye hoti hai.
    val cameraPermission = remember { mutableStateOf(false) }


    //Yeh permission launcher user se permission maangta hai (CAMERA)
    //Agar mil gayi, to cameraPermission.value = true
    val permissionLauncher = rememberLauncherForActivityResult(
        //Android 6.0+ me sensitive permissions runtime me maangni padti hai.
        //Isliye RequestPermission() launcher ka use hota hai.
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            cameraPermission.value = isGranted
        }
    )
// Check permission on Launch
    //LaunchedEffect(Unit) => Compose ka special block jo screen load hote hi chalega
//    Jab screen open ho tab check karna hai: camera permission already mili hai ya nahi
//            Agar permission mili hai → set true
//    Agar nahi mili → user se permission maango
    LaunchedEffect(Unit) {
//        Kya kar raha hai?
//        Ye line check kar rahi hai:
//        "Kya camera permission already mili hui hai?"
//        ContextCompat.checkSelfPermission(...) ka return hota hai:
//        PackageManager.PERMISSION_GRANTED 👉 matlab: permission mili hai
//        PackageManager.PERMISSION_DENIED 👉 matlab: permission nahi mili
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            cameraPermission.value = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .padding(paddingValues = paddingValues)
                .padding(16.dp)

        ) {

            if (cameraPermission.value) {
                CameraBox { text ->
                    recognizedText = text
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Camera permission is required to use this feature. ", color = Color.Red)
                }
            }

            Spacer(Modifier.height(16.dp))

            IconButton(
                onClick = { gallaryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .width(180.dp)
                        .padding(5.dp)
                        .background(Color.Yellow)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.gallary),
                        contentDescription = "Image gallery",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Black
                    )
                    Spacer(Modifier.width(3.dp))
                    Text("Gallery", color = Color.Black)


                }
            }
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .background(Color.White)
            ) {
                Text(
                    text = recognizedText,
                    fontSize = 16.sp,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }


    }
}
