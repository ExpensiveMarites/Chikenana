package com.example.project_withfirebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project_withfirebase.databinding.ActivityLogInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LogIn_activity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signUpTextView.setOnClickListener {
            val intent = Intent(this, signUp_activity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.userNameInput.text.toString()
            val pass = binding.passWordInput.editText?.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.googleBtn.setOnClickListener {
            getGoogleAccounts()
        }
    }

    private fun getGoogleAccounts() {
        googleSignInClient.silentSignIn()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val account = task.result
                    showAccountChooser(listOf(account))
                } else {
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_GET_ACCOUNTS)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GET_ACCOUNTS) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                showAccountChooser(listOf(account))
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun showAccountChooser(accounts: List<GoogleSignInAccount>) {
        val accountNames = accounts.map { it.displayName }
        val accountChooserDialog = android.app.AlertDialog.Builder(this)
            .setTitle("Choose a Google account")
            .setItems(accountNames.toTypedArray()) { _, which ->
                val selectedAccount = accounts[which]
                firebaseAuthWithGoogle(selectedAccount.idToken!!)
            }
            .create()
        accountChooserDialog.show()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val RC_GET_ACCOUNTS = 9002
        private const val TAG = "LogInActivity"
    }
}