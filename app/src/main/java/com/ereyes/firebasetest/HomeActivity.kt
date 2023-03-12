package com.ereyes.firebasetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ereyes.firebasetest.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpIntent()
        setUpButton()
    }

    private fun setUpButton() {
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setUpIntent() {
        val bundle = intent.extras
        val email = bundle?.getString(Constants.EMAIL)
        val provider = bundle?.getString(Constants.PROVIDER)
        setUpEdiText(email ?: "", provider ?: "")
    }

    private fun setUpEdiText(email: String, provider: String) {
        binding.tvEmail.text = email
        binding.tvProvider.text = provider
    }
}