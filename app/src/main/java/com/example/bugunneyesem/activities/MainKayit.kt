package com.example.bugunneyesem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bugunneyesem.databinding.ActivityMainKayitBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainKayit : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var kullaniciAdi = ""
    private var kullaniciParola = ""
    private var kullaniciMail = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainKayitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        //val database = FirebaseDatabase.getInstance("https://neyeseklogin-default-rtdb.europe-west1.firebasedatabase.app/").reference


        binding.btnKayitOlustur.setOnClickListener {
            FirebaseApp.initializeApp(this)
            kullaniciAdi = binding.kayitKullaniciadi.text.toString()
            kullaniciParola = binding.kayitParola.text.toString()
            kullaniciMail = binding.kayitMail.text.toString()
            val parolaTekrar = binding.kayitParolaTK.text.toString()
            if(kullaniciAdi.isNotEmpty() && kullaniciParola.isNotEmpty() && kullaniciMail.isNotEmpty()){
                if(kullaniciParola == parolaTekrar){
                    firebaseAuth.createUserWithEmailAndPassword(kullaniciMail, kullaniciParola)
                        .addOnSuccessListener {
                            //database.child(firebaseAuth.currentUser?.uid).setValue(KullaniciBilgileri(kullaniciAdi, kullaniciMail))
                            updateUserInfo()
                            Toast.makeText(applicationContext,"KAYIT OLUŞTURULDU",Toast.LENGTH_LONG).show()
                            //database.setValue(KullaniciBilgileri(kullaniciAdi, kullaniciMail))


                        }
                        .addOnFailureListener{e->
                            Toast.makeText(applicationContext,"${e.message}'dan dolayı hesap oluşturuldu",Toast.LENGTH_LONG).show()

                        }
                }else{
                    Toast.makeText(applicationContext,"Parolalar Eşleşmiyor",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(applicationContext,"Tüm alanları doldurunuz",Toast.LENGTH_LONG).show()
            }

        }


        binding.btnGirisDon.setOnClickListener {
            intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUserInfo() {
        //kayıt tarihi
        val timestamp = System.currentTimeMillis()
        //şuanki kullanıcının uid
        val uid = firebaseAuth.currentUser?.uid
        val hashmap: HashMap<String, Any?> = HashMap()
        hashmap["uid"] = uid
        hashmap["email"] = kullaniciMail
        hashmap["kadi"] = kullaniciAdi
        hashmap["profilPhoto"] = ""
        hashmap["usertype"] = "user"
        hashmap["kayit_tarih"] = timestamp

        //database'e gönderme

        val ref = FirebaseDatabase.getInstance().reference
        ref.child("Users").child(uid!!)
            .setValue(hashmap)
            .addOnSuccessListener {
                Toast.makeText(applicationContext,"Kayıt oluşturuldu",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(applicationContext,"Kullanıcı bilgileri kaydedilirken hata oldu ${e.message}",Toast.LENGTH_LONG).show()

            }
    }
}