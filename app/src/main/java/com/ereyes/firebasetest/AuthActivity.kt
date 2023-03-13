package com.ereyes.firebasetest

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.ereyes.firebasetest.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInApi
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val signInIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null)
                {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if(it.isSuccessful)
                            showHome(account.email ?: "", ProviderType.GOOGLE)
                        else
                            binding.tvMessage.text = getString(R.string.message_error_register_user)
                    }
                }
            } catch (e: ApiException) {
                val message = "signInResult:failed code=" + e.statusCode
                binding.tvMessage.text = message
            }
        } else {
            binding.tvMessage.text = "signInResult: User canceled"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpLogEvent()
        setUpButton()
    }

    private fun setUpLogEvent(){
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString(Constants.MESSAGE, "Integracion de firebase completa")
        analytics.logEvent(Constants.INIT_SCREEN,bundle)
    }

    private fun setUpButton(){
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
        binding.btnAccess.setOnClickListener {
            accesUse()
        }
        binding.btnSignInGoogle.setOnClickListener {
            singInGoogle()
        }
        binding.btnSignInFacebook.setOnClickListener {
            singInFacebook()
        }
    }

    private fun singInFacebook() {
        TODO("Not yet implemented")
    }

    private fun singInGoogle() {
        val googleConf = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()
        signInIntentLauncher.launch(googleClient.signInIntent)
    }

    private fun accesUse() {
        if(validationFields(binding.tilPassword, binding.tilEmail))
        {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful)
                    showHome(email, ProviderType.BASIC)
                else
                    binding.tvMessage.text = getString(R.string.message_error_access_user)
            }
        }
        else
            binding.tvMessage.text = getString(R.string.message_error_data_empty)
    }

    private fun registerUser() {
        if(validationFields(binding.tilPassword, binding.tilEmail))
        {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if(it.isSuccessful)
                    showHome(email, ProviderType.BASIC)
                else
                    binding.tvMessage.text = getString(R.string.message_error_register_user)
            }
        }
        else
            binding.tvMessage.text = getString(R.string.message_error_data_empty)
    }

    private fun showHome(email: String, provider: ProviderType) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(Constants.PROVIDER, provider.toString())
        intent.putExtra(Constants.EMAIL, email)
        startActivity(intent)
        finish()
    }

    private fun showAlert(messageId: Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.title_error))
        builder.setMessage(getString(messageId))
        builder.setPositiveButton(getString(R.string.dialog_acept), null)
        builder.create().show()
    }

    private fun clearText(){
        binding.etEmail.setText(Constants.EMPTY)
        binding.etPassword.setText(Constants.EMPTY)
        binding.tvMessage.text = Constants.EMPTY
    }

    private fun validationFields(vararg textFields: TextInputLayout): Boolean {
        var isValid = true

        for (textField in textFields)
        {
            if(textField.editText?.text.toString().trim().isEmpty())
            {
                textField.error = "Required"
                isValid = false
            }
            else textField.error = null
        }

        return isValid
    }
}