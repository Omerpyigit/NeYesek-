package com.example.bugunneyesem.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.bugunneyesem.databinding.ActivitySplashAnimBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Suppress("DEPRECATION")
class SplashAnim : AppCompatActivity() {
    private val splashscreen = 5000
    lateinit var binding: ActivitySplashAnimBinding
    lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashAnimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        Handler().postDelayed({
            checkIfUserLogged()
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            //finish()
        },splashscreen.toLong())

        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun checkIfUserLogged(){
        if(firebaseAuth.currentUser != null){
            checkUser()
        }
        else{
            startActivity(Intent(this@SplashAnim,MainActivity::class.java))
        }
    }
    private fun checkUser(){
        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userType = snapshot.child("usertype").value
                    if(userType == "user"){
                        startActivity(Intent(this@SplashAnim, ManiAnasayfa::class.java))
                    }
                    else if(userType == "admin"){
                        startActivity(Intent(this@SplashAnim, AdminPanel::class.java))
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}