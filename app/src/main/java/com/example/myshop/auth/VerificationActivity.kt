package com.example.myshop.auth

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.myshop.MainActivity
import com.example.myshop.R
import com.example.myshop.databinding.ActivityVerificationBinding
import com.example.myshop.firebase.FirebaseConsts.USER_PATH
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class VerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerificationBinding
    private lateinit var auth: FirebaseAuth
    var storedVerificationId = ""
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private var phoneNumber = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.otpET.isEnabled = false
        binding.verifyCodeBTN.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        auth = FirebaseAuth.getInstance()

        phoneNumber = intent.getStringExtra(LoginActivity.PHONE_NUMBER)!!
        sendCode("+88 $phoneNumber")

        binding.verifyCodeBTN.setOnClickListener {
            val otp = binding.otpET.text.toString().trim()
            if (otp.length < 6) {
                binding.otpET.error = "Enter your 6 digit OTP"

            } else {
                binding.verifyCodeBTN.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
                signInWithPhoneAuthCredential(credential)
            }
        }
    }

    private fun sendCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            binding.otpET.setText(credential.smsCode)
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(this@VerificationActivity, "Invalid request", Toast.LENGTH_SHORT)
                    .show()
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(
                    this@VerificationActivity,
                    "The SMS quota for the project has been exceeded",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                Toast.makeText(
                    this@VerificationActivity,
                    "reCAPTCHA verification attempted with null Activity",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.verifyCodeBTN.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.verifyCodeBTN.isEnabled = false

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Toast.makeText(this@VerificationActivity, "OTP Send", Toast.LENGTH_SHORT).show()
            binding.verifyCodeBTN.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.otpET.isEnabled = true
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = task.result?.user
                    Firebase.firestore.collection(USER_PATH)
                        .document(user?.uid!!)
                        .get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                startActivity(
                                    Intent(
                                        this@VerificationActivity,
                                        MainActivity::class.java
                                    )
                                )
                                finishAffinity()
                            } else {
                                createUserDialog(user.uid)
                            }
                        }
                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            this@VerificationActivity,
                            "Verification code is invalid!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.verifyCodeBTN.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE

                }
            }
    }

    private fun createUserDialog(uid: String) {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.user_info_dialog)
        dialog.setCancelable(false)

        val editName: EditText = dialog.findViewById(R.id.nameET)!!
        val progressBar: ProgressBar = dialog.findViewById(R.id.progressBar)!!
        val createBtn: Button = dialog.findViewById(R.id.createBtn)!!

        createBtn.setOnClickListener {
            val name = editName.text.toString().trim()
            if (name.isEmpty()) {
                editName.error = "Please enter your full name"
            } else {
                progressBar.visibility = View.VISIBLE
                createBtn.visibility = View.GONE
                createUser(uid, name)
            }
        }
        dialog.show()
    }

    private fun createUser(uid: String, name: String) {
        val user = User(
            name,
            phoneNumber
        )
        Firebase.firestore.collection(USER_PATH)
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                startActivity(
                    Intent(
                        this@VerificationActivity,
                        MainActivity::class.java
                    )
                )
                finishAffinity()
            }.addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
}