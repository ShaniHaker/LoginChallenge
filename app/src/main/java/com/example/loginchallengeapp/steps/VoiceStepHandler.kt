package com.example.loginchallengeapp.steps

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import com.example.loginchallengeapp.utilities.Constants
import java.util.*

class VoiceStepHandler(private val activity: Activity) {

    private var callback: ((Boolean) -> Unit)? = null

    fun start(onResult: (Boolean) -> Unit) {
        this.callback = onResult

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the magic word...")
        }

        try {
            activity.startActivityForResult(intent, Constants.Progress.VOICE_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(activity, "Speech not supported", Toast.LENGTH_SHORT).show()
            callback?.invoke(false)
        }
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.Progress.VOICE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = result?.get(0)?.lowercase(Locale.ROOT) ?: ""
            val isCorrect = spokenText.contains(Constants.Progress.CORRECT_PASSWORD)
            callback?.invoke(isCorrect)
        }
    }
}
