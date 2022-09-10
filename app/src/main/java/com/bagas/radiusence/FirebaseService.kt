package com.bagas.radiusence

import com.bagas.radiusence.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class FirebaseService {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    private var name: String = ""
    private var nim: String = ""
    private var email: String = ""
    private var faculty: String = ""
    private var major: String = ""
    private var userType: String = ""
    private var avatarUrl: String = ""

    fun logout() {
        auth = Firebase.auth
        auth.signOut()
    }

    fun getUserData(uid: String): Users {
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    name = snapshot.child("name").value.toString().trim()
                    nim = snapshot.child("nim").value.toString().trim()
                    email = snapshot.child("email").value.toString().trim()
                    faculty = snapshot.child("faculty").value.toString().trim()
                    major = snapshot.child("major").value.toString().trim()
                    userType = snapshot.child("userType").value.toString().trim()
                    avatarUrl = snapshot.child("avatarUrl").value.toString().trim()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return Users(name, nim, email, faculty, major, userType,avatarUrl)
    }

}