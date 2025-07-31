package com.example.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object TextRecognitionHelper {

    // Helper Function
    //This function takes:
    //context: The Android context (required for file access).
    //uri: The location of the image (usually from gallery or camera).
    //onResult: A callback function that gives back the recognized text as a String.
    fun recognizeTextFromUri(context: Context, uri: Uri, onResult:(String)->Unit){
        // Converts the image file into a format (InputImage) that ML Kit can understand.
        //fromFilePath() reads the image from the given URI.
        val inputImage = InputImage.fromFilePath(context,uri)
        //Gets the default ML Kit text recognizer.
        //This recognizer will scan the image and extract text using machine learning.
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

//        Starts the asynchronous process to read text from the image.
//        This returns a Task, so we attach listeners.
        recognizer.process(inputImage)
            //If ML Kit successfully reads text, this block runs.
            //visionText.text gives the full recognized text.
            //We send this back to the calling function using onResult.
            .addOnSuccessListener { visionText ->
                val formattedText = visionText.textBlocks.joinToString("\n") { block ->
                    block.lines.joinToString(" ") { it.text }
                }
                onResult(formattedText)
            }
            // if failed
            .addOnFailureListener { exception ->
                onResult("Recognition Failed : ${exception.localizedMessage}")
            }
    }

}