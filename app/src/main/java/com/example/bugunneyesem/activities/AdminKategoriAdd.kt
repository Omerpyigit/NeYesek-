package com.example.bugunneyesem.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bugunneyesem.adapters.KategoriAdapter
import com.example.bugunneyesem.databinding.ActivityAdminKategoriAddBinding
import com.example.bugunneyesem.models.ModelKategori
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminKategoriAdd : AppCompatActivity() {
    lateinit var binding: ActivityAdminKategoriAddBinding
    private lateinit var firebaseAuth: FirebaseAuth

    //kategorileri tutmak için array list
    private lateinit var kategoriArrayList: ArrayList<ModelKategori>
    //adapter
    private lateinit var adapterCategory: KategoriAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminKategoriAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        loadKategoriler()

        /*
        //arama
        binding.searchEt.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //kullanıcı yazarken arar
                try{
                    adapterCategory.filter.filter(s)
                }
                catch(e: Exception){

                }
            }

            override fun afterTextChanged(p0: Editable?) {
                TODO("Not yet implemented")
            }

        }) */

        binding.addTarif.setOnClickListener{
            startActivity(Intent(this, TarifEkleAdmin::class.java))
        }
        binding.backbutton1.setOnClickListener{
            startActivity(Intent(this, AdminPanel::class.java))
        }
        binding.addKatego.setOnClickListener {
            startActivity(Intent(this, KategoriEkle::class.java))
        }
    }

    private fun loadKategoriler(){
        kategoriArrayList = ArrayList()

        //database den kategorileri al
        val ref = FirebaseDatabase.getInstance().getReference("Kategoriler")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kategoriArrayList.clear()
                for(ds in snapshot.children){
                    val model = ds.getValue(ModelKategori::class.java)

                    kategoriArrayList.add(model!!)
                }
                //adapter
                adapterCategory = KategoriAdapter(this@AdminKategoriAdd, kategoriArrayList)
                //adapterı recylerview e bağla
                binding.kategorilerRW.adapter = adapterCategory
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        } )
    }
}