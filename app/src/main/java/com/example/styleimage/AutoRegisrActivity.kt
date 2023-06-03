package com.example.styleimage

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.styleimage.databinding.ActivityAutoregistrBinding

class AutoRegisrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAutoregistrBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutoregistrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        autorization()

        binding.tvAutorization.setOnClickListener{
            autorization()
        }

        binding.tvRegistration.setOnClickListener{
            registration()
        }
    }

    private fun autorization(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fAuroRegist, AuthorizationFragment.newInstance())
            .commit()
        binding.tvAutorization.setBackgroundColor(Color.parseColor("#6b5b89"))

        binding.tvRegistration.setBackgroundColor(Color.parseColor("#00ffffff"))
    }

    private fun registration(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fAuroRegist, RegistrFragment.newInstance())
            .commit()
        binding.tvAutorization.setBackgroundColor(Color.parseColor("#00ffffff"))

        binding.tvRegistration.setBackgroundColor(Color.parseColor("#6b5b89"))
    }
}