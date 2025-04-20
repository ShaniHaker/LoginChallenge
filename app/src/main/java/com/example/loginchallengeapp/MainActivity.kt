package com.example.loginchallengeapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.loginchallengeapp.steps.*
import com.example.loginchallengeapp.utilities.Constants
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var progressText: MaterialTextView
    private lateinit var startButton: MaterialButton
    private lateinit var voiceStepHandler: VoiceStepHandler
    private lateinit var biometricStepHandler: BiometricStepHandler
    private lateinit var powerModeStepHandler: PowerModeStepHandler
    private lateinit var chargingStepHandler: ChargingStepHandler
    private lateinit var flatSurfaceStepHandler: FlatSurfaceStepHandler

    private var currentStep = 1
    private val totalSteps = Constants.Progress.TOTAL_STEPS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.progressText)
        startButton = findViewById(R.id.startButton)

        // Initialize step handlers
        voiceStepHandler = VoiceStepHandler(this)
        biometricStepHandler = BiometricStepHandler(this)
        powerModeStepHandler = PowerModeStepHandler(this)
        chargingStepHandler = ChargingStepHandler(this)
        flatSurfaceStepHandler = FlatSurfaceStepHandler(this)

        updateProgress()

        //listener on button
        startButton.setOnClickListener {
            when (currentStep) {
                1 -> voiceStepHandler.start { success ->
                    if (success) {
                        Toast.makeText(this, " U passed the voice test!", Toast.LENGTH_SHORT).show()
                        moveToNextStep()
                    } else {
                        Toast.makeText(this, "Try again.", Toast.LENGTH_SHORT).show()
                    }
                }

                2 -> biometricStepHandler.start { success ->
                    if (success) {
                        Toast.makeText(this, "U passed the Biometric authentication!", Toast.LENGTH_SHORT).show()
                        moveToNextStep()
                    } else {
                        Toast.makeText(this, "Biometric failed or canceled.", Toast.LENGTH_SHORT).show()
                    }
                }

                3 -> powerModeStepHandler.start { success ->
                    if (success) {
                        Toast.makeText(this, "Power saving mode is ON – access granted!", Toast.LENGTH_SHORT).show()
                        moveToNextStep()
                    } else {
                        Toast.makeText(this, "Please turn ON Power Saving Mode to continue.", Toast.LENGTH_LONG).show()
                    }
                }

                4 -> chargingStepHandler.start { success ->
                    if (success) {
                        moveToNextStep()
                    } else {
                        Toast.makeText(this, "Please connect your phone to power to continue.", Toast.LENGTH_LONG).show()
                    }
                }

                5 -> flatSurfaceStepHandler.start { success ->
                    if (success) {
                        moveToNextStep()
                    }
                }

            }
        }
    }

    private fun updateProgress() {
        //update number of step
        progressText.text = "Step $currentStep of $totalSteps"

        //visual update of the progress bar
        progressBar.progress = when (currentStep) {
            in 1..<totalSteps -> currentStep - 1
            totalSteps -> totalSteps - 1
            else -> totalSteps
        }

        // change button text depending on current step
        startButton.text = when (currentStep) {
            1 -> "Start Voice Login"
            2 -> "Verify with Fingerprint"
            3 -> "Check Low Battery Mode"
            4 -> "Check Charger Connection"
            5 -> "Check Device is Flat to Finish"
            else -> "Done"
        }
    }

    private fun moveToNextStep() {
        if (currentStep < totalSteps) {
            currentStep++
            updateProgress()
        } else if (currentStep == totalSteps) {
            progressBar.setProgress(totalSteps)
            progressBar.setTrackColor(ContextCompat.getColor(this, R.color.green))

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, SuccessActivity::class.java))
                finish()
            }, 700)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        voiceStepHandler.handleResult(requestCode, resultCode, data)
    }
}
