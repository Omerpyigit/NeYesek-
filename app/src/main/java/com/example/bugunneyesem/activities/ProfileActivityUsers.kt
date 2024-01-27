package com.example.bugunneyesem.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bugunneyesem.MyApplication
import com.example.bugunneyesem.R
import com.example.bugunneyesem.adapters.TarifAdapterFavoriUsers
import com.example.bugunneyesem.databinding.ActivityProfileUsersBinding
import com.example.bugunneyesem.models.ModelTarif
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivityUsers : AppCompatActivity() {
    private lateinit var binding: ActivityProfileUsersBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var foodsArrayList: ArrayList<ModelTarif>
    private lateinit var adapterFavoriUsers: TarifAdapterFavoriUsers

    private var userUid = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)



        userUid = intent.getStringExtra("userUid")!!

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()
        loadFavoriTarifler()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadUserInfo(){
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(userUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val kadi = "${snapshot.child("kadi").value}"
                    val kayit_tarih = "${snapshot.child("kayit_tarih").value}"
                    val usertype = "${snapshot.child("usertype").value}"
                    val profilPhoto = "${snapshot.child("profilPhoto").value}"
                    val uid = "${snapshot.child("uid").value}"

                    //timestamp i normal tarihe çevir
                    val formattedDate = MyApplication.formatTimeStamp(kayit_tarih.toLong())

                    //verileri aktar
                    binding.nameTv.text = kadi
                    binding.emailTv.text = email
                    binding.memberDateTv.text = formattedDate
                    binding.accountTypeTv.text = usertype

                    //set image
                    try {
                        Glide.with(this@ProfileActivityUsers)
                            .load(profilPhoto)
                            .placeholder(R.drawable.ic_profile_gray)
                            .into(binding.profileTv)
                    }
                    catch (e: Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun loadFavoriTarifler(){
        foodsArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(userUid).child("Favoriler")
            .addValueEventListener(object  : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    foodsArrayList.clear()
                    for (ds in snapshot.children){
                        val tarifId = "${ds.child("tarifId").value}"

                        val modelTarif = ModelTarif()
                        modelTarif.id = tarifId

                        foodsArrayList.add(modelTarif)
                    }
                    //favori tarif sayısı
                    binding.favoriTarifCountTv.text = "${foodsArrayList.size}"

                    //adapter oluşturma
                    adapterFavoriUsers = TarifAdapterFavoriUsers(this@ProfileActivityUsers, foodsArrayList)
                    //adapterı recyclerview'e bağlama
                    binding.favorilerRv.adapter = adapterFavoriUsers
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }
}