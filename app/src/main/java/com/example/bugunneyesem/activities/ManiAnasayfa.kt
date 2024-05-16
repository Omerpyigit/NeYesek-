package com.example.bugunneyesem.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bugunneyesem.databinding.ActivityManiAnasayfaBinding
import com.example.bugunneyesem.models.ModelTarif
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_mani_anasayfa.textView3

class ManiAnasayfa : AppCompatActivity() {
    lateinit var binding: ActivityManiAnasayfaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var foodArrayList: ArrayList<ModelTarif>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityManiAnasayfaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        loadAllFoods()

        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val kullaniciadi = snapshot.child("kadi").value
                    textView3.setText("Hoş Geldin ${kullaniciadi}")
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })



        binding.btnTarifler.setOnClickListener {
            intent = Intent(this, TariflerPageUser::class.java) //TariflerPage
            startActivity(intent)
            finish()
        }

        binding.btnFavorilerim.setOnClickListener {
            startActivity(Intent(this, FavorilerAnasayfa::class.java))

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

        binding.btnNePisirsem.setOnClickListener {
            startActivity(Intent(this, NePisirsem::class.java))
        }


    }

    private fun loadAllFoods(){

        foodArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                foodArrayList.clear()
                for(ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelTarif::class.java)
                    //listeye ekle
                    foodArrayList.add(model!!)
                }



                val model = foodArrayList.shuffled()[0]
                val tarifId = model.id
                val img = model.img

                try {
                    Glide.with(this@ManiAnasayfa)
                        .load(img)
                        .into(binding.imgRandomm)
                }
                catch (e: Exception){

                }
                binding.imgRandomm.setOnClickListener{
                    val intent = Intent(this@ManiAnasayfa, TarifDetayActivityAdmin::class.java)
                    intent.putExtra("tarifId", tarifId)
                    startActivity(intent)
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}