package com.example.bugunneyesem.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bugunneyesem.MyApplication
import com.example.bugunneyesem.activities.TarifDetayActivityAdmin
import com.example.bugunneyesem.databinding.SatirTarifUserBinding
import com.example.bugunneyesem.models.ModelTarif
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class TarifAdapterFavoriUsers : RecyclerView.Adapter<TarifAdapterFavoriUsers.HolderTarifFavorite> {
    private var context: Context

    public var foodsArrayList: ArrayList<ModelTarif>

    private lateinit var binding: SatirTarifUserBinding

    constructor(context: Context, foodsArrayList: ArrayList<ModelTarif>){
        this.context = context
        this.foodsArrayList = foodsArrayList
    }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderTarifFavorite {
        //binding ve inflate
        binding = SatirTarifUserBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderTarifFavorite(binding.root)
    }

    override fun onBindViewHolder(holder: HolderTarifFavorite, position: Int) {
        //set data get data ve t�klama i�lemleri

        val model = foodsArrayList[position]
        loadTarifDetails(model, holder, position)

        /*Picasso.get()
            .load(foodsArrayList[position].img)
            .into(holder.imgView)*/

        //tarife t�kland���nda detaylara git
        holder.itemView.setOnClickListener {
            val intent = Intent(context, TarifDetayActivityAdmin::class.java)
            intent.putExtra("tarifId", model.id)
            context.startActivity(intent)
        }

    }

    private fun loadTarifDetails(model: ModelTarif, holder: TarifAdapterFavoriUsers.HolderTarifFavorite, position: Int){
        val tarifId = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
        ref.child(tarifId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //tarif detaylar�n� al
                    val kategoriId = "${snapshot.child("kategoriId").value}"
                    val aciklama = "${snapshot.child("aciklama").value}"
                    val favoriCount = "${snapshot.child("favoriler").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val baslik = "${snapshot.child("baslik").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val img = "${snapshot.child("img").value}"
                    val viewsCounts = "${snapshot.child("viewsCount").value}"

                    model.isFavorite = true
                    model.baslik = baslik
                    model.aciklama = aciklama
                    model.kategoriId = kategoriId
                    model.timestamp = timestamp.toLong()
                    model.uid = uid
                    model.img = img
                    model.viewsCount = viewsCounts.toLong()
                    model.favoriler = favoriCount.toLong()


                    //tarih d�zenleneme
                    val date = MyApplication.formatTimeStamp(timestamp.toLong())
                    MyApplication.loadKategori("$kategoriId",holder.kategoriTv)

                    holder.titleTv.text = baslik
                    holder.aciklamaTv.text = aciklama
                    holder.dateTv.text = date

                    Picasso.get()
                        .load(foodsArrayList[position].img)
                        .into(holder.imgView)


                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,"�ptal Edildi", Toast.LENGTH_SHORT).show()
                }

            })
    }
    override fun getItemCount(): Int {
        return foodsArrayList.size
    }


    //UI da satir_tarif_favoriyi d�zenlemek i�in ViewHolder
    inner class HolderTarifFavorite(itemView: View) : RecyclerView.ViewHolder(itemView){

        var imgView = binding.imgView
        //var progressBar = binding.progressBar
        var titleTv = binding.titleTv
        var aciklamaTv = binding.aciklamaTv
        var kategoriTv = binding.kategoriTv
        var dateTv = binding.dateTv
    }


}
