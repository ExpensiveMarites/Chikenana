package com.example.project_withfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.project_withfirebase.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest

class signUp_activity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)


        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleSigInBtn.setOnClickListener {
            signInWithGoogle()
        }

        binding.signUpBtn.setOnClickListener {
            val username = binding.usernameTextInput.text.toString()
            val email = binding.emailTxtInput.text.toString().trim()
            val pass = binding.passTextInput.editText?.text.toString()
            val confirmPass = binding.RepassTextInput.editText?.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && username.isNotEmpty()) {
                if (pass == confirmPass) {
                    if (isValidEmail(email)) {
                        if (isValidPassword(pass)) {
                            createUserWithEmailAndPassword(username, email, pass)
                        } else {
                            Toast.makeText(this, "Password must be at least 4 characters, including at least one number and one special character", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                when (e.statusCode) {
                    10 -> {
                        // User cancelled the sign-in flow
                        Toast.makeText(this, "Sign-in cancelled", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // Handle other error codes
                        Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                    Toast.makeText(this, "Sign-in successful!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createUserWithEmailAndPassword(username: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val user = firebaseAuth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()

                user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                        firebaseAuth.signOut()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Patterns.EMAIL_ADDRESS.toRegex()
        return emailRegex.matches(email) && email.endsWith("@google.com")
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = """^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{6,}$""".toRegex()
        return passwordRegex.matches(password)
    }

    companion object {
        private const val RC_SIGN_IN = 123
        private const val TAG = "SignUpActivity"
    }
}