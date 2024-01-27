package com.example.bugunneyesem.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bugunneyesem.databinding.ActivityAdminPanelBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_mani_anasayfa.textView3

class AdminPanel : AppCompatActivity() {
    lateinit var binding: ActivityAdminPanelBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val kullaniciadi = snapshot.child("kadi").value
                    textView3.setText("ADMIN PANEL")
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })




        binding.btnTarifler.setOnClickListener {
            startActivity(Intent(this, AdminKategoriAdd::class.java))
        }
        binding.btnCikis.setOnClickListener {
            firebaseAuth.signOut()
            intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }
    }
}