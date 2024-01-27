package com.example.bugunneyesem.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bugunneyesem.MyApplication
import com.example.bugunneyesem.R
import com.example.bugunneyesem.adapters.AdapterComment
import com.example.bugunneyesem.databinding.ActivityTarifDetayAdminBinding
import com.example.bugunneyesem.databinding.DialogCommentAddBinding
import com.example.bugunneyesem.models.ModelComment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TarifDetayActivityAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityTarifDetayAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth
    //private lateinit var progressDialog: ProgressDialog



    private companion object{
        const val TAG = "TARIF_DETAY_TAG"
    }

    private var tarifId = ""

    private var userId = ""

    private var isInMyFavorite = false

    private lateinit var commentArrayList: ArrayList<ModelComment>
    //yorumları recycler view de göstermek için adaptar
    private lateinit var adapterComment: AdapterComment

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTarifDetayAdminBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        tarifId = intent.getStringExtra("tarifId")!!

        firebaseAuth = FirebaseAuth.getInstance()


        //sayfa açıldığında görüntüleme sayısını arttır
        MyApplication.incrementTarifViewCount(tarifId)

        loadTarifDetay()
        checkIsFavorite()
        showComments()

        binding.backbutton.setOnClickListener{
            onBackPressed()
        //startActivity(Intent(this, TarifListActivityAdmin::class.java))
        }

        binding.favButton.setOnClickListener {
            if(firebaseAuth.currentUser == null){
                Toast.makeText(this, "Lütfen Giriş Yapın", Toast.LENGTH_SHORT).show()
            }
            else{
                if(isInMyFavorite){
                    //tıklandığında zaten favoriler listesindeyse favoriden çıkar
                    MyApplication.removeFavorite(this, tarifId)
                }
                else{
                    //tıkladığında favorilerde değil kalp boş
                    addToFavorite()
                }
            }
        }

        binding.yayinlayanTv.setOnClickListener{
            if(firebaseAuth.uid == userId){
                startActivity(Intent(this,ProfileActivity::class.java))
            }
            else{
                val intent = Intent(this, ProfileActivityUsers::class.java)
                intent.putExtra("userUid", userId)
                startActivity(intent)

            }
        }

        binding.addCommentBtn.setOnClickListener {
            addCommentDialog()
        }
    }

    private fun showComments() {
        commentArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
        ref.child(tarifId).child("Yorumlar")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentArrayList.clear()
                    for(ds in snapshot.children){
                        val model = ds.getValue(ModelComment::class.java)

                        commentArrayList.add(model!!)
                    }

                    adapterComment = AdapterComment(this@TarifDetayActivityAdmin, commentArrayList)

                    binding.commentsRv.adapter = adapterComment
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private var comment = ""

    private fun addCommentDialog(){
        val commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this))

        //alert dialog için setup
        val builder = AlertDialog.Builder(this,R.style.CustomDialog)
        builder.setView(commentAddBinding.root)

        //alert dialog oluştur ve göster
        val alertDialog = builder.create()
        alertDialog.show()

        //geri tuşuna tıklandığında
        commentAddBinding.backBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        //göndere tıklandığında
        commentAddBinding.submitBtn.setOnClickListener {

            comment = commentAddBinding.commentEt.text.toString().trim()
            if(comment.isEmpty()){
                Toast.makeText(this, "Yorum giriniz", Toast.LENGTH_SHORT).show()
            }
            else{
                alertDialog.dismiss()
                addComment()
            }

        }
    }

    private fun addComment(){
        val timestamp = "${System.currentTimeMillis()}"

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["tarifId"] = "$tarifId"
        hashMap["timestamp"] = "$timestamp"
        hashMap["comment"] = "$comment"
        hashMap["uid"] = "${firebaseAuth.uid}"

        //veritabanına kaydetme
        //Tarifler > tarifId > Yorumlar > yorumId > commentData
        val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
        ref.child(tarifId).child("Yorumlar").child(timestamp)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Yorum Gönderildi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(this, "Yorum Gönderme Başarısız ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadTarifDetay(){

        val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
        ref.child(tarifId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //verileri al
                    val kategoriId = "${snapshot.child("kategoriId").value}"
                    val baslik = "${snapshot.child("baslik").value}"
                    val aciklama = "${snapshot.child("aciklama").value}"
                    val favoriler = "${snapshot.child("favoriler").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val viewsCount = "${snapshot.child("viewsCount").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val imgUrl = "${snapshot.child("img").value}"
                    userId = uid


                    val refUser = FirebaseDatabase.getInstance().getReference("Users")
                    refUser.child(uid)
                        .addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val yayinlayan = "${snapshot.child("kadi").value}"
                                binding.yayinlayanTv.text = yayinlayan
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })

                    //tarih formatini düzenleme

                    val date = MyApplication.formatTimeStamp(timestamp.toLong())

                    MyApplication.loadKategori(kategoriId, binding.kategoriTv)

                    try {
                        Glide.with(this@TarifDetayActivityAdmin)
                            .load(imgUrl)
                            .into(binding.imgView)
                    }
                    catch (e: Exception){

                    }


                    /*
                    Glide.with(this@TarifDetayActivityAdmin)
                        .load(imgUrl)
                        .into(binding.imgView) */



                    /*Picasso.get()
                        .load(imgUrl)
                        .into(binding.imgView) */

                    binding.titleTv.text = baslik

                    binding.aciklamaTv.text = aciklama
                    binding.dateTv.text = date
                    binding.goruntulemeTv.text = viewsCount
                    binding.favoriTv.text = favoriler

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


    }

    private fun checkIsFavorite(){
        Log.d(TAG, "checkIsFavorite: Favori checking")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favoriler").child(tarifId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInMyFavorite = snapshot.exists()
                    if(isInMyFavorite){
                        //zaten favori listesinde
                        binding.favButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,
                            R.drawable.favori_dolu,0)

                    }
                    else{
                        //favorilerde yok
                        binding.favButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,
                            R.drawable.favori_bos_beyaz,0)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun addToFavorite(){
        Log.d(TAG, "addToFavorite: Favoriye ekleniyor")
        val timestamp = System.currentTimeMillis()

        //favori yapılan tarifin bilgileri
        val hashMap = HashMap<String, Any>()
        hashMap["tarifId"] = tarifId
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favoriler").child(tarifId)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "addToFavorite: Favoriye eklendi")
                Toast.makeText(this,"Tarif Favorilere Eklendi",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Log.d(TAG, "addToFavorite: Favori ekleme başarısız${e.message}")
                Toast.makeText(this,"Favori ekleme başarısız${e.message}",Toast.LENGTH_SHORT).show()
            }
    }


}