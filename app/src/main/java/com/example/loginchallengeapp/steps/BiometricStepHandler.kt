package com.example.loginchallengeapp.steps

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricStepHandler(private val context: FragmentActivity) {

    private var callback: ((Boolean) -> Unit)? = null

    fun start(onResult: (Boolean) -> Unit) {
        this.callback = onResult

        val biometricManager = BiometricManager.from(context)
        // for old devices the biometric weak
        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )

        when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(context, "No biometric hardware found on this device", Toast.LENGTH_LONG).show()
                callback?.invoke(false)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(context, "Biometric hardware is currently unavailable", Toast.LENGTH_LONG).show()
                callback?.invoke(false)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(context, "No fingerprint or face data enrolled", Toast.LENGTH_LONG).show()
                callback?.invoke(false)
            }
            else -> {
                Toast.makeText(context, "Biometric authentication is not supported", Toast.LENGTH_LONG).show()
                callback?.invoke(false)
            }
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(context)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Use your fingerprint to continue")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(context, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    callback?.invoke(true)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callback?.invoke(false)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback?.invoke(false)
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }
}
