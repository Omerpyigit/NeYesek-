package com.example.bugunneyesem.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bugunneyesem.adapters.TarifAdapterFavori
import com.example.bugunneyesem.databinding.ActivityFavorilerAnasayfaBinding
import com.example.bugunneyesem.models.ModelTarif
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavorilerAnasayfa : AppCompatActivity() {
    private lateinit var binding: ActivityFavorilerAnasayfaBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var foodsArrayList: ArrayList<ModelTarif>
    private lateinit var adapterTarifFavorite: TarifAdapterFavori

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavorilerAnasayfaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadFavoriTarifler()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadFavoriTarifler(){
        foodsArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favoriler")
            .addValueEventListener(object  : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    foodsArrayList.clear()
                    for (ds in snapshot.children){
                        val tarifId = "${ds.child("tarifId").value}"

                        val modelTarif = ModelTarif()
                        modelTarif.id = tarifId

                        foodsArrayList.add(modelTarif)
                    }


                    //adapter oluþturma
                    adapterTarifFavorite = TarifAdapterFavori(this@FavorilerAnasayfa, foodsArrayList)
                    //adapterý recyclerview'e baðlama
                    binding.favorilerRv.adapter = adapterTarifFavorite
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }
}