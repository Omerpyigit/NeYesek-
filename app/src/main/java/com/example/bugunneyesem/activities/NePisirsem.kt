package com.example.bugunneyesem.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bugunneyesem.MalzemeDialog
import com.example.bugunneyesem.MalzemeDialogListener
import com.example.bugunneyesem.adapters.TarifAdapterNePisirsem
import com.example.bugunneyesem.databinding.ActivityNePisirsemBinding
import com.example.bugunneyesem.models.ModelMalzeme
import com.example.bugunneyesem.models.ModelTarif
import com.example.bugunneyesem.yapayzeka.OneriYemeklerTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NePisirsem : AppCompatActivity(), MalzemeDialogListener {
    private lateinit var binding: ActivityNePisirsemBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var malzemelerArrayList: ArrayList<ModelMalzeme>


    private lateinit var foodsArrayList: ArrayList<ModelTarif>
    private lateinit var adapterNePisirsem: TarifAdapterNePisirsem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNePisirsemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        loadMalzemeler()



        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.malzemelerTv.setOnClickListener {
            malzemePickDialog()
        }

        binding.kaydetBut1.setOnClickListener {
            val selectedMalzemeler = MalzemeDialog.selectedMalzemeler
            val task = OneriYemeklerTask(this)
            task.execute(selectedMalzemeler) { yemeklerList ->
                // Önerilen yemeklerin listesini aldıktan sonra Firebase'den tarifleri yükle
                loadTarifler(yemeklerList)
            }
        }
    }
    private var id = ""
    private var name = ""

    private fun loadMalzemeler(){
        malzemelerArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Malzemeler")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                malzemelerArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelMalzeme::class.java)

                    malzemelerArrayList.add(model!!)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        } )
    }


    private fun malzemePickDialog() {
        val malzemelerArray = arrayOfNulls<String>(malzemelerArrayList.size)

        for(i in malzemelerArrayList.indices){
            malzemelerArray[i] = malzemelerArrayList[i].name
        }
        var data = malzemelerArray


        val builder = MalzemeDialog(this,data)
        builder.setMalzemeDialogListener(this)
        builder.show(supportFragmentManager,"")
        builder.isCancelable = false
        val selectedMalzemeler = MalzemeDialog.selectedMalzemeler

    }
    override fun onMalzemelerSecildi(selectedMalzemeler: List<String>) {
        // Seçilen malzemeleri burada kullanabilirsiniz
        // Örneğin, bir listede görüntülemek veya işlemek için
        // Seçilen malzemeleri bir listeye atayarak kullanabilirsiniz
        val selectedMalzemeList = ArrayList<String>()
        selectedMalzemeList.addAll(selectedMalzemeler)
    }



    private fun loadTarifler(yemekler: List<String>){
        foodsArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
        ref.addValueEventListener(object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                foodsArrayList.clear()
                for (ds in snapshot.children){
                    val tarifId = ds.key?.toString()
                    if (yemekler.contains(tarifId)) {
                        val modelTarif = ds.getValue(ModelTarif::class.java)
                        modelTarif?.let {
                            foodsArrayList.add(it)
                        }
                    }
                    


                }

                //adapter oluşturma
                adapterNePisirsem = TarifAdapterNePisirsem(this@NePisirsem, foodsArrayList)
                //adapterı recyclerview'e bağlama
                binding.favorilerRv.adapter = adapterNePisirsem
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}