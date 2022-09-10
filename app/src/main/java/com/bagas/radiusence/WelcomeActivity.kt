package com.bagas.radiusence

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bagas.radiusence.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        binding.btnToMain.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            if (name.isEmpty() || name.equals("") || name == "") {
                binding.layoutEdtName.error = "Nama harus diisi"
            } else {
                editor.apply {
                    putString("name", name)
                    apply()
                }
                val toMain = Intent(this@WelcomeActivity, MainActivity::class.java)
                startActivity(toMain)
            }
        }
    }
}