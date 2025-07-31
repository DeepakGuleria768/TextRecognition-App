package com.example.ocr

import android.media.Image
import android.view.RoundedCorner
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.takePicture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.util.concurrent.Executors


//PreviewView = Screen jisme live feed dikh rahi hai
//CameraProvider = Camera ka remote system
//Lifecycle = Jab screen on ho tab camera chalu, jab band ho tab off
//ImageCapture = Jab chaho photo khinch lo
@Composable
fun CameraBox(onTextRecognized:(String)->Unit){

    // Kya Hai?
    //context: Android environment ka reference chahiye hota hai har cheez ko initialize karne ke liye (like camera, view, etc.)
    //lifeCycleOwner: Jetpack Compose me screen ke lifecycle ko manage karta hai (jaise onCreate, onDestroy, etc.)
    //✅ Kyun Zaroori Hai?
    //Camera lifecycle bindToLifecycle() me dena hota hai — jisse camera screen ke saath attach/detach ho sake (crash na ho app switch karne pe)
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current

// Kya Hai?
//PreviewView ek Android View hai jo camera ka live feed show karta hai.
//layoutParams se ye view full screen banaya gaya.
//✅ Kyun Zaroori Hai?
//Jetpack Compose me XML nahi hota, to hume imperative style me PreviewView banana padta hai camera preview dikhane ke liye.
    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

//    cameraProviderFuture
//    Iska kaam hai device ke camera hardware access karna.
//    getInstance() async way se CameraX engine initialize karta hai.
//    ✅ 2. imageCapture
//    Ye object banata hai jiska use hum photo click karne ke liye karte hain.
//    ✅ 3. cameraExecutor
//    Ye ek background thread hai jo camera operations (like capturing image) main thread pe load na daale isliye use hota hai.


    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Bind the Camera
    //Jetpack Compose ka block jo screen load hote hi ek baar chalta hai.
    LaunchedEffect(Unit) {
        try {
         //   Is line se hume actual cameraProvider instance milta hai — ye async future ko sync me convert karta hai.
            val cameraProvider = cameraProviderFuture.get()
            //Preview object banate hain jo camera ki live feed previewView me send karega.
            //surfaceProvider connect karta hai camera output ko display se.
            val preview = Preview.Builder().build().apply {
                surfaceProvider = previewView.surfaceProvider
            }
            //Hum specify karte hain ki hume back camera use karna hai.
            //Aur chahe to CameraSelector.DEFAULT_FRONT_CAMERA bhi likh sakte ho.
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            //unbindAll():
            //Pichle koi camera bindings active hain to unko hata deta hai — taaki clash na ho.
            //🔹 bindToLifecycle(...):
            //Camera ke components ko app ke lifecycle ke saath bind karta hai.
            //Jab screen khuli — camera on.
            //Jab screen band — camera band (memory save + crash safe)
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner = lifeCycleOwner,
                cameraSelector = cameraSelector,
                preview,
                imageCapture
            )
        }catch (e:Exception){
            e.message
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(450.dp)
            .border(2.dp, color = Color.Gray, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))

            .background(Color.Black)
    ){
        AndroidView(
            factory = {previewView},
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {

                //You are creating a new temporary file in the app’s cache directory.
                //System.currentTimeMillis() ensures each file name is unique.
                //The file will store the photo the user captures.
                val outputFile = File(context.cacheDir,"captured_image_${System.currentTimeMillis()}.jpg")
                //You’re wrapping that file into an OutputFileOptions object.
                //This object is required by the imageCapture.takePicture() method.
                //It tells the camera where and how to save the captured photo.
                val outputOptions  = ImageCapture.OutputFileOptions.Builder(outputFile).build()
                //You’re asking the camera to take a picture asynchronously.
                //outputOptions defines where to save it.
                //cameraExecutor runs this in a background thread (so UI is not blocked).
                imageCapture.takePicture(
                    outputOptions,
                    cameraExecutor,
                    //You're passing an anonymous class implementation of the OnImageSavedCallback interface.
                    //It gives you two override methods:
                    //onImageSaved() – when image is saved successfully.
                    //onError() – if something goes wrong.
                    // In Kotlin class can contain the interface also . This is called nested Interface
                    object: ImageCapture.OnImageSavedCallback{
                        //This method is called after the image has been saved.
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            //The camera may return a savedUri (optional).
                            //If not, we manually convert the file into a URI using toUri().
                            val savedUri = outputFileResults.savedUri ?: outputFile.toUri()
                            //We call our TextRecognitionHelper (which uses ML Kit Text Recognition).
                            //Pass the image URI to it.
                            //It scans the image for any text (OCR).
                            //Once done, it returns the recognized text to:
                            //onTextRecognized(it)
                            //This is most likely a function/lambda defined by you somewhere to display, log, or save the scanned text.
                            TextRecognitionHelper.recognizeTextFromUri(context,savedUri){
                                onTextRecognized(it)
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            exception.message
                        }
                    }
                )

            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(Color(0xff999997))
        ) {
            Text("Capture")
        }
    }
}