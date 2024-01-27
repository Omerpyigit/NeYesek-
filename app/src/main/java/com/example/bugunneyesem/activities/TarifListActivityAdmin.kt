package com.example.bugunneyesem.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bugunneyesem.adapters.TarifAdapterAdmin
import com.example.bugunneyesem.databinding.ActivityTarifListAdminBinding
import com.example.bugunneyesem.models.ModelTarif
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TarifListActivityAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityTarifListAdminBinding

    private  companion object{
        const val TAG = "FOOD_LIST_ADMIN_TAG"
    }

    private var kategoriID = ""
    private var kategori = ""

    private lateinit var foodArrayList:ArrayList<ModelTarif>

    private lateinit var adapterImgAdmin: TarifAdapterAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTarifListAdminBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = intent
        kategoriID = intent.getStringExtra("kategoriID")!!
        kategori = intent.getStringExtra("kategori")!!

        binding.subtitleTv.text = kategori
        loadFoodList()
        binding.searchEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                try{
                    adapterImgAdmin.filter!!.filter(s)
                }
                catch(e: Exception){
                    Log.d(TAG,"onTextChanged hata ${e.message}")
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.backbutton4.setOnClickListener{
            startActivity(Intent(this, AdminKategoriAdd::class.java))
        }

    }

    private fun loadFoodList(){
        foodArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
        ref.orderByChild("kategoriId").equalTo(kategoriID)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    foodArrayList.clear()
                    for(ds in snapshot.children){
                        val model = ds.getValue(ModelTarif::class.java)
                        if (model != null) {
                            foodArrayList.add(model)
                            Log.d(TAG,"onDataChange: ${model.baslik} ${model.kategoriId}")
                        }

                    }

                    adapterImgAdmin = TarifAdapterAdmin(this@TarifListActivityAdmin, foodArrayList)
                    binding.yemeklerRv.adapter = adapterImgAdmin
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }
}