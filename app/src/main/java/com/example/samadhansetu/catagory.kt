package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.samadhansetu.databinding.ActivityCatagoryBinding


class catagory : AppCompatActivity() {
    lateinit var  binding: ActivityCatagoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding= ActivityCatagoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
//
        binding.student.setOnClickListener{
            val intent = Intent(this@catagory,student_interface::class.java)
            startActivity(intent)
           // finish()
        }
        binding.caretaker.setOnClickListener{
            val intent = Intent(this@catagory,caretaker_interface::class.java)
            startActivity(intent)
        }
        binding.background.setImageResource(R.drawable.background)
        binding.logo.setImageResource(R.drawable.reallogo)
        binding.student.setImageResource(R.drawable.st)
        binding.caretaker.setImageResource(R.drawable.ct)


    }
}