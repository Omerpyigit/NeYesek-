package com.example.bugunneyesem.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bugunneyesem.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var preferences: SharedPreferences
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance() //mutlaka ekle sayfa geçişleri için
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //checkIfUserLogged()

        binding.btnGiris.setOnClickListener {
            FirebaseApp.initializeApp(this)
            val kullaniciParola = binding.girisParola.text.toString()
            val kullaniciMail = binding.girisKullaniciMail.text.toString()
            if (kullaniciParola.isNotEmpty() && kullaniciMail.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(kullaniciMail, kullaniciParola)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            checkUser()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                it.exception.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(applicationContext, "Tüm alanları doldurunuz", Toast.LENGTH_LONG)
                    .show()
            }

        }
        binding.btnKayit.setOnClickListener {
            FirebaseApp.initializeApp(this)
            intent = Intent(applicationContext, MainKayit::class.java)
            startActivity(intent)
        }

        binding.forgotTv.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }
    }
    /*
    private fun checkIfUserLogged(){
        if(firebaseAuth.currentUser != null){
            checkUser()
        }
    }
    */
    private fun checkUser(){
        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userType = snapshot.child("usertype").value
                    if(userType == "user"){
                        startActivity(Intent(this@MainActivity, ManiAnasayfa::class.java))
                    }
                    else if(userType == "admin"){
                        startActivity(Intent(this@MainActivity, AdminPanel::class.java))
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }


    public override fun onBackPressed(){
        super.onBackPressed()

    }
}