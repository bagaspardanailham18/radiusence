package com.bagas.radiusence.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.bagas.radiusence.MainActivity
import com.bagas.radiusence.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnLogin.setOnClickListener { formValidation() }
    }

    private fun formValidation() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (email.isEmpty() || email == "") {
            binding.layoutEdtEmail.error = "Email harus diisi!"
            binding.layoutEdtEmail.requestFocus()
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEdtEmail.error = "Email tidak valid!"
            binding.layoutEdtEmail.requestFocus()
            return
        } else if (password.isEmpty() || password == "") {
            binding.layoutEdtPassword.error = "Password harus diisi!"
            binding.layoutEdtPassword.requestFocus()
            return
        } else {
            performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        Log.d("Cek", "Email : $email")
        Log.d("Cek", "Password : $password")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(this, "User belum terdaftar atau password salah.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()

            ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(currentUser.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").value.toString().trim()
                        val nim = snapshot.child("nim").value.toString().trim()
                        val email = snapshot.child("email").value.toString().trim()
                        val avatarUrl = snapshot.child("avatarUrl").value.toString().trim()
                        val faculty = snapshot.child("faculty").value.toString().trim()
                        val major = snapshot.child("major").value.toString().trim()
                        val usertype = snapshot.child("usertype").value.toString().trim()

                        editor.apply {
                            putString("userId", currentUser.uid)
                            putString("name", name)
                            putString("nim", nim)
                            putString("email", email)
                            putString("avatarUrl", avatarUrl)
                            putString("faculty", faculty)
                            putString("major", major)
                            putString("usertype", usertype)
                            apply()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            return
        }
    }
}