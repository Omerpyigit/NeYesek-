package com.example.bugunneyesem.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bugunneyesem.databinding.ActivityKategoriEkleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class KategoriEkle : AppCompatActivity() {
    private lateinit var binding: ActivityKategoriEkleBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityKategoriEkleBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.backbutton2.setOnClickListener{
            startActivity(Intent(this, AdminKategoriAdd::class.java))
        }

        binding.kaydetBut1.setOnClickListener {
            validateData()
        }
    }

    private var kategori = ""

    private fun validateData(){

        //girdi al
        kategori = binding.kategoriEt.text.toString().trim()

        if(kategori.isEmpty()){
            Toast.makeText(this,"Lütfen Kategori Girin",Toast.LENGTH_SHORT).show()

        }
        else{
            addKategoriFirebase()
        }

    }

    private fun addKategoriFirebase(){
        val timestamp = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["kategori"] = kategori
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Kategoriler")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this,"Kategori başarıyla Eklendi",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Toast.makeText(this,"${e.message}'dan dolayı başarısız oldu",Toast.LENGTH_SHORT).show()
            }



    }
}